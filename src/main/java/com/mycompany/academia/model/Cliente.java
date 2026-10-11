package com.mycompany.academia.model;
import java.util.ArrayList;
import java.util.List;


public class Cliente extends Pessoa {

    private String cartaoCredito;
    private List<Integer> idsAgendamentos;

  
    public Cliente(int id, String nome, String endereco, String telefone,
                   String email, String cpfAnonimizado, String cartaoCredito) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.cartaoCredito = cartaoCredito;
        this.idsAgendamentos = new ArrayList<>();
    }

    public String getCartaoCredito() { return cartaoCredito; }
    public void setCartaoCredito(String cartaoCredito) { this.cartaoCredito = cartaoCredito; }

    public List<Integer> getIdsAgendamentos() { return idsAgendamentos; }
    public void setIdsAgendamentos(List<Integer> idsAgendamentos) { this.idsAgendamentos = idsAgendamentos; }

    public void adicionarAgendamento(int idAgendamento) {
        idsAgendamentos.add(idAgendamento);
    }

    public boolean removerAgendamento(int idAgendamento) {
        return idsAgendamentos.remove(Integer.valueOf(idAgendamento));
    }

    @Override
    public String toString() {
        return "Cliente{" + super.toString()
                + ", cartao=" + mascararCartao()
                + ", agendamentos=" + idsAgendamentos + "}";
    }

    /**
     * Mostra só os 4 últimos dígitos do cartão, para não expor o número inteiro
     * ao imprimir o cliente.
     */
    private String mascararCartao() {
        if (cartaoCredito == null || cartaoCredito.length() < 4) {
            return "****";
        }
        return "****" + cartaoCredito.substring(cartaoCredito.length() - 4);
    }
}
