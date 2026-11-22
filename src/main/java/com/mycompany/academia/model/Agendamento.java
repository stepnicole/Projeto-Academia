package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.StatusAgendamento;
import com.mycompany.academia.model.enums.TipoAgendamento;
import com.mycompany.academia.model.state.EstadoAgendamento;
import com.mycompany.academia.model.state.EstadoFactory;
import com.mycompany.academia.model.state.Preliminar;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agendamento de aula feito por um cliente (contexto do padrão State).
 * <p>
 * Nasce como PRELIMINAR e só se torna CONFIRMADO se o cliente confirmar até
 * 5 dias úteis antes do acesso. Se desistir após confirmar, até 3 dias antes
 * da data marcada, recebe metade do valor de volta. Depois que a última data
 * passa, o agendamento confirmado se torna REALIZADO.
 * <p>
 * O comportamento de cada operação depende do estado atual e fica nas
 * classes do pacote {@code state} ({@code Preliminar}, {@code Confirmado},
 * {@code Realizado}, {@code Cancelado}); esta classe apenas delega.
 * Guarda apenas os ids do cliente e da aula, para facilitar o JSON.
 *
 */
public class Agendamento implements Identificador {

    public static final int DIAS_UTEIS_CONFIRMACAO = 5;
    public static final int DIAS_CANCELAMENTO = 3;
    public static final double PERCENTUAL_REEMBOLSO = 0.5;

    private int id;
    private int idCliente;
    private int idAula;
    private List<LocalDate> datas;
    private double preco;
    private TipoAgendamento tipo;
    private StatusAgendamento status;
    /** Objeto de estado (State). transient: não vai para o JSON, só o status. */
    private transient EstadoAgendamento estado;
    private LocalDate dataCriacao;

    /**
     * Cria um agendamento preliminar.
     *
     * @param id          identificador
     * @param idCliente   id do cliente
     * @param idAula      id da aula escolhida
     * @param datas       data(s) escolhida(s); para mensalidade, a data de início
     * @param preco       preço que será cobrado
     * @param tipo        diária ou mensalidade
     * @param dataCriacao dia em que o agendamento foi feito
     * @throws IllegalArgumentException se nenhuma data for informada
     */
    public Agendamento(int id, int idCliente, int idAula, List<LocalDate> datas,
                       double preco, TipoAgendamento tipo, LocalDate dataCriacao) {
        if (datas == null || datas.isEmpty()) {
            throw new IllegalArgumentException("O agendamento precisa de ao menos uma data.");
        }
        this.id = id;
        this.idCliente = idCliente;
        this.idAula = idAula;
        this.datas = new ArrayList<>(datas);
        this.preco = preco;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
        setEstado(new Preliminar());
    }

    /** @return a data mais próxima entre as datas agendadas */
    public LocalDate getPrimeiraData() {
        return Collections.min(datas);
    }

    /**
     * Último dia coberto pelo agendamento. Diária: a maior data escolhida.
     * Mensalidade: um mês a partir da data de início (menos um dia).
     *
     * @return último dia coberto
     */
    public LocalDate getUltimaData() {
        if (tipo == TipoAgendamento.MENSALIDADE) {
            return getPrimeiraData().plusMonths(1).minusDays(1);
        }
        return Collections.max(datas);
    }

    /**
     * Último dia em que o cliente ainda pode confirmar: 5 dias úteis
     * (segunda a sexta) antes do primeiro acesso. Feriados não são considerados.
     *
     * @return data limite de confirmação
     */
    public LocalDate getPrazoConfirmacao() {
        LocalDate data = getPrimeiraData();
        int restantes = DIAS_UTEIS_CONFIRMACAO;
        while (restantes > 0) {
            data = data.minusDays(1);
            if (data.getDayOfWeek() != DayOfWeek.SATURDAY
                    && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
                restantes--;
            }
        }
        return data;
    }

    /**
     * Último dia em que o cancelamento ainda dá direito a reembolso.
     *
     * @return primeira data menos 3 dias
     */
    public LocalDate getPrazoCancelamento() {
        return getPrimeiraData().minusDays(DIAS_CANCELAMENTO);
    }

