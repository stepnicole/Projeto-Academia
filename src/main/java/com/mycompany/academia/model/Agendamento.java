package com.mycompany.academia.model;

import com.mycompany.academia.model.enums.TipoAgendamento;
import com.mycompany.academia.model.enums.StatusAgendamento;
import com.mycompany.academia.model.state.EstadoAgendamento;
import com.mycompany.academia.model.state.EstadoFactory;
import com.mycompany.academia.model.state.Preliminar;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agendamento de aula feito por um cliente.
 * <p>
 * Nasce como PRELIMINAR e só se torna CONFIRMADO se o cliente confirmar até
 * 5 dias úteis antes do acesso. Se desistir após confirmar, até 3 dias antes
 * da data marcada, recebe metade do valor de volta.
 */
public class Agendamento {

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
    private transient EstadoAgendamento estado;
    private LocalDate dataCriacao;

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

    public LocalDate getPrimeiraData() {
        return Collections.min(datas);
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
     * A cobrança no cartão será feita pelo sistema após a confirmação.
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
     * Um já cancelado não é alterado.
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
    public void setStatus(StatusAgendamento status) {
        this.status = status;
        this.estado = null; // recriado sob demanda a partir do status
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