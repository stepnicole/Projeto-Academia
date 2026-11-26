package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Agendamento;
import java.util.Comparator;

/**
 * Agrupa agendamentos por cliente (id do cliente) e depois pelo id do agendamento.
 *
 */
public class AgendamentoPorCliente implements Comparator<Agendamento> {

    @Override
    public int compare(Agendamento a, Agendamento b) {
        // primeiro agrupa pelo id do cliente (menor primeiro)
        if (a.getIdCliente() < b.getIdCliente()) {
            return -1;
        }
        if (a.getIdCliente() > b.getIdCliente()) {
            return 1;
        }
        // mesmo cliente: ordena pelo id do agendamento
        if (a.getId() < b.getId()) {
            return -1;
        }
        if (a.getId() > b.getId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AgendamentoPorCliente{idCliente, depois id}";
    }
}