package com.midoribank.atm.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movimentacao {

    private int id;
    private int contaId;
    private String tipoMovimentacao;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private Integer contaDestinoId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContaId() {
        return contaId;
    }

    public void setContaId(int contaId) {
        this.contaId = contaId;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getContaDestinoId() {
        return contaDestinoId;
    }

    public void setContaDestinoId(Integer contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }
}
