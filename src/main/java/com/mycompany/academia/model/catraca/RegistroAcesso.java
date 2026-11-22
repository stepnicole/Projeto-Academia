package com.mycompany.academia.model.catraca;

import com.mycompany.academia.model.Identificador;
import com.mycompany.academia.model.enums.TipoAcesso;
import java.time.LocalDateTime;

/**
 * Uma marcação da catraca: qual cliente, se entrou ou saiu e quando.
 *
 */
public class RegistroAcesso implements Identificador {

    private int id;
    private int idCliente;
    private TipoAcesso tipo;
    private LocalDateTime dataHora;

    /**
     * @param id        identificador
     * @param idCliente cliente que passou pela catraca
     * @param tipo      entrada ou saída
     * @param dataHora  momento da passagem
     */
    public RegistroAcesso(int id, int idCliente, TipoAcesso tipo, LocalDateTime dataHora) {
        this.id = id;
        this.idCliente = idCliente;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public TipoAcesso getTipo() { return tipo; }
    public void setTipo(TipoAcesso tipo) { this.tipo = tipo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    @Override
    public String toString() {
        return "RegistroAcesso{id=" + id + ", idCliente=" + idCliente
                + ", tipo=" + tipo + ", dataHora=" + dataHora + "}";
    }
}