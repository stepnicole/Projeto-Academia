package com.mycompany.academia.model.enums;
import com.mycompany.academia.model.enums.StatusAgendamento;

/**
 * Tipo de movimentação financeira de um pagamento.
 */
public enum TipoPagamento {
    COBRANCA("Cobrança"),
    REEMBOLSO("Reembolso");

    private final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}