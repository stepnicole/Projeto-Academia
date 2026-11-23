package com.mycompany.academia.controller;

import com.mycompany.academia.model.Administrador;
import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.Aula;
import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Colaborador;
import com.mycompany.academia.model.Despesa;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Pagamento;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.RelatorioVendas;
import com.mycompany.academia.model.catraca.Catraca;
import com.mycompany.academia.model.catraca.RegistroAcesso;
import com.mycompany.academia.model.lojinha.Estoque;
import com.mycompany.academia.model.lojinha.Lojinha;
import com.mycompany.academia.model.lojinha.Produto;
import com.mycompany.academia.model.lojinha.Venda;
import java.io.IOException;

/**
 * Salvar e recuperartodos os dados do sistema
 * em arquivos JSON (um arquivo por tipo de dado), usando {@link Persistencia}.
 *
 * @author Ste
 */
public class RepositorioDados {

    private final String pasta;
    private final CadastroClientes clientes;
    private final CadastroColaboradores colaboradores;
    private final CadastroAulas aulas;
    private final ControleAgendamentos agendamentos;
    private final Financeiro financeiro;
    private final Lojinha lojinha;
    private final Catraca catraca;

    /**
     * @param pasta         pasta dos arquivos
     * @param clientes      cadastro de clientes
     * @param colaboradores cadastro de funcionários
     * @param aulas         grade de aulas
     * @param agendamentos  agendamentos e pagamentos
     * @param financeiro    despesas e relatórios
     * @param lojinha       produtos, estoque e vendas
     * @param catraca       registros de acesso
     */
    public RepositorioDados(String pasta, CadastroClientes clientes, CadastroColaboradores colaboradores,
                            CadastroAulas aulas, ControleAgendamentos agendamentos, Financeiro financeiro,
                            Lojinha lojinha, Catraca catraca) {
        this.pasta = pasta;
        this.clientes = clientes;
        this.colaboradores = colaboradores;
        this.aulas = aulas;
        this.agendamentos = agendamentos;
        this.financeiro = financeiro;
        this.lojinha = lojinha;
        this.catraca = catraca;
    }

    private String arquivo(String nome) {
        return pasta + "/" + nome;
    }

    /**
     * Grava todos os dados.
     *
     * @throws IOException se algum arquivo não puder ser gravado
     */
    public void salvar() throws IOException {
        Persistencia.salvarLista(arquivo("clientes.json"), clientes.getTodos());
        Persistencia.salvarLista(arquivo("colaboradores.json"), colaboradores.getColaboradores());
        Persistencia.salvarLista(arquivo("administradores.json"), colaboradores.getAdministradores());
        Persistencia.salvarLista(arquivo("instrutores.json"), aulas.getInstrutores());
        Persistencia.salvarLista(arquivo("aulas.json"), aulas.getAulas());
        Persistencia.salvarLista(arquivo("agendamentos.json"), agendamentos.getAgendamentos());
        Persistencia.salvarLista(arquivo("pagamentos.json"), agendamentos.getPagamentos());
        Persistencia.salvarLista(arquivo("despesas.json"), financeiro.getDespesas());
        Persistencia.salvarLista(arquivo("relatorios_vendas.json"), financeiro.getRelatoriosVendas());
        Persistencia.salvarLista(arquivo("produtos.json"), lojinha.getCatalogo().getProdutos());
        Persistencia.salvarObjeto(arquivo("estoque.json"), lojinha.getEstoque());
        Persistencia.salvarLista(arquivo("vendas.json"), lojinha.getHistoricoVendas().getVendas());
        Persistencia.salvarLista(arquivo("acessos_catraca.json"), catraca.getRegistros());
    }

    /**
     * Lê todos os dados, substituindo o que está em memória. Arquivos que não
     * existem viram listas vazias.
     *
     * @throws IOException se algum arquivo não puder ser lido
     */
    public void carregar() throws IOException {
        clientes.carregar(Persistencia.carregarLista(arquivo("clientes.json"), Cliente.class));
        colaboradores.carregar(
                Persistencia.carregarLista(arquivo("colaboradores.json"), Colaborador.class),
                Persistencia.carregarLista(arquivo("administradores.json"), Administrador.class));
        aulas.carregar(
                Persistencia.carregarLista(arquivo("instrutores.json"), Instrutor.class),
                Persistencia.carregarLista(arquivo("aulas.json"), Aula.class));
        agendamentos.carregar(
                Persistencia.carregarLista(arquivo("agendamentos.json"), Agendamento.class),
                Persistencia.carregarLista(arquivo("pagamentos.json"), Pagamento.class));
        financeiro.carregar(
                Persistencia.carregarLista(arquivo("despesas.json"), Despesa.class),
                Persistencia.carregarLista(arquivo("relatorios_vendas.json"), RelatorioVendas.class));
        lojinha.getCatalogo().carregar(Persistencia.carregarLista(arquivo("produtos.json"), Produto.class));
        Estoque estoque = Persistencia.carregarObjeto(arquivo("estoque.json"), Estoque.class);
        lojinha.setEstoque(estoque != null ? estoque : new Estoque());
        lojinha.getHistoricoVendas().carregar(Persistencia.carregarLista(arquivo("vendas.json"), Venda.class));
        catraca.setRegistros(Persistencia.carregarLista(arquivo("acessos_catraca.json"), RegistroAcesso.class));
    }

    public String getPasta() { return pasta; }

    @Override
    public String toString() {
        return "RepositorioDados{pasta=" + pasta + ", formato=JSON}";
    }
}