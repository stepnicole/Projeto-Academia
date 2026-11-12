package com.mycompany.academia.model.state;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import java.time.LocalDate;

/**
 * Estado abstrato (padrão State) de um Agendamento. Cada implementação
 * define como o agendamento reage às operações quando está naquele estado.
 */
public interface EstadoAgendamento {

    StatusAgendamento getStatus();

    boolean confirmar(Agendamento ag, LocalDate hoje);

    double calcularReembolso(Agendamento ag, LocalDate hoje);

    double cancelar(Agendamento ag, LocalDate hoje);

    boolean expirar(Agendamento ag, LocalDate hoje);
}