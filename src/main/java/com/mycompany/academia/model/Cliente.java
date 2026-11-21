package com.mycompany.academia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente da academia (turista de diária ou mensalista).
 * <p>
 * Guarda os ids dos seus agendamentos e todos os extratos emitidos para ele,
 * que juntos formam a "conta" do cliente.
 * <p>
 * Cada vez que um cliente é criado pelo construtor, os dois contadores de
 * instâncias da classe {@link Sistema} são incrementados, um por cada
 * estratégia pedida no trabalho (private + get/set e protected).
 *
 */
public class Cliente extends Pessoa {

    private String cartaoCredito;
    private List<Integer> idsAgendamentos;
    private List<Extrato> extratos;

    /**
     * @param id             identificador (definido pelo CadastroClientes)
     * @param nome           nome
     * @param endereco       endereço
     * @param telefone       telefone
     * @param email          e-mail
     * @param cpfAnonimizado CPF pseudo-anonimizado
     * @param cartaoCredito  número do cartão de crédito
     */
    public Cliente(int id, String nome, String endereco, String telefone,
                   String email, String cpfAnonimizado, String cartaoCredito) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.cartaoCredito = cartaoCredito;
        this.idsAgendamentos = new ArrayList<>();
        this.extratos = new ArrayList<>();

        // Estratégia (a): atributo private static acessado por get/set.
        Sistema.setTotalClientesCriados(Sistema.getTotalClientesCriados() + 1);
        // Estratégia (b): atributo protected static acessado diretamente
        // (possível porque Cliente está no mesmo pacote de Sistema).
        Sistema.contadorClientesProtected++;
    }

    public String getCartaoCredito() { return cartaoCredito; }
    public void setCartaoCredito(String cartaoCredito) { this.cartaoCredito = cartaoCredito; }

    public List<Integer> getIdsAgendamentos() { return idsAgendamentos; }
    public void setIdsAgendamentos(List<Integer> idsAgendamentos) { this.idsAgendamentos = idsAgendamentos; }

    public List<Extrato> getExtratos() { return extratos; }
    public void setExtratos(List<Extrato> extratos) { this.extratos = extratos; }

    /**
     * Vincula um agendamento ao cliente.
     *
     * @param idAgendamento id do agendamento
     */
    public void adicionarAgendamento(int idAgendamento) {
        if (!idsAgendamentos.contains(idAgendamento)) {
            idsAgendamentos.add(idAgendamento);
        }
    }

    /**
     * @param idAgendamento id do agendamento
     * @return true se estava vinculado e foi removido
     */
    public boolean removerAgendamento(int idAgendamento) {
        return idsAgendamentos.remove(Integer.valueOf(idAgendamento));
    }

    /**
     * Guarda um extrato junto com os dados do cliente.
     *
     * @param extrato extrato emitido
     */
    public void adicionarExtrato(Extrato extrato) {
        extratos.add(extrato);
    }

    /**
     * Soma de todos os extratos: cobranças e compras entram positivas e
     * reembolsos entram negativos.
     *
     * @return total gasto pelo cliente na academia
     */
    public double calcularTotalConta() {
        double total = 0;
        for (Extrato e : extratos) {
            total += e.getValorTotal();
        }
        return total;
    }

    /**
     * Mostra só os 4 últimos dígitos do cartão, para não expor o número inteiro
     * ao imprimir o cliente.
     *
     * @return cartão mascarado
     */
    public String getCartaoMascarado() {
        if (cartaoCredito == null || cartaoCredito.length() < 4) {
            return "****";
        }
        return "****" + cartaoCredito.substring(cartaoCredito.length() - 4);
    }

    @Override
    public String toString() {
        return "Cliente{" + super.toString()
                + ", cartao=" + getCartaoMascarado()
                + ", agendamentos=" + idsAgendamentos
                + ", extratos=" + extratos.size() + "}";
    }
}