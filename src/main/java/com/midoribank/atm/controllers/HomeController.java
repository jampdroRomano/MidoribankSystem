package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class HomeController {

    @FXML
    private Label labelNomeUsuario;
    @FXML
    private Label labelNumeroConta;
    @FXML
    private Pane paneSacar;
    @FXML
    private Pane paneDepositar;
    @FXML
    private Pane paneEncerrar;
    @FXML
    private Pane paneTransferir;
    @FXML
    private Pane paneExtrato;
    @FXML
    private Pane paneCartao;
    @FXML
    private Pane paneDetalhes;

    private UserProfile currentUser;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        carregarDadosUsuario();
        configurarEventos();
    }

    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelNomeUsuario.setText(currentUser.getNome());
            labelNumeroConta.setText(currentUser.getNumeroConta());
        } else {
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void configurarEventos() {
        paneSacar.setOnMouseClicked(e -> abrirTelaSaque());
        paneDepositar.setOnMouseClicked(e -> abrirTelaDeposito());
        paneEncerrar.setOnMouseClicked(e -> handleEncerrar());

        paneTransferir.setOnMouseClicked(e -> abrirTelaTransferencia());
        paneExtrato.setOnMouseClicked(e -> abrirTelaExtrato());
        paneCartao.setOnMouseClicked(e -> abrirTelaDadosCartao());
        paneDetalhes.setOnMouseClicked(e -> abrirTelaDetalhesDaConta());

        AnimationUtils.setupNodeHoverEffects(paneSacar);
        AnimationUtils.setupNodeHoverEffects(paneDepositar);
        AnimationUtils.setupNodeHoverEffects(paneEncerrar);
        AnimationUtils.setupNodeHoverEffects(paneTransferir);
        AnimationUtils.setupNodeHoverEffects(paneExtrato);
        AnimationUtils.setupNodeHoverEffects(paneCartao);
        AnimationUtils.setupNodeHoverEffects(paneDetalhes);
    }

    private void abrirTelaSaque() {
        try {
            SessionManager.setOperacaoContext("Saque");
            App.setRoot("OperacaoValor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaDeposito() {
        try {
            SessionManager.setOperacaoContext("Depósito");
            App.setRoot("OperacaoValor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaTransferencia() {
        try {
            SessionManager.setOperacaoContext("Transferencia");
            App.setRoot("Transferencia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEncerrar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Você está prestes a encerrar a sessão.");
        alert.setContentText("Deseja realmente sair?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            com.midoribank.atm.utils.LoadingUtils.runWithLoading("Encerrando sessão...", () -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            com.midoribank.atm.App.setRoot("opcoesLogin");
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void showInDevelopmentAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText("Esta função ainda está em desenvolvimento.");
        alert.showAndWait();
    }

    private void abrirTelaExtrato() {
        try {
            App.setRoot("Extrato");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaDadosCartao() {
        try {
            App.setRoot("DadosCartao");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaDetalhesDaConta() {
        try {
            App.setRoot("DetalhesDaConta");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}