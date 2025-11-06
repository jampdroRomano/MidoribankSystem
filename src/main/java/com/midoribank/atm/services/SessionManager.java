package com.midoribank.atm.services;

import com.midoribank.atm.models.UserProfile;
import java.util.Random;

public class SessionManager {

    public enum PinEntryContext {
        OPERACAO_FINANCEIRA,
        CADASTRO_PIN
    }

    private static UserProfile currentUser;
    private static double currentTransactionAmount;
    private static String currentTransactionType;

    private static String cadastroNome;
    private static String cadastroEmail;
    private static String cadastroSenhaConta;

    private static String cadastroAgencia;
    private static String cadastroNumeroConta;

    private static String cadastroNumeroCartao;
    private static String cadastroCVV;
    private static String cadastroSenhaCartao;

    private static String emailRecuperacao;
    private static String codigoRecuperacaoVerificado;

    private static PinEntryContext pinContext;

    private static String currentOperacaoContext;

    private static UserProfile contaDestino;


    public static void setOperacaoContext(String context) {
        currentOperacaoContext = context;
    }

    public static String getOperacaoContext() {
        return currentOperacaoContext;
    }

    public static void setContaDestino(UserProfile user) {
        contaDestino = user;
    }

    public static UserProfile getContaDestino() {
        return contaDestino;
    }

    public static void clearTransferenciaData() {
        contaDestino = null;

    }

    public static void setPinEntryContext(PinEntryContext context) {
        pinContext = context;
    }

    public static PinEntryContext getPinEntryContext() {
        if (pinContext == null) {
            throw new IllegalStateException("Contexto do PIN não foi definido antes de carregar a tela.");
        }
        return pinContext;
    }

    public static void setCurrentUser(UserProfile user) {
        currentUser = user;
    }

    public static UserProfile getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentTransaction(double amount, String type) {
        currentTransactionAmount = amount;
        currentTransactionType = type;
    }

    public static double getCurrentTransactionAmount() {
        return currentTransactionAmount;
    }

    public static String getCurrentTransactionType() {
        return currentTransactionType;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static void clearTransaction() {
        currentTransactionAmount = 0;
        currentTransactionType = null;
    }

    public static void setCadastroUsuario(String nome, String email, String senha) {
        cadastroNome = nome;
        cadastroEmail = email;
        cadastroSenhaConta = senha;
    }

   public static void setCadastroCartao(String numeroCartao, String cvv) {
        cadastroNumeroCartao = numeroCartao;
        cadastroCVV = cvv;

        Random rand = new Random();
        cadastroAgencia = String.format("%04d-%d", rand.nextInt(10000), rand.nextInt(10)); 
        
        cadastroNumeroConta = String.format("%05d-%d", rand.nextInt(100000), rand.nextInt(10));
    }

    public static void setCadastroSenhaCartao(String senhaCartao) {
        cadastroSenhaCartao = senhaCartao;
    }

    public static String getCadastroNome() {
        return cadastroNome;
    }

    public static void clearCadastroData() {
        cadastroNome = null;
        cadastroEmail = null;
        cadastroSenhaConta = null;
        cadastroAgencia = null;
        cadastroNumeroConta = null;
        cadastroNumeroCartao = null;
        cadastroCVV = null;
        cadastroSenhaCartao = null;
    }

    public static java.util.concurrent.CompletableFuture<Boolean> salvarCadastroCompletoNoBanco() {
        CadastroService cadastroService = new CadastroService();

        return cadastroService.realizarCadastroCompleto(
                cadastroNome, cadastroEmail, cadastroSenhaConta,
                cadastroAgencia, cadastroNumeroConta,
                cadastroNumeroCartao, cadastroCVV, cadastroSenhaCartao
        );
    }

    public static void setEmailRecuperacao(String email) {
        emailRecuperacao = email;
    }

    public static String getEmailRecuperacao() {
        return emailRecuperacao;
    }

    public static void setCodigoVerificado(String codigo) {
        codigoRecuperacaoVerificado = codigo;
    }

    public static String getCodigoVerificado() {
        return codigoRecuperacaoVerificado;
    }

    public static void clearRecuperacao() {
        emailRecuperacao = null;
        codigoRecuperacaoVerificado = null;
    }
}