package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.TipoAula;
import java.time.LocalDateTime;

/**
 * Turma/horário de uma modalidade, numa sala e com um instrutor.
 * Controla quantas vagas da sala já foram ocupadas por agendamentos.
 *
 */
public class Aula implements Identificador {

    private int id;
    private TipoAula tipoAula;
    private int idSala;
    private int idInstrutor;
    private LocalDateTime dataHora;
    private int vagasOcupadas;

    /**
     * @param id          identificador
     * @param tipoAula    modalidade
     * @param idSala      id da sala (1 a 4)
     * @param idInstrutor id do instrutor responsável
     * @param dataHora    horário da aula
     */
    public Aula(int id, TipoAula tipoAula, int idSala, int idInstrutor,
                LocalDateTime dataHora) {
        this.id = id;
        this.tipoAula = tipoAula;
        this.idSala = idSala;
        this.idInstrutor = idInstrutor;
        this.dataHora = dataHora;
        this.vagasOcupadas = 0;
    }

    /** @return a sala da aula (consulta a lista estática de salas) */
    public Sala getSala() {
        return Sala.buscarPorId(idSala);
    }

    /** @return capacidade da sala menos as vagas ocupadas */
    public int getVagasDisponiveis() {
        Sala sala = getSala();
        if (sala == null) {
            return 0;
        }
        return sala.getCapacidade() - vagasOcupadas;
    }

    /** @return true se ainda há vaga */
    public boolean temVaga() {
        return getVagasDisponiveis() > 0;
    }

    /**
     * Ocupa uma vaga, se houver.
     *
     * @return true se ocupou
     */
    public boolean ocuparVaga() {
        if (temVaga()) {
            vagasOcupadas++;
            return true;
        }
        return false;
    }

    /**
     * Libera uma vaga (quando um agendamento é cancelado).
     *
     * @return true se havia vaga ocupada
     */
    public boolean liberarVaga() {
        if (vagasOcupadas > 0) {
            vagasOcupadas--;
            return true;
        }
        return false;
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public TipoAula getTipoAula() { return tipoAula; }
    public void setTipoAula(TipoAula tipoAula) { this.tipoAula = tipoAula; }

    public int getIdSala() { return idSala; }
    public void setIdSala(int idSala) { this.idSala = idSala; }

    public int getIdInstrutor() { return idInstrutor; }
    public void setIdInstrutor(int idInstrutor) { this.idInstrutor = idInstrutor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public int getVagasOcupadas() { return vagasOcupadas; }
    public void setVagasOcupadas(int vagasOcupadas) { this.vagasOcupadas = vagasOcupadas; }

    @Override
    public String toString() {
        Sala sala = getSala();
        return "Aula{id=" + id + ", tipo=" + tipoAula
                + ", sala=" + (sala != null ? sala.getNome() : "?")
                + ", idInstrutor=" + idInstrutor + ", dataHora=" + dataHora
                + ", vagas=" + vagasOcupadas + "/" + (sala != null ? sala.getCapacidade() : 0) + "}";
    }
}