package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


public class DetalhesDaContaController {

    @FXML
    private Label labelNumeroConta;

    @FXML
    private Label labelSaldoAtual;

    @FXML
    private Label numeroContaDetalhes;

    @FXML
    private Label nomeUsuario;

    @FXML
    private Label numeroAgencia;

    @FXML
    private Label emailUsuario;

    @FXML
    private Pane paneVoltar;

    private UserProfile currentUser;

    /*     * Inicializa o controller, carregando os detalhes da conta do usuário.
     */
    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        carregarDadosUsuario();
        configurarEventos();
    }

    /*     * Carrega e exibe os dados do usuário na tela.
     */
    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelNumeroConta.setText(currentUser.getNumeroConta());
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            nomeUsuario.setText(currentUser.getNome());
            numeroAgencia.setText(currentUser.getAgencia());
            emailUsuario.setText(currentUser.getEmail());
            numeroContaDetalhes.setText(currentUser.getNumeroConta());
        } else {
            // Redireciona para a tela de login se não houver usuário na sessão
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*     * Configura os eventos de clique, como o botão de voltar.
     */
    private void configurarEventos() {
        paneVoltar.setOnMouseClicked(e -> voltarParaHome());
        AnimationUtils.setupNodeHoverEffects(paneVoltar);
    }

    /*     * Lida com a ação de voltar para a tela inicial.
     */
    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

