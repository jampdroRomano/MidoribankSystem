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


    /*     * Obtém o ID da movimentação.

     */
    public int getId() {
        return id;
    }

    /*     * Define o ID da movimentação.

     */
    public void setId(int id) {
        this.id = id;
    }

    /*     * Obtém o ID da conta associada à movimentação.

     */
    public int getContaId() {
        return contaId;
    }

    /*     * Define o ID da conta associada à movimentação.

     */
    public void setContaId(int contaId) {
        this.contaId = contaId;
    }

    /*     * Obtém o tipo da movimentação (ex: SAQUE, DEPOSITO).

     */
    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    /*     * Define o tipo da movimentação.

     */
    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    /*     * Obtém o valor da movimentação.

     */
    public BigDecimal getValor() {
        return valor;
    }

    /*     * Define o valor da movimentação.

     */
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    /*     * Obtém a data e hora da movimentação.

     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /*     * Define a data e hora da movimentação.

     */
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    /*     * Obtém o ID da conta de destino (relevante para transferências).

     */
    public Integer getContaDestinoId() {
        return contaDestinoId;
    }

    /*     * Define o ID da conta de destino.

     */
    public void setContaDestinoId(Integer contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }
}

