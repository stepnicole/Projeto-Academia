package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.enums.CategoriaProduto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cadastro dos produtos da lanchonete e da
 * loja (incluir, editar, remover, buscar). Não sabe quantas unidades existem
 * (isso é do {@link Estoque}) nem quanto foi vendido (isso é do
 * {@link HistoricoVendas}).
 *
 */
public class CatalogoProdutos {

    private final List<Produto> produtos;

    /** Cria o catálogo vazio. */
    public CatalogoProdutos() {
        this.produtos = new ArrayList<>();
    }

    /**
     * @param nome      nome
     * @param descricao descrição
     * @param categoria lanchonete ou loja
     * @param preco     preço de venda (não negativo)
     * @return o produto criado, com id definido automaticamente
     */
    public Produto cadastrar(String nome, String descricao, CategoriaProduto categoria, double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }
        Produto p = new Produto(Identificador.proximoId(produtos), nome, descricao, categoria, preco);
        produtos.add(p);
        return p;
    }

    /**
     * @param id        id do produto
     * @param nome      novo nome
     * @param descricao nova descrição
     * @param categoria nova categoria
     * @param preco     novo preço
     * @return false se o produto não existe
     */
    public boolean editar(int id, String nome, String descricao, CategoriaProduto categoria, double preco) {
        Produto p = buscar(id);
        if (p == null) {
            return false;
        }
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setCategoria(categoria);
        p.setPreco(preco);
        return true;
    }

    /**
     * @param id id do produto
     * @return true se removeu
     */
    public boolean remover(int id) {
        return produtos.remove(buscar(id));
    }

    /**
     * @param id id do produto
     * @return o produto ou null
     */
    public Produto buscar(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * @param texto parte do nome (sem diferenciar maiúsculas)
     * @return produtos encontrados
     */
    public List<Produto> buscarPorNome(String texto) {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(texto.toLowerCase())) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * @param categoria lanchonete ou loja
     * @return produtos da categoria
     */
    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getCategoria() == categoria) {
                lista.add(p);
            }
        }
        return lista;
    }

    /** @return todos os produtos (somente leitura) */
    public List<Produto> getProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    /**
     * Substitui os produtos pelos lidos do arquivo.
     *
     * @param lidos produtos recuperados
     */
    public void carregar(List<Produto> lidos) {
        produtos.clear();
        produtos.addAll(lidos);
    }

    @Override
    public String toString() {
        return "CatalogoProdutos{produtos=" + produtos.size() + "}";
    }
}