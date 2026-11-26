package com.mycompany.academia.controller;

import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.Aula;
import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Extrato;
import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Pagamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import com.mycompany.academia.model.enums.TipoAgendamento;
import com.mycompany.academia.model.enums.TipoExtrato;
import com.mycompany.academia.model.enums.TipoPagamento;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Ciclo de vida dos agendamentos (reservar,
 * confirmar com cobrança, cancelar com reembolso, expirar, concluir) e os
 * pagamentos gerados por eles.
 * <p>
 * As regras de cada situação ficam nos estados do padrão State
 * ({@code Preliminar}, {@code Confirmado}, {@code Realizado},
 * {@code Cancelado}); este serviço coordena vaga na aula, pagamento e extrato.
 *
 */
public class ControleAgendamentos {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Sessao sessao;
    private final CadastroClientes clientes;
    private final CadastroAulas aulas;
    private final EmissorExtratos extratos;
    private final List<Agendamento> agendamentos;
    private final List<Pagamento> pagamentos;

    /**
     * @param sessao   sessão (login)
     * @param clientes cadastro de clientes
     * @param aulas    grade de aulas
     * @param extratos emissor de extratos
     */
    public ControleAgendamentos(Sessao sessao, CadastroClientes clientes,
                                CadastroAulas aulas, EmissorExtratos extratos) {
        this.sessao = sessao;
        this.clientes = clientes;
        this.aulas = aulas;
        this.extratos = extratos;
        this.agendamentos = new ArrayList<>();
        this.pagamentos = new ArrayList<>();
    }

    /**
     * Faz um agendamento preliminar e reserva a vaga na aula.
     *
     * @param idCliente cliente
     * @param idAula    aula escolhida
     * @param datas     data(s) escolhida(s) (mensalidade: data de início)
     * @param preco     preço a cobrar
     * @param tipo      diária ou mensalidade
     * @param hoje      data em que o agendamento está sendo feito
     * @return o agendamento criado
     * @throws IllegalArgumentException se cliente/aula não existem, não há vaga
     *                                  ou o prazo de confirmação já passou
     */
    public Agendamento realizar(int idCliente, int idAula, List<LocalDate> datas,
                                double preco, TipoAgendamento tipo, LocalDate hoje) {
        sessao.exigirLogin();
        Cliente cliente = clientes.buscar(idCliente);
        Aula aula = aulas.buscarAula(idAula);
        if (cliente == null || aula == null) {
            throw new IllegalArgumentException("Cliente ou aula não encontrado.");
        }
        if (!aula.temVaga()) {
            throw new IllegalArgumentException("Não há vaga na aula " + idAula);
        }
        Agendamento ag = new Agendamento(Identificador.proximoId(agendamentos), idCliente, idAula,
                datas, preco, tipo, hoje);
        if (hoje.isAfter(ag.getPrazoConfirmacao())) {
            throw new IllegalArgumentException("Prazo de confirmação (" + ag.getPrazoConfirmacao()
                    + ") já passou; escolha uma data mais distante.");
        }
        aula.ocuparVaga();
        agendamentos.add(ag);
        cliente.adicionarAgendamento(ag.getId());
        return ag;
    }

