package com.mycompany.academia.controller;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Extrato;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.enums.TipoExtrato;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Emitir extratos. Cria o extrato, guarda no
 * cliente, imprime e salva em um arquivo .txt. É usado tanto pelos
 * agendamentos quanto pela lojinha, por isso fica separado de ambos.
 *
 * @author Ste
 */
public class EmissorExtratos {

    private final CadastroClientes clientes;
    private final String pastaExtratos;

    /**
     * @param clientes      cadastro (para gerar ids únicos entre todos os clientes)
     * @param pastaExtratos pasta onde os .txt são salvos
     */
    public EmissorExtratos(CadastroClientes clientes, String pastaExtratos) {
        this.clientes = clientes;
        this.pastaExtratos = pastaExtratos;
    }

    private int proximoId() {
        int maior = 0;
        for (Cliente c : clientes.getTodos()) {
            for (Extrato e : c.getExtratos()) {
                maior = Math.max(maior, e.getId());
            }
        }
        return maior + 1;
    }

    /**
     * Emite um extrato.
     *
     * @param cliente cliente
     * @param quando  momento da emissão
     * @param tipo    origem
     * @param linhas  itens descritos
     * @param total   valor (negativo para reembolso)
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

    public String getPastaExtratos() { return pastaExtratos; }

    @Override
    public String toString() {
        return "EmissorExtratos{pasta=" + pastaExtratos + "}";
    }
}