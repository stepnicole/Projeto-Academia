package com.mycompany.academia.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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
 * Salva e recupera listas de objetos em arquivos de texto no formato JSON.
 */
public class Persistencia {

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
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
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
}
