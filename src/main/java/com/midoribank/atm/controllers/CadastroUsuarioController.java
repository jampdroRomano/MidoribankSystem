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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        this.userDAO = new UserDAO();

        if (cadastrarButton != null) {
            cadastrarButton.setOnAction(e -> handleCadastroClick());
            AnimationUtils.setupButtonHoverEffects(cadastrarButton);

            cadastrarButton.setFocusTraversable(false);
        } else {
            System.err.println("Aviso: cadastrarButton não encontrado no FXML.");
        }

        if (btnVoltarCadastrar != null) {
            btnVoltarCadastrar.setOnMouseClicked(e -> handleVoltarClick());
            AnimationUtils.setupNodeHoverEffects(btnVoltarCadastrar);
        } else {
            System.err.println("Aviso: btnVoltarCadastrar não encontrado no FXML.");
        }

        Platform.runLater(() -> nomeField.requestFocus());
    }

    private void handleCadastroClick() {
        String nome = nomeField.getText();
        String email = emailFieldCadastro.getText();
        String senha = senhaFieldCadastro.getText();
        String confirmacaoSenha = confirmeSenha.getText();

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder();

        if (nome.isEmpty()) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- O campo nome não pode estar vazio.");
            AnimationUtils.errorAnimation(nomeField);
            hasError = true;
        }

        if (email.isEmpty()) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- O campo de e-mail não pode estar vazio.");
            AnimationUtils.errorAnimation(emailFieldCadastro);
            hasError = true;
        } else if (!isValidEmail(email)) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- O e-mail fornecido não é válido ou não é de um domínio suportado.");
            AnimationUtils.errorAnimation(emailFieldCadastro);
            hasError = true;
        }

        if (senha.isEmpty()) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- O campo de senha não pode estar vazio.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            hasError = true;
        } else if (!isStrongPassword(senha)) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- A senha deve ter no mínimo 8 caracteres, incluindo uma letra maiúscula, uma minúscula e um número.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            hasError = true;
        }

        if (confirmacaoSenha.isEmpty()) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- O campo de confirmação de senha não pode estar vazio.");
            AnimationUtils.errorAnimation(confirmeSenha);
            hasError = true;
        } else if (!senha.isEmpty() && !senha.equals(confirmacaoSenha)) {
            if (errorMessage.length() > 0) errorMessage.append("\n");
            errorMessage.append("- As senhas não conferem.");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            AnimationUtils.errorAnimation(confirmeSenha);
            senhaFieldCadastro.clear();
            confirmeSenha.clear();
            hasError = true;
        }

        if (hasError) {
            exibirMensagemErro(errorMessage.toString());
            return;
        }

        LoadingUtils.runWithLoading("Verificando dados...", () -> {
            boolean emailJaExiste = userDAO.verificarEmailExistente(email);

            Platform.runLater(() -> {
                if (emailJaExiste) {
                    exibirMensagemErro("Este e-mail já está cadastrado.");
                    AnimationUtils.errorAnimation(emailFieldCadastro);
                } else {
                    SessionManager.setCadastroUsuario(nome, email, senha);
                    try {
                        App.setRoot("CadastroCartao");
                    } catch (IOException e) {
                        e.printStackTrace();
                        exibirMensagemErro("Não foi possível carregar a tela de cadastro de cartão.");
                    }
                }
            });
        });
    }

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

    @FXML
    private void handleVoltarClick() {
        try {
            App.setRoot("Login");
        } catch (IOException e) {
            System.err.println("Falha ao carregar Login.fxml!");
            e.printStackTrace();
        }
    }

    private void exibirMensagemErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Cadastro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}