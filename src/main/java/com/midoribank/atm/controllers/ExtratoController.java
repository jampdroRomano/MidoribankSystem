package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ExtratoController {

    @FXML
    private Pane paneVoltar;
    @FXML
    private Label labelSaldoAtual;
    @FXML
    private Label labelNumeroConta;

    private UserProfile currentUser;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        carregarDadosUsuario();
        paneVoltar.setOnMouseClicked(e -> voltarParaHome());
    }

    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            labelNumeroConta.setText(currentUser.getNumeroConta());
        } else {
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
