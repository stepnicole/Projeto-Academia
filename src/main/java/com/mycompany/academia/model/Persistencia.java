package com.mycompany.academia.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Salva e recupera dados em arquivos de texto no formato JSON, usando a
 * biblioteca Gson (Google).
 * <p>
 * Todo arquivo é aberto dentro de um <b>try-with-resources</b>: o
 * {@code Writer}/{@code Reader} é fechado automaticamente ao final do bloco,
 * mesmo que ocorra uma exceção, liberando o recurso com segurança.
 * <p>
 * Classe utilitária: só tem métodos estáticos, por isso o construtor é
 * privado e não há atributos de instância (nem get/set).
 *
 */
public final class Persistencia {

    /** Gson configurado para gravar LocalDate/LocalDateTime como texto ISO. */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, t, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, t, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, t, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, t, ctx) -> LocalDateTime.parse(json.getAsString()))
            .create();

    private Persistencia() {
        // não deve ser instanciada
    }

    /** Cria a pasta do arquivo, se ainda não existir. */
    private static void criarPastas(Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
    }

    /**
     * Grava uma lista de objetos em um arquivo JSON.
     *
     * @param <T>     tipo dos elementos
     * @param caminho caminho do arquivo (ex.: "dados/clientes.json")
     * @param lista   lista a ser salva
     * @throws IOException se houver erro de escrita
     */
    public static <T> void salvarLista(String caminho, List<T> lista) throws IOException {
        Path path = Paths.get(caminho);
        criarPastas(path);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(lista, writer);
        }
    }

    /**
     * Lê uma lista de objetos de um arquivo JSON.
     * Se o arquivo não existir, retorna uma lista vazia.
     *
     * @param <T>     tipo dos elementos
     * @param caminho caminho do arquivo
     * @param classe  classe dos elementos (ex.: Cliente.class)
     * @return lista carregada
     * @throws IOException se houver erro de leitura
     */
    public static <T> List<T> carregarLista(String caminho, Class<T> classe) throws IOException {
        Path path = Paths.get(caminho);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Type tipo = TypeToken.getParameterized(List.class, classe).getType();
            List<T> lista = GSON.fromJson(reader, tipo);
            return lista != null ? lista : new ArrayList<>();
        }
    }

    /**
     * Grava um único objeto (ex.: o {@code Estoque}) em JSON.
     *
     * @param caminho caminho do arquivo
     * @param objeto  objeto a salvar
     * @throws IOException se houver erro de escrita
     */
    public static void salvarObjeto(String caminho, Object objeto) throws IOException {
        Path path = Paths.get(caminho);
        criarPastas(path);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(objeto, writer);
        }
    }

    /**
     * Lê um único objeto de um arquivo JSON.
     *
     * @param <T>     tipo do objeto
     * @param caminho caminho do arquivo
     * @param classe  classe do objeto
     * @return o objeto, ou null se o arquivo não existir
     * @throws IOException se houver erro de leitura
     */
    public static <T> T carregarObjeto(String caminho, Class<T> classe) throws IOException {
        Path path = Paths.get(caminho);
        if (!Files.exists(path)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, classe);
        }
    }

    /**
     * Grava um texto simples (usado para os extratos impressos).
     *
     * @param caminho caminho do arquivo
     * @param texto   conteúdo
     * @throws IOException se houver erro de escrita
     */
    public static void salvarTexto(String caminho, String texto) throws IOException {
        Path path = Paths.get(caminho);
        criarPastas(path);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(texto);
        }
    }

    /**
     * Lê todas as linhas de um arquivo de texto (usado para importar os
     * eventos da catraca externa).
     *
     * @param caminho caminho do arquivo
     * @return linhas do arquivo
     * @throws IOException se o arquivo não existir ou não puder ser lido
     */
    public static List<String> lerLinhas(String caminho) throws IOException {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
        }
        return linhas;
    }

    @Override
    public String toString() {
        return "Persistencia{formato=JSON, biblioteca=Gson}";
    }
}