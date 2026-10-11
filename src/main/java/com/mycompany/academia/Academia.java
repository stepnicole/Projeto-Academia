package com.mycompany.academia;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.TipoAula;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Academia {

    public static void main(String[] args) throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        Cliente c = new Cliente(1, "Ana", "Rua A", "3899999-0000",
                "ana@email.com", "xxx.123.xxx-45", "1234567812345678");
        c.adicionarAgendamento(10);
        clientes.add(c);

        Persistencia.salvarLista("dados/clientes.json", clientes);

        List<Cliente> lidos = Persistencia.carregarLista("dados/clientes.json", Cliente.class);
        System.out.println(lidos);
        
        Instrutor i = new Instrutor(4, "Marcos", "Rua D", "3866666-0000",
        "marcos@email.com", "xxx.321.xxx-09", TipoAula.SPINNING, 2500.0);
        System.out.println(i);

        List<Instrutor> instrutores = new ArrayList<>();
        instrutores.add(i);

        Persistencia.salvarLista("dados/instrutores.json", instrutores);

        List<Instrutor> instrutoresLidos = Persistencia.carregarLista("dados/instrutores.json", Instrutor.class);
        System.out.println(instrutoresLidos);
    }
}