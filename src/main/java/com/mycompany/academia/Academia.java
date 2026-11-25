package com.mycompany.academia;

import com.mycompany.academia.comparador.AgendamentoPorCliente;
import com.mycompany.academia.comparador.AgendamentoPorData;
import com.mycompany.academia.comparador.AgendamentoPorPreco;
import com.mycompany.academia.comparador.AgendamentoPorStatus;
import com.mycompany.academia.comparador.ClientePorId;
import com.mycompany.academia.comparador.ClientePorNome;
import com.mycompany.academia.comparador.ClientePorQuantidadeAgendamentos;
import com.mycompany.academia.comparador.ClientePorTotalGasto;
import com.mycompany.academia.controller.CadastroAulas;
import com.mycompany.academia.controller.CadastroClientes;
import com.mycompany.academia.controller.CadastroColaboradores;
import com.mycompany.academia.controller.ControleAgendamentos;
import com.mycompany.academia.controller.ControleCatraca;
import com.mycompany.academia.controller.ControleLojinha;
import com.mycompany.academia.controller.Financeiro;
import com.mycompany.academia.model.AcessoNegadoExcecao;
import com.mycompany.academia.model.Administrador;
import com.mycompany.academia.model.Agendamento;
import com.mycompany.academia.model.Aula;
import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Colaborador;
import com.mycompany.academia.model.Despesa;
import com.mycompany.academia.model.Extrato;
import com.mycompany.academia.model.Instrutor;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.Pessoa;
import com.mycompany.academia.model.Sala;
import com.mycompany.academia.model.Sistema;
import com.mycompany.academia.model.enums.CategoriaDespesa;
import com.mycompany.academia.model.enums.CategoriaProduto;
import com.mycompany.academia.model.enums.StatusAgendamento;
import com.mycompany.academia.model.enums.TipoAcesso;
import com.mycompany.academia.model.enums.TipoAgendamento;
import com.mycompany.academia.model.enums.TipoAula;
import com.mycompany.academia.model.lojinha.EstoqueInsuficienteExcecao;
import com.mycompany.academia.model.lojinha.Lojinha;
import com.mycompany.academia.model.lojinha.Produto;
import com.mycompany.academia.model.lojinha.Venda;
import com.mycompany.academia.model.state.EstadoFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Classe principal: testa, em ordem, todas as funcionalidades do sistema da
 * academia (cada seção corresponde a um caso de uso do trabalho).
 * <p>
 * As datas são fixas (novembro de 2026) para que o resultado seja sempre o
 * mesmo, independente do dia em que o programa for executado.
 *
 * @author Stephanie
 */
public class Academia {

    /**
     * Construtor privado: a classe só tem o método main.
     */
    private Academia() {
    }

    private static void titulo(String texto) {
        System.out.println("\n==================== " + texto + " ====================");
    }

