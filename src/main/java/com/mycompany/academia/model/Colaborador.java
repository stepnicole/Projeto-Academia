package com.mycompany.academia.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funcionário da academia com acesso ao sistema.
 * <p>
 * Pode usar todas as funcionalidades, exceto o gerenciamento de despesas e o
 * balanço mensal (ver {@link #podeGerenciarFinanceiro()}).
 * <p>
 * A senha nunca é guardada em texto puro: o objeto (e o arquivo JSON) guarda
 * apenas o hash SHA-256. Por isso <b>não existe getSenha()/setSenha()</b>: a
 * senha só é verificada por {@link #autenticar} e trocada por
 * {@link #alterarSenha}, que exige a senha atual.
 *
 * @author Stephanie Nicole de Souza Silva
 */
public class Colaborador extends Pessoa {

    private String login;
    private String senhaHash;
    private double salario;
    private String cargo;

    /**
     * @param id             identificador
     * @param nome           nome
     * @param endereco       endereço
     * @param telefone       telefone
     * @param email          e-mail
     * @param cpfAnonimizado CPF pseudo-anonimizado
     * @param login          login de acesso
     * @param senha          senha em texto puro (é convertida em hash)
     * @param salario        salário mensal
     * @param cargo          cargo (ex.: Recepcionista)
     */
    public Colaborador(int id, String nome, String endereco, String telefone,
                       String email, String cpfAnonimizado, String login,
                       String senha, double salario, String cargo) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.login = login;
        this.senhaHash = gerarHash(senha);
        this.salario = salario;
        this.cargo = cargo;
    }

    /**
     * Gera o hash SHA-256 (em hexadecimal) de um texto.
     *
     * @param texto texto original
     * @return hash em hexadecimal
     */
    protected static String gerarHash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 existe em toda JVM; se faltar, é erro de ambiente.
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    /**
     * @param login login digitado
     * @param senha senha digitada
     * @return true se login e senha conferem
     */
    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && senha != null
                && this.senhaHash.equals(gerarHash(senha));
    }

    /**
     * Troca a senha, desde que a senha atual esteja correta e a nova não
     * seja vazia.
     *
     * @param senhaAtual senha atual
     * @param novaSenha  nova senha
     * @return true se a senha foi alterada
     */
    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (senhaAtual != null && senhaHash.equals(gerarHash(senhaAtual))
                && novaSenha != null && !novaSenha.isBlank()) {
            this.senhaHash = gerarHash(novaSenha);
            return true;
        }
        return false;
    }

    /**
     * @return false: colaborador comum não acessa despesas nem balanço
     */
    public boolean podeGerenciarFinanceiro() {
        return false;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String toString() {
        return "Colaborador{" + super.toString() + ", login=" + login
                + ", cargo=" + cargo + ", salario=" + salario + "}";
    }
}