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

    /**
     * Inicializa o controller, carregando os dados da operação para confirmação.
     */
    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.valorOperacao = SessionManager.getCurrentTransactionAmount();
        this.tipoOperacao = SessionManager.getCurrentTransactionType();

        carregarDados();
        configurarEventos();
    }

    /**
     * Carrega e exibe os dados da operação na tela.
     */
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

    /**
     * Configura os eventos de clique para os botões de confirmar e voltar.
     */
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

    /**
     * Lida com o clique no botão de continuar, avançando para a tela de inserção de PIN.
     */
    private void handleContinuarParaSenha() {
        SessionManager.setPinEntryContext(SessionManager.PinEntryContext.OPERACAO_FINANCEIRA);
        try {
            App.setRoot("TelaPin");
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível carregar a tela de senha.");
        }
    }

    /**
     * Lida com o clique no botão de voltar, retornando para a tela anterior.
     */
    private void handleVoltar() {
        try {
            String telaAnterior = "home";
            if ("Saque".equals(tipoOperacao) || "Depósito".equals(tipoOperacao) || "Transferencia".equals(tipoOperacao)) {
                telaAnterior = "OperacaoValor";
            }

            App.setRoot(telaAnterior);
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível voltar para a tela anterior.");
        }
    }

    /**
     * Exibe uma mensagem de erro em um pop-up.

     */
    private void exibirMensagemErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
