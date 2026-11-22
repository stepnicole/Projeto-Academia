package com.mycompany.academia.model.lojinha;

/**
 * Quantidade em estoque de um produto e o mínimo desejado (para alertas de
 * reposição).
 *
 */
public class ItemEstoque {

    private int idProduto;
    private int quantidade;
    private int quantidadeMinima;

    /**
     * @param idProduto        id do produto
     * @param quantidade       quantidade inicial
     * @param quantidadeMinima abaixo disso o produto aparece nos alertas
     */
    public ItemEstoque(int idProduto, int quantidade, int quantidadeMinima) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.quantidadeMinima = quantidadeMinima;
    }

    /**
     * @param qtd unidades que chegaram (deve ser positiva)
     */
    public void adicionar(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        quantidade += qtd;
    }

    /**
     * @param qtd unidades a retirar
     * @return true se havia unidades suficientes
     */
    public boolean retirar(int qtd) {
        if (qtd <= 0 || qtd > quantidade) {
            return false;
        }
        quantidade -= qtd;
        return true;
    }

    /** @return true se a quantidade está abaixo do mínimo */
    public boolean estaAbaixoDoMinimo() {
        return quantidade < quantidadeMinima;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(int quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }

    @Override
    public String toString() {
        return "ItemEstoque{idProduto=" + idProduto + ", quantidade=" + quantidade
                + ", minimo=" + quantidadeMinima + "}";
    }
}