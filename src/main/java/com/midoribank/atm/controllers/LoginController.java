package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.dao.UserDAO;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.LoadingUtils;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField senhaField;
    @FXML
    private Button cadastrarButton;
    @FXML
    private Button entrarButton;
    @FXML
    private ImageView btnVoltarLogin;
    @FXML
    private Label esqueciSenhaLabel;

    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label senhaErrorLabel;
    @FXML
    private Label loginErrorLabel;

    private UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inicializa o controller de login, configurando os eventos e animações.
     */
    @FXML
    public void initialize() {
        System.out.println("LoginController inicializado.");

        entrarButton.setOnAction(event -> autenticar());
        cadastrarButton.setOnAction(event -> handleAbrirCadastro());

        AnimationUtils.setupButtonHoverEffects(entrarButton);
        AnimationUtils.setupButtonHoverEffects(cadastrarButton);

        if (btnVoltarLogin != null) {
            AnimationUtils.setupNodeHoverEffects(btnVoltarLogin);
        } else {
            System.err.println("Aviso: btnVoltarLogin não encontrado no FXML.");
        }

        // Configura o cursor para "mão" ao passar o mouse sobre o label "Esqueci minha senha"
        if (esqueciSenhaLabel != null) {
            esqueciSenhaLabel.setOnMouseEntered(e -> {
                if (esqueciSenhaLabel.getScene() != null) {
                    esqueciSenhaLabel.getScene().setCursor(Cursor.HAND);
                }
            });

            esqueciSenhaLabel.setOnMouseExited(e -> {
                if (esqueciSenhaLabel.getScene() != null) {
                    esqueciSenhaLabel.getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailErrorLabel.setText("");
            senhaErrorLabel.setText("");
        });

        senhaField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailErrorLabel.setText("");
            senhaErrorLabel.setText("");
            loginErrorLabel.setText("");
        });
    }

    /**
     * Navega para a tela de recuperação de senha.

     */
    @FXML
    private void handleEsqueciMinhaSenha() throws IOException {
        App.setRoot("EnviarEmailRecuperacao");
    }

    /**
     * Lida com o clique no botão de voltar, retornando para a tela de opções de login.
     */
    @FXML
    private void handleVoltarClick() {
        try {
            App.setRoot("opcoesLogin");
        } catch (IOException e) {
            System.err.println("Falha ao carregar opcoesLogin.fxml!");
            e.printStackTrace();
            loginErrorLabel.setText("Não foi possível voltar para a tela anterior.");
        }
    }

    /**
     * Navega para a tela de cadastro de usuário.
     */
    private void handleAbrirCadastro() {
        try {
            App.setRoot("CadastroUsuario");
        } catch (IOException e) {
            System.err.println("Falha ao carregar CadastroUsuario.fxml!");
            e.printStackTrace();
            loginErrorLabel.setText("Não foi possível abrir a tela de cadastro.");
        }
    }

    /**
     * Autentica o usuário com base no e-mail e senha fornecidos.
     */
    private void autenticar() {
        String email = emailField.getText();
        String senha = senhaField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            emailErrorLabel.setText("Preencha todos os campos!");
            AnimationUtils.errorAnimation(emailField);
            AnimationUtils.errorAnimation(senhaField);
            return;
        }

        // Executa a autenticação em uma thread separada para não bloquear a interface
        LoadingUtils.runWithLoading("Autenticando...", () -> {
            boolean autentica = userDAO.autenticar(email, senha);

            Platform.runLater(() -> {
                if (autentica) {
                    AnimationUtils.successAnimation(entrarButton);
                    UserProfile userProfile = userDAO.getProfile(email);
                    SessionManager.setCurrentUser(userProfile);

                    try {
                        Thread.sleep(300);
                        App.setRoot("home");
                    } catch (InterruptedException | IOException e) {
                        System.err.println("Falha ao carregar home.fxml!");
                        e.printStackTrace();
                    }
                } else {
                    AnimationUtils.errorAnimation(emailField);
                    AnimationUtils.errorAnimation(senhaField);
                    senhaErrorLabel.setText("Email ou senha inválidos.");
                }
            });
        });
    }
}
