package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.services.RecuperacaoSenhaService;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.LoadingUtils;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.util.concurrent.CompletableFuture;
import javafx.util.Duration;

public class VerificarCodigoEmailController {

    @FXML
    private TextField codigoField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button entrarButton;
    @FXML
    private ImageView btnVoltarLogin;
    @FXML
    private Label timerLabel;
    @FXML
    private Label reenviarLabel;

    private RecuperacaoSenhaService recuperacaoService;
    private Timeline timeline;
    private final IntegerProperty segundosRestantes = new SimpleIntegerProperty(15 * 60);

    @FXML
    public void initialize() {
        this.recuperacaoService = new RecuperacaoSenhaService();

        // Agora isto vai funcionar porque os IDs estão corretos
        AnimationUtils.setupButtonHoverEffects(entrarButton);
        AnimationUtils.setupNodeHoverEffects(btnVoltarLogin);

        if (reenviarLabel != null) {
            AnimationUtils.setupNodeHoverEffects(reenviarLabel);
            reenviarLabel.setOnMouseClicked(e -> handleReenviarCodigo());
        }

        startTimer();
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        segundosRestantes.set(15 * 60);

        timerLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int totalSegundos = segundosRestantes.get();
            int minutos = totalSegundos / 60;
            int segundos = totalSegundos % 60;
            return String.format("%02d:%02d", minutos, segundos);
        }, segundosRestantes));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int segundos = segundosRestantes.get();
            if (segundos > 0) {
                segundosRestantes.set(segundos - 1);
            } else {
                timeline.stop();
                errorLabel.setText("Código expirado. Por favor, solicite um novo.");
                if (reenviarLabel != null) {
                    reenviarLabel.setText("Reenviar código");
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
            timerLabel.textProperty().unbind();
        }
    }

    @FXML
    private void handleVerificarCodigo() {
        String codigo = codigoField.getText();
        String email = SessionManager.getEmailRecuperacao();

        if (codigo.isEmpty() || codigo.length() != 6) {
            errorLabel.setText("O código deve ter 6 dígitos.");
            AnimationUtils.errorAnimation(codigoField);
            return;
        }

        if (email == null) {
            exibirMensagemErro("Erro de sessão. Volte e informe seu e-mail novamente.");
            return;
        }

        recuperacaoService.validarCodigo(email, codigo).thenAccept(codigoValido -> {
            Platform.runLater(() -> {
                if (codigoValido) {
                    stopTimer();
                    SessionManager.setCodigoVerificado(codigo);
                    try {
                        App.setRoot("AlterarSenha");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    errorLabel.setText("Código inválido ou expirado. Tente novamente.");
                    AnimationUtils.errorAnimation(codigoField);
                }
            });
        });
    }

    @FXML
    private void handleReenviarCodigo() {
        String email = SessionManager.getEmailRecuperacao();
        if (email == null) {
            exibirMensagemErro("Erro de sessão. Volte e informe seu e-mail novamente.");
            return;
        }

        if (reenviarLabel != null) {
            reenviarLabel.setText("Enviando...");
        }

        recuperacaoService.iniciarRecuperacao(email).thenAccept(sucesso -> {
            Platform.runLater(() -> {
                if (sucesso) {
                    if (reenviarLabel != null) {
                        reenviarLabel.setText("Mandar outro código");
                    }
                    startTimer();
                } else {
                    exibirMensagemErro("Não foi possível reenviar o código. Tente novamente mais tarde.");
                }
            });
        });
    }

    @FXML
    private void handleVoltar() throws IOException {
        stopTimer();
        App.setRoot("EnviarEmailRecuperacao");
    }

    private void exibirMensagemErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}