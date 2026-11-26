package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Agendamento;
import java.util.Comparator;

/**
 * Ordena agendamentos pela primeira data agendada (mais próxima primeiro).
 *
 */
public class AgendamentoPorData implements Comparator<Agendamento> {

    @Override
    public int compare(Agendamento a, Agendamento b) {
        // data mais próxima (anterior) vem primeiro
        if (a.getPrimeiraData().isBefore(b.getPrimeiraData())) {
            return -1;
        }
        if (a.getPrimeiraData().isAfter(b.getPrimeiraData())) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AgendamentoPorData{data mais próxima primeiro}";
    }
}