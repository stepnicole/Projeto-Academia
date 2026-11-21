package com.mycompany.academia.model.enums;

/**
 * Forma de contratação: diária (foco nos turistas) ou mensalidade.
 */
public enum TipoAgendamento {
    DIARIA("Diária"),
    MENSALIDADE("Mensalidade");

    private final String descricao;

    TipoAgendamento(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}