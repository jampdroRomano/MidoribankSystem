package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.services.RecuperacaoSenhaService;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class AlterarSenhaController {

    @FXML
    private PasswordField novaSenhaField;
    @FXML
    private PasswordField confirmarSenhaField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button redefinirButton;
    
    @FXML
    private ImageView btnVoltar; 
    
    @FXML
    private Label emailLabel; 

    private RecuperacaoSenhaService recuperacaoService;

    @FXML
    public void initialize() {
        this.recuperacaoService = new RecuperacaoSenhaService();
        AnimationUtils.setupButtonHoverEffects(redefinirButton);
        
        AnimationUtils.setupNodeHoverEffects(btnVoltar); 
        errorLabel.setOpacity(0);

        redefinirButton.setOnAction(event -> {
            AnimationUtils.buttonClickAnimation(redefinirButton);
            handleRedefinirSenha();
        });

        String email = SessionManager.getEmailRecuperacao();
        if (email != null && !email.isEmpty()) {
            emailLabel.setText(email);
        } else {
            emailLabel.setText("E-mail não encontrado"); 
        }

        btnVoltar.setOnMouseClicked(event -> { 
            AnimationUtils.buttonClickAnimation(btnVoltar);
            try {
                handleVoltar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleRedefinirSenha() {
        String novaSenha = novaSenhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();
        String email = SessionManager.getEmailRecuperacao();
        String codigoVerificado = SessionManager.getCodigoVerificado();

        if (email == null || codigoVerificado == null) {
            exibirMensagemErro("Sessão inválida. Por favor, reinicie o processo de recuperação.");
            try {
                App.setRoot("Login");
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            exibirMensagemErro("Por favor, preencha ambos os campos.");
            AnimationUtils.errorAnimation(novaSenhaField);
            AnimationUtils.errorAnimation(confirmarSenhaField);
            return;
        }

        if (novaSenha.length() < 6) {
            exibirMensagemErro("A senha deve ter pelo menos 6 caracteres.");
            AnimationUtils.errorAnimation(novaSenhaField);
            return;
        }

        if (!novaSenha.equals(confirmarSenha)) {
            exibirMensagemErro("As senhas não conferem.");
            AnimationUtils.errorAnimation(novaSenhaField);
            AnimationUtils.errorAnimation(confirmarSenhaField);
            return;
        }

        recuperacaoService.redefinirSenha(email, novaSenha).thenAccept(sucesso -> {
            Platform.runLater(() -> {
                if (sucesso) {
                    exibirMensagemInfo("Sucesso", "Sua senha foi redefinida. Por favor, faça o login.");
                    SessionManager.clearRecuperacao();
                    try {
                        App.setRoot("Login");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    exibirMensagemErro("Ocorreu um erro ao atualizar sua senha. Tente novamente.");
                }
            });
        });
    }

    @FXML
    private void handleVoltar() throws IOException {
        SessionManager.clearRecuperacao();
        App.setRoot("Login");
    }

    private void exibirMensagemErro(String mensagem) {
        errorLabel.setText(mensagem);
        AnimationUtils.fadeIn(errorLabel, 200);
        AnimationUtils.errorAnimation(errorLabel);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> AnimationUtils.fadeOut(errorLabel, 200));
        pause.play();
    }

    private void exibirMensagemInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}