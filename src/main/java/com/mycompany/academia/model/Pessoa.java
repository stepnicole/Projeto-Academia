package com.mycompany.academia.model;

public abstract class Pessoa {

    private int id;
    private String nome;
    private String endereco;
    private String telefone;
    private String email;
    private String cpfAnonimizado;

    public Pessoa(int id, String nome, String endereco, String telefone,
                  String email, String cpfAnonimizado) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.cpfAnonimizado = cpfAnonimizado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpfAnonimizado() { return cpfAnonimizado; }
    public void setCpfAnonimizado(String cpfAnonimizado) { this.cpfAnonimizado = cpfAnonimizado; }

    @Override
    public String toString() {
        return "id=" + id + ", nome=" + nome + ", endereco=" + endereco
                + ", telefone=" + telefone + ", email=" + email
                + ", cpf=" + cpfAnonimizado;
    }
}
