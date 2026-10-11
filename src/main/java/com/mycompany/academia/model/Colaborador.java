package com.mycompany.academia.model;

public class Colaborador extends Pessoa {

    private String login;
    private String senha;
    private double salario;

    
    public Colaborador(int id, String nome, String endereco, String telefone,
                       String email, String cpfAnonimizado, String login,
                       String senha, double salario) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.login = login;
        this.senha = senha;
        this.salario = salario;
    }

   
    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
    }

    
    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (this.senha.equals(senhaAtual) && novaSenha != null && !novaSenha.isBlank()) {
            this.senha = novaSenha;
            return true;
        }
        return false;
    }

    public boolean podeGerenciarFinanceiro() {
        return false;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    // Sem getSenha() de propósito: a senha só é verificada por autenticar()
    // e alterada por alterarSenha(), para não ser exposta.

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    @Override
    public String toString() {
        return "Colaborador{" + super.toString() + ", login=" + login
                + ", salario=" + salario + "}";
    }
}