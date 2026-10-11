package com.mycompany.academia.model;

import java.time.LocalDateTime;

public class Aula {

    private int id;
    private TipoAula tipoAula;
    private int idSala;
    private int idInstrutor;
    private LocalDateTime dataHora;
    private int vagasOcupadas;

    public Aula(int id, TipoAula tipoAula, int idSala, int idInstrutor,
                LocalDateTime dataHora) {
        this.id = id;
        this.tipoAula = tipoAula;
        this.idSala = idSala;
        this.idInstrutor = idInstrutor;
        this.dataHora = dataHora;
        this.vagasOcupadas = 0;
    }


    public Sala getSala() {
        return Sala.buscarPorId(idSala);
    }


    public int getVagasDisponiveis() {
        Sala sala = getSala();
        if (sala == null) {
            return 0;
        }
        return sala.getCapacidade() - vagasOcupadas;
    }

    public boolean temVaga() {
        return getVagasDisponiveis() > 0;
    }

    public boolean ocuparVaga() {
        if (temVaga()) {
            vagasOcupadas++;
            return true;
        }
        return false;
    }


    public boolean liberarVaga() {
        if (vagasOcupadas > 0) {
            vagasOcupadas--;
            return true;
        }
        return false;
    }

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