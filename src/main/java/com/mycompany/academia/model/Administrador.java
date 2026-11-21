package com.mycompany.academia.model;

/**
 * Administrador (dona da academia). Tem todas as permissões do colaborador e
 * também gerencia despesas, gera o balanço mensal e gerencia outros
 * administradores.
 *
 */
public class Administrador extends Colaborador {

    /**
     * @param id             identificador
     * @param nome           nome
     * @param endereco       endereço
     * @param telefone       telefone
     * @param email          e-mail
     * @param cpfAnonimizado CPF pseudo-anonimizado
     * @param login          login
     * @param senha          senha em texto puro (vira hash)
     * @param salario        pró-labore/salário
     */
    public Administrador(int id, String nome, String endereco, String telefone,
                         String email, String cpfAnonimizado, String login,
                         String senha, double salario) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado,
              login, senha, salario, "Administrador");
    }

    /**
     * @return true: o administrador acessa o financeiro
     */
    @Override
    public boolean podeGerenciarFinanceiro() {
        return true;
    }

    @Override
    public String toString() {
        return "Administrador{" + super.toString() + "}";
    }
}