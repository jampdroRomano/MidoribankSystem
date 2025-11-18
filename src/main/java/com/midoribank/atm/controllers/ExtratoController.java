package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.dao.MovimentacaoDAO;
import com.midoribank.atm.models.Movimentacao;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.utils.AnimationUtils; 
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class ExtratoController {

    @FXML
    private Pane paneVoltar;
    @FXML
    private Label labelSaldoAtual;
    @FXML
    private Label labelNumeroConta;
    @FXML
    private VBox vboxMovimentacoes;
    @FXML
    private Pane paneAbaExportar;

    private UserProfile currentUser;
    private MovimentacaoDAO movimentacaoDAO;

    /**
     * Inicializa o controller, carregando os dados do usuário e as movimentações da conta.
     */
    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.movimentacaoDAO = new MovimentacaoDAO();
        carregarDadosUsuario();
        carregarMovimentacoes();
        
        paneVoltar.setOnMouseClicked(e -> voltarParaHome());
        
        if (paneAbaExportar != null) {
            paneAbaExportar.setOnMouseClicked(e -> irParaExportar());
            AnimationUtils.setupNodeHoverEffects(paneAbaExportar); 
        }

        paneVoltar.setOnMouseEntered(e -> paneVoltar.setCursor(Cursor.HAND));
        paneVoltar.setOnMouseExited(e -> paneVoltar.setCursor(Cursor.DEFAULT));
    }

    /**
     * Carrega e exibe os dados do usuário na tela.
     */
    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            labelNumeroConta.setText(currentUser.getNumeroConta());
        } else {
            // Redireciona para a tela de login se não houver usuário na sessão
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Carrega as movimentações da conta do usuário e as exibe na tela.
     */
    private void carregarMovimentacoes() {

        if (currentUser != null) {
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarMovimentacoesPorContaId(currentUser.getContaId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Cria um painel para cada movimentação e o adiciona ao VBox
            for (Movimentacao mov : movimentacoes) {
                Pane movPane = new Pane();
                movPane.setPrefHeight(80);
                movPane.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 10;");

                Label tipoLabel = new Label(mov.getTipoMovimentacao().replace("_", " "));
                tipoLabel.setLayoutX(20);
                tipoLabel.setLayoutY(10);
                tipoLabel.setStyle("-fx-font-weight: bold;");

                Label valorLabel = new Label(String.format("R$ %.2f", mov.getValor()));
                valorLabel.setLayoutX(20);
                valorLabel.setLayoutY(40);

                Label dataLabel = new Label(mov.getDataHora().format(dateFormatter));
                dataLabel.setLayoutX(400);
                dataLabel.setLayoutY(10);

                Label horaLabel = new Label(mov.getDataHora().format(timeFormatter));
                horaLabel.setLayoutX(400);
                horaLabel.setLayoutY(40);

                movPane.getChildren().addAll(tipoLabel, valorLabel, dataLabel, horaLabel);
                vboxMovimentacoes.getChildren().add(movPane);
            }
        }
    }

    /**
     * Lida com a ação de voltar para a tela inicial.
     */
    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega para a tela de exportar extrato.
     */
    private void irParaExportar() {
        try {
            App.setRoot("ExportarExtrato");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
