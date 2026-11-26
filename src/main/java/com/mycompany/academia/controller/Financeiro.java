package com.mycompany.academia.controller;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.BalancoMensal;
import com.mycompany.academia.model.Colaborador;
import com.mycompany.academia.model.Despesa;
import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Pagamento;
import com.mycompany.academia.model.RelatorioVendas;
import com.mycompany.academia.model.catraca.Catraca;
import com.mycompany.academia.model.catraca.RelatorioAcesso;
import com.mycompany.academia.model.enums.CategoriaDespesa;
import com.mycompany.academia.model.enums.StatusAgendamento;
import com.mycompany.academia.model.enums.TipoAgendamento;
import com.mycompany.academia.model.enums.TipoPagamento;
import com.mycompany.academia.model.lojinha.Lojinha;
import com.mycompany.academia.model.lojinha.Venda;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Financeiro da academia, relatórios de vendas (dia/mês), despesas e balanço
 * mensal.
 * <p>
 * Relatórios de vendas podem ser emitidos por qualquer funcionário logado;
 * despesas e balanço só pelo administrador.
 *
 */
public class Financeiro {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Sessao sessao;
    private final ControleAgendamentos agendamentos;
    private final Lojinha lojinha;
    private final Catraca catraca;
    private final CadastroColaboradores colaboradores;
    private final CadastroAulas aulas;
    private final List<Despesa> despesas;
    private final List<RelatorioVendas> relatoriosVendas;

    /**
     * @param sessao sessão (login e permissão)
     * @param agendamentos fonte dos pagamentos de diárias/mensalidades
     * @param lojinha fonte das vendas da lojinha
     * @param catraca fonte das estatísticas de acesso
     * @param colaboradores fonte dos salários de funcionários
     * @param aulas fonte dos salários de instrutores
     */
    public Financeiro(Sessao sessao, ControleAgendamentos agendamentos, Lojinha lojinha,
            Catraca catraca, CadastroColaboradores colaboradores, CadastroAulas aulas) {
        this.sessao = sessao;
        this.agendamentos = agendamentos;
        this.lojinha = lojinha;
        this.catraca = catraca;
        this.colaboradores = colaboradores;
        this.aulas = aulas;
        this.despesas = new ArrayList<>();
        this.relatoriosVendas = new ArrayList<>();
    }

    // ------------------------------------------------------------ vendas
    /**
     * Soma as vendas do período, sem guardar o relatório.
     */
    private RelatorioVendas calcularVendas(String descricao, LocalDate inicio, LocalDate fim) {
        RelatorioVendas r = new RelatorioVendas(Identificador.proximoId(relatoriosVendas),
                descricao, inicio, fim);
        for (Pagamento p : agendamentos.getPagamentos()) {
            if (!r.contem(p.getData())) {
                continue;
            }
            if (p.getTipo() == TipoPagamento.REEMBOLSO) {
                r.registrarReembolso(p.getValor());
            } else {
                Agendamento ag = agendamentos.buscar(p.getIdAgendamento());
                if (ag != null && ag.getTipo() == TipoAgendamento.MENSALIDADE) {
                    r.registrarMensalidade(p.getValor());
                } else {
                    r.registrarDiaria(p.getValor());
                }
            }
        }
        for (Venda v : lojinha.getHistoricoVendas().vendasDoPeriodo(inicio, fim)) {
            r.registrarVendaLojinha(v.getTotal());
        }
        return r;
    }

    /**
     * Relatório de vendas de um dia (fica guardado no histórico).
     *
     * @param dia dia
     * @return relatório
     */
    public RelatorioVendas relatorioVendasDia(LocalDate dia) {
        sessao.exigirLogin();
        RelatorioVendas r = calcularVendas("Dia " + dia.format(DATA_BR), dia, dia);
        relatoriosVendas.add(r);
        return r;
    }

    /**
     * Relatório de vendas de um mês (fica guardado no histórico).
     *
     * @param ano ano
     * @param mes mês
     * @return relatório
     */
    public RelatorioVendas relatorioVendasMes(int ano, int mes) {
        sessao.exigirLogin();
        YearMonth ym = YearMonth.of(ano, mes);
        RelatorioVendas r = calcularVendas(String.format("Mês %02d/%d", mes, ano),
                ym.atDay(1), ym.atEndOfMonth());
        relatoriosVendas.add(r);
        return r;
    }

