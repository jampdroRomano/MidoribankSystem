package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


public class opcoesLoginController {

    @FXML
    private Pane entrar_conta;

    @FXML
    private Pane recarga_cartao;

    @FXML
    private Pane recarga_celular;

    @FXML
    private Pane paneEncerrar; 

    /**
     * Inicializa o controller, configurando os eventos de clique e animações.
     */
    @FXML
    public void initialize() {
        recarga_cartao.setOnMouseClicked(e -> showInDevelopmentAlert());
        recarga_celular.setOnMouseClicked(e -> showInDevelopmentAlert());

        paneEncerrar.setOnMouseClicked(e -> handleEncerrar());

        AnimationUtils.setupNodeHoverEffects(entrar_conta);
        AnimationUtils.setupNodeHoverEffects(recarga_cartao);
        AnimationUtils.setupNodeHoverEffects(recarga_celular);
        AnimationUtils.setupNodeHoverEffects(paneEncerrar);
    }

    /**
     * Lida com o clique para entrar na conta, navegando para a tela de login.

     */
    @FXML
    private void handleEntrarComContaClick(MouseEvent event) {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lida com a ação de encerrar a aplicação, exibindo um diálogo de confirmação.
     */
    private void handleEncerrar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Você está prestes a fechar o aplicativo.");
        alert.setContentText("Deseja realmente sair?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Encerrando a aplicação...");
            javafx.application.Platform.exit();
        }
    }

    /**
     * Exibe um alerta informando que a função está em desenvolvimento.
     */
    private void showInDevelopmentAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText("Esta função ainda está em desenvolvimento.");
        alert.showAndWait();
    }
}
