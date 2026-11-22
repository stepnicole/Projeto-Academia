package com.mycompany.academia.model.lojinha;

/**
 * Linha de uma venda: produto, quantidade e preço unitário no momento da
 * venda (guardado para que uma mudança de preço futura não altere vendas
 * antigas).
 * 
 */
public class ItemVenda {

    private int idProduto;
    private String nomeProduto;
    private int quantidade;
    private double precoUnitario;

    /**
     * @param produto    produto vendido
     * @param quantidade unidades
     */
    public ItemVenda(Produto produto, int quantidade) {
        this.idProduto = produto.getId();
        this.nomeProduto = produto.getNome();
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
    }

    /** @return quantidade vezes preço unitário */
    public double getSubtotal() {
        return quantidade * precoUnitario;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    @Override
    public String toString() {
        return String.format("%dx %s (R$ %.2f) = R$ %.2f",
                quantidade, nomeProduto, precoUnitario, getSubtotal());
    }
}