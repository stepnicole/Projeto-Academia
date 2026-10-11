package com.mycompany.academia.model;

public class Administrador extends Colaborador {

   
    public Administrador(int id, String nome, String endereco, String telefone,
                         String email, String cpfAnonimizado, String login,
                         String senha, double salario) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado,
              login, senha, salario);
    }

    @Override
    public boolean podeGerenciarFinanceiro() {
        return true;
    }

    @Override
    public String toString() {
        return "Administrador{" + super.toString() + "}";
    }
}