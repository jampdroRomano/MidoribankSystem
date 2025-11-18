package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.services.RecuperacaoSenhaService;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;


public class EnviarEmailRecuperacaoController {

    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button entrarButton;
    @FXML
    private ImageView btnVoltarLogin;

    private RecuperacaoSenhaService recuperacaoService;

    /**
     * Inicializa o controller, configurando o serviço de recuperação de senha e as animações.
     */
    @FXML
    public void initialize() {
        this.recuperacaoService = new RecuperacaoSenhaService();

        AnimationUtils.setupButtonHoverEffects(entrarButton);
        AnimationUtils.setupNodeHoverEffects(btnVoltarLogin);
    }

    /**
     * Lida com o envio do código de recuperação para o e-mail do usuário.
     */
    @FXML
    private void handleEnviarCodigo() {
        String email = emailField.getText();

        // Valida o formato do e-mail
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            errorLabel.setText("Formato de e-mail inválido.");
            AnimationUtils.errorAnimation(emailField);
            return;
        }

        // Limpa a sessão de recuperação e define o novo e-mail
        SessionManager.clearRecuperacao();
        SessionManager.setEmailRecuperacao(email);

        // Inicia o processo de recuperação de senha
        recuperacaoService.iniciarRecuperacao(email).thenAccept(sucesso -> {
            Platform.runLater(() -> {
                if (sucesso) {
                    try {
                        App.setRoot("VerificarCodigoEmail");
                    } catch (IOException e) {
                        e.printStackTrace();
                        exibirMensagemErro("Erro ao carregar a próxima tela.");
                    }
                } else {
                    exibirMensagemErro("Falha ao enviar o código. Verifique o e-mail e tente novamente.");
                }
            });
        });
    }

    /**
     * Lida com a ação de voltar para a tela de login.

     */
    @FXML
    private void handleVoltar() throws IOException {
        App.setRoot("Login");
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
