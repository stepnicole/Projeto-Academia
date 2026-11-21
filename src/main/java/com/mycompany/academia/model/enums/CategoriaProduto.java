package com.mycompany.academia.model.enums;

/**
 * Onde o produto é vendido: na lanchonete ou na lojinha de material Esportivo.
 */
public enum CategoriaProduto {
    LANCHONETE("Lanchonete"),
    LOJA("Loja de material Esportivo");

    private final String descricao;

    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}