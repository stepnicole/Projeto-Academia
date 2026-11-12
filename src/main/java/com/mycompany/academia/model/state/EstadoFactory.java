package com.mycompany.academia.model.state;
import com.mycompany.academia.model.enums.StatusAgendamento;

/** Cria o objeto de estado a partir do status gravado no JSON. */
public class EstadoFactory {

    private EstadoFactory() { }

    public static EstadoAgendamento fromStatus(StatusAgendamento status) {
        return switch (status) {
            case PRELIMINAR -> new Preliminar();
            case CONFIRMADO -> new Confirmado();
            case CANCELADO  -> new Cancelado();
        };
    }
}