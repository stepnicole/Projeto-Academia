package com.mycompany.academia.model.state;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import java.time.LocalDate;

/** Estado concreto (State): agendamento cancelado, estado final. */
public class Cancelado implements EstadoAgendamento {

    @Override
    public StatusAgendamento getStatus() {
        return StatusAgendamento.CANCELADO;
    }

    @Override
    public boolean confirmar(Agendamento ag, LocalDate hoje) {
        return false;
    }

    @Override
    public double calcularReembolso(Agendamento ag, LocalDate hoje) {
        return 0.0;
    }

    @Override
    public double cancelar(Agendamento ag, LocalDate hoje) {
        return 0.0; // já cancelado, nada muda
    }

    @Override
    public boolean expirar(Agendamento ag, LocalDate hoje) {
        return false;
    }
}