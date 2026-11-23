package com.mycompany.academia.comparador;

import com.mycompany.academia.model.Cliente;
import java.util.Comparator;

/**
 * Ordena clientes pelo nome, em ordem alfabética (ignora maiúsculas/minúsculas).
 *
 * @author Ste
 */
public class ClientePorNome implements Comparator<Cliente> {

    @Override
    public int compare(Cliente a, Cliente b) {
        return a.getNome().compareToIgnoreCase(b.getNome());
    }

    @Override
    public String toString() {
        return "ClientePorNome{nome A-Z}";
    }
}