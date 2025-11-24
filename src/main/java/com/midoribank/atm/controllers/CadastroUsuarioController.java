package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.dao.UserDAO;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.LoadingUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


public class CadastroUsuarioController {

    @FXML private TextField nomeField;
    @FXML private TextField emailFieldCadastro;
    @FXML private PasswordField senhaFieldCadastro;
    @FXML private PasswordField confirmeSenha;
    @FXML private Button cadastrarButton;
    @FXML private ImageView btnVoltarCadastrar;
    @FXML private Label nomeErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label senhaErrorLabel;
    @FXML private Label confirmeSenhaErrorLabel;
    @FXML private Label cadastroErrorLabel;
    

    private UserDAO userDAO;

    /**
     * Inicializa o controller, configurando o DAO, os handlers de eventos e as animações.
     */
    @FXML
    public void initialize() {
        this.userDAO = new UserDAO();

        // Configura o botão de cadastro
        if (cadastrarButton != null) {
            cadastrarButton.setOnAction(e -> handleCadastroClick());
            AnimationUtils.setupButtonHoverEffects(cadastrarButton);

            cadastrarButton.setFocusTraversable(false);
        } else {
            System.err.println("Aviso: cadastrarButton não encontrado no FXML.");
        }

        // Configura o botão de voltar
        if (btnVoltarCadastrar != null) {
            btnVoltarCadastrar.setOnMouseClicked(e -> handleVoltarClick());
            AnimationUtils.setupNodeHoverEffects(btnVoltarCadastrar);
        } else {
            System.err.println("Aviso: btnVoltarCadastrar não encontrado no FXML.");
        }
        
        nomeField.textProperty().addListener((observable, oldValue, newValue) -> {
            nomeErrorLabel.setText("");
        });
        
        emailFieldCadastro.textProperty().addListener((observable, oldValue, newValue) -> {
            emailErrorLabel.setText("");
        });
        
        senhaFieldCadastro.textProperty().addListener((observable, oldValue, newValue) -> {
            senhaErrorLabel.setText("");
        });
        
        confirmeSenha.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmeSenhaErrorLabel.setText("");
            cadastroErrorLabel.setText("");
        });

        // Foca no campo de nome ao iniciar a tela
        Platform.runLater(() -> nomeField.requestFocus());
    }

    /**
     * Lida com o clique no botão de cadastro, validando os dados do usuário e avançando para a próxima etapa.
     */
    private void handleCadastroClick() {
        String nome = nomeField.getText();
        String email = emailFieldCadastro.getText();
        String senha = senhaFieldCadastro.getText();
        String confirmacaoSenha = confirmeSenha.getText();

        boolean hasError = false;

        if (nome.isEmpty()) {
            nomeErrorLabel.setText("O campo nome não pode estar vazio.");
            AnimationUtils.errorAnimation(nomeField);
            hasError = true;
        }

        if (email.isEmpty()) {
            emailErrorLabel.setText("O campo de e-mail não pode estar vazio.");
            AnimationUtils.errorAnimation(emailFieldCadastro);
            hasError = true;
        } else if (!isValidEmail(email)) {
            emailErrorLabel.setText("O e-mail fornecido não é válido.");
            AnimationUtils.errorAnimation(emailFieldCadastro);
            hasError = true;
        }

        if (senha.isEmpty()) {
            senhaErrorLabel.setText("O campo de senha não pode estar vazio.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            hasError = true;
        } else if (!isStrongPassword(senha)) {
            senhaErrorLabel.setText("Senha fraca. Use 8+ caracteres com maiúscula, minúscula e número.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            hasError = true;
        }

        if (confirmacaoSenha.isEmpty()) {
            confirmeSenhaErrorLabel.setText("O campo de confirmação de senha não pode estar vazio.");
            AnimationUtils.errorAnimation(confirmeSenha);
            hasError = true;
        } else if (!senha.isEmpty() && !senha.equals(confirmacaoSenha)) {
            confirmeSenhaErrorLabel.setText("As senhas não conferem.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            AnimationUtils.errorAnimation(confirmeSenha);
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Verifica se o e-mail já existe no banco de dados
        LoadingUtils.runWithLoading("Verificando dados...", () -> {
            boolean emailJaExiste = userDAO.verificarEmailExistente(email);

            Platform.runLater(() -> {
                if (emailJaExiste) {
                    emailErrorLabel.setText("Este e-mail já está cadastrado.");
                    AnimationUtils.errorAnimation(emailFieldCadastro);
                } else {
                    // Salva os dados do usuário na sessão e avança para a tela de cadastro de cartão
                    SessionManager.setCadastroUsuario(nome, email, senha);
                    try {
                        App.setRoot("CadastroCartao");
                    } catch (IOException e) {
                        e.printStackTrace();
                        cadastroErrorLabel.setText("Não foi possível carregar a tela de cadastro de cartão.");
                    }
                }
            });
        });
    }

    /**
     * Valida se a senha é forte o suficiente.


     */
    private boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Valida se o e-mail tem um formato válido e pertence a um domínio suportado.


     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return false;
        }
        String[] parts = email.split("@");
        String domain = parts[1].toLowerCase();
        List<String> validDomains = Arrays.asList("gmail.com", "hotmail.com", "outlook.com", "apple.com", "icloud.com", "yahoo.com", "live.com", "msn.com");
        return validDomains.contains(domain);
    }

    /**
     * Lida com o clique no botão de voltar, retornando para a tela de login.
     */
    @FXML
    private void handleVoltarClick() {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            System.err.println("Falha ao carregar Login.fxml!");
            e.printStackTrace();
        }
    }
}
