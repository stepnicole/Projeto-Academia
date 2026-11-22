package com.mycompany.academia.model.catraca;

import com.mycompany.academia.model.enums.TipoAcesso;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Relatório de acessos da catraca de um período (diário, mensal ou anual).
 *
 */
public class RelatorioAcesso {

    private String periodo;
    private LocalDate inicio;
    private LocalDate fim;
    private List<RegistroAcesso> registros;

    /**
     * @param periodo   descrição do período (ex.: "Novembro/2026")
     * @param inicio    primeiro dia (inclusive)
     * @param fim       último dia (inclusive)
     * @param registros marcações do período
     */
    public RelatorioAcesso(String periodo, LocalDate inicio, LocalDate fim,
                           List<RegistroAcesso> registros) {
        this.periodo = periodo;
        this.inicio = inicio;
        this.fim = fim;
        this.registros = new ArrayList<>(registros);
    }

    /** @return número de entradas */
    public int getTotalEntradas() {
        return contar(TipoAcesso.ENTRADA);
    }

    /** @return número de saídas */
    public int getTotalSaidas() {
        return contar(TipoAcesso.SAIDA);
    }

    private int contar(TipoAcesso tipo) {
        int n = 0;
        for (RegistroAcesso r : registros) {
            if (r.getTipo() == tipo) {
                n++;
            }
        }
        return n;
    }

    /** @return quantos clientes diferentes entraram no período */
    public int getClientesDistintos() {
        Set<Integer> ids = new HashSet<>();
        for (RegistroAcesso r : registros) {
            ids.add(r.getIdCliente());
        }
        return ids.size();
    }

    /** @return entradas agrupadas por hora do dia (0 a 23) */
    public Map<Integer, Integer> getEntradasPorHora() {
        Map<Integer, Integer> mapa = new TreeMap<>();
        for (RegistroAcesso r : registros) {
            if (r.getTipo() == TipoAcesso.ENTRADA) {
                mapa.merge(r.getDataHora().getHour(), 1, Integer::sum);
            }
        }
        return mapa;
    }

    /** @return hora com mais entradas, ou -1 se não houve entradas */
    public int getHorarioDePico() {
        int pico = -1;
        int maior = 0;
        for (Map.Entry<Integer, Integer> e : getEntradasPorHora().entrySet()) {
            if (e.getValue() > maior) {
                maior = e.getValue();
                pico = e.getKey();
            }
        }
        return pico;
    }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }

    public List<RegistroAcesso> getRegistros() { return registros; }
    public void setRegistros(List<RegistroAcesso> registros) { this.registros = registros; }

    @Override
    public String toString() {
        int pico = getHorarioDePico();
        return "RelatorioAcesso{" + periodo + ": entradas=" + getTotalEntradas()
                + ", saidas=" + getTotalSaidas()
                + ", clientesDistintos=" + getClientesDistintos()
                + ", pico=" + (pico < 0 ? "-" : pico + "h")
                + ", entradasPorHora=" + getEntradasPorHora() + "}";
    }
}