package com.mycompany.academia.model.lojinha;

import java.util.ArrayList;
import java.util.List;

/**
 * Estoque da lojinha e da lanchonete: guarda um {@link ItemEstoque} por
 * produto.
 *
 */
public class Estoque {

    private List<ItemEstoque> itens;

    /** Cria um estoque vazio. */
    public Estoque() {
        this.itens = new ArrayList<>();
    }

    /**
     * @param idProduto id do produto
     * @return o item do estoque ou null se o produto não estiver no estoque
     */
    public ItemEstoque buscarItem(int idProduto) {
        for (ItemEstoque item : itens) {
            if (item.getIdProduto() == idProduto) {
                return item;
            }
        }
        return null;
    }

    /**
     * Passa a controlar o estoque de um produto.
     *
     * @param idProduto        id do produto
     * @param quantidade       quantidade inicial
     * @param quantidadeMinima mínimo desejado
     * @return false se o produto já estava no estoque
     */
    public boolean registrarProduto(int idProduto, int quantidade, int quantidadeMinima) {
        if (buscarItem(idProduto) != null) {
            return false;
        }
        itens.add(new ItemEstoque(idProduto, quantidade, quantidadeMinima));
        return true;
    }

    /**
     * @param idProduto id do produto
     * @return true se o produto foi removido do estoque
     */
    public boolean removerProduto(int idProduto) {
        return itens.remove(buscarItem(idProduto));
    }

    /**
     * Registra a chegada de mercadoria.
     *
     * @param idProduto  id do produto
     * @param quantidade unidades recebidas
     * @return false se o produto não está no estoque
     */
    public boolean darEntrada(int idProduto, int quantidade) {
        ItemEstoque item = buscarItem(idProduto);
        if (item == null) {
            return false;
        }
        item.adicionar(quantidade);
        return true;
    }

    /**
     * Retira unidades vendidas.
     *
     * @param idProduto  id do produto
     * @param quantidade unidades vendidas
     * @return true se havia estoque suficiente
     */
    public boolean darBaixa(int idProduto, int quantidade) {
        ItemEstoque item = buscarItem(idProduto);
        return item != null && item.retirar(quantidade);
    }

    /**
     * @param idProduto id do produto
     * @return quantidade disponível (0 se não estiver no estoque)
     */
    public int consultarQuantidade(int idProduto) {
        ItemEstoque item = buscarItem(idProduto);
        return item == null ? 0 : item.getQuantidade();
    }

    /**
     * @param idProduto  id do produto
     * @param quantidade quantidade desejada
     * @return true se há pelo menos essa quantidade
     */
    public boolean temDisponivel(int idProduto, int quantidade) {
        return consultarQuantidade(idProduto) >= quantidade;
    }

    /** @return itens com quantidade abaixo do mínimo (precisam de reposição) */
    public List<ItemEstoque> itensAbaixoDoMinimo() {
        List<ItemEstoque> lista = new ArrayList<>();
        for (ItemEstoque item : itens) {
            if (item.estaAbaixoDoMinimo()) {
                lista.add(item);
            }
        }
        return lista;
    }

    public List<ItemEstoque> getItens() { return itens; }
    public void setItens(List<ItemEstoque> itens) { this.itens = itens; }

    @Override
    public String toString() {
        return "Estoque{itens=" + itens + "}";
    }
}