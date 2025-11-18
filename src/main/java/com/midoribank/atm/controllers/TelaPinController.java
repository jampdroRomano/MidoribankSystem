package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.OperacaoService;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.CriptografiaUtils;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;


public class TelaPinController {

    @FXML private PasswordField senhaField;
    @FXML private Pane button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    @FXML private Node paneConfirmar;
    @FXML private Node paneVoltar;
    @FXML private Pane buttonApagar;
    @FXML private Pane buttonC;
    @FXML private Label labelTitulo;

    private UserProfile currentUser;
    private final int MAX_SENHA_LENGTH = 4;

    private OperacaoService operacaoService;

    private SessionManager.PinEntryContext context;
    private String senhaCadastroTemporaria = null;

    /**
     * Inicializa o controller, configurando o contexto da tela de PIN (cadastro ou operação).
     */
    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.context = SessionManager.getPinEntryContext();
        this.operacaoService = new OperacaoService();

        configurarVisuais();
        configurarBotoesNumericos();
        configurarControles();
        configurarBotoesEdicao();
    }

    /**
     * Configura os elementos visuais da tela, como o título, de acordo com o contexto.
     */
    private void configurarVisuais() {
        if (labelTitulo != null) {
            if (context == SessionManager.PinEntryContext.CADASTRO_PIN) {
                labelTitulo.setText("Digite uma senha para o seu cartão");
            } else {
                labelTitulo.setText("Digite a senha do seu cartão");
            }
        }
    }

    /**
     * Configura os eventos de clique para os botões numéricos do teclado virtual.
     */
    private void configurarBotoesNumericos() {
        Pane[] panes = {button0, button1, button2, button3, button4, button5, button6, button7, button8, button9};
        for (int i = 0; i < panes.length; i++) {
            Pane pane = panes[i];
            if (pane != null) {
                final String numero = String.valueOf(i);
                pane.setOnMouseClicked(e -> adicionarDigito(numero));
                AnimationUtils.setupNodeHoverEffects(pane);
            }
        }
    }

    /**
     * Configura os eventos de clique para os botões de controle (confirmar e voltar).
     */
    private void configurarControles() {
        if (paneConfirmar != null) {
            paneConfirmar.setOnMouseClicked(e -> handleConfirmarSenha());
            AnimationUtils.setupNodeHoverEffects(paneConfirmar);
        }
        if (paneVoltar != null) {
            paneVoltar.setOnMouseClicked(e -> handleVoltar());
            AnimationUtils.setupNodeHoverEffects(paneVoltar);
        }
    }

    /**
     * Configura os eventos de clique para os botões de edição (apagar e limpar).
     */
    private void configurarBotoesEdicao() {
        if (buttonApagar != null) {
            buttonApagar.setOnMouseClicked(e -> apagarDigito());
            AnimationUtils.setupNodeHoverEffects(buttonApagar);
        }
        if (buttonC != null) {
            buttonC.setOnMouseClicked(e -> limparSenha());
            AnimationUtils.setupNodeHoverEffects(buttonC);
        }
    }

    /**
     * Lida com a ação de voltar para a tela anterior, dependendo do contexto.
     */
    @FXML
    private void handleVoltar() {
        try {
            if (context == SessionManager.PinEntryContext.CADASTRO_PIN) {
                App.setRoot("CadastroCartao");
            } else {
                App.setRoot("confirmar-operacao");
            }
        } catch (IOException e) {
            e.printStackTrace();
            exibirMensagemErro("Não foi possível voltar para a tela anterior.");
        }
    }

    /**
     * Lida com a confirmação da senha, direcionando para o fluxo de cadastro ou operação.
     */
    @FXML
    private void handleConfirmarSenha() {
        switch (context) {
            case CADASTRO_PIN:
                handleConfirmarCadastroPin();
                break;
            case OPERACAO_FINANCEIRA:
                handleConfirmarOperacaoPin();
                break;
        }
    }

    /**
     * Lida com a confirmação do PIN durante o processo de cadastro.
     */
    private void handleConfirmarCadastroPin() {
        String senhaDigitada = senhaField.getText();

        if (senhaCadastroTemporaria == null) {
            if (senhaDigitada.length() != MAX_SENHA_LENGTH) {
                exibirMensagemErro("A senha deve ter " + MAX_SENHA_LENGTH + " dígitos.");
                return;
            }
            this.senhaCadastroTemporaria = senhaDigitada;
            labelTitulo.setText("Digite novamente a senha");
            limparSenha();

        } else {
            if (senhaDigitada.equals(this.senhaCadastroTemporaria)) {
                SessionManager.setCadastroSenhaCartao(senhaDigitada);

                SessionManager.salvarCadastroCompletoNoBanco().thenAccept(sucesso -> {
                    Platform.runLater(() -> {
                        if(sucesso) {
                            SessionManager.clearCadastroData();
                            exibirMensagemInfo("Sucesso", "Cadastro realizado! Faça o login.");
                            try {
                                App.setRoot("Login");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            exibirMensagemErro("Falha crítica no cadastro. Tente novamente mais tarde.");
                        }
                    });
                });

            } else {
                exibirMensagemErro("As senhas não conferem! Tente novamente.");
                this.senhaCadastroTemporaria = null;
                labelTitulo.setText("Digite uma senha para o seu cartão");
                limparSenha();
            }
        }
    }

    /**
     * Lida com a confirmação do PIN para uma operação financeira.
     */
    private void handleConfirmarOperacaoPin() {
        if (currentUser == null) {
            exibirMensagemErro("Erro de sessão. Faça login novamente.");
            try { App.setRoot("Login"); } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        String senhaDigitada = senhaField.getText();

        if (senhaDigitada.length() != MAX_SENHA_LENGTH) {
            exibirMensagemErro("A senha do cartão deve ter " + MAX_SENHA_LENGTH + " dígitos.");
            return;
        }

        String senhaHashBanco = currentUser.getSenhaCartaoHash();
        boolean senhaCorreta = CriptografiaUtils.checkPassword(senhaDigitada, senhaHashBanco);

        if (senhaCorreta) {
            executarOperacaoFinanceira();
        }
        else {
            exibirMensagemErro("Senha do cartão incorreta!");
            limparSenha();
        }
    }

    /**
     * Executa a operação financeira (saque, depósito ou transferência) após a validação do PIN.
     */
    private void executarOperacaoFinanceira() {
        String tipo = SessionManager.getCurrentTransactionType();
        double valor = SessionManager.getCurrentTransactionAmount();
        double saldoAtual = currentUser.getSaldo();
        java.util.concurrent.CompletableFuture<Boolean> futuroSucesso;

        if ("Saque".equals(tipo)) {
            if (valor <= 0 || valor > saldoAtual) {
                exibirMensagemErro("Saldo insuficiente. Operação cancelada.");
                SessionManager.clearTransaction();
                try { App.setRoot("home"); } catch (IOException e) { e.printStackTrace(); }
                return;
            }

            futuroSucesso = this.operacaoService.executarSaque(currentUser, valor);

        } else if ("Depósito".equals(tipo)) {
            if (valor <= 0) {
                exibirMensagemErro("Valor de depósito deve ser positivo.");
                SessionManager.clearTransaction();
                try { App.setRoot("home"); } catch (IOException e) { e.printStackTrace(); }
                return;
            }

            futuroSucesso = this.operacaoService.executarDeposito(currentUser, valor);

        } else if ("Transferencia".equals(tipo)) {
            UserProfile contaDestino = SessionManager.getContaDestino();

            if (contaDestino == null) {
                exibirMensagemErro("Erro crítico: Conta de destino não encontrada na sessão.");
                return;
            }
            if (valor <= 0 || valor > saldoAtual) {
                exibirMensagemErro("Saldo insuficiente. Operação cancelada.");
                SessionManager.clearTransaction();
                try { App.setRoot("home"); } catch (IOException e) { e.printStackTrace(); }
                return;
            }

            futuroSucesso = this.operacaoService.executarTransferencia(currentUser, contaDestino, valor);
            
        } else {
            exibirMensagemErro("Tipo de operação desconhecido: " + tipo);
            return;
        }

        futuroSucesso.thenAccept(sucessoNoBanco -> {
            Platform.runLater(() -> {
                if (sucessoNoBanco) {
                    double novoSaldo;
                    if ("Saque".equals(tipo) || "Transferencia".equals(tipo)) { 
                        novoSaldo = saldoAtual - valor;
                    } else {
                        novoSaldo = saldoAtual + valor;
                    }
                    currentUser.setSaldo(novoSaldo);
                    
                    if ("Transferencia".equals(tipo)) { 
                        SessionManager.clearTransferenciaData();
                    }

                    try {
                        App.setRoot("ConclusaoOperacao");
                    } catch (IOException e) {
                        e.printStackTrace();
                        exibirMensagemErro("Não foi possível carregar a tela de conclusão.");
                    }
                } else {
                    exibirMensagemErro("Falha de comunicação com o banco. A operação foi cancelada. Tente mais tarde.");
                    SessionManager.clearTransaction();
                    try { App.setRoot("home"); } catch (IOException e) { e.printStackTrace(); }
                }
            });
        });
    }

    /**
     * Adiciona um dígito ao campo de senha.

     */
    private void adicionarDigito(String digito) {
        if (senhaField.getText().length() < MAX_SENHA_LENGTH) {
            senhaField.appendText(digito);
        }
    }

    /**
     * Apaga o último dígito do campo de senha.
     */
    private void apagarDigito() {
        String currentText = senhaField.getText();
        if (!currentText.isEmpty()) {
            senhaField.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    /**
     * Limpa o campo de senha.
     */
    private void limparSenha() {
        senhaField.clear();
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

    /**
     * Exibe uma mensagem de informação em um pop-up.


     */
    private void exibirMensagemInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
