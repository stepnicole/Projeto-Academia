package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.CategoriaDespesa;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.Map;

/**
 * Balanço de receitas e despesas de um mês, com estatísticas básicas.
 * Só o administrador pode gerar (ver {@link com.mycompany.academia.servico.Financeiro#gerarBalancoMensal}).
 *
 */
public class BalancoMensal {

    private int ano;
    private int mes;
    private RelatorioVendas vendas;
    private Map<CategoriaDespesa, Double> despesasPorCategoria;
    private int qtdAgendamentosConfirmados;
    private int qtdAgendamentosCancelados;
    private int qtdAcessosCatraca;
    private int clientesDistintosNaAcademia;

    /**
     * @param ano    ano do balanço
     * @param mes    mês (1 a 12)
     * @param vendas relatório de vendas do mês (receitas)
     */
    public BalancoMensal(int ano, int mes, RelatorioVendas vendas) {
        this.ano = ano;
        this.mes = mes;
        this.vendas = vendas;
        this.despesasPorCategoria = new EnumMap<>(CategoriaDespesa.class);
    }

    /**
     * Soma uma despesa na sua categoria.
     *
     * @param despesa despesa do mês
     */
    public void adicionarDespesa(Despesa despesa) {
        despesasPorCategoria.merge(despesa.getCategoria(), despesa.getValor(), Double::sum);
    }

    /** @return receitas líquidas (vendas menos reembolsos) */
    public double getTotalReceitas() {
        return vendas.getTotalLiquido();
    }

    /** @return soma de todas as despesas do mês */
    public double getTotalDespesas() {
        double total = 0;
        for (double v : despesasPorCategoria.values()) {
            total += v;
        }
        return total;
    }

    /** @return receitas menos despesas (lucro ou prejuízo) */
    public double getSaldo() {
        return getTotalReceitas() - getTotalDespesas();
    }

    /** @return receita líquida média por dia do mês */
    public double getMediaReceitaDiaria() {
        return getTotalReceitas() / YearMonth.of(ano, mes).lengthOfMonth();
    }

    /** @return percentual do saldo sobre as receitas (0 se não houve receita) */
    public double getMargemPercentual() {
        double receitas = getTotalReceitas();
        return receitas == 0 ? 0 : getSaldo() / receitas * 100;
    }

    /** @return categoria com maior gasto, ou null se não houve despesas */
    public CategoriaDespesa getMaiorCategoriaDespesa() {
        CategoriaDespesa maior = null;
        double maiorValor = -1;
        for (Map.Entry<CategoriaDespesa, Double> e : despesasPorCategoria.entrySet()) {
            if (e.getValue() > maiorValor) {
                maior = e.getKey();
                maiorValor = e.getValue();
            }
        }
        return maior;
    }

    /** @return percentual de agendamentos cancelados sobre os feitos no mês */
    public double getTaxaCancelamento() {
        int total = qtdAgendamentosConfirmados + qtdAgendamentosCancelados;
        return total == 0 ? 0 : 100.0 * qtdAgendamentosCancelados / total;
    }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public RelatorioVendas getVendas() { return vendas; }
    public void setVendas(RelatorioVendas vendas) { this.vendas = vendas; }

    public Map<CategoriaDespesa, Double> getDespesasPorCategoria() { return despesasPorCategoria; }
    public void setDespesasPorCategoria(Map<CategoriaDespesa, Double> despesasPorCategoria) {
        this.despesasPorCategoria = despesasPorCategoria;
    }

    public int getQtdAgendamentosConfirmados() { return qtdAgendamentosConfirmados; }
    public void setQtdAgendamentosConfirmados(int qtd) { this.qtdAgendamentosConfirmados = qtd; }

    public int getQtdAgendamentosCancelados() { return qtdAgendamentosCancelados; }
    public void setQtdAgendamentosCancelados(int qtd) { this.qtdAgendamentosCancelados = qtd; }

    public int getQtdAcessosCatraca() { return qtdAcessosCatraca; }
    public void setQtdAcessosCatraca(int qtd) { this.qtdAcessosCatraca = qtd; }

    public int getClientesDistintosNaAcademia() { return clientesDistintosNaAcademia; }
    public void setClientesDistintosNaAcademia(int qtd) { this.clientesDistintosNaAcademia = qtd; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("BalancoMensal{%02d/%d%n", mes, ano));
        sb.append(String.format("  Receitas líquidas:     R$ %10.2f%n", getTotalReceitas()));
        sb.append(String.format("    (diárias R$ %.2f | mensalidades R$ %.2f | lojinha R$ %.2f | reembolsos -R$ %.2f)%n",
                vendas.getTotalDiarias(), vendas.getTotalMensalidades(),
                vendas.getTotalLojinha(), vendas.getTotalReembolsos()));
        sb.append(String.format("  Despesas:              R$ %10.2f%n", getTotalDespesas()));
        for (Map.Entry<CategoriaDespesa, Double> e : despesasPorCategoria.entrySet()) {
            sb.append(String.format("    - %-28s R$ %10.2f%n", e.getKey(), e.getValue()));
        }
        sb.append(String.format("  SALDO:                 R$ %10.2f (%s)%n", getSaldo(),
                getSaldo() >= 0 ? "lucro" : "prejuízo"));
        sb.append("  --- Estatísticas ---\n");
        sb.append(String.format("  Margem: %.1f%% | Receita média/dia: R$ %.2f%n",
                getMargemPercentual(), getMediaReceitaDiaria()));
        sb.append(String.format("  Maior despesa: %s%n", getMaiorCategoriaDespesa()));
        sb.append(String.format("  Agendamentos confirmados: %d | cancelados: %d (%.1f%%)%n",
                qtdAgendamentosConfirmados, qtdAgendamentosCancelados, getTaxaCancelamento()));
        sb.append(String.format("  Entradas na catraca: %d | clientes distintos: %d}",
                qtdAcessosCatraca, clientesDistintosNaAcademia));
        return sb.toString();
    }
}