    /**
     * Confirma o agendamento, debita o valor no cartão e emite o extrato.
     *
     * @param idAgendamento agendamento
     * @param hoje          data da confirmação
     * @return o extrato emitido, ou null se não pôde confirmar
     */
    public Extrato confirmar(int idAgendamento, LocalDate hoje) {
        sessao.exigirLogin();
        Agendamento ag = buscar(idAgendamento);
        if (ag == null || !ag.confirmar(hoje)) {
            return null;
        }
        pagamentos.add(Pagamento.debitar(Identificador.proximoId(pagamentos), ag, hoje));

        Cliente cliente = clientes.buscar(ag.getIdCliente());
        Aula aula = aulas.buscarAula(ag.getIdAula());
        Instrutor instrutor = aula == null ? null : aulas.buscarInstrutor(aula.getIdInstrutor());
        List<String> linhas = new ArrayList<>();
        linhas.add(String.format("%s - %s (%s)", ag.getTipo(),
                aula == null ? "?" : aula.getTipoAula(),
                aula == null ? "?" : aula.getSala().getNome()));
        linhas.add("Agendamento nº " + ag.getId() + " - data(s): " + formatarDatas(ag.getDatas()));
        linhas.add("Instrutor(a): " + (instrutor == null ? "-" : instrutor.getNome()));
        linhas.add("Cobrado no cartão " + cliente.getCartaoMascarado()
                + String.format(": R$ %.2f", ag.getPreco()));
        return extratos.emitir(cliente, hoje.atStartOfDay(), TipoExtrato.AGENDAMENTO, linhas, ag.getPreco());
    }

    /**
     * Cancela um agendamento. Se estava confirmado e for até 3 dias antes,
     * devolve 50% (a academia retém a outra metade) e emite extrato de
     * reembolso. A vaga na aula é liberada.
     *
     * @param idAgendamento agendamento
     * @param hoje          data do cancelamento
     * @return valor devolvido (0 se não houver reembolso)
     */
    public double cancelar(int idAgendamento, LocalDate hoje) {
        sessao.exigirLogin();
        Agendamento ag = buscar(idAgendamento);
        if (ag == null) {
            return 0.0;
        }
        // Quem decide se pode cancelar é o estado (State): cancelado e
        // realizado não mudam. Só libera a vaga se houve mudança de fato.
        StatusAgendamento antes = ag.getStatus();
        double reembolso = ag.cancelar(hoje);
        if (ag.getStatus() == antes) {
            return 0.0;
        }
        liberarVaga(ag);
        if (reembolso > 0) {
            pagamentos.add(Pagamento.reembolsar(Identificador.proximoId(pagamentos), ag, reembolso, hoje));
            Cliente cliente = clientes.buscar(ag.getIdCliente());
            if (cliente != null) {
                List<String> linhas = new ArrayList<>();
                linhas.add("Cancelamento do agendamento nº " + ag.getId());
                linhas.add(String.format("Valor pago:            R$ %.2f", ag.getPreco()));
                linhas.add(String.format("Retido pela academia:  R$ %.2f", ag.getPreco() - reembolso));
                linhas.add(String.format("Devolvido no cartão %s: R$ %.2f",
                        cliente.getCartaoMascarado(), reembolso));
                extratos.emitir(cliente, hoje.atStartOfDay(), TipoExtrato.REEMBOLSO, linhas, -reembolso);
            }
        }
        return reembolso;
    }

    /**
     * Cancela os preliminares cujo prazo de confirmação passou (rodar todo dia).
     *
     * @param hoje data atual
     * @return quantos expiraram
     */
    public int expirarPendentes(LocalDate hoje) {
        int n = 0;
        for (Agendamento ag : agendamentos) {
            if (ag.expirar(hoje)) {
                liberarVaga(ag);
                n++;
            }
        }
        return n;
    }

    /**
     * Marca como realizados os agendamentos confirmados cuja última data já
     * passou (rodar todo dia, junto com {@link #expirarPendentes}).
     *
     * @param hoje data atual
     * @return quantos passaram para realizado
     */
    public int concluirRealizados(LocalDate hoje) {
        int n = 0;
        for (Agendamento ag : agendamentos) {
            if (ag.concluir(hoje)) {
                n++;
            }
        }
        return n;
    }

