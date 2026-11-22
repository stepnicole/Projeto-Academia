package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.Identificador;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Venda feita na lojinha/lanchonete para um cliente.
 *
 */
public class Venda implements Identificador {

    private int id;
    private int idCliente;
    private LocalDateTime dataHora;
    private List<ItemVenda> itens;

    /**
     * @param id        identificador
     * @param idCliente cliente que comprou
     * @param dataHora  momento da venda
     * @param itens     itens vendidos
     */
    public Venda(int id, int idCliente, LocalDateTime dataHora, List<ItemVenda> itens) {
        this.id = id;
        this.idCliente = idCliente;
        this.dataHora = dataHora;
        this.itens = new ArrayList<>(itens);
    }

    /** @return soma dos subtotais dos itens */
    public double getTotal() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    @Override
    public String toString() {
        return "Venda{id=" + id + ", idCliente=" + idCliente + ", dataHora=" + dataHora
                + ", itens=" + itens + ", total=" + String.format("%.2f", getTotal()) + "}";
    }
}