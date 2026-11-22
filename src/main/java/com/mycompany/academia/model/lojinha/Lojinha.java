package com.mycompany.academia.model.lojinha;

import com.mycompany.academia.model.enums.CategoriaProduto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fachada (padrão <b>Facade</b>) do subsistema Lojinha.
 * <p>
 * Não guarda dados nem regras próprias: só coordena as três partes do
 * subsistema nas operações que envolvem mais de uma delas.
 * <ul>
 *   <li>{@link CatalogoProdutos} — quais produtos existem e seus preços;</li>
 *   <li>{@link Estoque} — quantas unidades há de cada produto;</li>
 *   <li>{@link HistoricoVendas} — o que já foi vendido.</li>
 * </ul>
 * Quem usa a lojinha (o {@code ControleLojinha}) fala só com esta classe e
 * não precisa saber como ela é organizada por dentro. Operações que envolvem
 * uma parte só (ex.: buscar produto por nome, ranking de vendas) são feitas
 * direto na parte, obtida por {@link #getCatalogo()}, {@link #getEstoque()}
 * ou {@link #getHistoricoVendas()}.
 *
 */
public class Lojinha {

    private final CatalogoProdutos catalogo;
    private Estoque estoque;
    private final HistoricoVendas historicoVendas;

    /** Cria a lojinha vazia. */
    public Lojinha() {
        this.catalogo = new CatalogoProdutos();
        this.estoque = new Estoque();
        this.historicoVendas = new HistoricoVendas();
    }

    /**
     * Cadastra o produto no catálogo e já o coloca no estoque.
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
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }
        Produto p = catalogo.cadastrar(nome, descricao, categoria, preco);
        estoque.registrarProduto(p.getId(), quantidade, quantidadeMinima);
        return p;
    }

    /**
     * Remove o produto do catálogo e do estoque. Vendas antigas continuam no
     * histórico, pois cada item de venda guarda o nome e o preço da época.
     *
     * @param idProduto produto
     * @return true se removeu
     */
    public boolean removerProduto(int idProduto) {
        if (catalogo.buscar(idProduto) == null) {
            return false;
        }
        estoque.removerProduto(idProduto);
        return catalogo.remover(idProduto);
    }

    /**
     * Caso de uso "Verificar produto no estoque da lojinha".
     *
     * @param idProduto id do produto
     * @return texto com nome, preço, quantidade e situação do estoque
     */
    public String verificarProduto(int idProduto) {
        Produto p = catalogo.buscar(idProduto);
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
     * @param idProduto  produto
     * @param quantidade unidades recebidas
     * @return false se o produto não está no estoque
     */
    public boolean reporEstoque(int idProduto, int quantidade) {
        return estoque.darEntrada(idProduto, quantidade);
    }

    /** @return produtos que precisam ser repostos */
    public List<Produto> produtosParaRepor() {
        List<Produto> lista = new ArrayList<>();
        for (ItemEstoque item : estoque.itensAbaixoDoMinimo()) {
            Produto p = catalogo.buscar(item.getIdProduto());
            if (p != null) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Registra uma venda: confere no catálogo e no estoque se todos os itens
     * existem e estão disponíveis; só depois dá baixa e grava no histórico,
     * para não deixar o estoque pela metade se um item faltar.
     *
     * @param idCliente cliente que está comprando
     * @param pedido    mapa idProduto → quantidade
     * @param dataHora  momento da venda
     * @return a venda registrada
     * @throws IllegalArgumentException    se o pedido estiver vazio ou um produto não existir
     * @throws EstoqueInsuficienteExcecao se faltar estoque de algum item
     */
    public Venda registrarVenda(int idCliente, Map<Integer, Integer> pedido, LocalDateTime dataHora) {
        if (pedido == null || pedido.isEmpty()) {
            throw new IllegalArgumentException("A venda precisa de pelo menos um item.");
        }
        List<ItemVenda> itens = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : pedido.entrySet()) {
            Produto p = catalogo.buscar(e.getKey());
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
        return historicoVendas.registrar(idCliente, itens, dataHora);
    }

    /** @return o catálogo de produtos */
    public CatalogoProdutos getCatalogo() { return catalogo; }

    /** @return o estoque */
    public Estoque getEstoque() { return estoque; }

    /**
     * Troca o estoque (usado ao carregar o JSON, pois o Estoque é salvo como
     * um objeto único).
     *
     * @param estoque estoque recuperado
     */
    public void setEstoque(Estoque estoque) { this.estoque = estoque; }

    /** @return o histórico de vendas */
    public HistoricoVendas getHistoricoVendas() { return historicoVendas; }

    // Sem setters para catálogo e histórico: são final e recebem os dados
    // do arquivo pelos seus próprios métodos carregar().

    @Override
    public String toString() {
        return "Lojinha{" + catalogo + ", " + historicoVendas
                + ", paraRepor=" + produtosParaRepor().size() + "}";
    }
}