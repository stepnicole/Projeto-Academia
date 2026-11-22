package com.mycompany.academia.model.enums;

/**
 * Situação do agendamento. É o que fica gravado no JSON; o objeto de estado
 * (padrão State) é recriado a partir dele pela
 * {@link com.mycompany.academia.model.state.EstadoFactory}.
 * <ul>
 *   <li>PRELIMINAR: reservado, aguardando confirmação (até 5 dias úteis antes);</li>
 *   <li>CONFIRMADO: confirmado e cobrado no cartão;</li>
 *   <li>REALIZADO: a aula já aconteceu;</li>
 *   <li>CANCELADO: expirou ou foi desfeito.</li>
 * </ul>
 *
 */
public enum StatusAgendamento {
    PRELIMINAR("Preliminar"),
    CONFIRMADO("Confirmado"),
    REALIZADO("Realizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    /** @return descrição para exibição */
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}