    // ------------------------------------------------------------ despesas
    /**
     * @param descricao descrição
     * @param categoria categoria
     * @param valor valor (positivo)
     * @param data data
     * @return a despesa lançada
     */
    public Despesa lancarDespesa(String descricao, CategoriaDespesa categoria, double valor, LocalDate data) {
        sessao.exigirAdministrador();
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor da despesa deve ser positivo.");
        }
        Despesa d = new Despesa(Identificador.proximoId(despesas), descricao, categoria, valor, data);
        despesas.add(d);
        return d;
    }

    /**
     * Edita uma despesa. Parâmetros {@code null} mantêm o valor atual.
     *
     * @param id despesa
     * @param descricao nova descrição ou null
     * @param categoria nova categoria ou null
     * @param valor novo valor ou null
     * @param data nova data ou null
     * @return false se não existe
     */
    public boolean editarDespesa(int id, String descricao, CategoriaDespesa categoria,
            Double valor, LocalDate data) {
        sessao.exigirAdministrador();
        Despesa d = buscarDespesa(id);
        if (d == null) {
            return false;
        }
        if (descricao != null) {
            d.setDescricao(descricao);
        }
        if (categoria != null) {
            d.setCategoria(categoria);
        }
        if (valor != null && valor > 0) {
            d.setValor(valor);
        }
        if (data != null) {
            d.setData(data);
        }
        return true;
    }

    /**
     * @param id despesa
     * @return true se removeu
     */
    public boolean removerDespesa(int id) {
        sessao.exigirAdministrador();
        return despesas.remove(buscarDespesa(id));
    }

    private Despesa buscarDespesa(int id) {
        for (Despesa d : despesas) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    /**
     * @param ano ano
     * @param mes mês
     * @return despesas do mês
     */
    public List<Despesa> despesasDoMes(int ano, int mes) {
        sessao.exigirAdministrador();
        List<Despesa> lista = new ArrayList<>();
        for (Despesa d : despesas) {
            if (d.getData().getYear() == ano && d.getData().getMonthValue() == mes) {
                lista.add(d);
            }
        }
        return lista;
    }

    /**
     * Lança como despesa o salário de todos os funcionários e instrutores, no
     * último dia do mês.
     *
     * @param ano ano
     * @param mes mês
     * @return quantas despesas foram lançadas
     */
    public int lancarFolhaDePagamento(int ano, int mes) {
        sessao.exigirAdministrador();
        LocalDate dia = YearMonth.of(ano, mes).atEndOfMonth();
        int n = 0;
        for (Colaborador c : colaboradores.listarTodos()) {
            lancarDespesa("Salário " + c.getNome() + " (" + c.getCargo() + ")",
                    CategoriaDespesa.SALARIOS, c.getSalario(), dia);
            n++;
        }
        for (Instrutor i : aulas.getInstrutores()) {
            lancarDespesa("Salário instrutor(a) " + i.getNome(), CategoriaDespesa.SALARIOS, i.getSalario(), dia);
            n++;
        }
        return n;
    }

    // ------------------------------------------------------------ balanço
    /**
     * Gera o balanço do mês: receitas, despesas por categoria, saldo e
     * estatísticas (agendamentos, cancelamentos, acessos na catraca).
     *
     * @param ano ano
     * @param mes mês
     * @return o balanço
     */
    public BalancoMensal gerarBalancoMensal(int ano, int mes) {
        sessao.exigirAdministrador();
        YearMonth ym = YearMonth.of(ano, mes);
        BalancoMensal b = new BalancoMensal(ano, mes, calcularVendas(
                String.format("Mês %02d/%d", mes, ano), ym.atDay(1), ym.atEndOfMonth()));
        for (Despesa d : despesasDoMes(ano, mes)) {
            b.adicionarDespesa(d);
        }
        int confirmados = 0;
        int cancelados = 0;
        for (Agendamento ag : agendamentos.getAgendamentos()) {
            if (YearMonth.from(ag.getDataCriacao()).equals(ym)) {
                StatusAgendamento st = ag.getStatus();
                // realizado também foi confirmado e pago
                if (st == StatusAgendamento.CONFIRMADO || st == StatusAgendamento.REALIZADO) {
                    confirmados++;
                }
                if (st == StatusAgendamento.CANCELADO) {
                    cancelados++;
                }
            }
        }
        b.setQtdAgendamentosConfirmados(confirmados);
        b.setQtdAgendamentosCancelados(cancelados);
        RelatorioAcesso acessos = catraca.relatorioMensal(ano, mes);
        b.setQtdAcessosCatraca(acessos.getTotalEntradas());
        b.setClientesDistintosNaAcademia(acessos.getClientesDistintos());
        return b;
    }

    /**
     * @return relatórios de vendas emitidos (somente leitura)
     */
    public List<RelatorioVendas> getRelatoriosVendas() {
        return Collections.unmodifiableList(relatoriosVendas);
    }

    /**
     * @return todas as despesas (somente leitura)
     */
    public List<Despesa> getDespesas() {
        return Collections.unmodifiableList(despesas);
    }

    /**
     * Substitui os dados em memória pelos lidos do arquivo.
     *
     * @param despesasLidas despesas recuperadas
     * @param relatoriosLidos relatórios recuperados
     */
    public void carregar(List<Despesa> despesasLidas, List<RelatorioVendas> relatoriosLidos) {
        despesas.clear();
        despesas.addAll(despesasLidas);
        relatoriosVendas.clear();
        relatoriosVendas.addAll(relatoriosLidos);
    }

    @Override
    public String toString() {
        return "Financeiro{despesas=" + despesas.size()
                + ", relatoriosVendas=" + relatoriosVendas.size() + "}";
    }
}
