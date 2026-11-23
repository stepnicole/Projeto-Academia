package com.mycompany.academia.model;

import com.mycompany.academia.model.catraca.Catraca;
import com.mycompany.academia.model.lojinha.Lojinha;
import com.mycompany.academia.controller.CadastroAulas;
import com.mycompany.academia.controller.CadastroClientes;
import com.mycompany.academia.controller.CadastroColaboradores;
import com.mycompany.academia.controller.ControleAgendamentos;
import com.mycompany.academia.controller.ControleCatraca;
import com.mycompany.academia.controller.ControleLojinha;
import com.mycompany.academia.controller.EmissorExtratos;
import com.mycompany.academia.controller.Financeiro;
import com.mycompany.academia.controller.RepositorioDados;
import com.mycompany.academia.controller.Sessao;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ponto de entrada do sistema da academia GYM CORN - Milho Verde.
 * <p>
 * A classe Sistema apenas cria os serviços, liga um ao outro
 * (composição) e dá acesso a eles. Cada serviço tem uma única
 * responsabilidade:
 * <ul>
 *   <li>{@link Sessao} — login e permissões;</li>
 *   <li>{@link CadastroClientes} — clientes e conta do cliente;</li>
 *   <li>{@link CadastroColaboradores} — colaboradores, administradores e senhas;</li>
 *   <li>{@link CadastroAulas} — instrutores e aulas;</li>
 *   <li>{@link ControleAgendamentos} — agendamentos e pagamentos;</li>
 *   <li>{@link ControleLojinha} — vendas da lojinha/lanchonete;</li>
 *   <li>{@link ControleCatraca} — integração com a catraca externa;</li>
 *   <li>{@link Financeiro} — relatórios de vendas, despesas e balanço;</li>
 *   <li>{@link EmissorExtratos} — emissão dos extratos;</li>
 *   <li>{@link RepositorioDados} — salvar/carregar em JSON.</li>
 * </ul>
 *
 * <h2>Contadores de instâncias de Cliente (item 11)</h2>
 * <ol>
 *   <li><b>{@code private static int totalClientesCriados}</b> com
 *   {@code get}/{@code set} estáticos (encapsulamento).<br>
 *   <i>Vantagens:</i> nenhuma outra classe altera o valor diretamente; o
 *   {@code set} pode validar (aqui, não aceita valor negativo); a
 *   implementação interna pode mudar sem afetar quem usa.<br>
 *   <i>Desvantagens:</i> mais código; o {@code set} público ainda permite que
 *   qualquer classe coloque um valor errado (positivo).</li>
 *   <li><b>{@code protected static int contadorClientesProtected}</b>, acessado
 *   diretamente com {@code Sistema.contadorClientesProtected++}.<br>
 *   <i>Vantagens:</i> mais simples e direto; não precisa de métodos.<br>
 *   <i>Desvantagens:</i> em Java, {@code protected} libera o acesso para
 *   <b>todas as classes do mesmo pacote</b> e para subclasses, sem nenhuma
 *   validação (dá para zerar ou deixar negativo); quebra o encapsulamento e
 *   acopla as classes ao nome do atributo.</li>
 * </ol>
 * Os contadores contam só objetos criados pelo construtor. Objetos lidos do
 * JSON não contam, pois o Gson não chama o construtor (não são clientes
 * novos, apenas recuperados).
 *
 */
public class Sistema {

    // ===================================================== variáveis de classe

    /** Estratégia (a): private static + get/set. */
    private static int totalClientesCriados = 0;

    /** Estratégia (b): protected static, acesso direto pelo pacote/subclasses. */
    protected static int contadorClientesProtected = 0;

    /** Quantidade de produtos da loja criados. */
    private static int totalProdutosCriados = 0;

    // ===================================================== serviços

    private final Sessao sessao;
    private final CadastroClientes cadastroClientes;
    private final CadastroColaboradores cadastroColaboradores;
    private final CadastroAulas cadastroAulas;
    private final ControleAgendamentos controleAgendamentos;
    private final ControleLojinha controleLojinha;
    private final ControleCatraca controleCatraca;
    private final Financeiro financeiro;
    private final RepositorioDados repositorio;

