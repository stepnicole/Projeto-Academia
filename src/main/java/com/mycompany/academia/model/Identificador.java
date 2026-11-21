package com.mycompany.academia.model;

import java.util.List;

/**
 * Contrato para todo objeto do sistema que possui um identificador numérico.
 * <p>
 * Permite que os serviços gerem o próximo id de qualquer lista
 * (clientes, agendamentos, produtos...) com um único método genérico.
 *
 */
public interface Identificador {

    /**
     * @return identificador único do objeto
     */
    int getId();

    /**
     * Calcula o próximo id livre de uma lista: o maior id existente mais 1
     * (ou 1 se a lista estiver vazia).
     *
     * @param lista objetos já cadastrados
     * @return próximo id
     */
    static int proximoId(List<? extends Identificador> lista) {
        int maior = 0;
        for (Identificador o : lista) {
            maior = Math.max(maior, o.getId());
        }
        return maior + 1;
    }
}