package com.mycompany.academia.controller;

import com.mycompany.academia.model.Aula;
import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Pessoa;
import com.mycompany.academia.model.Sala;
import com.mycompany.academia.model.enums.TipoAula;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grade de aulas (instrutores e as aulas de
 * cada sala).
 *
 */
public class CadastroAulas {

    private final Sessao sessao;
    private final List<Instrutor> instrutores;
    private final List<Aula> aulas;

    /**
     * @param sessao sessão usada para checar o login
     */
    public CadastroAulas(Sessao sessao) {
        this.sessao = sessao;
        this.instrutores = new ArrayList<>();
        this.aulas = new ArrayList<>();
    }

    /**
     * @param nome          nome
     * @param endereco      endereço
     * @param telefone      telefone
     * @param email         e-mail
     * @param cpf           CPF
     * @param especialidade modalidade
     * @param salario       salário
     * @return o instrutor criado
     */
    public Instrutor cadastrarInstrutor(String nome, String endereco, String telefone,
                                        String email, String cpf, TipoAula especialidade,
                                        double salario) {
        sessao.exigirLogin();
        Instrutor i = new Instrutor(Identificador.proximoId(instrutores), nome, endereco, telefone,
                email, Pessoa.anonimizarCpf(cpf), especialidade, salario);
        instrutores.add(i);
        return i;
    }

    /**
     * Remove um instrutor que não tenha aulas cadastradas.
     *
     * @param id id do instrutor
     * @return true se removeu
     */
    public boolean removerInstrutor(int id) {
        sessao.exigirLogin();
        for (Aula a : aulas) {
            if (a.getIdInstrutor() == id) {
                return false;
            }
        }
        return instrutores.remove(buscarInstrutor(id));
    }

    /**
     * @param id id do instrutor
     * @return o instrutor ou null
     */
    public Instrutor buscarInstrutor(int id) {
        for (Instrutor i : instrutores) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    /**
     * Cria uma aula na sala da modalidade.
     *
     * @param tipo        modalidade
     * @param idInstrutor instrutor (precisa ter essa especialidade)
     * @param dataHora    horário
     * @return a aula criada
     * @throws IllegalArgumentException se o instrutor não existe ou não é da modalidade
     */
    public Aula cadastrarAula(TipoAula tipo, int idInstrutor, LocalDateTime dataHora) {
        sessao.exigirLogin();
        Instrutor i = buscarInstrutor(idInstrutor);
        if (i == null || i.getEspecialidade() != tipo) {
            throw new IllegalArgumentException("Instrutor inexistente ou não ministra " + tipo);
        }
        Aula a = new Aula(Identificador.proximoId(aulas), tipo, Sala.buscarPorTipo(tipo).getId(),
                idInstrutor, dataHora);
        aulas.add(a);
        return a;
    }

    /**
     * @param id id da aula
     * @return a aula ou null
     */
    public Aula buscarAula(int id) {
        for (Aula a : aulas) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    /**
     * Verificar a agenda: aulas de uma modalidade que ainda têm vaga.
     *
     * @param tipo modalidade
     * @return aulas com vaga
     */
    public List<Aula> listarComVaga(TipoAula tipo) {
        sessao.exigirLogin();
        List<Aula> lista = new ArrayList<>();
        for (Aula a : aulas) {
            if (a.getTipoAula() == tipo && a.temVaga()) {
                lista.add(a);
            }
        }
        return lista;
    }

    /** @return instrutores (somente leitura) */
    public List<Instrutor> getInstrutores() {
        return Collections.unmodifiableList(instrutores);
    }

    /** @return aulas (somente leitura) */
    public List<Aula> getAulas() {
        return Collections.unmodifiableList(aulas);
    }

    /**
     * Substitui os dados em memória pelos lidos do arquivo.
     *
     * @param instrutoresLidos instrutores recuperados
     * @param aulasLidas       aulas recuperadas
     */
    public void carregar(List<Instrutor> instrutoresLidos, List<Aula> aulasLidas) {
        instrutores.clear();
        instrutores.addAll(instrutoresLidos);
        aulas.clear();
        aulas.addAll(aulasLidas);
    }

    @Override
    public String toString() {
        return "CadastroAulas{instrutores=" + instrutores.size() + ", aulas=" + aulas.size() + "}";
    }
}