package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ConfirmarContaDestinoController {

    @FXML private Label labelNome;
    @FXML private Label labelAgencia;
    @FXML private Label labelConta;
    @FXML private Pane paneConfirmar;
    @FXML private Pane paneVoltar;

    private UserProfile contaDestino;

    @FXML
    public void initialize() {
        this.contaDestino = SessionManager.getContaDestino();

        if (this.contaDestino == null) {
            exibirMensagemErro("Erro", "Não foi possível carregar os dados da conta de destino. Tente novamente.");
            handleVoltar();
            return;
        }

        labelNome.setText(this.contaDestino.getNome());
        labelAgencia.setText(this.contaDestino.getAgencia());
        labelConta.setText(this.contaDestino.getNumeroConta());

        configurarEventos();
    }

    private void configurarEventos() {
        if (paneConfirmar != null) {
            paneConfirmar.setOnMouseClicked(e -> handleContinuar());
            AnimationUtils.setupNodeHoverEffects(paneConfirmar);
        }
        if (paneVoltar != null) {
            paneVoltar.setOnMouseClicked(e -> handleVoltar());
            AnimationUtils.setupNodeHoverEffects(paneVoltar);
        }
    }

    @FXML
    private void handleContinuar() {
        try {
            App.setRoot("OperacaoValor");
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Erro", "Não foi possível carregar a tela de valor.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            SessionManager.clearTransferenciaData();
            App.setRoot("Transferencia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exibirMensagemErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}