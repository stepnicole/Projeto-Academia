package com.mycompany.academia.model.state;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import java.time.LocalDate;

/** Estado concreto (State): agendamento ainda não confirmado. */
public class Preliminar implements EstadoAgendamento {

    @Override
    public StatusAgendamento getStatus() {
        return StatusAgendamento.PRELIMINAR;
    }

    @Override
    public boolean confirmar(Agendamento ag, LocalDate hoje) {
        if (hoje.isAfter(ag.getPrazoConfirmacao())) {
            return false;
        }
        ag.setEstado(new Confirmado());
        return true;
    }

    @Override
    public double calcularReembolso(Agendamento ag, LocalDate hoje) {
        return 0.0; // preliminar ainda não foi cobrado
    }

    @Override
    public double cancelar(Agendamento ag, LocalDate hoje) {
        ag.setEstado(new Cancelado());
        return 0.0;
    }

    @Override
    public boolean expirar(Agendamento ag, LocalDate hoje) {
        if (!hoje.isAfter(ag.getPrazoConfirmacao())) {
            return false;
        }
        ag.setEstado(new Cancelado());
        return true;
    }
}