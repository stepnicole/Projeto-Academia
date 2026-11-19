package com.mycompany.academia.model.enums;

/**
 * Situação do agendamento: preliminar até a confirmação do cliente (até 5 dias
 * úteis antes de acessar a academia), confirmado após a confirmação, realizado
 * depois que a data da aula passa e cancelado se expirar ou for desfeito.
 */
public enum StatusAgendamento {
    PRELIMINAR, CONFIRMADO, REALIZADO, CANCELADO
}