    /**
     * Retorna o estado atual. Depois que o Gson carrega o JSON, o objeto de
     * estado fica nulo (é transient), então é recriado a partir do status.
     */
    private EstadoAgendamento getEstado() {
        if (estado == null) {
            estado = EstadoFactory.fromStatus(status);
        }
        return estado;
    }

    /**
     * Troca o estado atual e mantém o status (usado no JSON) sincronizado.
     * Chamado pelas próprias classes de estado nas transições.
     *
     * @param novo novo estado do agendamento
     */
    public void setEstado(EstadoAgendamento novo) {
        this.estado = novo;
        this.status = novo.getStatus();
    }

    /**
     * Confirma o agendamento, se ainda estiver preliminar e dentro do prazo.
     * A cobrança no cartão é feita pelo ControleAgendamentos após a confirmação.
     *
     * @param hoje data em que a confirmação está sendo feita
     * @return true se foi confirmado
     */
    public boolean confirmar(LocalDate hoje) {
        return getEstado().confirmar(this, hoje);
    }

    /**
     * Calcula quanto seria devolvido ao cliente se cancelasse hoje.
     * Só há reembolso (50%) se o agendamento está confirmado e o cancelamento
     * ocorre até 3 dias antes da data marcada.
     *
     * @param hoje data do cancelamento
     * @return valor a devolver (0 se não tiver direito)
     */
    public double calcularReembolso(LocalDate hoje) {
        return getEstado().calcularReembolso(this, hoje);
    }

    /**
     * Cancela o agendamento e informa o valor a devolver.
     * Um agendamento preliminar não foi cobrado, então não há reembolso.
     * Um já cancelado ou realizado não é alterado.
     *
     * @param hoje data do cancelamento
     * @return valor a devolver ao cliente
     */
    public double cancelar(LocalDate hoje) {
        return getEstado().cancelar(this, hoje);
    }

    /**
     * Cancela automaticamente o agendamento preliminar cujo prazo de
     * confirmação já passou.
     *
     * @param hoje data atual
     * @return true se o agendamento expirou e foi cancelado
     */
    public boolean expirar(LocalDate hoje) {
        return getEstado().expirar(this, hoje);
    }

    /**
     * Marca como realizado o agendamento confirmado cuja última data já
     * chegou (a aula aconteceu).
     *
     * @param hoje data atual
     * @return true se o agendamento passou para realizado
     */
    public boolean concluir(LocalDate hoje) {
        return getEstado().concluir(this, hoje);
    }

    /**
     * Diz se o agendamento dá direito de entrar na academia numa data (usado
     * pela catraca). Precisa estar confirmado ou realizado (foi pago) e a data
     * precisa estar coberta: diária, se for uma das datas escolhidas;
     * mensalidade, se estiver dentro do mês contratado.
     *
     * @param data dia do acesso
     * @return true se o cliente pode entrar nesse dia
     */
    public boolean cobreData(LocalDate data) {
        if (status != StatusAgendamento.CONFIRMADO && status != StatusAgendamento.REALIZADO) {
            return false;
        }
        if (tipo == TipoAgendamento.MENSALIDADE) {
            return !data.isBefore(getPrimeiraData()) && !data.isAfter(getUltimaData());
        }
        return datas.contains(data);
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdAula() { return idAula; }
    public void setIdAula(int idAula) { this.idAula = idAula; }

    public List<LocalDate> getDatas() { return datas; }
    public void setDatas(List<LocalDate> datas) { this.datas = datas; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public TipoAgendamento getTipo() { return tipo; }
    public void setTipo(TipoAgendamento tipo) { this.tipo = tipo; }

    public StatusAgendamento getStatus() { return status; }

    /**
     * Altera o status diretamente (usado na leitura do JSON). O objeto de
     * estado é recriado sob demanda a partir do novo status.
     *
     * @param status novo status
     */
    public void setStatus(StatusAgendamento status) {
        this.status = status;
        this.estado = null;
    }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
        return "Agendamento{id=" + id + ", idCliente=" + idCliente + ", idAula=" + idAula
                + ", datas=" + datas + ", preco=" + preco + ", tipo=" + tipo
                + ", status=" + status + ", criado=" + dataCriacao + "}";
    }
}