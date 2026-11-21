package com.mycompany.academia.model.enums;

/**
 * Tipo de marcação enviada pela catraca externa.
 */
public enum TipoAcesso {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;

    TipoAcesso(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}