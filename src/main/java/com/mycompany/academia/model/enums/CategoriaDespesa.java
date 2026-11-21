package com.mycompany.academia.model.enums;

/**
 * Categorias de despesa da academia, usadas no balanço mensal.
 * 
 */
public enum CategoriaDespesa {
    LIMPEZA("Limpeza"),
    SALARIOS("Instrutores e funcionários"),
    MATERIAL("Material de uso diário"),
    MANUTENCAO("Manutenção"),
    CONTAS("Água, luz e internet"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaDespesa(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}