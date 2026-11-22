package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Sistema;
import com.mycompany.academia.model.enums.CategoriaProduto;

/**
 * Produto vendido na lanchonete ou na lojinha de material de academia.
 * A quantidade disponível não fica aqui, e sim no {@link Estoque}.
 * <p>
 * Cada produto criado incrementa o contador de produtos do {@link Sistema}.
 *
 */
public class Produto implements Identificador {

    private int id;
    private String nome;
    private String descricao;
    private CategoriaProduto categoria;
    private double preco;

    /**
     * @param id        identificador
     * @param nome      nome do produto
     * @param descricao descrição
     * @param categoria lanchonete ou loja
     * @param preco     preço de venda
     */
    public Produto(int id, String nome, String descricao,
                   CategoriaProduto categoria, double preco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        Sistema.setTotalProdutosCriados(Sistema.getTotalProdutosCriados() + 1);
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public CategoriaProduto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProduto categoria) { this.categoria = categoria; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome=" + nome + ", categoria=" + categoria
                + ", preco=" + String.format("%.2f", preco) + "}";
    }
}