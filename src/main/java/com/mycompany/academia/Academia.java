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
                "38 99999-0001", "Ste@gymcorn.com", "123.456.789-01", "stephanie", "admin123", 8000);
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
        // usadas por dentro de outras (item "testar todas as funções").
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