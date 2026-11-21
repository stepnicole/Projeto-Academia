package com.mycompany.academia.model;

/**
 * Superclasse abstrata com os dados comuns a clientes, colaboradores e
 * instrutores. É abstrata porque o sistema nunca cria uma "pessoa" genérica.
 *
 */
public abstract class Pessoa implements Identificador {

    private int id;
    private String nome;
    private String endereco;
    private String telefone;
    private String email;
    private String cpfAnonimizado;

    /**
     * @param id             identificador
     * @param nome           nome completo
     * @param endereco       endereço
     * @param telefone       telefone de contato
     * @param email          e-mail
     * @param cpfAnonimizado CPF pseudo-anonimizado (ex.: xxx.123.xxx-45)
     */
    public Pessoa(int id, String nome, String endereco, String telefone,
                  String email, String cpfAnonimizado) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.cpfAnonimizado = cpfAnonimizado;
    }

    /**
     * Pseudo-anonimiza um CPF, mantendo só os dígitos do meio e os verificadores.
     * Ex.: "123.456.789-01" vira "xxx.456.xxx-01". Se já vier anonimizado,
     * é devolvido como está.
     *
     * @param cpf CPF completo ou já anonimizado
     * @return CPF pseudo-anonimizado
     */
    public static String anonimizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return cpf;
        }
        return "xxx." + digitos.substring(3, 6) + ".xxx-" + digitos.substring(9);
    }

    @Override
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