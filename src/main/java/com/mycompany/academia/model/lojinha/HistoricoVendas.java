package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.Identificador;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Guardar as vendas da lojinha/lanchonete e
 * responder perguntas sobre elas (vendas do período, faturamento, produtos
 * mais vendidos). Não confere estoque: recebe a venda já validada pela
 * {@link Lojinha}.
 *
 */
public class HistoricoVendas {

    private final List<Venda> vendas;

    /** Cria o histórico vazio. */
    public HistoricoVendas() {
        this.vendas = new ArrayList<>();
    }

    /**
     * Registra uma venda já validada.
     *
     * @param idCliente cliente
     * @param itens     itens vendidos
     * @param dataHora  momento da venda
     * @return a venda criada
     */
    public Venda registrar(int idCliente, List<ItemVenda> itens, LocalDateTime dataHora) {
        Venda venda = new Venda(Identificador.proximoId(vendas), idCliente, dataHora, itens);
        vendas.add(venda);
        return venda;
    }

    /**
     * @param inicio primeiro dia (inclusive)
     * @param fim    último dia (inclusive)
     * @return vendas do período
     */
    public List<Venda> vendasDoPeriodo(LocalDate inicio, LocalDate fim) {
        List<Venda> lista = new ArrayList<>();
        for (Venda v : vendas) {
            LocalDate dia = v.getDataHora().toLocalDate();
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) {
                lista.add(v);
            }
        }
        return lista;
    }

    /** @return soma de todas as vendas */
    public double faturamentoTotal() {
        double total = 0;
        for (Venda v : vendas) {
            total += v.getTotal();
        }
        return total;
    }

    /**
     * Ranking dos produtos mais vendidos (por unidades).
     *
     * @param limite quantos produtos mostrar
     * @return mapa nome → unidades vendidas, do maior para o menor
     */
    public Map<String, Integer> produtosMaisVendidos(int limite) {
        Map<String, Integer> contagem = new HashMap<>();
        for (Venda v : vendas) {
            for (ItemVenda item : v.getItens()) {
                contagem.merge(item.getNomeProduto(), item.getQuantidade(), Integer::sum);
            }
        }
        Map<String, Integer> ranking = new LinkedHashMap<>();
        contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .forEach(e -> ranking.put(e.getKey(), e.getValue()));
        return ranking;
    }

    /** @return todas as vendas (somente leitura) */
    public List<Venda> getVendas() {
        return Collections.unmodifiableList(vendas);
    }

    /**
     * Substitui as vendas pelas lidas do arquivo.
     *
     * @param lidas vendas recuperadas
     */
    public void carregar(List<Venda> lidas) {
        vendas.clear();
        vendas.addAll(lidas);
    }

    @Override
    public String toString() {
        return "HistoricoVendas{vendas=" + vendas.size()
                + ", faturamento=" + String.format("%.2f", faturamentoTotal()) + "}";
    }
}