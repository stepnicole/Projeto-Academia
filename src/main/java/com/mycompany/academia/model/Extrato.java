package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.TipoExtrato;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrato gerado a cada venda (diária, mensalidade ou compra na lojinha) ou
 * reembolso. É impresso e salvo junto com o cliente que fez a compra
 * (dentro de {@link Cliente#getExtratos()}) e também em um arquivo .txt.
 * <p>
 * O valor total é positivo para cobranças e negativo para reembolsos, assim a
 * conta do cliente é simplesmente a soma dos extratos.
 *
 */
public class Extrato implements Identificador {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private int idCliente;
    private String nomeCliente;
    private String cpfCliente;
    private LocalDateTime dataHora;
    private TipoExtrato tipo;
    private List<String> linhas;
    private double valorTotal;

    /**
     * @param id         identificador
     * @param cliente    cliente que fez a compra
     * @param dataHora   momento da emissão
     * @param tipo       origem do extrato
     * @param linhas     itens descritos (uma linha por item)
     * @param valorTotal total (negativo para reembolso)
     */
    public Extrato(int id, Cliente cliente, LocalDateTime dataHora, TipoExtrato tipo,
                   List<String> linhas, double valorTotal) {
        this.id = id;
        this.idCliente = cliente.getId();
        this.nomeCliente = cliente.getNome();
        this.cpfCliente = cliente.getCpfAnonimizado();
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.linhas = new ArrayList<>(linhas);
        this.valorTotal = valorTotal;
    }

    /**
     * Monta o texto do extrato para impressão.
     *
     * @return extrato formatado
     */
    public String imprimir() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== GYM CORN - MILHO VERDE ==========\n");
        sb.append(String.format("Extrato nº %d - %s%n", id, tipo));
        sb.append("Data: ").append(dataHora.format(FORMATO)).append('\n');
        sb.append(String.format("Cliente: %s (id %d, CPF %s)%n", nomeCliente, idCliente, cpfCliente));
        sb.append("------------------------------------------\n");
        for (String linha : linhas) {
            sb.append(linha).append('\n');
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format("TOTAL: R$ %.2f%n", valorTotal));
        sb.append("==========================================\n");
        return sb.toString();
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoExtrato getTipo() { return tipo; }
    public void setTipo(TipoExtrato tipo) { this.tipo = tipo; }

    public List<String> getLinhas() { return linhas; }
    public void setLinhas(List<String> linhas) { this.linhas = linhas; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    @Override
    public String toString() {
        return "Extrato{id=" + id + ", cliente=" + nomeCliente + ", tipo=" + tipo
                + ", data=" + dataHora + ", total=" + String.format("%.2f", valorTotal) + "}";
    }
}