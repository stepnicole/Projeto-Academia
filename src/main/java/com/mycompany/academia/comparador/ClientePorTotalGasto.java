package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Cliente;
import java.util.Comparator;

/**
 * Ordena clientes do que mais gastou na academia para o que menos gastou.
 *
 */
public class ClientePorTotalGasto implements Comparator<Cliente> {

    @Override
    public int compare(Cliente a, Cliente b) {
        double gastoA = a.calcularTotalConta();
        double gastoB = b.calcularTotalConta();
        // quem gastou MAIS vem primeiro (ordem decrescente)
        if (gastoA > gastoB) {
            return -1;
        }
        if (gastoA < gastoB) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ClientePorTotalGasto{maior gasto primeiro}";
    }
}