    /**
     * Cancela os agendamentos preliminares de um cliente (usado quando o
     * cliente é removido), liberando as vagas.
     *
     * @param idCliente cliente
     * @return quantos foram cancelados
     */
    public int cancelarPreliminaresDoCliente(int idCliente) {
        int n = 0;
        for (Agendamento ag : agendamentos) {
            if (ag.getIdCliente() == idCliente && ag.getStatus() == StatusAgendamento.PRELIMINAR) {
                ag.cancelar(LocalDate.now()); // preliminar → cancelado, sem reembolso
                liberarVaga(ag);
                n++;
            }
        }
        return n;
    }

    private void liberarVaga(Agendamento ag) {
        Aula aula = aulas.buscarAula(ag.getIdAula());
        if (aula != null) {
            aula.liberarVaga();
        }
    }

    /**
     * @param id id do agendamento
     * @return o agendamento ou null
     */
    public Agendamento buscar(int id) {
        for (Agendamento ag : agendamentos) {
            if (ag.getId() == id) {
                return ag;
            }
        }
        return null;
    }

    /**
     * @param comparador critério (ex.: {@code new AgendamentoPorData()})
     * @return cópia da lista ordenada
     */
    public List<Agendamento> listar(Comparator<Agendamento> comparador) {
        sessao.exigirLogin();
        List<Agendamento> copia = new ArrayList<>(agendamentos);
        copia.sort(comparador);
        return copia;
    }

    /**
     * @param idCliente cliente
     * @return agendamentos do cliente
     */
    public List<Agendamento> doCliente(int idCliente) {
        sessao.exigirLogin();
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento ag : agendamentos) {
            if (ag.getIdCliente() == idCliente) {
                lista.add(ag);
            }
        }
        return lista;
    }

    /**
     * @param idCliente cliente
     * @param data      dia do acesso
     * @return true se tem diária/mensalidade confirmada para o dia
     */
    public boolean possuiAcessoValido(int idCliente, LocalDate data) {
        for (Agendamento ag : agendamentos) {
            if (ag.getIdCliente() == idCliente && ag.cobreData(data)) {
                return true;
            }
        }
        return false;
    }

    /** @return texto com as vendas de diárias e mensalidades (cobranças e reembolsos) */
    public String imprimirVendas() {
        sessao.exigirLogin();
        StringBuilder sb = new StringBuilder("--- Vendas de diárias e mensalidades ---\n");
        for (Pagamento p : pagamentos) {
            Agendamento ag = buscar(p.getIdAgendamento());
            Cliente c = ag == null ? null : clientes.buscar(ag.getIdCliente());
            sb.append(String.format("%s | %-9s | %-11s | %-16s | R$ %8.2f%n",
                    p.getData().format(DATA_BR), p.getTipo(),
                    ag == null ? "?" : ag.getTipo(),
                    c == null ? "(cliente removido)" : c.getNome(),
                    p.getTipo() == TipoPagamento.REEMBOLSO ? -p.getValor() : p.getValor()));
        }
        return sb.toString();
    }

    private static String formatarDatas(List<LocalDate> datas) {
        List<String> textos = new ArrayList<>();
        for (LocalDate d : datas) {
            textos.add(d.format(DATA_BR));
        }
        return String.join(", ", textos);
    }

    /** @return agendamentos (somente leitura) */
    public List<Agendamento> getAgendamentos() {
        return Collections.unmodifiableList(agendamentos);
    }

    /** @return pagamentos (somente leitura) */
    public List<Pagamento> getPagamentos() {
        return Collections.unmodifiableList(pagamentos);
    }

    /**
     * Substitui os dados em memória pelos lidos do arquivo.
     *
     * @param ags  agendamentos recuperados
     * @param pags pagamentos recuperados
     */
    public void carregar(List<Agendamento> ags, List<Pagamento> pags) {
        agendamentos.clear();
        agendamentos.addAll(ags);
        pagamentos.clear();
        pagamentos.addAll(pags);
    }

    @Override
    public String toString() {
        return "ControleAgendamentos{agendamentos=" + agendamentos.size()
                + ", pagamentos=" + pagamentos.size() + "}";
    }
}