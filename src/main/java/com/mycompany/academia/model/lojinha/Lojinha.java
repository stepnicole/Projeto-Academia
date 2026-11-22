package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.enums.CategoriaProduto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Subsistema Lojinha: cadastro de produtos, controle de estoque e vendas da
 * lanchonete e da loja de material de esportivos.
 * <p>
 * O {@code ControleLojinha} usa esta classe e cuida das regras que dependem de outras
 * partes (cliente existir, gerar extrato, permissão do usuário).
 *
 */
public class Lojinha {

    private List<Produto> produtos;
    private Estoque estoque;
    private List<Venda> vendas;

    /** Cria a lojinha vazia. */
    public Lojinha() {
        this.produtos = new ArrayList<>();
        this.estoque = new Estoque();
        this.vendas = new ArrayList<>();
    }

    // ------------------------------------------------------------ produtos

    /**
     * Cadastra um produto e já o coloca no estoque.
     *
     * @param nome             nome
     * @param descricao        descrição
     * @param categoria        lanchonete ou loja
     * @param preco            preço de venda
     * @param quantidade       quantidade inicial no estoque
     * @param quantidadeMinima mínimo para alerta de reposição
     * @return o produto criado
     */
    public Produto cadastrarProduto(String nome, String descricao, CategoriaProduto categoria,
                                    double preco, int quantidade, int quantidadeMinima) {
        if (preco < 0 || quantidade < 0) {
            throw new IllegalArgumentException("Preço e quantidade não podem ser negativos.");
        }
        Produto p = new Produto(Identificador.proximoId(produtos), nome, descricao, categoria, preco);
        produtos.add(p);
        estoque.registrarProduto(p.getId(), quantidade, quantidadeMinima);
        return p;
    }

    /**
     * Altera os dados de um produto.
     *
     * @param id        id do produto
     * @param nome      novo nome
     * @param descricao nova descrição
     * @param categoria nova categoria
     * @param preco     novo preço
     * @return false se o produto não existe
     */
    public boolean editarProduto(int id, String nome, String descricao,
                                 CategoriaProduto categoria, double preco) {
        Produto p = buscarProduto(id);
        if (p == null) {
            return false;
        }
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setCategoria(categoria);
        p.setPreco(preco);
        return true;
    }

    /**
     * Remove o produto do cadastro e do estoque. Vendas antigas continuam
     * guardadas, pois cada item de venda tem o nome e o preço da época.
     *
     * @param id id do produto
     * @return true se removeu
     */
    public boolean removerProduto(int id) {
        Produto p = buscarProduto(id);
        if (p == null) {
            return false;
        }
        estoque.removerProduto(id);
        return produtos.remove(p);
    }

    /**
     * @param id id do produto
     * @return o produto ou null
     */
    public Produto buscarProduto(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca produtos cujo nome contém o texto (sem diferenciar maiúsculas).
     *
     * @param texto parte do nome
     * @return produtos encontrados
     */
    public List<Produto> buscarPorNome(String texto) {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(texto.toLowerCase())) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * @param categoria lanchonete ou loja
     * @return produtos da categoria
     */
    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getCategoria() == categoria) {
                lista.add(p);
            }
        }
        return lista;
    }

    // ------------------------------------------------------------ estoque

    /**
     * Caso de uso "Verificar produto no estoque da lojinha".
     *
     * @param idProduto id do produto
     * @return texto com nome, preço, quantidade e situação do estoque
     */
    public String verificarProduto(int idProduto) {
        Produto p = buscarProduto(idProduto);
        if (p == null) {
            return "Produto " + idProduto + " não cadastrado.";
        }
        ItemEstoque item = estoque.buscarItem(idProduto);
        int qtd = item == null ? 0 : item.getQuantidade();
        String situacao = qtd == 0 ? "ESGOTADO"
                : (item.estaAbaixoDoMinimo() ? "REPOR (abaixo do mínimo)" : "OK");
        return String.format("%s (%s) - R$ %.2f - %d unidade(s) - %s",
                p.getNome(), p.getCategoria(), p.getPreco(), qtd, situacao);
    }

    /**
     * Repõe o estoque de um produto.
     *
     * @param idProduto  id do produto
     * @param quantidade unidades recebidas
     * @return false se o produto não existe
     */
    public boolean reporEstoque(int idProduto, int quantidade) {
        return estoque.darEntrada(idProduto, quantidade);
    }

    /** @return produtos que precisam ser repostos */
    public List<Produto> produtosParaRepor() {
        List<Produto> lista = new ArrayList<>();
        for (ItemEstoque item : estoque.itensAbaixoDoMinimo()) {
            Produto p = buscarProduto(item.getIdProduto());
            if (p != null) {
                lista.add(p);
            }
        }
        return lista;
    }

    // ------------------------------------------------------------ vendas

    /**
     * Registra uma venda. Primeiro confere se todos os itens têm estoque; só
     * depois dá baixa, para não deixar o estoque pela metade se um item faltar.
     *
     * @param idCliente cliente que está comprando
     * @param pedido    mapa idProduto → quantidade
     * @param dataHora  momento da venda
     * @return a venda registrada
     * @throws IllegalArgumentException      se o pedido estiver vazio ou um produto não existir
     * @throws EstoqueInsuficienteExcecao se faltar estoque de algum item
     */
    public Venda registrarVenda(int idCliente, Map<Integer, Integer> pedido, LocalDateTime dataHora) {
        if (pedido == null || pedido.isEmpty()) {
            throw new IllegalArgumentException("A venda precisa de pelo menos um item.");
        }
        List<ItemVenda> itens = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : pedido.entrySet()) {
            Produto p = buscarProduto(e.getKey());
            if (p == null) {
                throw new IllegalArgumentException("Produto " + e.getKey() + " não cadastrado.");
            }
            if (e.getValue() <= 0) {
                throw new IllegalArgumentException("Quantidade inválida para " + p.getNome());
            }
            if (!estoque.temDisponivel(p.getId(), e.getValue())) {
                throw new EstoqueInsuficienteExcecao(p.getNome() + ": pedido " + e.getValue()
                        + ", disponível " + estoque.consultarQuantidade(p.getId()));
            }
            itens.add(new ItemVenda(p, e.getValue()));
        }
        for (ItemVenda item : itens) {
            estoque.darBaixa(item.getIdProduto(), item.getQuantidade());
        }
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

    /** @return soma de todas as vendas já feitas */
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

    public List<Produto> getProdutos() { return produtos; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }

    public Estoque getEstoque() { return estoque; }
    public void setEstoque(Estoque estoque) { this.estoque = estoque; }

    public List<Venda> getVendas() { return vendas; }
    public void setVendas(List<Venda> vendas) { this.vendas = vendas; }

    @Override
    public String toString() {
        return "Lojinha{produtos=" + produtos.size() + ", vendas=" + vendas.size()
                + ", faturamento=" + String.format("%.2f", faturamentoTotal())
                + ", paraRepor=" + produtosParaRepor().size() + "}";
    }
}