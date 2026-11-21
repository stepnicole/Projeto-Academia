package com.mycompany.academia.model.enums;

/**
 * Modalidades de aula oferecidas pela academia (uma para cada sala).
 */
public enum TipoAula {
    SPINNING("Spinning"),
    MUSCULACAO("Musculação"),
    FIT_DANCE("Fit Dance"),
    PILATES("Pilates");

    private final String descricao;

    TipoAula(String descricao) {
        this.descricao = descricao;
    }

    /** @return nome da modalidade para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}