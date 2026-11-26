package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Agendamento;
import java.util.Comparator;

/**
 * Ordena agendamentos pelo preço, do mais barato para o mais caro.
 *
 */
public class AgendamentoPorPreco implements Comparator<Agendamento> {

    @Override
    public int compare(Agendamento a, Agendamento b) {
        // mais barato vem primeiro
        if (a.getPreco() < b.getPreco()) {
            return -1;
        }
        if (a.getPreco() > b.getPreco()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AgendamentoPorPreco{preço crescente}";
    }
}