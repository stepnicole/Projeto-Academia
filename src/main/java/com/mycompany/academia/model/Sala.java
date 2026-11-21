package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.TipoAula;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sala de exercícios. As 4 salas da academia são armazenadas de forma
 * <b>estática</b> (lista {@code static final} preenchida num bloco
 * {@code static}), pois são fixas e iguais para todo o sistema.
 *
 */
public class Sala implements Identificador {

    private static final List<Sala> SALAS = new ArrayList<>();

    static {
        SALAS.add(new Sala(1, "Sala de Spinning", TipoAula.SPINNING, 15));
        SALAS.add(new Sala(2, "Sala de Musculação", TipoAula.MUSCULACAO, 30));
        SALAS.add(new Sala(3, "Sala de Fit Dance", TipoAula.FIT_DANCE, 20));
        SALAS.add(new Sala(4, "Sala de Pilates", TipoAula.PILATES, 12));
    }

    private int id;
    private String nome;
    private TipoAula tipoAula;
    private int capacidade;

    /**
     * @param id         identificador
     * @param nome       nome da sala
     * @param tipoAula   modalidade praticada
     * @param capacidade número máximo de alunos
     */
    public Sala(int id, String nome, TipoAula tipoAula, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.tipoAula = tipoAula;
        this.capacidade = capacidade;
    }

    /**
     * @return as 4 salas (lista somente leitura)
     */
    public static List<Sala> getSalas() {
        return Collections.unmodifiableList(SALAS);
    }

    /**
     * @param id id da sala
     * @return a sala ou null
     */
    public static Sala buscarPorId(int id) {
        for (Sala s : SALAS) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * @param tipo modalidade
     * @return a sala da modalidade ou null
     */
    public static Sala buscarPorTipo(TipoAula tipo) {
        for (Sala s : SALAS) {
            if (s.getTipoAula() == tipo) {
                return s;
            }
        }
        return null;
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoAula getTipoAula() { return tipoAula; }
    public void setTipoAula(TipoAula tipoAula) { this.tipoAula = tipoAula; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    @Override
    public String toString() {
        return "Sala{id=" + id + ", nome=" + nome + ", tipo=" + tipoAula
                + ", capacidade=" + capacidade + "}";
    }
}