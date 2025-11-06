package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ConfirmarOperacaoController {

    @FXML private Label labelNome;
    @FXML private Label labelAgencia;
    @FXML private Label labelConta;
    @FXML private Label labelTipo;
    @FXML private Label labelValor;
    @FXML private Label labelData;
    @FXML private Pane paneConfirmar;
    @FXML private Pane paneVoltar;

    private UserProfile currentUser;
    private double valorOperacao;
    private String tipoOperacao;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.valorOperacao = SessionManager.getCurrentTransactionAmount();
        this.tipoOperacao = SessionManager.getCurrentTransactionType();

        carregarDados();
        configurarEventos();
    }

    private void carregarDados() {
        if (currentUser != null) {
            labelNome.setText(currentUser.getNome());
            labelAgencia.setText(currentUser.getAgencia());
            labelConta.setText(currentUser.getNumeroConta());
        }

        labelTipo.setText(tipoOperacao != null ? tipoOperacao : "N/D");
        labelValor.setText(String.format("R$ %.2f", valorOperacao));

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        labelData.setText(hoje.format(formatador));
    }

    private void configurarEventos() {
        if (paneConfirmar != null) {
            paneConfirmar.setOnMouseClicked(e -> handleContinuarParaSenha());
            AnimationUtils.setupNodeHoverEffects(paneConfirmar);
        }
        if (paneVoltar != null) {
            paneVoltar.setOnMouseClicked(e -> handleVoltar());
            AnimationUtils.setupNodeHoverEffects(paneVoltar);
        }
    }

    private void handleContinuarParaSenha() {
        SessionManager.setPinEntryContext(SessionManager.PinEntryContext.OPERACAO_FINANCEIRA);
        try {
            App.setRoot("TelaPin");
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível carregar a tela de senha.");
        }
    }


    private void handleVoltar() {
        try {
            String telaAnterior = "home";
            // Verifica o tipo de operação e aponta para a tela correta (OperacaoValor)
            if ("Saque".equals(tipoOperacao) || "Depósito".equals(tipoOperacao)) {
                telaAnterior = "OperacaoValor";
            }

            App.setRoot(telaAnterior);
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível voltar para a tela anterior.");
        }
    }

    private void exibirMensagemErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}