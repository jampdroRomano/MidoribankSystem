package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.dao.UserDAO;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.LoadingUtils;
import java.io.IOException;
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

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmacaoSenha.isEmpty()) {
            exibirMensagemErro("Preencha todos os campos!");
            AnimationUtils.errorAnimation(nomeField);
            AnimationUtils.errorAnimation(emailFieldCadastro);
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            AnimationUtils.errorAnimation(confirmeSenha);
            return;
        }

        if (!senha.equals(confirmacaoSenha)) {
            exibirMensagemErro("As senhas não conferem!");
            AnimationUtils.errorAnimation(senhaFieldCadastro);
            AnimationUtils.errorAnimation(confirmeSenha);
            senhaFieldCadastro.clear();
            confirmeSenha.clear();
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