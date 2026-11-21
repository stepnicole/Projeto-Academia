package com.mycompany.academia.model.enums;

/**
 * Origem de um extrato emitido para o cliente.
 */
public enum TipoExtrato {
    AGENDAMENTO("Diária/Mensalidade"),
    LOJINHA("Compra na lojinha/lanchonete"),
    REEMBOLSO("Reembolso de cancelamento");

    private final String descricao;

    TipoExtrato(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}