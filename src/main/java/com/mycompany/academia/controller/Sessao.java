package com.mycompany.academia.controller;

import com.mycompany.academia.model.AcessoNegadoExcecao;
import com.mycompany.academia.model.Colaborador;
import java.util.List;

/**
 * Responsabilidade saber <b>quem está logado</b> e verificar
 * permissões. Todos os serviços recebem a mesma sessão e a consultam antes
 * de executar uma operação.
 *
 */
public class Sessao {

    private Colaborador usuarioLogado;

    /**
     * Tenta autenticar entre os funcionários informados.
     *
     * @param login      login digitado
     * @param senha      senha digitada
     * @param candidatos colaboradores e administradores cadastrados
     * @return true se autenticou
     */
    public boolean login(String login, String senha, List<Colaborador> candidatos) {
        for (Colaborador c : candidatos) {
            if (c.autenticar(login, senha)) {
                usuarioLogado = c;
                return true;
            }
        }
        return false;
    }

    /** Encerra a sessão. */
    public void logout() {
        usuarioLogado = null;
    }

    /** @return usuário logado ou null */
    public Colaborador getUsuarioLogado() {
        return usuarioLogado;
    }

    /** @return true se há alguém logado */
    public boolean estaLogado() {
        return usuarioLogado != null;
    }

    /**
     * @throws AcessoNegadoExcecao se ninguém estiver logado
     */
    public void exigirLogin() {
        if (usuarioLogado == null) {
            throw new AcessoNegadoExcecao("É necessário fazer login.");
        }
    }

    /**
     * @throws AcessoNegadoExcecao se o usuário não for administrador
     */
    public void exigirAdministrador() {
        exigirLogin();
        if (!usuarioLogado.podeGerenciarFinanceiro()) {
            throw new AcessoNegadoExcecao(usuarioLogado.getNome()
                    + " não é administrador(a) e não tem acesso a esta função.");
        }
    }

    @Override
    public String toString() {
        return "Sessao{logado=" + (usuarioLogado == null ? "ninguém" : usuarioLogado.getLogin()) + "}";
    }
}