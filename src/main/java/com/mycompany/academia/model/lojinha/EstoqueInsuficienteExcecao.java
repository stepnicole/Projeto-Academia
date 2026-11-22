package com.mycompany.academia.model.lojinha;

/**
 * Lançada quando uma venda pede mais unidades do que há no estoque.
 *
 */
public class EstoqueInsuficienteExcecao extends RuntimeException {

    /**
     * @param mensagem descrição do produto e quantidades
     */
    public EstoqueInsuficienteExcecao(String mensagem) {
        super(mensagem);
    }

    @Override
    public String toString() {
        return "EstoqueInsuficienteExcecao{" + getMessage() + "}";
    }
}