package com.mycompany.academia.model.state;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import java.time.LocalDate;

/** Estado concreto (State): agendamento confirmado e cobrado. */
public class Confirmado implements EstadoAgendamento {

    @Override
    public StatusAgendamento getStatus() {
        return StatusAgendamento.CONFIRMADO;
    }

    @Override
    public boolean confirmar(Agendamento ag, LocalDate hoje) {
        return false; // já confirmado
    }

    @Override
    public double calcularReembolso(Agendamento ag, LocalDate hoje) {
        if (hoje.isAfter(ag.getPrazoCancelamento())) {
            return 0.0;
        }
        return ag.getPreco() * Agendamento.PERCENTUAL_REEMBOLSO;
    }

    @Override
    public double cancelar(Agendamento ag, LocalDate hoje) {
        double reembolso = calcularReembolso(ag, hoje);
        ag.setEstado(new Cancelado());
        return reembolso;
    }

    @Override
    public boolean expirar(Agendamento ag, LocalDate hoje) {
        return false; // só preliminar expira
    }
}