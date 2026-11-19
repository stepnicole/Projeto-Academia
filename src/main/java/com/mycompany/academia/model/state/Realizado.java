package com.mycompany.academia.model.state;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import java.time.LocalDate;

/** Estado concreto (State): a aula já aconteceu, estado final. */
public class Realizado implements EstadoAgendamento {

    @Override
    public StatusAgendamento getStatus() {
        return StatusAgendamento.REALIZADO;
    }

    @Override
    public boolean confirmar(Agendamento ag, LocalDate hoje) {
        return false;
    }

    @Override
    public double calcularReembolso(Agendamento ag, LocalDate hoje) {
        return 0.0; // a aula foi usada, não há devolução
    }

    @Override
    public double cancelar(Agendamento ag, LocalDate hoje) {
        return 0.0; // não é possível cancelar o que já aconteceu
    }

    @Override
    public boolean expirar(Agendamento ag, LocalDate hoje) {
        return false;
    }

    @Override
    public boolean concluir(Agendamento ag, LocalDate hoje) {
        return false; // já realizado
    }
}