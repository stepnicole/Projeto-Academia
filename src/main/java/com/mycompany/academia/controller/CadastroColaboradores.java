package com.mycompany.academia.controller;

import com.mycompany.academia.model.AcessoNegadoExcecao;
import com.mycompany.academia.model.Administrador;
import com.mycompany.academia.model.Colaborador;
import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Pessoa;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cadastro de <b>funcionários</b> (colaboradores e
 * administradores) e troca de senhas.
 * <p>
 * Administradores ficam numa lista separada porque o Gson não guarda a
 * subclasse; numa lista única eles voltariam do JSON como Colaborador comum.
 *
 */

public class CadastroColaboradores {

    private final Sessao sessao;
    private final List<Colaborador> colaboradores;
    private final List<Administrador> administradores;

    /**
     * @param sessao sessão usada para checar login e permissões
     */
    public CadastroColaboradores(Sessao sessao) {
        this.sessao = sessao;
        this.colaboradores = new ArrayList<>();
        this.administradores = new ArrayList<>();
    }

    /**
     * Cria o primeiro administrador ("primeiro acesso"). Só funciona enquanto
     * não existe nenhum administrador.
     *
     * @param nome     nome
     * @param endereco endereço
     * @param telefone telefone
     * @param email    e-mail
     * @param cpf      CPF
     * @param login    login
     * @param senha    senha
     * @param salario  pró-labore
     * @return o administrador criado
     * @throws AcessoNegadoExcecao se já existir administrador
     */
    public Administrador criarAdministradorInicial(String nome, String endereco, String telefone,
                                                   String email, String cpf, String login,
                                                   String senha, double salario) {
        if (!administradores.isEmpty()) {
            throw new AcessoNegadoExcecao("Já existe administrador cadastrado.");
        }
        Administrador adm = new Administrador(Identificador.proximoId(listarTodos()), nome, endereco,
                telefone, email, Pessoa.anonimizarCpf(cpf), login, senha, salario);
        administradores.add(adm);
        return adm;
    }

    private void validarLoginLivre(String login) {
        for (Colaborador c : listarTodos()) {
            if (c.getLogin().equals(login)) {
                throw new IllegalArgumentException("Login já está em uso: " + login);
            }
        }
    }

    /**
     * Inclui um colaborador (funcionário comum).
     *
     * @param nome     nome
     * @param endereco endereço
     * @param telefone telefone
     * @param email    e-mail
     * @param cpf      CPF
     * @param login    login (único)
     * @param senha    senha
     * @param salario  salário
     * @param cargo    cargo
     * @return o colaborador criado
     */
    public Colaborador cadastrarColaborador(String nome, String endereco, String telefone,
                                            String email, String cpf, String login,
                                            String senha, double salario, String cargo) {
        sessao.exigirLogin();
        validarLoginLivre(login);
        Colaborador c = new Colaborador(Identificador.proximoId(listarTodos()), nome, endereco,
                telefone, email, Pessoa.anonimizarCpf(cpf), login, senha, salario, cargo);
        colaboradores.add(c);
        return c;
    }

    /**
     * Inclui outro administrador. Só administrador pode (decisão de projeto:
     * evita que um funcionário se dê acesso ao financeiro).
     *
     * @param nome     nome
     * @param endereco endereço
     * @param telefone telefone
     * @param email    e-mail
     * @param cpf      CPF
     * @param login    login (único)
     * @param senha    senha
     * @param salario  salário
     * @return o administrador criado
     */
    public Administrador cadastrarAdministrador(String nome, String endereco, String telefone,
                                                String email, String cpf, String login,
                                                String senha, double salario) {
        sessao.exigirAdministrador();
        validarLoginLivre(login);
        Administrador adm = new Administrador(Identificador.proximoId(listarTodos()), nome, endereco,
                telefone, email, Pessoa.anonimizarCpf(cpf), login, senha, salario);
        administradores.add(adm);
        return adm;
    }

    /**
     * Edita um funcionário. Parâmetros {@code null} mantêm o valor atual.
     * Editar administrador exige ser administrador.
     *
     * @param id       id
     * @param nome     novo nome ou null
     * @param endereco novo endereço ou null
     * @param telefone novo telefone ou null
     * @param email    novo e-mail ou null
     * @param salario  novo salário ou null
     * @param cargo    novo cargo ou null (ignorado para administrador)
     * @return false se não existe
     */
    public boolean editar(int id, String nome, String endereco, String telefone,
                          String email, Double salario, String cargo) {
        sessao.exigirLogin();
        Colaborador c = buscar(id);
        if (c == null) {
            return false;
        }
        if (c instanceof Administrador) {
            sessao.exigirAdministrador();
        }
        if (nome != null) c.setNome(nome);
        if (endereco != null) c.setEndereco(endereco);
        if (telefone != null) c.setTelefone(telefone);
        if (email != null) c.setEmail(email);
        if (salario != null) c.setSalario(salario);
        if (cargo != null && !(c instanceof Administrador)) c.setCargo(cargo);
        return true;
    }

    /**
     * Remove um funcionário. Ninguém remove a si mesmo e o último
     * administrador não pode ser removido.
     *
     * @param id id
     * @return true se removeu
     */
    public boolean remover(int id) {
        sessao.exigirLogin();
        Colaborador c = buscar(id);
        if (c == null || c == sessao.getUsuarioLogado()) {
            return false;
        }
        if (c instanceof Administrador adm) {
            sessao.exigirAdministrador();
            return administradores.size() > 1 && administradores.remove(adm);
        }
        return colaboradores.remove(c);
    }

    /**
     * @param id id
     * @return colaborador/administrador ou null
     */
    public Colaborador buscar(int id) {
        for (Colaborador c : listarTodos()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /** @return administradores e colaboradores juntos */
    public List<Colaborador> listarTodos() {
        List<Colaborador> todos = new ArrayList<>(administradores);
        todos.addAll(colaboradores);
        return todos;
    }

    /**
     * Altera a senha de um administrador (só administrador pode).
     *
     * @param idAdministrador id do administrador
     * @param senhaAtual      senha atual dele
     * @param novaSenha       nova senha
     * @return true se alterou
     */
    public boolean alterarSenhaAdministrador(int idAdministrador, String senhaAtual, String novaSenha) {
        sessao.exigirAdministrador();
        Colaborador c = buscar(idAdministrador);
        return c instanceof Administrador && c.alterarSenha(senhaAtual, novaSenha);
    }

    /**
     * O usuário logado troca a própria senha.
     *
     * @param senhaAtual senha atual
     * @param novaSenha  nova senha
     * @return true se alterou
     */
    public boolean alterarMinhaSenha(String senhaAtual, String novaSenha) {
        sessao.exigirLogin();
        return sessao.getUsuarioLogado().alterarSenha(senhaAtual, novaSenha);
    }

    /** @return colaboradores comuns (somente leitura) */
    public List<Colaborador> getColaboradores() {
        return Collections.unmodifiableList(colaboradores);
    }

    /** @return administradores (somente leitura) */
    public List<Administrador> getAdministradores() {
        return Collections.unmodifiableList(administradores);
    }

    /**
     * Substitui os dados em memória pelos lidos do arquivo.
     *
     * @param colabs colaboradores recuperados
     * @param admins administradores recuperados
     */
    public void carregar(List<Colaborador> colabs, List<Administrador> admins) {
        colaboradores.clear();
        colaboradores.addAll(colabs);
        administradores.clear();
        administradores.addAll(admins);
    }

    @Override
    public String toString() {
        return "CadastroColaboradores{colaboradores=" + colaboradores.size()
                + ", administradores=" + administradores.size() + "}";
    }
}