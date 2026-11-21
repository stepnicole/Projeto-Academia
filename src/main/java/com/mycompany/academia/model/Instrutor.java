package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.TipoAula;

/**
 * Instrutor que acompanha as aulas. Não acessa o sistema (por isso herda de
 * {@link Pessoa} e não de {@link Colaborador}), mas seu salário entra nas
 * despesas da academia.
 *
 */
public class Instrutor extends Pessoa {

    private TipoAula especialidade;
    private double salario;

    /**
     * @param id             identificador
     * @param nome           nome
     * @param endereco       endereço
     * @param telefone       telefone
     * @param email          e-mail
     * @param cpfAnonimizado CPF pseudo-anonimizado
     * @param especialidade  modalidade que ministra
     * @param salario        salário mensal
     */
    public Instrutor(int id, String nome, String endereco, String telefone,
                     String email, String cpfAnonimizado,
                     TipoAula especialidade, double salario) {
        super(id, nome, endereco, telefone, email, cpfAnonimizado);
        this.especialidade = especialidade;
        this.salario = salario;
    }

    public TipoAula getEspecialidade() { return especialidade; }
    public void setEspecialidade(TipoAula especialidade) { this.especialidade = especialidade; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    @Override
    public String toString() {
        return "Instrutor{" + super.toString() + ", especialidade=" + especialidade
                + ", salario=" + salario + "}";
    }
}