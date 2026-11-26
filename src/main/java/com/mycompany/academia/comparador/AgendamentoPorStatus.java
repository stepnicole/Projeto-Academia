package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Agendamento;
import java.util.Comparator;

/**
 * Ordena agendamentos pelo status (preliminar, confirmado, realizado, cancelado)
 * e, em caso de empate, pela data.
 *
 */
public class AgendamentoPorStatus implements Comparator<Agendamento> {

    @Override
    public int compare(Agendamento a, Agendamento b) {
        // ordinal() é a posição do valor no enum:
        // PRELIMINAR=0, CONFIRMADO=1, REALIZADO=2, CANCELADO=3
        int posicaoA = a.getStatus().ordinal();
        int posicaoB = b.getStatus().ordinal();
        if (posicaoA < posicaoB) {
            return -1;
        }
        if (posicaoA > posicaoB) {
            return 1;
        }
        // mesmo status: desempata pela data (mais próxima primeiro)
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
        return "AgendamentoPorStatus{status, depois data}";
    }
}