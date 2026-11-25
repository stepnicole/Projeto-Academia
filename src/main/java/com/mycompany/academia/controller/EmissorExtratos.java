package com.mycompany.academia.controller;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Extrato;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.enums.TipoExtrato;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Emitir extratos. Cria o extrato, guarda no cliente, imprime e salva em um
 * arquivo .txt. É usado tanto pelos agendamentos quanto pela lojinha, por isso
 * fica separado de ambos.
 *
 * @author Ste
 */
public class EmissorExtratos {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CadastroClientes clientes;
    private final String pastaExtratos;
    private int ultimaNotaFiscal;
    private int ultimoExtrato;

    /**
     * @param clientes cadastro (para gerar ids únicos entre todos os clientes)
     * @param pastaExtratos pasta onde os .txt são salvos
     */
    public EmissorExtratos(CadastroClientes clientes, String pastaExtratos) {
        this.clientes = clientes;
        this.pastaExtratos = pastaExtratos;
    }

        /**
     * Próximo número de extrato. Guarda o último número usado para não repetir
     * números quando um cliente (e seus extratos) é removido do cadastro.
     */
    private int proximoId() {
        int maior = ultimoExtrato;
        for (Cliente c : clientes.getTodos()) {
            for (Extrato e : c.getExtratos()) {
                maior = Math.max(maior, e.getId());
            }
        }
        ultimoExtrato = maior + 1;
        return ultimoExtrato;
    }

    /**
     * Emite um extrato.
     *
     * @param cliente cliente
     * @param quando momento da emissão
     * @param tipo origem
     * @param linhas itens descritos
     * @param total valor (negativo para reembolso)
     * @return o extrato emitido
     */
    public Extrato emitir(Cliente cliente, LocalDateTime quando, TipoExtrato tipo,
            List<String> linhas, double total) {
        Extrato extrato = new Extrato(proximoId(), cliente, quando, tipo, linhas, total);
        cliente.adicionarExtrato(extrato);
        System.out.println(extrato.imprimir());
        String arquivo = pastaExtratos + "/extrato_" + extrato.getId()
                + "_cliente_" + cliente.getId() + ".txt";
        try {
            Persistencia.salvarTexto(arquivo, extrato.imprimir());
        } catch (IOException e) {
            // O extrato continua guardado no cliente (e no JSON); só o .txt falhou.
            System.err.println("Não foi possível salvar " + arquivo + ": " + e.getMessage());
        }
        return extrato;
    }

        /**
     * Emite a nota fiscal de fechamento do cliente: reúne todos os extratos
     * (diárias, mensalidades, compras e reembolsos) e o valor total. A nota é
     * impressa e salva em um arquivo .txt.
     *
     * @param cliente cliente
     * @param data    data de emissão
     * @return texto da nota fiscal
     */
    public String emitirNotaFiscal(Cliente cliente, LocalDate data) {
        ultimaNotaFiscal++;
        StringBuilder sb = new StringBuilder();
        sb.append("============ GYM CORN - MILHO VERDE ============\n");
        sb.append("                 NOTA FISCAL\n");
        sb.append(String.format("Nº %06d          Emissão: %s%n", ultimaNotaFiscal, data.format(DATA_BR)));
        sb.append(String.format("Cliente: %s (id %d)%n", cliente.getNome(), cliente.getId()));
        sb.append(String.format("CPF: %s   Cartão: %s%n", cliente.getCpfAnonimizado(), cliente.getCartaoMascarado()));
        sb.append("------------------------------------------------\n");
        for (Extrato e : cliente.getExtratos()) {
            sb.append(String.format("%s  %-28s R$ %8.2f%n",
                    e.getDataHora().toLocalDate().format(DATA_BR), e.getTipo(), e.getValorTotal()));
        }
        sb.append("------------------------------------------------\n");
        sb.append(String.format("VALOR TOTAL:                            R$ %8.2f%n", cliente.calcularTotalConta()));
        sb.append("================================================\n");
        String texto = sb.toString();
        System.out.println(texto);
        String arquivo = pastaExtratos + "/nota_fiscal_" + ultimaNotaFiscal
                + "_cliente_" + cliente.getId() + ".txt";
        try {
            Persistencia.salvarTexto(arquivo, texto);
        } catch (IOException e) {
            System.err.println("Não foi possível salvar " + arquivo + ": " + e.getMessage());
        }
        return texto;
    }
    
    public String getPastaExtratos() {
        return pastaExtratos;
    }

    @Override
    public String toString() {
        return "EmissorExtratos{pasta=" + pastaExtratos + "}";
    }
}
