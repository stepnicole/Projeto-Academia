package com.mycompany.academia.model;

import java.time.LocalDate;

/**
 * Balanço de receitas e despesas de um mês, com estatísticas básicas.
 * Só o administrador pode gerar (ver {@link com.mycompany.academia.controller.Financeiro#gerarBalancoMensal}).
 */

public class RelatorioVendas implements Identificador {

    private int id;
    private String periodo;
    private LocalDate inicio;
    private LocalDate fim;
    private int qtdDiarias;
    private double totalDiarias;
    private int qtdMensalidades;
    private double totalMensalidades;
    private int qtdVendasLojinha;
    private double totalLojinha;
    private int qtdReembolsos;
    private double totalReembolsos;

    /**
     * Cria um relatório vazio; os totais são preenchidos com
     * {@link #registrarDiaria}, {@link #registrarMensalidade},
     * {@link #registrarVendaLojinha} e {@link #registrarReembolso}.
     *
     * @param id      identificador
     * @param periodo descrição (ex.: "Dia 20/11/2026")
     * @param inicio  primeiro dia (inclusive)
     * @param fim     último dia (inclusive)
     */
    public RelatorioVendas(int id, String periodo, LocalDate inicio, LocalDate fim) {
        this.id = id;
        this.periodo = periodo;
        this.inicio = inicio;
        this.fim = fim;
    }

    /** @param valor valor de uma diária vendida */
    public void registrarDiaria(double valor) { qtdDiarias++; totalDiarias += valor; }

    /** @param valor valor de uma mensalidade vendida */
    public void registrarMensalidade(double valor) { qtdMensalidades++; totalMensalidades += valor; }

    /** @param valor valor de uma venda da lojinha */
    public void registrarVendaLojinha(double valor) { qtdVendasLojinha++; totalLojinha += valor; }

    /** @param valor valor devolvido a um cliente */
    public void registrarReembolso(double valor) { qtdReembolsos++; totalReembolsos += valor; }

    /** @return soma de diárias, mensalidades e lojinha */
    public double getTotalBruto() {
        return totalDiarias + totalMensalidades + totalLojinha;
    }

    /** @return total bruto menos os reembolsos */
    public double getTotalLiquido() {
        return getTotalBruto() - totalReembolsos;
    }

    /**
     * @param data dia a testar
     * @return true se o dia está dentro do período do relatório
     */
    public boolean contem(LocalDate data) {
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }

    public int getQtdDiarias() { return qtdDiarias; }
    public void setQtdDiarias(int qtdDiarias) { this.qtdDiarias = qtdDiarias; }

    public double getTotalDiarias() { return totalDiarias; }
    public void setTotalDiarias(double totalDiarias) { this.totalDiarias = totalDiarias; }

    public int getQtdMensalidades() { return qtdMensalidades; }
    public void setQtdMensalidades(int qtdMensalidades) { this.qtdMensalidades = qtdMensalidades; }

    public double getTotalMensalidades() { return totalMensalidades; }
    public void setTotalMensalidades(double totalMensalidades) { this.totalMensalidades = totalMensalidades; }

    public int getQtdVendasLojinha() { return qtdVendasLojinha; }
    public void setQtdVendasLojinha(int qtdVendasLojinha) { this.qtdVendasLojinha = qtdVendasLojinha; }

    public double getTotalLojinha() { return totalLojinha; }
    public void setTotalLojinha(double totalLojinha) { this.totalLojinha = totalLojinha; }

    public int getQtdReembolsos() { return qtdReembolsos; }
    public void setQtdReembolsos(int qtdReembolsos) { this.qtdReembolsos = qtdReembolsos; }

    public double getTotalReembolsos() { return totalReembolsos; }
    public void setTotalReembolsos(double totalReembolsos) { this.totalReembolsos = totalReembolsos; }

    @Override
    public String toString() {
        return String.format(
                "RelatorioVendas{%s%n"
              + "  Diárias:      %3d  R$ %10.2f%n"
              + "  Mensalidades: %3d  R$ %10.2f%n"
              + "  Lojinha:      %3d  R$ %10.2f%n"
              + "  Reembolsos:   %3d -R$ %10.2f%n"
              + "  Total bruto:       R$ %10.2f%n"
              + "  Total líquido:     R$ %10.2f}",
                periodo, qtdDiarias, totalDiarias, qtdMensalidades, totalMensalidades,
                qtdVendasLojinha, totalLojinha, qtdReembolsos, totalReembolsos,
                getTotalBruto(), getTotalLiquido());
    }
}