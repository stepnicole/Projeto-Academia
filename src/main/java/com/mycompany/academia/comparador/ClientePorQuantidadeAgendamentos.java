package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Cliente;
import java.util.Comparator;

/**
 * Ordena clientes do que tem mais agendamentos para o que tem menos (empate: nome).
 *
 */
public class ClientePorQuantidadeAgendamentos implements Comparator<Cliente> {

    @Override
    public int compare(Cliente a, Cliente b) {
        int qtdA = a.getIdsAgendamentos().size();
        int qtdB = b.getIdsAgendamentos().size();
        // quem tem MAIS agendamentos vem primeiro (ordem decrescente)
        if (qtdA > qtdB) {
            return -1;
        }
        if (qtdA < qtdB) {
            return 1;
        }
        // empate: desempata pelo nome em ordem alfabética
        return a.getNome().compareToIgnoreCase(b.getNome());
    }

    @Override
    public String toString() {
        return "ClientePorQuantidadeAgendamentos{mais agendamentos primeiro}";
    }
}