    /**
     * Ponto de entrada.
     *
     * @param args não usados
     * @throws IOException se houver erro ao salvar/carregar os arquivos
     */
    public static void main(String[] args) throws IOException {
        Sistema sistema = new Sistema("dados");
        // Cada serviço cuida de uma parte do sistema (responsabilidade única).
        CadastroColaboradores colabs = sistema.getCadastroColaboradores();
        CadastroClientes clientes = sistema.getCadastroClientes();
        CadastroAulas aulas = sistema.getCadastroAulas();
        ControleAgendamentos agenda = sistema.getControleAgendamentos();
        ControleLojinha lojaCtrl = sistema.getControleLojinha();
        ControleCatraca catraca = sistema.getControleCatraca();
        Financeiro financeiro = sistema.getFinanceiro();

        // ------------------------------------------------------------------
        titulo("1. AUTENTICAÇÃO");
        Administrador dona = colabs.criarAdministradorInicial("Stephanie Nicole", "Rua Principal, 10",
                "38 99999-0001", "ste@gymcorn.com", "123.456.789-01", "stephanie", "admin123", 8000);
        System.out.println("Administrador inicial: " + dona);
        try {
            clientes.cadastrar("Teste", "-", "-", "-", "000.000.000-00", "0000");
        } catch (AcessoNegadoExcecao e) {
            System.out.println("Sem login: " + e.getMessage());
        }
        System.out.println("Login com senha errada: " + sistema.login("stephanie", "errada"));
        System.out.println("Login correto: " + sistema.login("stephanie", "admin123"));
        System.out.println("Usuário logado: " + sistema.getSessao().getUsuarioLogado().getNome());

        // ------------------------------------------------------------------
        titulo("2. COLABORADORES E ADMINISTRADORES");
        Colaborador maria = colabs.cadastrarColaborador("Maria Augusta", "Rua B, 20", "38 98888-0002",
                "maria-augusta@gymcorn.com", "987.654.321-00", "maria", "1234", 2200, "Recepcionista");
        Colaborador bia = colabs.cadastrarColaborador("Bia Souza", "Rua C, 30", "38 97777-0003",
                "bia@gymcorn.com", "111.222.333-44", "bia", "abcd", 1800, "Atendente da lojinha");
        Administrador socio = colabs.cadastrarAdministrador("Mateus Verissimo", "Rua Principal, 10",
                "38 99999-0004", "mateus@gymcorn.com", "555.666.777-88", "mateus", "socio1", 6000);
        System.out.println(maria);
        System.out.println(bia);
        System.out.println(socio);
        try {
            colabs.cadastrarColaborador("Outro", "-", "-", "-", "-", "maria", "x", 1, "x");
        } catch (IllegalArgumentException e) {
            System.out.println("Login repetido: " + e.getMessage());
        }
        System.out.println("Editar Maria (salário 2400): "
                + colabs.editar(maria.getId(), null, "Rua B, 22", null, null, 2400.0, null));
        System.out.println(colabs.buscar(maria.getId()));
        System.out.println("Alterar senha do sócio (senha atual errada): "
                + colabs.alterarSenhaAdministrador(socio.getId(), "errada", "novaSenha"));
        System.out.println("Alterar senha do sócio: "
                + colabs.alterarSenhaAdministrador(socio.getId(), "socio1", "novaSenha"));
        System.out.println("Remover a si mesma: " + colabs.remover(dona.getId()));
        System.out.println("Remover sócio: " + colabs.remover(socio.getId()));
        System.out.println("Todos os funcionários: " + colabs.listarTodos());

        // ------------------------------------------------------------------
        titulo("3. CLIENTES");
        Cliente ana = clientes.cadastrar("Ana Turista", "Rua A, 1 - BH", "31 99999-0000",
                "ana@email.com", "123.123.123-45", "1234567812345678");
        Cliente pedro = clientes.cadastrar("Pedro Mensalista", "Rua D, 4 - Milho Verde",
                "38 96666-0000", "pedro@email.com", "321.321.321-09", "8765432187654321");
        Cliente carla = clientes.cadastrar(50, "Carla Viajante", "Av. E, 5 - SP",
                "11 95555-0000", "carla@email.com", "456.456.456-78", "1111222233334444");
        Cliente temp = clientes.cadastrar("Cliente Temporário", "-", "-", "-", "999.999.999-99", "0000");
        System.out.println(clientes.imprimirDados());
        try {
            clientes.cadastrar(50, "Repetido", "-", "-", "-", "-", "-");
        } catch (IllegalArgumentException e) {
            System.out.println("Id repetido: " + e.getMessage());
        }
        System.out.println("Editar telefone da Ana: "
                + clientes.editar(ana.getId(), null, null, "31 98888-1111", null, null));
        System.out.println("Buscar 'pedro': " + clientes.buscarPorNome("pedro"));
        System.out.println("Remover temporário: " + sistema.removerCliente(temp.getId()));
        System.out.println("Remover inexistente: " + sistema.removerCliente(999));

        // ------------------------------------------------------------------
        titulo("4. SALAS (ESTÁTICAS), INSTRUTORES E AULAS");
        for (Sala s : Sala.getSalas()) {
            System.out.println(s);
        }
        Instrutor marcos = aulas.cadastrarInstrutor("Marcos", "Rua F", "38 94444-0000",
                "marcos@email.com", "222.333.444-55", TipoAula.SPINNING, 2500);
        Instrutor lu = aulas.cadastrarInstrutor("Lu", "Rua G", "38 93333-0000",
                "lu@email.com", "666.777.888-99", TipoAula.MUSCULACAO, 2500);
        Instrutor rita = aulas.cadastrarInstrutor("Rita", "Rua H", "38 92222-0000",
                "rita@email.com", "777.888.999-00", TipoAula.PILATES, 2300);
        System.out.println(marcos + "\n" + lu + "\n" + rita);
        Aula spinning = aulas.cadastrarAula(TipoAula.SPINNING, marcos.getId(), LocalDateTime.of(2026, 11, 20, 8, 0));
        Aula musculacao = aulas.cadastrarAula(TipoAula.MUSCULACAO, lu.getId(), LocalDateTime.of(2026, 11, 16, 7, 0));
        Aula pilates = aulas.cadastrarAula(TipoAula.PILATES, rita.getId(), LocalDateTime.of(2026, 11, 20, 18, 0));
        System.out.println(spinning + "\n" + musculacao + "\n" + pilates);
        try {
            aulas.cadastrarAula(TipoAula.FIT_DANCE, marcos.getId(), LocalDateTime.of(2026, 11, 20, 9, 0));
        } catch (IllegalArgumentException e) {
            System.out.println("Aula inválida: " + e.getMessage());
        }
        System.out.println("Remover instrutor com aula: " + aulas.removerInstrutor(marcos.getId()));
        System.out.println("Aulas de spinning com vaga: " + aulas.listarComVaga(TipoAula.SPINNING));

        // ------------------------------------------------------------------
        titulo("5. AGENDAMENTOS");
        LocalDate hoje = LocalDate.of(2026, 11, 3);
        Agendamento agAna = agenda.realizar(ana.getId(), spinning.getId(),
                List.of(LocalDate.of(2026, 11, 20)), 80.0, TipoAgendamento.DIARIA, hoje);
        Agendamento agPedro = agenda.realizar(pedro.getId(), musculacao.getId(),
                List.of(LocalDate.of(2026, 11, 16)), 250.0, TipoAgendamento.MENSALIDADE, hoje);
        Agendamento agCarla = agenda.realizar(carla.getId(), pilates.getId(),
                List.of(LocalDate.of(2026, 11, 20)), 90.0, TipoAgendamento.DIARIA, hoje);
        Agendamento agAna2 = agenda.realizar(ana.getId(), pilates.getId(),
                List.of(LocalDate.of(2026, 11, 20)), 90.0, TipoAgendamento.DIARIA, hoje);
        System.out.println(agAna + "\n" + agPedro + "\n" + agCarla + "\n" + agAna2);
        System.out.println("Prazo de confirmação da Ana: " + agAna.getPrazoConfirmacao());
        System.out.println("Prazo de cancelamento com reembolso: " + agAna.getPrazoCancelamento());
        System.out.println("Vagas do spinning: " + spinning);
        try {
            agenda.realizar(ana.getId(), spinning.getId(),
                    List.of(LocalDate.of(2026, 11, 5)), 80.0, TipoAgendamento.DIARIA, hoje);
        } catch (IllegalArgumentException e) {
            System.out.println("Agendamento em cima da hora: " + e.getMessage());
        }

        System.out.println("\n-- Confirmações (cada uma gera e imprime um extrato) --");
        Extrato e1 = agenda.confirmar(agAna.getId(), LocalDate.of(2026, 11, 5));
        Extrato e2 = agenda.confirmar(agPedro.getId(), LocalDate.of(2026, 11, 5));
        Extrato e3 = agenda.confirmar(agCarla.getId(), LocalDate.of(2026, 11, 6));
        System.out.println("Extratos: " + e1 + ", " + e2 + ", " + e3);
        System.out.println("Confirmar de novo: " + agenda.confirmar(agAna.getId(), LocalDate.of(2026, 11, 6)));

        System.out.println("\n-- Cancelamento da Carla (retém 50%) --");
        double devolvido = agenda.cancelar(agCarla.getId(), LocalDate.of(2026, 11, 10));
        System.out.println("Devolvido para Carla: R$ " + String.format("%.2f", devolvido));
        System.out.println("Cancelar de novo: R$ " + String.format("%.2f", agenda.cancelar(agCarla.getId(), LocalDate.of(2026, 11, 10))));

        System.out.println("\n-- Expiração de preliminares não confirmados --");
        System.out.println("Expirados em 16/11: " + agenda.expirarPendentes(LocalDate.of(2026, 11, 16)));
        System.out.println(agAna2);
        System.out.println("Vagas do pilates: " + pilates);

        System.out.println("\n-- Comparators de Agendamento --");
        System.out.println(new AgendamentoPorData() + ": " + agenda.listar(new AgendamentoPorData()));
        System.out.println(new AgendamentoPorPreco() + ": " + agenda.listar(new AgendamentoPorPreco()));
        System.out.println(new AgendamentoPorStatus() + ": " + agenda.listar(new AgendamentoPorStatus()));
        System.out.println(new AgendamentoPorCliente() + ": " + agenda.listar(new AgendamentoPorCliente()));
        System.out.println("Agendamentos da Ana: " + agenda.doCliente(ana.getId()));
        System.out.println(agenda.imprimirVendas());

        // ------------------------------------------------------------------
        titulo("6. LOJINHA E LANCHONETE");
        sistema.logout();
        sistema.login("bia", "abcd");
        System.out.println("Logada: " + sistema.getSessao().getUsuarioLogado());
        Produto agua = lojaCtrl.cadastrarProduto("Água 500ml", "Sem gás", CategoriaProduto.LANCHONETE, 3.0, 50, 10);
        Produto barra = lojaCtrl.cadastrarProduto("Barra de proteína", "Chocolate", CategoriaProduto.LANCHONETE, 8.5, 12, 5);
        Produto luva = lojaCtrl.cadastrarProduto("Luva de treino", "Tamanho M", CategoriaProduto.LOJA, 45.0, 3, 2);
        Produto garrafa = lojaCtrl.cadastrarProduto("Garrafa 1L", "Logo da academia", CategoriaProduto.LOJA, 30.0, 10, 3);
        Lojinha loja = lojaCtrl.getLojinha();
        System.out.println(loja.getCatalogo().getProdutos());
        System.out.println("Verificar: " + lojaCtrl.verificarProduto(luva.getId()));
        System.out.println("Verificar inexistente: " + lojaCtrl.verificarProduto(99));

        Map<Integer, Integer> pedidoAna = new LinkedHashMap<>();
        pedidoAna.put(agua.getId(), 2);
        pedidoAna.put(barra.getId(), 1);
        Venda v1 = lojaCtrl.registrarVenda(ana.getId(), pedidoAna, LocalDateTime.of(2026, 11, 20, 9, 30));
        Map<Integer, Integer> pedidoPedro = new LinkedHashMap<>();
        pedidoPedro.put(luva.getId(), 2);
        pedidoPedro.put(agua.getId(), 1);
        Venda v2 = lojaCtrl.registrarVenda(pedro.getId(), pedidoPedro, LocalDateTime.of(2026, 11, 17, 8, 15));
        System.out.println(v1 + "\n" + v2);
        try {
            lojaCtrl.registrarVenda(pedro.getId(), Map.of(luva.getId(), 5), LocalDateTime.of(2026, 11, 18, 8, 0));
        } catch (EstoqueInsuficienteExcecao e) {
            System.out.println("Venda recusada: " + e.getMessage());
        }
        System.out.println("Verificar luva: " + lojaCtrl.verificarProduto(luva.getId()));
        System.out.println("Produtos para repor: " + loja.produtosParaRepor());
        System.out.println("Repor luvas: " + loja.reporEstoque(luva.getId(), 10));
        System.out.println("Verificar luva: " + lojaCtrl.verificarProduto(luva.getId()));
        System.out.println("Editar garrafa (R$ 35): "
                + loja.getCatalogo().editar(garrafa.getId(), "Garrafa 1L", "Logo da academia", CategoriaProduto.LOJA, 35.0));
        System.out.println("Buscar 'garrafa': " + loja.getCatalogo().buscarPorNome("garrafa"));
        System.out.println("Só loja: " + loja.getCatalogo().listarPorCategoria(CategoriaProduto.LOJA));
        System.out.println("Mais vendidos: " + loja.getHistoricoVendas().produtosMaisVendidos(3));
        System.out.println("Estoque: " + loja.getEstoque());
        System.out.println(loja);

        // ------------------------------------------------------------------
        titulo("7. CATRACA");
        System.out.println("Pedro entra 16/11 07:00: " + catraca.receberEvento(pedro.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 11, 16, 7, 0)));
        System.out.println("Pedro entra de novo: " + catraca.receberEvento(pedro.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 11, 16, 7, 5)));
        System.out.println("Pedro sai 16/11 08:10: " + catraca.receberEvento(pedro.getId(), TipoAcesso.SAIDA, LocalDateTime.of(2026, 11, 16, 8, 10)));
        System.out.println("Carla (cancelou) tenta entrar: " + catraca.receberEvento(carla.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 11, 20, 18, 0)));
        System.out.println("Ana tenta entrar no dia errado: " + catraca.receberEvento(ana.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 11, 19, 8, 0)));

        // Arquivo simulando a exportação do sistema de catraca externo.
        String arquivoCatraca = "dados/catraca_externa.txt";
        Persistencia.salvarTexto(arquivoCatraca, String.join("\n",
                "# idCliente;tipo;dataHora",
                ana.getId() + ";ENTRADA;2026-11-20T07:50",
                pedro.getId() + ";ENTRADA;2026-11-17T07:55",
                pedro.getId() + ";SAIDA;2026-11-17T09:00",
                pedro.getId() + ";ENTRADA;2026-11-20T08:05",
                ana.getId() + ";SAIDA;2026-11-20T09:45",
                pedro.getId() + ";ENTRADA;2026-12-20T08:00",
                "linha;quebrada",
                ana.getId() + ";ENTRADA;2026-11-20T17:55"));
        System.out.println("Eventos importados da catraca: " + catraca.importarArquivo(arquivoCatraca));
        System.out.println("Pessoas na academia agora: " + catraca.quantidadePresentes());
        System.out.println("Quem está dentro: " + catraca.clientesPresentes());
        System.out.println("Histórico do Pedro: " + catraca.historico(pedro.getId()));
        System.out.println(catraca.relatorioDiario(LocalDate.of(2026, 11, 20)));
        System.out.println(catraca.relatorioMensal(2026, 11));
        System.out.println(catraca.relatorioAnual(2026));

        // ------------------------------------------------------------------
        titulo("7.1 PADRÃO STATE: CONFIRMADO -> REALIZADO");
        System.out.println("Concluídos em 21/11 (aula já aconteceu): "
                + agenda.concluirRealizados(LocalDate.of(2026, 11, 21)));
        System.out.println(agAna);
        System.out.println("Cancelar agendamento realizado devolve: R$ "
                + String.format("%.2f", agenda.cancelar(agAna.getId(), LocalDate.of(2026, 11, 21))) + " (status " + agAna.getStatus() + ")");
        System.out.println("Mensalidade do Pedro vale até " + agPedro.getUltimaData()
                + ", status: " + agPedro.getStatus());

        // ------------------------------------------------------------------
        titulo("8. RELATÓRIOS DE VENDAS E CONTA DO CLIENTE");
        System.out.println(financeiro.relatorioVendasDia(LocalDate.of(2026, 11, 5)));
        System.out.println(financeiro.relatorioVendasMes(2026, 11));
        System.out.println(clientes.emitirConta(ana.getId()));
        System.out.println(clientes.emitirConta(carla.getId()));

        System.out.println("-- Comparators de Cliente --");
        System.out.println(new ClientePorNome() + ": " + nomes(clientes.listar(new ClientePorNome())));
        System.out.println(new ClientePorId() + ": " + nomes(clientes.listar(new ClientePorId())));
        System.out.println(new ClientePorQuantidadeAgendamentos() + ": "
                + nomes(clientes.listar(new ClientePorQuantidadeAgendamentos())));
        System.out.println(new ClientePorTotalGasto() + ": " + nomes(clientes.listar(new ClientePorTotalGasto())));

        // ------------------------------------------------------------------
        titulo("9. PERMISSÕES: COLABORADOR NÃO ACESSA O FINANCEIRO");
        try {
            financeiro.lancarDespesa("Tentativa", CategoriaDespesa.OUTROS, 10, LocalDate.of(2026, 11, 1));
        } catch (AcessoNegadoExcecao e) {
            System.out.println("Despesa: " + e.getMessage());
        }
        try {
            financeiro.gerarBalancoMensal(2026, 11);
        } catch (AcessoNegadoExcecao e) {
            System.out.println("Balanço: " + e.getMessage());
        }
        try {
            colabs.cadastrarAdministrador("X", "-", "-", "-", "-", "x", "x", 1);
        } catch (AcessoNegadoExcecao e) {
            System.out.println("Novo administrador: " + e.getMessage());
        }
        System.out.println("Bia troca a própria senha: " + colabs.alterarMinhaSenha("abcd", "nova123"));

        // ------------------------------------------------------------------
        titulo("10. DESPESAS E BALANÇO MENSAL (ADMINISTRADORA)");
        sistema.logout();
        sistema.login("stephanie", "admin123");
        Despesa limpeza = financeiro.lancarDespesa("Produtos de limpeza", CategoriaDespesa.LIMPEZA, 350, LocalDate.of(2026, 11, 2));
        financeiro.lancarDespesa("Toalhas e álcool", CategoriaDespesa.MATERIAL, 180, LocalDate.of(2026, 11, 9));
        financeiro.lancarDespesa("Conta de luz", CategoriaDespesa.CONTAS, 620, LocalDate.of(2026, 11, 10));
        Despesa errada = financeiro.lancarDespesa("Lançada por engano", CategoriaDespesa.OUTROS, 99, LocalDate.of(2026, 11, 11));
        System.out.println("Editar limpeza (R$ 400): " + financeiro.editarDespesa(limpeza.getId(), null, null, 400.0, null));
        System.out.println("Remover despesa errada: " + financeiro.removerDespesa(errada.getId()));
        System.out.println("Folha de pagamento lançada: " + financeiro.lancarFolhaDePagamento(2026, 11) + " salários");
        for (Despesa d : financeiro.despesasDoMes(2026, 11)) {
            System.out.println(d);
        }
        System.out.println(financeiro.gerarBalancoMensal(2026, 11));

        // ------------------------------------------------------------------
        titulo("11. CONTADORES DE INSTÂNCIAS (VARIÁVEIS E MÉTODO DE CLASSE)");
        System.out.println("Clientes (private static + get): " + Sistema.getTotalClientesCriados());
        System.out.println("Clientes (protected static):     " + Sistema.getContadorClientesProtected());
        System.out.println("Produtos:                        " + Sistema.getTotalProdutosCriados());
        System.out.println("contarInstancias(): " + Sistema.contarInstancias());
        try {
            Sistema.setTotalClientesCriados(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("set com valor negativo bloqueado: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        titulo("12. SALVAR E RECUPERAR EM JSON");
        sistema.salvarDados();
        System.out.println("Dados salvos em 'dados/'. " + sistema);

        Sistema recuperado = new Sistema("dados");
        recuperado.carregarDados();
        System.out.println("Recuperado:            " + recuperado);
        System.out.println("Login da Bia com a senha nova após carregar: " + recuperado.login("bia", "nova123"));
        System.out.println(recuperado.getCadastroClientes().imprimirDados());
        System.out.println("Extratos da Ana recuperados: " + recuperado.getCadastroClientes().buscar(ana.getId()).getExtratos());
        System.out.println("Estoque recuperado: " + recuperado.getControleLojinha().getLojinha().getEstoque());
        System.out.println("Relatórios de vendas recuperados: " + recuperado.getFinanceiro().getRelatoriosVendas().size());
        System.out.println("Funcionários recuperados: " + recuperado.getCadastroColaboradores().listarTodos());
        Agendamento agRecuperado = recuperado.getControleAgendamentos().buscar(agAna.getId());
        System.out.println("Estado recriado do JSON pela EstadoFactory: " + agRecuperado.getStatus()
                + " | tentar confirmar de novo: " + agRecuperado.confirmar(LocalDate.of(2026, 11, 21)));
        System.out.println("Stephanie continua administradora? "
                + recuperado.getCadastroColaboradores().buscar(dona.getId()).podeGerenciarFinanceiro());
        System.out.println("Contadores após carregar (não mudam): " + Sistema.contarInstancias());

        // ------------------------------------------------------------------
        // Chamadas diretas de funções que nas seções anteriores só eram
        // usadas por dentro de outras.
        titulo("13. OUTRAS FUNÇÕES");
        System.out.println("Alguém logado? " + recuperado.getSessao().estaLogado()
                + " (" + recuperado.getSessao().getUsuarioLogado().getNome() + ")");
        System.out.println("Sala 2: " + Sala.buscarPorId(2));
        System.out.println("Sala de pilates: " + Sala.buscarPorTipo(TipoAula.PILATES));
        System.out.println("CPF anonimizado: " + Pessoa.anonimizarCpf("123.456.789-01"));
        System.out.println("Estado criado pela factory: " + EstadoFactory.fromStatus(StatusAgendamento.CONFIRMADO));

        Agendamento simulacao = new Agendamento(99, ana.getId(), pilates.getId(),
                List.of(LocalDate.of(2026, 12, 20)), 100.0, TipoAgendamento.DIARIA, LocalDate.of(2026, 12, 1));
        simulacao.confirmar(LocalDate.of(2026, 12, 2));
        System.out.println("Reembolso se cancelar em 10/12: R$ "
                + String.format("%.2f", simulacao.calcularReembolso(LocalDate.of(2026, 12, 10))));
        System.out.println("Reembolso se cancelar em 19/12: R$ "
                + String.format("%.2f", simulacao.calcularReembolso(LocalDate.of(2026, 12, 19))));
        System.out.println("Cobre 20/12? " + simulacao.cobreData(LocalDate.of(2026, 12, 20))
                + " | cobre 21/12? " + simulacao.cobreData(LocalDate.of(2026, 12, 21)));

        System.out.println("Ana antes: agendamentos " + ana.getIdsAgendamentos()
                + " | remove o 4 (cancelado): " + ana.removerAgendamento(4)
                + " | depois: " + ana.getIdsAgendamentos());

        Lojinha lojaRec = recuperado.getControleLojinha().getLojinha();
        System.out.println("Remover produto Garrafa: " + lojaRec.removerProduto(garrafa.getId()));
        System.out.println("Remover produto inexistente: " + lojaRec.removerProduto(99));
        System.out.println("Produtos restantes: " + lojaRec.getCatalogo().getProdutos());
        System.out.println("Faturamento total da lojinha: R$ "
                + String.format("%.2f", lojaRec.getHistoricoVendas().faturamentoTotal()));

        recuperado.logout();
        System.out.println("Depois do logout, alguém logado? " + recuperado.getSessao().estaLogado());
        
        
        // ==================================================================
        // Questão 18
        // ==================================================================
        {
            titulo("QUESTÃO 18 - ITERATOR E FOREACH");
            // O ArrayList implementa a interface Iterable, então sabe criar um
            // Iterator: um objeto que "aponta" para a posição atual da lista.
            // hasNext() pergunta se ainda há elemento; next() devolve o
            // elemento e avança para o próximo.
            List<Cliente> listaClientes = new ArrayList<>(clientes.getTodos());
            Iterator<Cliente> iterator = listaClientes.iterator();
            System.out.println("-- Percorrendo com Iterator (while hasNext / next) --");
            while (iterator.hasNext()) {
                imprimir(iterator.next());
            }

            // O foreach é uma forma resumida de escrever o mesmo laço: o
            // compilador o transforma no código acima (cria o Iterator e chama
            // hasNext() e next() automaticamente).
            System.out.println("-- Percorrendo com foreach --");
            for (Cliente c : listaClientes) {
                imprimir(c);
            }

            // Diferença prática: com o Iterator dá para remover com segurança
            // durante o percurso; no foreach, remover da lista lança
            // ConcurrentModificationException.
            Iterator<Cliente> it2 = listaClientes.iterator();
            while (it2.hasNext()) {
                if (it2.next().calcularTotalConta() < 50) {
                    it2.remove();
                }
            }
            System.out.println("Iterator.remove() tirou da cópia quem gastou menos de R$ 50: " + nomes(listaClientes));
            try {
                for (Cliente c : listaClientes) {
                    if (c.getNome().startsWith("Ana")) {
                        listaClientes.remove(c);
                    }
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("Remover dentro do foreach: ConcurrentModificationException");
            }
        }

        // ==================================================================
        // Questão 19
        // ==================================================================
        {
            titulo("QUESTÃO 19 - COMPARATOR E COLLECTIONS.SORT");
            Comparator<Cliente> porNome = new ClientePorNome();
            Comparator<Cliente> porGasto = new ClientePorTotalGasto();
            System.out.println("porNome.compare(Ana, Pedro)  = " + porNome.compare(ana, pedro) + " (negativo: Ana vem antes)");
            System.out.println("porNome.compare(Pedro, Ana)  = " + porNome.compare(pedro, ana) + " (positivo: Pedro vem depois)");
            System.out.println("porNome.compare(Ana, Ana)    = " + porNome.compare(ana, ana) + " (zero: empate)");
            System.out.println("porGasto.compare(Ana, Pedro) = " + porGasto.compare(ana, pedro) + " (Pedro gastou mais)");

            List<Cliente> lista = new ArrayList<>(clientes.getTodos());
            Collections.sort(lista, porNome);
            System.out.println("1ª ordenação - Collections.sort(lista, new ClientePorNome()):");
            for (Cliente c : lista) {
                imprimir(c.getNome());
            }
            Collections.sort(lista, porGasto);
            System.out.println("2ª ordenação - Collections.sort(lista, new ClientePorTotalGasto()):");
            for (Cliente c : lista) {
                imprimir(c.getNome() + " - R$ " + String.format("%.2f", c.calcularTotalConta()));
            }

            List<Agendamento> ags = new ArrayList<>(agenda.getAgendamentos());
            Collections.sort(ags, new AgendamentoPorPreco());
            System.out.println("Agendamentos - Collections.sort(..., new AgendamentoPorPreco()): ");
            for (Agendamento a : ags) {
                imprimir("nº " + a.getId() + " R$ " + a.getPreco() + " " + a.getStatus());
            }
            Collections.sort(ags, new AgendamentoPorStatus());
            System.out.println("Agendamentos - Collections.sort(..., new AgendamentoPorStatus()): ");
            for (Agendamento a : ags) {
                imprimir("nº " + a.getId() + " " + a.getStatus() + " " + a.getPrimeiraData());
            }
        }

        // ==================================================================
        // Questão 20
        // ==================================================================
        {
            titulo("QUESTÃO 20 - ENCONTRAR (ITERATOR + COMPARATOR) x BINARYSEARCH");
            Comparator<Cliente> porNome = new ClientePorNome();
            Comparator<Cliente> porId = new ClientePorId();
            // A chave é um Cliente "modelo" só com o campo procurado preenchido.
            // (Ela também é uma instância de Cliente, por isso entra nos contadores.)
            Cliente chaveNome = new Cliente(0, "Carla Viajante", "", "", "", "", "");
            Cliente chaveId = new Cliente(2, "", "", "", "", "", "");
            Cliente chaveInexistente = new Cliente(0, "Zeca", "", "", "", "", "");

            System.out.println("encontrar(chave nome 'Carla Viajante', ClientePorNome): " + clientes.encontrar(chaveNome, porNome));
            System.out.println("encontrar(chave id 2, ClientePorId): " + clientes.encontrar(chaveId, porId));
            System.out.println("encontrar(chave nome 'Zeca', ClientePorNome): " + clientes.encontrar(chaveInexistente, porNome));

            // binarySearch exige a lista ORDENADA pelo mesmo Comparator.
            List<Cliente> ordenadaPorNome = clientes.listar(porNome);
            int pos = Collections.binarySearch(ordenadaPorNome, chaveNome, porNome);
            System.out.println("binarySearch(lista ordenada por nome, 'Carla Viajante') = posição " + pos
                    + " -> " + ordenadaPorNome.get(pos).getNome());
            int posZeca = Collections.binarySearch(ordenadaPorNome, chaveInexistente, porNome);
            System.out.println("binarySearch(..., 'Zeca') = " + posZeca
                    + " (negativo: não achou; entraria na posição " + (-posZeca - 1) + ")");
            List<Cliente> ordenadaPorId = clientes.listar(porId);
            int posId = Collections.binarySearch(ordenadaPorId, chaveId, porId);
            System.out.println("binarySearch(lista ordenada por id, id 2) = posição " + posId
                    + " -> " + ordenadaPorId.get(posId).getNome());

            System.out.println("Comparação:");
            System.out.println("  encontrar(): busca sequencial com Iterator, funciona com a lista em qualquer ordem,");
            System.out.println("               devolve o próprio cliente (ou null); no pior caso olha todos (O(n)).");
            System.out.println("  binarySearch(): divide a lista ao meio a cada passo (O(log n)), mas exige a lista");
            System.out.println("               ordenada pelo mesmo critério e devolve a POSIÇÃO (negativa se não achar).");
        }

        // ==================================================================
        // Questão 21
        // ==================================================================
        cenarioDezClientes();
    }

        /** Imprime um objeto com recuo (usado nos testes do Iterator). */
    private static void imprimir(Object objeto) {
        System.out.println("  " + objeto);
    }

    /** Imprime o título de uma etapa do cenário da Questão 21. */
    private static void etapa(String texto) {
        System.out.println("\n--- " + texto + " ---");
    }

    /**
     * Questão 21: atendimento de 10 clientes, cada um com um caminho
     * diferente, do cadastro até a nota fiscal ou o cancelamento da
     * matrícula. Usa um sistema separado (pasta "dados_cenario") e datas de
     * dezembro de 2026.
     *
     * @throws IOException se houver erro ao salvar os dados do cenário
     */
    private static void cenarioDezClientes() throws IOException {
        titulo("QUESTÃO 21 - CENÁRIO COM 10 CLIENTES");
        Sistema s = new Sistema("dados_cenario");
        CadastroColaboradores colabs = s.getCadastroColaboradores();
        CadastroClientes clientes = s.getCadastroClientes();
        CadastroAulas aulas = s.getCadastroAulas();
        ControleAgendamentos agenda = s.getControleAgendamentos();
        ControleLojinha lojaCtrl = s.getControleLojinha();
        ControleCatraca catraca = s.getControleCatraca();
        Financeiro financeiro = s.getFinanceiro();

        // ----------------------------------------------------- preparação
        etapa("Preparação: administradora, recepcionista, aulas e produtos");
        colabs.criarAdministradorInicial("Stephanie Nicole", "Rua Principal, 10", "38 99999-0001",
                "ste@gymcorn.com", "123.456.789-01", "stephanie", "admin123", 8000);
        s.login("stephanie", "admin123");
        colabs.cadastrarColaborador("Maria Augusta", "Rua B, 20", "38 98888-0002",
                "maria@gymcorn.com", "987.654.321-00", "maria", "1234", 2200, "Recepcionista");
        s.logout();
        System.out.println("Recepcionista entra no sistema: " + s.login("maria", "1234"));
        Instrutor marcos = aulas.cadastrarInstrutor("Marcos", "Rua F", "38 94444-0000", "marcos@gymcorn.com", "222.333.444-55", TipoAula.SPINNING, 2500);
        Instrutor lu = aulas.cadastrarInstrutor("Lu", "Rua G", "38 93333-0000", "lu@gymcorn.com", "666.777.888-99", TipoAula.MUSCULACAO, 2500);
        Instrutor rita = aulas.cadastrarInstrutor("Rita", "Rua H", "38 92222-0000", "rita@gymcorn.com", "777.888.999-00", TipoAula.PILATES, 2300);
        Instrutor duda = aulas.cadastrarInstrutor("Duda", "Rua I", "38 91111-0000", "duda@gymcorn.com", "111.999.888-77", TipoAula.FIT_DANCE, 2300);
        Aula spinning = aulas.cadastrarAula(TipoAula.SPINNING, marcos.getId(), LocalDateTime.of(2026, 12, 21, 8, 0));
        Aula musculacao = aulas.cadastrarAula(TipoAula.MUSCULACAO, lu.getId(), LocalDateTime.of(2026, 12, 14, 7, 0));
        Aula pilates = aulas.cadastrarAula(TipoAula.PILATES, rita.getId(), LocalDateTime.of(2026, 12, 21, 18, 0));
        Aula fitDance = aulas.cadastrarAula(TipoAula.FIT_DANCE, duda.getId(), LocalDateTime.of(2026, 12, 18, 19, 0));
        Produto agua = lojaCtrl.cadastrarProduto("Água 500ml", "Sem gás", CategoriaProduto.LANCHONETE, 3.0, 40, 10);
        Produto barra = lojaCtrl.cadastrarProduto("Barra de proteína", "Chocolate", CategoriaProduto.LANCHONETE, 8.5, 20, 5);
        Produto luva = lojaCtrl.cadastrarProduto("Luva de treino", "Tamanho M", CategoriaProduto.LOJA, 45.0, 2, 2);
        Produto toalha = lojaCtrl.cadastrarProduto("Toalha GYM CORN", "Microfibra", CategoriaProduto.LOJA, 25.0, 8, 3);
        System.out.println("Aulas: " + aulas.getAulas().size() + " | Produtos: " + lojaCtrl.getLojinha().getCatalogo().getProdutos().size());

        // ----------------------------------------------------- 1. cadastro
        etapa("1. Cadastro dos 10 clientes");
        Cliente lucas = clientes.cadastrar("Lucas Turista", "Rua 1 - BH", "31 90000-0001", "lucas@email.com", "100.200.300-01", "4111111111110001");
        Cliente marina = clientes.cadastrar("Marina Mensalista", "Rua 2 - Milho Verde", "38 90000-0002", "marina@email.com", "100.200.300-02", "4111111111110002");
        Cliente rafael = clientes.cadastrar("Rafael Indeciso", "Rua 3 - Serro", "38 90000-0003", "rafael@email.com", "100.200.300-03", "4111111111110003");
        Cliente julia = clientes.cadastrar("Júlia Viajante", "Rua 4 - SP", "11 90000-0004", "julia@email.com", "100.200.300-04", "4111111111110004");
        Cliente bruno = clientes.cadastrar("Bruno Atrasado", "Rua 5 - RJ", "21 90000-0005", "bruno@email.com", "100.200.300-05", "4111111111110005");
        Cliente camila = clientes.cadastrar("Camila Dançarina", "Rua 6 - Diamantina", "38 90000-0006", "camila@email.com", "100.200.300-06", "4111111111110006");
        Cliente diego = clientes.cadastrar("Diego Apressado", "Rua 7 - BH", "31 90000-0007", "diego@email.com", "100.200.300-07", "4111111111110007");
        Cliente fernanda = clientes.cadastrar("Fernanda Desistente", "Rua 8 - Milho Verde", "38 90000-0008", "fernanda@email.com", "100.200.300-08", "4111111111110008");
        Cliente gustavo = clientes.cadastrar(100, "Gustavo Esquecido", "Rua 9 - Curvelo", "38 90000-0009", "gustavo@email.com", "100.200.300-09", "4111111111110009");
        Cliente isabela = clientes.cadastrar("Isabela Turista", "Rua 10 - Vitória", "27 90000-0010", "isabela@email.com", "100.200.300-10", "4111111111110010");
        System.out.println("Gustavo recebeu o id escolhido: " + gustavo.getId());
        System.out.println("Isabela atualiza o telefone: " + clientes.editar(isabela.getId(), null, null, "27 98888-0010", null, null));
        System.out.println("Clientes cadastrados: " + nomes(clientes.listar(new ClientePorNome())));

        // ----------------------------------------------------- 2. agendamentos
        etapa("2. Agendamentos (03/12)");
        LocalDate d03 = LocalDate.of(2026, 12, 3);
        Agendamento agLucas = agenda.realizar(lucas.getId(), spinning.getId(), List.of(LocalDate.of(2026, 12, 21)), 80, TipoAgendamento.DIARIA, d03);
        Agendamento agMarina = agenda.realizar(marina.getId(), musculacao.getId(), List.of(LocalDate.of(2026, 12, 14)), 250, TipoAgendamento.MENSALIDADE, d03);
        Agendamento agRafael = agenda.realizar(rafael.getId(), fitDance.getId(), List.of(LocalDate.of(2026, 12, 18)), 70, TipoAgendamento.DIARIA, d03);
        Agendamento agJulia = agenda.realizar(julia.getId(), pilates.getId(), List.of(LocalDate.of(2026, 12, 21)), 90, TipoAgendamento.DIARIA, d03);
        Agendamento agBruno = agenda.realizar(bruno.getId(), spinning.getId(), List.of(LocalDate.of(2026, 12, 21)), 80, TipoAgendamento.DIARIA, d03);
        Agendamento agCamila = agenda.realizar(camila.getId(), fitDance.getId(),
                List.of(LocalDate.of(2026, 12, 18), LocalDate.of(2026, 12, 19)), 130, TipoAgendamento.DIARIA, d03);
        try {
            agenda.realizar(diego.getId(), spinning.getId(), List.of(LocalDate.of(2026, 12, 4)), 80, TipoAgendamento.DIARIA, d03);
        } catch (IllegalArgumentException e) {
            System.out.println("Diego tentou agendar para o dia seguinte: " + e.getMessage());
        }
        Agendamento agDiego = agenda.realizar(diego.getId(), spinning.getId(), List.of(LocalDate.of(2026, 12, 21)), 80, TipoAgendamento.DIARIA, d03);
        Agendamento agFernanda = agenda.realizar(fernanda.getId(), musculacao.getId(), List.of(LocalDate.of(2026, 12, 14)), 250, TipoAgendamento.MENSALIDADE, d03);
        Agendamento agGustavo = agenda.realizar(gustavo.getId(), pilates.getId(), List.of(LocalDate.of(2026, 12, 21)), 90, TipoAgendamento.DIARIA, d03);
        Agendamento agIsabela = agenda.realizar(isabela.getId(), spinning.getId(), List.of(LocalDate.of(2026, 12, 21)), 80, TipoAgendamento.DIARIA, d03);
        System.out.println("Agendamentos preliminares criados: " + agenda.getAgendamentos().size());
        System.out.println("Vagas do spinning: " + spinning.getVagasOcupadas() + "/" + spinning.getSala().getCapacidade());
        System.out.println("Camila agendou dois dias de fit dance: " + agCamila.getDatas());

        // ----------------------------------------------------- 3. confirmações
        etapa("3. Confirmações e débito no cartão (04/12)");
        LocalDate d04 = LocalDate.of(2026, 12, 4);
        for (Agendamento ag : List.of(agLucas, agMarina, agJulia, agBruno, agCamila, agDiego, agFernanda, agIsabela)) {
            Extrato e = agenda.confirmar(ag.getId(), d04);
            System.out.println("Confirmado agendamento nº " + ag.getId() + " -> extrato nº " + e.getId());
        }
        System.out.println("Rafael não confirmou: " + agRafael.getStatus());
        System.out.println("Gustavo tenta confirmar em 15/12 (prazo era " + agGustavo.getPrazoConfirmacao() + "): "
                + (agenda.confirmar(agGustavo.getId(), LocalDate.of(2026, 12, 15)) != null ? "confirmado" : "recusado"));

        // ----------------------------------------------------- 4. cancelamentos
        etapa("4. Cancelamentos e expirações");
        System.out.println("Fernanda cancela a matrícula em 10/12 (antes do prazo de reembolso): devolvido R$ "
                + String.format("%.2f", s.cancelarMatricula(fernanda.getId(), LocalDate.of(2026, 12, 10))));
        System.out.println("Expirados em 12/12: " + agenda.expirarPendentes(LocalDate.of(2026, 12, 12)) + " (Rafael)");
        System.out.println("Júlia cancela em 15/12 (até 3 dias antes): devolvido R$ "
                + String.format("%.2f", agenda.cancelar(agJulia.getId(), LocalDate.of(2026, 12, 15))));
        System.out.println("Expirados em 15/12: " + agenda.expirarPendentes(LocalDate.of(2026, 12, 15)) + " (Gustavo)");
        System.out.println("Bruno cancela em 20/12 (menos de 3 dias): devolvido R$ "
                + String.format("%.2f", agenda.cancelar(agBruno.getId(), LocalDate.of(2026, 12, 20))));
        System.out.println("Vagas do spinning após o cancelamento do Bruno: " + spinning.getVagasOcupadas());

        // ----------------------------------------------------- 5. catraca
        etapa("5. Acesso às aulas pela catraca");
        for (int dia = 14; dia <= 16; dia++) {
            catraca.receberEvento(marina.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, dia, 6, 50));
            catraca.receberEvento(marina.getId(), TipoAcesso.SAIDA, LocalDateTime.of(2026, 12, dia, 8, 5));
        }
        System.out.println("Marina (mensalista) treinou 3 dias: " + catraca.historico(marina.getId()).size() + " marcações");
        System.out.println("Camila entra 18/12: " + (catraca.receberEvento(camila.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 18, 18, 50)) != null));
        System.out.println("Camila passa de novo sem sair: " + (catraca.receberEvento(camila.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 18, 18, 55)) != null));
        System.out.println("Pessoas na academia em 18/12: " + catraca.quantidadePresentes());
        catraca.receberEvento(camila.getId(), TipoAcesso.SAIDA, LocalDateTime.of(2026, 12, 18, 20, 10));
        catraca.receberEvento(camila.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 19, 18, 45));
        catraca.receberEvento(camila.getId(), TipoAcesso.SAIDA, LocalDateTime.of(2026, 12, 19, 20, 0));
        System.out.println("Rafael (expirado) tenta entrar 18/12: " + (catraca.receberEvento(rafael.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 18, 18, 55)) != null));
        System.out.println("Isabela tenta entrar no dia errado (20/12): " + (catraca.receberEvento(isabela.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 20, 8, 0)) != null));
        for (Cliente c : List.of(lucas, diego, isabela)) {
            catraca.receberEvento(c.getId(), TipoAcesso.ENTRADA, LocalDateTime.of(2026, 12, 21, 7, 50));
        }
        System.out.println("Pessoas na academia em 21/12 às 8h: " + catraca.quantidadePresentes() + " " + nomes(catraca.clientesPresentes()));
        for (Cliente c : List.of(lucas, diego, isabela)) {
            catraca.receberEvento(c.getId(), TipoAcesso.SAIDA, LocalDateTime.of(2026, 12, 21, 9, 10));
        }
        System.out.println(catraca.relatorioMensal(2026, 12));

        // ----------------------------------------------------- 6. lojinha
        etapa("6. Vendas na lojinha e baixas no estoque");
        lojaCtrl.registrarVenda(marina.getId(), Map.of(luva.getId(), 1), LocalDateTime.of(2026, 12, 15, 8, 10));
        Map<Integer, Integer> pedidoCamila = new LinkedHashMap<>();
        pedidoCamila.put(barra.getId(), 2);
        pedidoCamila.put(toalha.getId(), 1);
        lojaCtrl.registrarVenda(camila.getId(), pedidoCamila, LocalDateTime.of(2026, 12, 18, 20, 15));
        try {
            lojaCtrl.registrarVenda(diego.getId(), Map.of(luva.getId(), 5), LocalDateTime.of(2026, 12, 21, 9, 15));
        } catch (EstoqueInsuficienteExcecao e) {
            System.out.println("Diego quer 5 luvas: " + e.getMessage());
        }
        System.out.println("Produtos para repor: " + lojaCtrl.getLojinha().produtosParaRepor());
        System.out.println("Reposição de 10 luvas: " + lojaCtrl.getLojinha().reporEstoque(luva.getId(), 10));
        lojaCtrl.registrarVenda(diego.getId(), Map.of(luva.getId(), 1), LocalDateTime.of(2026, 12, 21, 9, 20));
        lojaCtrl.registrarVenda(lucas.getId(), Map.of(agua.getId(), 2), LocalDateTime.of(2026, 12, 21, 9, 15));
        System.out.println("Estoque de luvas: " + lojaCtrl.verificarProduto(luva.getId()));
        System.out.println("Mais vendidos: " + lojaCtrl.getLojinha().getHistoricoVendas().produtosMaisVendidos(3));

        // ----------------------------------------------------- 7. conclusão das aulas
        etapa("7. Aulas concluídas (22/12)");
        System.out.println("Agendamentos que passaram a Realizado: " + agenda.concluirRealizados(LocalDate.of(2026, 12, 22)));
        for (Agendamento ag : agenda.listar(new AgendamentoPorCliente())) {
            imprimir("nº " + ag.getId() + " cliente " + ag.getIdCliente() + " - " + ag.getTipo() + " - " + ag.getStatus());
        }

        // ----------------------------------------------------- 8. fechamento
        etapa("8. Fechamento: notas fiscais e cancelamentos de matrícula");
        System.out.println("Rafael cancela a matrícula (não confirmou): devolvido R$ "
                + String.format("%.2f", s.cancelarMatricula(rafael.getId(), LocalDate.of(2026, 12, 22))));
        System.out.println("Gustavo cancela a matrícula (perdeu o prazo): devolvido R$ "
                + String.format("%.2f", s.cancelarMatricula(gustavo.getId(), LocalDate.of(2026, 12, 22))));
        for (Cliente c : List.of(lucas, marina, julia, bruno, camila, diego, isabela)) {
            s.emitirNotaFiscal(c.getId(), LocalDate.of(2026, 12, 22));
        }
        System.out.println("Clientes que continuam cadastrados: " + nomes(clientes.listar(new ClientePorNome())));

        // ----------------------------------------------------- 9. resultado do mês
        etapa("9. Resultado de dezembro");
        System.out.println(financeiro.relatorioVendasMes(2026, 12));
        s.logout();
        s.login("stephanie", "admin123");
        financeiro.lancarDespesa("Produtos de limpeza", CategoriaDespesa.LIMPEZA, 300, LocalDate.of(2026, 12, 2));
        financeiro.lancarDespesa("Conta de luz", CategoriaDespesa.CONTAS, 580, LocalDate.of(2026, 12, 10));
        System.out.println(financeiro.gerarBalancoMensal(2026, 12));
        s.salvarDados();
        System.out.println("Cenário salvo em 'dados_cenario/'.");
    }

    
    /**
     * Lista só os nomes, para a saída ficar curta.
     */
    private static List<String> nomes(List<Cliente> clientes) {
        return clientes.stream().map(Cliente::getNome).toList();
    }

    @Override
    public String toString() {
        return "Academia{classe principal do sistema GYM CORN - Milho Verde}";
    }
}