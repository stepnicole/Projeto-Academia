package com.mycompany.academia.controller;

import com.mycompany.academia.model.Cliente;
import com.mycompany.academia.model.Persistencia;
import com.mycompany.academia.model.catraca.Catraca;
import com.mycompany.academia.model.catraca.RegistroAcesso;
import com.mycompany.academia.model.catraca.RelatorioAcesso;
import com.mycompany.academia.model.enums.TipoAcesso;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Integração com a catraca externa. Recebe as
 * marcações (uma a uma ou por arquivo), confere se o cliente pode entrar e
 * repassa para o subsistema {@link Catraca}, que guarda os registros e gera
 * os relatórios.
 *
 * @author Ste
 */
public class ControleCatraca {

    private final Sessao sessao;
    private final Catraca catraca;
    private final CadastroClientes clientes;
    private final ControleAgendamentos agendamentos;

    /**
     * @param sessao       sessão (login para consultas)
     * @param catraca      subsistema catraca
     * @param clientes     cadastro de clientes
     * @param agendamentos para saber se o cliente tem acesso no dia
     */
    public ControleCatraca(Sessao sessao, Catraca catraca, CadastroClientes clientes,
                           ControleAgendamentos agendamentos) {
        this.sessao = sessao;
        this.catraca = catraca;
        this.clientes = clientes;
        this.agendamentos = agendamentos;
    }

    /**
     * Recebe uma marcação da catraca externa. Não exige login, pois é chamada
     * pela integração, e não por um usuário. A entrada só é aceita se o
     * cliente existe e tem diária/mensalidade confirmada para o dia.
     *
     * @param idCliente cliente
     * @param tipo      entrada ou saída
     * @param dataHora  momento
     * @return o registro, ou null se a marcação foi recusada
     */
    public RegistroAcesso receberEvento(int idCliente, TipoAcesso tipo, LocalDateTime dataHora) {
        if (clientes.buscar(idCliente) == null) {
            return null;
        }
        if (tipo == TipoAcesso.ENTRADA && !agendamentos.possuiAcessoValido(idCliente, dataHora.toLocalDate())) {
            return null;
        }
        return catraca.receberEvento(idCliente, tipo, dataHora);
    }

    /**
     * Importa o arquivo exportado pela catraca externa. Cada linha:
     * {@code idCliente;ENTRADA|SAIDA;aaaa-mm-ddThh:mm}. Linhas vazias ou com #
     * são ignoradas; os eventos são processados em ordem de horário.
     *
     * @param caminho arquivo de texto
     * @return quantos eventos foram aceitos
     * @throws IOException se o arquivo não puder ser lido
     */
    public int importarArquivo(String caminho) throws IOException {
        List<String[]> eventos = new ArrayList<>();
        for (String linha : Persistencia.lerLinhas(caminho)) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("#")) {
                continue;
            }
            String[] partes = linha.split(";");
            if (partes.length == 3) {
                eventos.add(partes);
            }
        }
        eventos.sort(Comparator.comparing(p -> p[2].trim()));
        int aceitos = 0;
        for (String[] p : eventos) {
            try {
                int id = Integer.parseInt(p[0].trim());
                TipoAcesso tipo = TipoAcesso.valueOf(p[1].trim().toUpperCase());
                LocalDateTime quando = LocalDateTime.parse(p[2].trim());
                if (receberEvento(id, tipo, quando) != null) {
                    aceitos++;
                }
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.err.println("Linha inválida da catraca ignorada: " + String.join(";", p));
            }
        }
        return aceitos;
    }

    /** @return quantas pessoas estão na academia agora */
    public int quantidadePresentes() {
        sessao.exigirLogin();
        return catraca.getQuantidadePresentes();
    }

    /** @return clientes que estão na academia agora */
    public List<Cliente> clientesPresentes() {
        sessao.exigirLogin();
        List<Cliente> lista = new ArrayList<>();
        for (int id : catraca.getClientesPresentes()) {
            Cliente c = clientes.buscar(id);
            if (c != null) {
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * @param idCliente cliente
     * @return entradas e saídas do cliente
     */
    public List<RegistroAcesso> historico(int idCliente) {
        sessao.exigirLogin();
        return catraca.historicoDoCliente(idCliente);
    }

    /**
     * @param dia dia
     * @return relatório do dia
     */
    public RelatorioAcesso relatorioDiario(LocalDate dia) {
        sessao.exigirLogin();
        return catraca.relatorioDiario(dia);
    }

    /**
     * @param ano ano
     * @param mes mês
     * @return relatório do mês
     */
    public RelatorioAcesso relatorioMensal(int ano, int mes) {
        sessao.exigirLogin();
        return catraca.relatorioMensal(ano, mes);
    }

    /**
     * @param ano ano
     * @return relatório do ano
     */
    public RelatorioAcesso relatorioAnual(int ano) {
        sessao.exigirLogin();
        return catraca.relatorioAnual(ano);
    }

    @Override
    public String toString() {
        return "ControleCatraca{" + catraca + "}";
    }
}