package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class OperacaoValorController {

    @FXML private Label labelTituloOperacao;
    @FXML private Label labelSaldoAtual;
    @FXML private Label labelNumeroConta;
    @FXML private TextField valorField;
    @FXML private Pane paneVinte;
    @FXML private Pane paneCinquenta;
    @FXML private Pane paneCem;
    @FXML private Pane paneDuzentos;
    @FXML private Pane paneContinuar;
    @FXML private Pane paneCancelar;
    @FXML private Pane paneApagar;
    @FXML private Pane paneLimpar;
    @FXML private Label labelErro;

    private UserProfile currentUser;
    private String tipoOperacao;

    /**
     * Inicializa o controller, carregando dados do usuário e configurando eventos.
     */
    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.tipoOperacao = SessionManager.getOperacaoContext();

        carregarDadosUsuario();
        configurarEventos();
        labelErro.setOpacity(0);
    }

    /**
     * Carrega e exibe os dados do usuário e o título da operação na tela.
     */
    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelNumeroConta.setText(currentUser.getNumeroConta());
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
        }

        if (tipoOperacao != null) {
            labelTituloOperacao.setText(tipoOperacao);
        }
    }

    /**
     * Configura os eventos de clique e hover para os botões e painéis da tela.
     */
    private void configurarEventos() {
        paneVinte.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneVinte);
            adicionarValor(20.0);
        });
        paneCinquenta.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneCinquenta);
            adicionarValor(50.0);
        });
        paneCem.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneCem);
            adicionarValor(100.0);
        });
        paneDuzentos.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneDuzentos);
            adicionarValor(200.0);
        });

        paneApagar.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneApagar);
            handleApagar();
        });
        paneLimpar.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneLimpar);
            valorField.clear();
        });
        paneContinuar.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneContinuar);
            handleContinuar();
        });
        paneCancelar.setOnMouseClicked(e -> {
            AnimationUtils.buttonClickAnimation(paneCancelar);
            handleVoltar();
        });

        AnimationUtils.setupNodeHoverEffects(paneVinte);
        AnimationUtils.setupNodeHoverEffects(paneCinquenta);
        AnimationUtils.setupNodeHoverEffects(paneCem);
        AnimationUtils.setupNodeHoverEffects(paneDuzentos);
        AnimationUtils.setupNodeHoverEffects(paneContinuar);
        AnimationUtils.setupNodeHoverEffects(paneCancelar);
        AnimationUtils.setupNodeHoverEffects(paneApagar);
        AnimationUtils.setupNodeHoverEffects(paneLimpar);
    }

    /**
     * Lida com a continuação da operação, validando o valor e avançando para a tela de confirmação.
     */
    private void handleContinuar() {
        String valorTexto = valorField.getText().replace(",", ".");
        if (valorTexto.isEmpty()) {
            exibirMensagemErro("Por favor, insira um valor.");
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto);
            if (valor <= 0) {
                exibirMensagemErro("O valor deve ser positivo.");
                return;
            }

            // Verifica se há saldo suficiente para saque ou transferência
            if (tipoOperacao.equals("Saque") || tipoOperacao.equals("Transferencia")) {
                if (valor > currentUser.getSaldo()) {
                    exibirMensagemErro("Saldo insuficiente para realizar esta operação.");
                    return;
                }
            }

            SessionManager.setCurrentTransaction(valor, this.tipoOperacao);

            App.setRoot("confirmar-operacao");

        } catch (NumberFormatException e) {
            exibirMensagemErro("Valor inválido. Por favor, insira apenas números.");
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível carregar a próxima tela.");
        }
    }

    /**
     * Lida com a ação de voltar para a tela anterior.
     */
    private void handleVoltar() {
        try {
            if (tipoOperacao.equals("Transferencia")) {
                App.setRoot("ConfirmarDadosDestino");
            } else {
                App.setRoot("home");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adiciona um valor pré-definido ao campo de valor.

     */
    private void adicionarValor(double valor) {
        String valorAtualTexto = valorField.getText();
        try {
            double valorAtual = valorAtualTexto.isEmpty() ? 0 : Double.parseDouble(valorAtualTexto);
            valorField.setText(String.valueOf(valorAtual + valor));
        } catch (NumberFormatException e) {
            valorField.setText(String.valueOf(valor));
        }
    }

    /**
     * Apaga o último caractere do campo de valor.
     */
    private void handleApagar() {
        String texto = valorField.getText();
        if (texto != null && !texto.isEmpty()) {
            valorField.setText(texto.substring(0, texto.length() - 1));
        }
    }

    /**
     * Exibe uma mensagem de erro temporária na tela.

     */
    private void exibirMensagemErro(String mensagem) {
        AnimationUtils.errorAnimation(valorField);
        labelErro.setText(mensagem);
        AnimationUtils.fadeIn(labelErro, 200);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> AnimationUtils.fadeOut(labelErro, 200));
        pause.play();
    }
}
