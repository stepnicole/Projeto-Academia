package com.mycompany.academia.controller;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.enums.CategoriaProduto;
import com.mycompany.academia.model.enums.TipoExtrato;
import com.mycompany.academia.model.lojinha.ItemVenda;
import com.mycompany.academia.model.lojinha.Lojinha;
import com.mycompany.academia.model.lojinha.Produto;
import com.mycompany.academia.model.lojinha.Venda;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Ligar o subsistema {@link Lojinha} ao resto do
 * sistema (exige login, confere se o cliente existe e emite o extrato da
 * venda). As regras de estoque continuam dentro da própria Lojinha.
 *
 */
public class ControleLojinha {

    private final Sessao sessao;
    private final Lojinha lojinha;
    private final CadastroClientes clientes;
    private final EmissorExtratos extratos;

    /**
     * @param sessao   sessão (login)
     * @param lojinha  subsistema lojinha
     * @param clientes cadastro de clientes
     * @param extratos emissor de extratos
     */
    public ControleLojinha(Sessao sessao, Lojinha lojinha, CadastroClientes clientes,
                           EmissorExtratos extratos) {
        this.sessao = sessao;
        this.lojinha = lojinha;
        this.clientes = clientes;
        this.extratos = extratos;
    }

    /**
     * @param nome             nome
     * @param descricao        descrição
     * @param categoria        lanchonete ou loja
     * @param preco            preço
     * @param quantidade       estoque inicial
     * @param quantidadeMinima mínimo para alerta
     * @return o produto criado
     */
    public Produto cadastrarProduto(String nome, String descricao, CategoriaProduto categoria,
                                    double preco, int quantidade, int quantidadeMinima) {
        sessao.exigirLogin();
        return lojinha.cadastrarProduto(nome, descricao, categoria, preco, quantidade, quantidadeMinima);
    }

    /**
     * Caso de uso "Verificar produto no estoque da lojinha".
     *
     * @param idProduto produto
     * @return situação do produto no estoque
     */
    public String verificarProduto(int idProduto) {
        sessao.exigirLogin();
        return lojinha.verificarProduto(idProduto);
    }

    /**
     * Vende produtos para um cliente e emite o extrato.
     *
     * @param idCliente cliente
     * @param pedido    idProduto → quantidade
     * @param dataHora  momento da venda
     * @return a venda
     * @throws IllegalArgumentException se o cliente não existir
     */
    public Venda registrarVenda(int idCliente, Map<Integer, Integer> pedido, LocalDateTime dataHora) {
        sessao.exigirLogin();
        Cliente cliente = clientes.buscar(idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente " + idCliente + " não encontrado.");
        }
        Venda venda = lojinha.registrarVenda(idCliente, pedido, dataHora);
        List<String> linhas = new ArrayList<>();
        linhas.add("Venda nº " + venda.getId());
        for (ItemVenda item : venda.getItens()) {
            linhas.add("  " + item);
        }
        extratos.emitir(cliente, dataHora, TipoExtrato.LOJINHA, linhas, venda.getTotal());
        return venda;
    }

    /**
     * Acesso às demais operações da lojinha (editar, repor, listar, ranking).
     *
     * @return a lojinha
     */
    public Lojinha getLojinha() {
        sessao.exigirLogin();
        return lojinha;
    }

    @Override
    public String toString() {
        return "ControleLojinha{" + lojinha + "}";
    }
}