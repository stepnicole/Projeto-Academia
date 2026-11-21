package com.mycompany.academia.model;

/**
 * Lançada quando o usuário não está autenticado ou não tem permissão para a
 * operação (por exemplo, um colaborador tentando ver o balanço mensal).
 *
 */
public class AcessoNegadoExcecao extends RuntimeException {

    /**
     * @param mensagem motivo da negação
     */
    public AcessoNegadoExcecao(String mensagem) {
        super(mensagem);
    }

    @Override
    public String toString() {
        return "AcessoNegadoExcecao{" + getMessage() + "}";
    }
}