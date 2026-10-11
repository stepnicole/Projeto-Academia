package com.mycompany.academia.model;

import java.time.LocalDate;

/**
 * Movimentação financeira ligada a um agendamento: a cobrança feita no cartão
 * na confirmação ou o reembolso devolvido ao cliente no cancelamento.
 * Guarda apenas o id do agendamento, para facilitar a gravação em JSON.
 */
public class Pagamento {

    private int id;
    private int idAgendamento;
    private double valor;
    private LocalDate data;
    private TipoPagamento tipo;

    /**
     * Cria um pagamento.
     *
     * @param id            identificador do pagamento
     * @param idAgendamento id do agendamento relacionado
     * @param valor         valor da movimentação (sempre positivo)
     * @param data          data da movimentação
     * @param tipo          cobrança ou reembolso
     */
    public Pagamento(int id, int idAgendamento, double valor,
                     LocalDate data, TipoPagamento tipo) {
        this.id = id;
        this.idAgendamento = idAgendamento;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
    }

    /**
     * Debita no cartão do cliente o valor do agendamento confirmado.
     *
     * @param id          identificador do novo pagamento
     * @param agendamento agendamento confirmado
     * @param data        data da cobrança
     * @return o pagamento de cobrança, ou null se o agendamento não estiver confirmado
     */
    public static Pagamento debitar(int id, Agendamento agendamento, LocalDate data) {
        if (agendamento.getStatus() != StatusAgendamento.CONFIRMADO) {
            return null;
        }
        return new Pagamento(id, agendamento.getId(), agendamento.getPreco(),
                data, TipoPagamento.COBRANCA);
    }

    /**
     * Registra a devolução de dinheiro ao cliente.
     *
     * @param id          identificador do novo pagamento
     * @param agendamento agendamento cancelado
     * @param valor       valor a devolver (retornado por {@link Agendamento#cancelar})
     * @param data        data do reembolso
     * @return o pagamento de reembolso, ou null se o valor for zero ou negativo
     */
    public static Pagamento reembolsar(int id, Agendamento agendamento,
                                       double valor, LocalDate data) {
        if (valor <= 0) {
            return null;
        }
        return new Pagamento(id, agendamento.getId(), valor,
                data, TipoPagamento.REEMBOLSO);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(int idAgendamento) { this.idAgendamento = idAgendamento; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public TipoPagamento getTipo() { return tipo; }
    public void setTipo(TipoPagamento tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return "Pagamento{id=" + id + ", idAgendamento=" + idAgendamento
                + ", valor=" + valor + ", data=" + data + ", tipo=" + tipo + "}";
    }
}