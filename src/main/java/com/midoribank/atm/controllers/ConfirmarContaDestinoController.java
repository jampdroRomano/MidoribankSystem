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

    /**
     * Inicializa o controller, exibindo os dados da conta de destino para confirmação.
     */
    @FXML
    public void initialize() {
        this.contaDestino = SessionManager.getContaDestino();

        // Valida se a conta de destino foi carregada
        if (this.contaDestino == null) {
            exibirMensagemErro("Erro", "Não foi possível carregar os dados da conta de destino. Tente novamente.");
            handleVoltar();
            return;
        }

        // Preenche os labels com os dados da conta de destino
        labelNome.setText(this.contaDestino.getNome());
        labelAgencia.setText(this.contaDestino.getAgencia());
        labelConta.setText(this.contaDestino.getNumeroConta());

        configurarEventos();
    }

    /**
     * Configura os eventos de clique para os botões de confirmar e voltar.
     */
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

    /**
     * Lida com o clique no botão de continuar, avançando para a tela de inserção de valor.
     */
    @FXML
    private void handleContinuar() {
        try {
            App.setRoot("OperacaoValor");
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Erro", "Não foi possível carregar a tela de valor.");
        }
    }

    /**
     * Lida com o clique no botão de voltar, limpando os dados da transferência e retornando para a tela anterior.
     */
    @FXML
    private void handleVoltar() {
        try {
            SessionManager.clearTransferenciaData();
            App.setRoot("Transferencia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exibe uma mensagem de erro em um pop-up.


     */
    private void exibirMensagemErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
