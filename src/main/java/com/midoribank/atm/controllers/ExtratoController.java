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

    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            labelNumeroConta.setText(currentUser.getNumeroConta());
        } else {
            try {
                App.setRoot("Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarMovimentacoes() {

        if (currentUser != null) {
            List<Movimentacao> movimentacoes = movimentacaoDAO.listarMovimentacoesPorContaId(currentUser.getContaId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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

    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irParaExportar() {
        try {
            App.setRoot("ExportarExtrato");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}