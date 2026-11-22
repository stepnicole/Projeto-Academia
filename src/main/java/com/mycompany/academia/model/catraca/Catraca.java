package com.mycompany.academia.model.catraca;

import com.mycompany.academia.model.enums.TipoAcesso;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Subsistema Catraca: recebe as marcações de entrada e saída do sistema de
 * catraca externo, sabe quem está dentro da academia agora e gera
 * relatórios diários, mensais e anuais.
 * <p>
 * Regras: um cliente não pode entrar se já está dentro, nem sair se não
 * entrou. A verificação de "tem agendamento válido" é feita pelo
 * {@code ControleCatraca}, que conhece os agendamentos.
 *
 */
public class Catraca {

    private List<RegistroAcesso> registros;

    /** Cria a catraca sem registros. */
    public Catraca() {
        this.registros = new ArrayList<>();
    }

    private int proximoId() {
        int maior = 0;
        for (RegistroAcesso r : registros) {
            maior = Math.max(maior, r.getId());
        }
        return maior + 1;
    }

    /**
     * @param idCliente id do cliente
     * @return true se a última marcação do cliente foi uma entrada
     */
    public boolean estaDentro(int idCliente) {
        RegistroAcesso ultimo = null;
        for (RegistroAcesso r : registros) {
            if (r.getIdCliente() == idCliente) {
                ultimo = r;
            }
        }
        return ultimo != null && ultimo.getTipo() == TipoAcesso.ENTRADA;
    }

    /**
     * Processa uma marcação vinda da catraca externa.
     *
     * @param idCliente cliente
     * @param tipo      entrada ou saída
     * @param dataHora  momento
     * @return o registro criado, ou null se a marcação é inválida
     *         (entrada de quem já está dentro / saída de quem não entrou)
     */
    public RegistroAcesso receberEvento(int idCliente, TipoAcesso tipo, LocalDateTime dataHora) {
        boolean dentro = estaDentro(idCliente);
        if ((tipo == TipoAcesso.ENTRADA && dentro) || (tipo == TipoAcesso.SAIDA && !dentro)) {
            return null;
        }
        RegistroAcesso r = new RegistroAcesso(proximoId(), idCliente, tipo, dataHora);
        registros.add(r);
        return r;
    }

    /** @return ids dos clientes que estão na academia agora */
    public List<Integer> getClientesPresentes() {
        Set<Integer> ids = new LinkedHashSet<>();
        for (RegistroAcesso r : registros) {
            ids.add(r.getIdCliente());
        }
        List<Integer> presentes = new ArrayList<>();
        for (int id : ids) {
            if (estaDentro(id)) {
                presentes.add(id);
            }
        }
        return presentes;
    }

    /** @return quantas pessoas estão na academia agora */
    public int getQuantidadePresentes() {
        return getClientesPresentes().size();
    }

    /**
     * @param idCliente id do cliente
     * @return todas as marcações do cliente, em ordem
     */
    public List<RegistroAcesso> historicoDoCliente(int idCliente) {
        List<RegistroAcesso> lista = new ArrayList<>();
        for (RegistroAcesso r : registros) {
            if (r.getIdCliente() == idCliente) {
                lista.add(r);
            }
        }
        return lista;
    }

    /**
     * @param inicio primeiro dia (inclusive)
     * @param fim    último dia (inclusive)
     * @return marcações do período, ordenadas por horário
     */
    public List<RegistroAcesso> registrosDoPeriodo(LocalDate inicio, LocalDate fim) {
        List<RegistroAcesso> lista = new ArrayList<>();
        for (RegistroAcesso r : registros) {
            LocalDate dia = r.getDataHora().toLocalDate();
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) {
                lista.add(r);
            }
        }
        lista.sort(Comparator.comparing(RegistroAcesso::getDataHora));
        return lista;
    }

    /**
     * @param dia dia desejado
     * @return relatório do dia
     */
    public RelatorioAcesso relatorioDiario(LocalDate dia) {
        return new RelatorioAcesso("Dia " + dia, dia, dia, registrosDoPeriodo(dia, dia));
    }

    /**
     * @param ano ano
     * @param mes mês (1 a 12)
     * @return relatório do mês
     */
    public RelatorioAcesso relatorioMensal(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        String nome = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR"));
        return new RelatorioAcesso(nome + "/" + ano, ym.atDay(1), ym.atEndOfMonth(),
                registrosDoPeriodo(ym.atDay(1), ym.atEndOfMonth()));
    }

    /**
     * @param ano ano
     * @return relatório do ano
     */
    public RelatorioAcesso relatorioAnual(int ano) {
        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fim = LocalDate.of(ano, 12, 31);
        return new RelatorioAcesso("Ano " + ano, inicio, fim, registrosDoPeriodo(inicio, fim));
    }

    public List<RegistroAcesso> getRegistros() { return registros; }
    public void setRegistros(List<RegistroAcesso> registros) { this.registros = registros; }

    @Override
    public String toString() {
        return "Catraca{registros=" + registros.size()
                + ", presentesAgora=" + getQuantidadePresentes() + "}";
    }
}