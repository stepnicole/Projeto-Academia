package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.CategoriaDespesa;
import java.time.LocalDate;

/**
 * Despesa da academia (limpeza, salários, material de uso diário...).
 * Só o administrador pode lançar, editar e remover despesas.
 *
 */
public class Despesa implements Identificador {

    private int id;
    private String descricao;
    private CategoriaDespesa categoria;
    private double valor;
    private LocalDate data;

    /**
     * @param id        identificador
     * @param descricao descrição livre
     * @param categoria categoria da despesa
     * @param valor     valor (positivo)
     * @param data      data do gasto
     */
    public Despesa(int id, String descricao, CategoriaDespesa categoria,
                   double valor, LocalDate data) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public CategoriaDespesa getCategoria() { return categoria; }
    public void setCategoria(CategoriaDespesa categoria) { this.categoria = categoria; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    @Override
    public String toString() {
        return "Despesa{id=" + id + ", descricao=" + descricao + ", categoria=" + categoria
                + ", valor=" + valor + ", data=" + data + "}";
    }
}