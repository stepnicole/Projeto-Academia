package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Cliente;
import java.util.Comparator;

/**
 * Ordena clientes pelo id, do menor para o maior.
 *
 * @author Ste
 */
public class ClientePorId implements Comparator<Cliente> {

    @Override
    public int compare(Cliente a, Cliente b) {
        // menor id vem primeiro
        if (a.getId() < b.getId()) {
            return -1;
        }
        if (a.getId() > b.getId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ClientePorId{id crescente}";
    }
}