    /**
     * Monta o sistema e liga os serviços entre si.
     *
     * @param pastaDados pasta onde ficam os arquivos JSON (ex.: "dados")
     */
    public Sistema(String pastaDados) {
        Lojinha lojinha = new Lojinha();
        Catraca catraca = new Catraca();
        this.sessao = new Sessao();
        this.cadastroClientes = new CadastroClientes(sessao);
        this.cadastroColaboradores = new CadastroColaboradores(sessao);
        this.cadastroAulas = new CadastroAulas(sessao);
        EmissorExtratos extratos = new EmissorExtratos(cadastroClientes, pastaDados + "/extratos");
        this.controleAgendamentos = new ControleAgendamentos(sessao, cadastroClientes, cadastroAulas, extratos);
        this.controleLojinha = new ControleLojinha(sessao, lojinha, cadastroClientes, extratos);
        this.controleCatraca = new ControleCatraca(sessao, catraca, cadastroClientes, controleAgendamentos);
        this.financeiro = new Financeiro(sessao, controleAgendamentos, lojinha, catraca,
                cadastroColaboradores, cadastroAulas);
        this.repositorio = new RepositorioDados(pastaDados, cadastroClientes, cadastroColaboradores,
                cadastroAulas, controleAgendamentos, financeiro, lojinha, catraca);
    }

    // ===================================================== métodos de classe (static)

    /** @return quantos clientes foram criados (estratégia private + get) */
    public static int getTotalClientesCriados() {
        return totalClientesCriados;
    }

    /**
     * @param valor novo valor do contador
     * @throws IllegalArgumentException se o valor for negativo
     */
    public static void setTotalClientesCriados(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Contador não pode ser negativo.");
        }
        totalClientesCriados = valor;
    }

    /** @return quantos clientes foram criados (estratégia protected) */
    public static int getContadorClientesProtected() {
        return contadorClientesProtected;
    }

    /** @return quantos produtos da loja foram criados */
    public static int getTotalProdutosCriados() {
        return totalProdutosCriados;
    }

    /**
     * @param valor novo valor do contador
     * @throws IllegalArgumentException se o valor for negativo
     */
    public static void setTotalProdutosCriados(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Contador não pode ser negativo.");
        }
        totalProdutosCriados = valor;
    }

    /**
     * Método de classe (item 12): quantas instâncias de Cliente e de Produto
     * foram criadas.
     *
     * @return mapa "Cliente" → quantidade, "Produto" → quantidade
     */
    public static Map<String, Integer> contarInstancias() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        mapa.put("Cliente", totalClientesCriados);
        mapa.put("Produto", totalProdutosCriados);
        return mapa;
    }

    // ===================================================== autenticação

    /**
     * Faz login de um colaborador ou administrador.
     *
     * @param login login
     * @param senha senha
     * @return true se autenticou
     */
    public boolean login(String login, String senha) {
        return sessao.login(login, senha, cadastroColaboradores.listarTodos());
    }

    /** Encerra a sessão do usuário atual. */
    public void logout() {
        sessao.logout();
    }

    // ===================================================== casos de uso que envolvem mais de um serviço

    /**
     * Caso de uso "Remover cliente": cancela os agendamentos preliminares do
     * cliente (liberando as vagas) e o remove do cadastro. Pagamentos antigos
     * ficam no histórico financeiro.
     *
     * @param idCliente cliente
     * @return true se removeu
     */
    public boolean removerCliente(int idCliente) {
        sessao.exigirLogin();
        if (cadastroClientes.buscar(idCliente) == null) {
            return false;
        }
        controleAgendamentos.cancelarPreliminaresDoCliente(idCliente);
        return cadastroClientes.remover(idCliente);
    }

    // ===================================================== persistência

    /**
     * Salva todos os dados em JSON.
     *
     * @throws IOException se algum arquivo não puder ser gravado
     */
    public void salvarDados() throws IOException {
        repositorio.salvar();
    }

    /**
     * Carrega todos os dados do JSON e encerra a sessão atual.
     *
     * @throws IOException se algum arquivo não puder ser lido
     */
    public void carregarDados() throws IOException {
        repositorio.carregar();
        sessao.logout();
    }

    // ===================================================== acesso aos serviços

    public Sessao getSessao() { return sessao; }
    public CadastroClientes getCadastroClientes() { return cadastroClientes; }
    public CadastroColaboradores getCadastroColaboradores() { return cadastroColaboradores; }
    public CadastroAulas getCadastroAulas() { return cadastroAulas; }
    public ControleAgendamentos getControleAgendamentos() { return controleAgendamentos; }
    public ControleLojinha getControleLojinha() { return controleLojinha; }
    public ControleCatraca getControleCatraca() { return controleCatraca; }
    public Financeiro getFinanceiro() { return financeiro; }

    // Sem setters para os serviços: eles são criados e ligados no construtor
    // e não devem ser trocados depois (por isso os atributos são final).

    @Override
    public String toString() {
        return "Sistema{" + sessao + ", " + cadastroClientes + ", " + cadastroColaboradores
                + ", " + cadastroAulas + ", " + controleAgendamentos + ", " + controleLojinha
                + ", " + controleCatraca + ", " + financeiro + "}";
    }
}