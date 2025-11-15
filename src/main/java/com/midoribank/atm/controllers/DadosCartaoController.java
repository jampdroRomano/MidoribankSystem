package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class DadosCartaoController {

    @FXML
    private Label labelNumeroConta;

    @FXML
    private Label labelSaldoAtual;

    @FXML
    private Label numeroCartao;

    @FXML
    private Label nomeCartao;

    @FXML
    private Label cvvCartao;

    @FXML
    private Pane paneVoltar;

    private UserProfile currentUser;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        carregarDadosUsuario();
        configurarEventos();
    }

    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelNumeroConta.setText(currentUser.getNumeroConta());
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            numeroCartao.setText(currentUser.getNumeroCartao());
            nomeCartao.setText(currentUser.getNome());
            cvvCartao.setText(currentUser.getCvvCartao());
        } else {
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void configurarEventos() {
        paneVoltar.setOnMouseClicked(e -> voltarParaHome());
        AnimationUtils.setupNodeHoverEffects(paneVoltar);
    }

    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
