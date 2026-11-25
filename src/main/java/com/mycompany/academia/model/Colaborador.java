package com.mycompany.academia.model;

/**
 * Funcionário da academia com acesso ao sistema.
 * <p>
 * Pode usar todas as funcionalidades, exceto o gerenciamento de despesas e o
 * balanço mensal (ver {@link #podeGerenciarFinanceiro()}).
 * <p>
 * <b>Não existe getSenha()/setSenha()</b>: a senha só é verificada por
 * {@link #autenticar} e trocada por {@link #alterarSenha}, que exige a
 * senha atual. Assim, nenhuma outra classe consegue ler a senha nem
 * trocá-la sem conhecer a senha antiga.
 *
 * @author Stephanie Nicole de Souza Silva
 */
public class Colaborador extends Pessoa {

    private String login;
    private String senha;
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
     * @param senha          senha de acesso
     * @param salario        salário mensal
     * @param cargo          cargo (ex.: Recepcionista)
     */
    public Colaborador(int id, String nome, String endereco, String telefone,
                       String email, String cpfAnonimizado, String login,
                       String senha, double salario, String cargo) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.login = login;
        this.senha = senha;
        this.salario = salario;
        this.cargo = cargo;
    }

    /**
     * @param login login digitado
     * @param senha senha digitada
     * @return true se login e senha conferem
     */
    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
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
        if (this.senha.equals(senhaAtual)
                && novaSenha != null && !novaSenha.isBlank()) {
            this.senha = novaSenha;
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