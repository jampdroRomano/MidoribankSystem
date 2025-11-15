package com.midoribank.atm.models;

public class UserProfile {
    private int id;
    private int contaId;
    private String nome;
    private String email;
    private String numeroConta;
    private String agencia;
    private String senhaContaHash;
    private double saldo;
    private String numeroCartao;
    private String senhaCartaoHash;

    private String cvvCartao;

    public UserProfile(int id, int contaId, String nome, String email, String numeroConta, String agencia, String senhaContaHash, double saldo, String numeroCartao, String senhaCartaoHash, String cvvCartao) {
        this.id = id;
        this.contaId = contaId;
        this.nome = nome;
        this.email = email;
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.senhaContaHash = senhaContaHash;
        this.saldo = saldo;
        this.numeroCartao = numeroCartao;
        this.senhaCartaoHash = senhaCartaoHash;
        this.cvvCartao = cvvCartao;
    }

    public int getId() { return id; }
    public int getContaId() { return contaId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getNumeroConta() { return numeroConta; }
    public String getAgencia() { return agencia; }
    public String getSenhaContaHash() { return senhaContaHash; }
    public double getSaldo() { return saldo; }
    public String getNumeroCartao() { return numeroCartao; }
    public String getSenhaCartaoHash() { return senhaCartaoHash; }
    public String getCvvCartao() { return cvvCartao; }

    public void setSaldo(double novoSaldo) {
        this.saldo = novoSaldo;
    }
}