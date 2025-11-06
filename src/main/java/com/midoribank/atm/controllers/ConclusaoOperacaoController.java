package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class ConclusaoOperacaoController {

    @FXML private Label labelMensagemSucesso;
    @FXML private Label labelPergunta;
    @FXML private Label labelTituloOperacao;
    @FXML private Label labelSaldoAtual;
    @FXML private Label labelNumeroConta;
    @FXML private Node paneSim;
    @FXML private Node paneNao;

    private UserProfile currentUser;
    private String tipoOperacao;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.tipoOperacao = SessionManager.getCurrentTransactionType();

        if (currentUser == null || tipoOperacao == null) {
            try { App.setRoot("home"); } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        configurarTela();
        configurarEventos();
    }

    private void configurarTela() {
        labelTituloOperacao.setText(tipoOperacao);

        if ("Transferencia".equals(tipoOperacao)) {
            labelMensagemSucesso.setText("Transferência concluída com sucesso!");
            labelPergunta.setText("Deseja realizar outra transferência?");
        } else {
            labelMensagemSucesso.setText(tipoOperacao + " concluído com sucesso!");
            labelPergunta.setText("Deseja realizar outro " + tipoOperacao.toLowerCase() + "?");
        }

        labelNumeroConta.setText(currentUser.getNumeroConta());
        labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
    }

    private void configurarEventos() {
        if (paneSim != null) {
            paneSim.setOnMouseClicked(e -> handleSim());
            AnimationUtils.setupNodeHoverEffects(paneSim);
        } else {
            System.err.println("Aviso: paneSim não encontrado no FXML.");
        }

        if (paneNao != null) {
            paneNao.setOnMouseClicked(e -> handleNao());
            AnimationUtils.setupNodeHoverEffects(paneNao);
        } else {
            System.err.println("Aviso: paneNao não encontrado no FXML.");
        }
    }

    private void handleSim() {
        SessionManager.clearTransaction();
        try {
            if ("Saque".equals(tipoOperacao) || "Depósito".equals(tipoOperacao)) {
                App.setRoot("OperacaoValor");
            
            } else if ("Transferencia".equals(tipoOperacao)) {
                App.setRoot("Transferencia"); 
            
            } else {
                App.setRoot("home");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNao() {
        SessionManager.clearTransaction();
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}