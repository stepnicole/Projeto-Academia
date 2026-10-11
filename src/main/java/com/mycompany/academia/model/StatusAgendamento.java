package com.mycompany.academia.model;

/**
 * Situação do agendamento: preliminar até 5 dias úteis antes de acessar a
academia,
 * confirmado após a confirmação e cancelado se expirar ou for desfeito.
 */
public enum StatusAgendamento {
    PRELIMINAR, CONFIRMADO, CANCELADO
}