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
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class AlterarSenhaController {

    @FXML
    private PasswordField novaSenhaField;
    @FXML
    private PasswordField confirmarSenhaField;
    @FXML
    private TextField novaSenhaTextField;
    @FXML
    private TextField confirmarSenhaTextField;
    @FXML
    private ImageView toggleNovaSenha;
    @FXML
    private ImageView toggleConfirmarSenha;
    @FXML
    private Label errorLabel;
    @FXML
    private Button redefinirButton;
    
    @FXML
    private ImageView btnVoltar; 
    
    @FXML
    private Label emailLabel; 

    private RecuperacaoSenhaService recuperacaoService;

    private boolean isNovaSenhaVisivel = false;
    private boolean isConfirmarSenhaVisivel = false;
    
    private Image openEye;
    private Image closeEye;

    /**
     * Inicializa o controller, configurando os serviços, animações e handlers de eventos.
     */
    @FXML
    public void initialize() {
        this.recuperacaoService = new RecuperacaoSenhaService();
        
        openEye = new Image(getClass().getResourceAsStream("/com/midoribank/atm/AlterarSenha/HugeIconOpen.png"));
        closeEye = new Image(getClass().getResourceAsStream("/com/midoribank/atm/AlterarSenha/HugeIconClose.png"));
        
        novaSenhaTextField.setManaged(false);
        novaSenhaTextField.setVisible(false);
        confirmarSenhaTextField.setManaged(false);
        confirmarSenhaTextField.setVisible(false);

        novaSenhaTextField.textProperty().bindBidirectional(novaSenhaField.textProperty());
        confirmarSenhaTextField.textProperty().bindBidirectional(confirmarSenhaField.textProperty());
        
        toggleNovaSenha.setOnMouseEntered(e -> toggleNovaSenha.setCursor(Cursor.HAND));
        toggleNovaSenha.setOnMouseExited(e -> toggleNovaSenha.setCursor(Cursor.DEFAULT));

        toggleConfirmarSenha.setOnMouseEntered(e -> toggleConfirmarSenha.setCursor(Cursor.HAND));
        toggleConfirmarSenha.setOnMouseExited(e -> toggleConfirmarSenha.setCursor(Cursor.DEFAULT));
        
        AnimationUtils.setupButtonHoverEffects(redefinirButton);
        
        AnimationUtils.setupNodeHoverEffects(btnVoltar); 
        errorLabel.setOpacity(0);

        // Configura a ação do botão de redefinir senha
        redefinirButton.setOnAction(event -> {
            AnimationUtils.buttonClickAnimation(redefinirButton);
            handleRedefinirSenha();
        });

        // Exibe o e-mail do usuário que está recuperando a senha
        String email = SessionManager.getEmailRecuperacao();
        if (email != null && !email.isEmpty()) {
            emailLabel.setText(email);
        } else {
            emailLabel.setText("E-mail não encontrado"); 
        }

        // Configura a ação do botão de voltar
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
    private void handleToggleNovaSenha() {
        isNovaSenhaVisivel = !isNovaSenhaVisivel;
        if (isNovaSenhaVisivel) {
            mostrarNovaSenha();
        } else {
            ocultarNovaSenha();
        }
    }
    
    @FXML
    private void handleToggleConfirmarSenha() {
        isConfirmarSenhaVisivel = !isConfirmarSenhaVisivel;
        if (isConfirmarSenhaVisivel) {
            mostrarConfirmarSenha();
        } else {
            ocultarConfirmarSenha();
        }
    }

    private void mostrarNovaSenha() {
        novaSenhaField.setManaged(false);
        novaSenhaField.setVisible(false);
        novaSenhaTextField.setManaged(true);
        novaSenhaTextField.setVisible(true);
        toggleNovaSenha.setImage(openEye);
    }

    private void ocultarNovaSenha() {
        novaSenhaTextField.setManaged(false);
        novaSenhaTextField.setVisible(false);
        novaSenhaField.setManaged(true);
        novaSenhaField.setVisible(true);
        toggleNovaSenha.setImage(closeEye);
    }
    
    private void mostrarConfirmarSenha() {
        confirmarSenhaField.setManaged(false);
        confirmarSenhaField.setVisible(false);
        confirmarSenhaTextField.setManaged(true);
        confirmarSenhaTextField.setVisible(true);
        toggleConfirmarSenha.setImage(openEye);
    }

    private void ocultarConfirmarSenha() {
        confirmarSenhaTextField.setManaged(false);
        confirmarSenhaTextField.setVisible(false);
        confirmarSenhaField.setManaged(true);
        confirmarSenhaField.setVisible(true);
        toggleConfirmarSenha.setImage(closeEye);
    }

    /**
     * Lida com a lógica de redefinição de senha, validando os campos e chamando o serviço de recuperação.
     */
    @FXML
    private void handleRedefinirSenha() {
        String novaSenha = novaSenhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();
        String email = SessionManager.getEmailRecuperacao();
        String codigoVerificado = SessionManager.getCodigoVerificado();

        // Valida a sessão de recuperação
        if (email == null || codigoVerificado == null) {
            exibirMensagemErro("Sessão inválida. Por favor, reinicie o processo de recuperação.");
            try {
                App.setRoot("Login");
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        // Valida se os campos de senha estão preenchidos
        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            exibirMensagemErro("Por favor, preencha ambos os campos.");
            AnimationUtils.errorAnimation(novaSenhaField);
            AnimationUtils.errorAnimation(confirmarSenhaField);
            return;
        }

        // Valida o comprimento mínimo da senha
        if (novaSenha.length() < 6) {
            exibirMensagemErro("A senha deve ter pelo menos 6 caracteres.");
            AnimationUtils.errorAnimation(novaSenhaField);
            return;
        }

        // Valida se as senhas coincidem
        if (!novaSenha.equals(confirmarSenha)) {
            exibirMensagemErro("As senhas não conferem.");
            AnimationUtils.errorAnimation(novaSenhaField);
            AnimationUtils.errorAnimation(confirmarSenhaField);
            return;
        }

        // Chama o serviço para redefinir a senha
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

    /**
     * Lida com a ação de voltar para a tela de login, limpando a sessão de recuperação.
     * se ocorrer um erro ao carregar a tela de login.
     */
    @FXML
    private void handleVoltar() throws IOException {
        SessionManager.clearRecuperacao();
        App.setRoot("Login");
    }

    /**
     * Exibe uma mensagem de erro na interface do usuário.
     * A mensagem de erro a ser exibida.
     */
    private void exibirMensagemErro(String mensagem) {
        errorLabel.setText(mensagem);
        AnimationUtils.fadeIn(errorLabel, 200);
        AnimationUtils.errorAnimation(errorLabel);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> AnimationUtils.fadeOut(errorLabel, 200));
        pause.play();
    }

    /**
     * Exibe uma mensagem de informação em um pop-up.
     * O título do pop-up.
     * A mensagem a ser exibida.
     */
    private void exibirMensagemInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
