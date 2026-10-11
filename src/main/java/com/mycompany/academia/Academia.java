package com.mycompany.academia;

import com.mycompany.academia.model.Cliente;

public class Academia {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        com.google.gson.Gson gson = new com.google.gson.Gson();
        System.out.println(gson.toJson("Gson funcionando"));
        
        Cliente c = new Cliente(1, "Ana", "Rua A", "3899999-0000",
        "ana@email.com", "xxx.123.xxx-45", "1234567812345678");
        c.adicionarAgendamento(10);
        System.out.println(c);
    }
}
