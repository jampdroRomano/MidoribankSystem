package com.midoribank.atm.controllers;

import com.midoribank.atm.App;
import com.midoribank.atm.dao.MovimentacaoDAO;
import com.midoribank.atm.models.Movimentacao;
import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.services.SessionManager;
import com.midoribank.atm.services.PdfGenerationService;
import com.midoribank.atm.utils.AnimationUtils;
import com.midoribank.atm.utils.LoadingUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import java.util.concurrent.CompletableFuture;

public class ExportarExtratoController {

    @FXML private Label labelSaldoAtual;
    @FXML private Label labelNumeroConta;
    @FXML private Pane paneVoltar;
    @FXML private Pane paneGerarExtrato;
    @FXML private Pane paneAbaHistorico;
    @FXML private Circle circle30;
    @FXML private Circle circle60;
    @FXML private Circle circle90;

    private UserProfile currentUser;
    private MovimentacaoDAO movimentacaoDAO;
    private int periodoSelecionado = 30;

    private final Paint FILL_SELECTED = Color.web("#14FF00");
    private final Paint FILL_TRANSPARENT = Color.TRANSPARENT;

    @FXML
    public void initialize() {
        this.currentUser = SessionManager.getCurrentUser();
        this.movimentacaoDAO = new MovimentacaoDAO();

        carregarDadosUsuario();
        configurarEventos();
        
        selecionarPeriodo(30);
    }

    private void carregarDadosUsuario() {
        if (currentUser != null) {
            labelSaldoAtual.setText(String.format("R$ %.2f", currentUser.getSaldo()));
            labelNumeroConta.setText(currentUser.getNumeroConta());
        }
    }

    private void configurarEventos() {
        paneVoltar.setOnMouseClicked(e -> voltarParaHome());
        paneAbaHistorico.setOnMouseClicked(e -> voltarParaExtrato());
        
        circle30.setOnMouseClicked(e -> selecionarPeriodo(30));
        circle60.setOnMouseClicked(e -> selecionarPeriodo(60));
        circle90.setOnMouseClicked(e -> selecionarPeriodo(90));

        paneGerarExtrato.setOnMouseClicked(e -> handleGerarExtrato());
        
        AnimationUtils.setupNodeHoverEffects(paneVoltar);
        AnimationUtils.setupNodeHoverEffects(paneAbaHistorico);
        AnimationUtils.setupNodeHoverEffects(paneGerarExtrato);
        
        paneVoltar.setOnMouseEntered(e -> paneVoltar.setCursor(Cursor.HAND));
        paneVoltar.setOnMouseExited(e -> paneVoltar.setCursor(Cursor.DEFAULT));

        circle30.setOnMouseEntered(e -> circle30.setCursor(Cursor.HAND));
        circle30.setOnMouseExited(e -> circle30.setCursor(Cursor.DEFAULT));

        circle60.setOnMouseEntered(e -> circle60.setCursor(Cursor.HAND));
        circle60.setOnMouseExited(e -> circle60.setCursor(Cursor.DEFAULT));

        circle90.setOnMouseEntered(e -> circle90.setCursor(Cursor.HAND));
        circle90.setOnMouseExited(e -> circle90.setCursor(Cursor.DEFAULT));
    }

    private void selecionarPeriodo(int dias) {
        periodoSelecionado = dias;
        circle30.setFill(dias == 30 ? FILL_SELECTED : FILL_TRANSPARENT);
        circle60.setFill(dias == 60 ? FILL_SELECTED : FILL_TRANSPARENT);
        circle90.setFill(dias == 90 ? FILL_SELECTED : FILL_TRANSPARENT);
    }

    private void handleGerarExtrato() {
        LoadingUtils.showLoading("Gerando PDF...");

        CompletableFuture.runAsync(() -> {
            try {
                LocalDate dataFiltro = LocalDate.now().minusDays(periodoSelecionado);
                List<Movimentacao> todas = movimentacaoDAO.listarMovimentacoesPorContaId(currentUser.getContaId());
                
                List<Movimentacao> filtradas = todas.stream()
                        .filter(m -> m.getDataHora().toLocalDate().isAfter(dataFiltro))
                        .collect(Collectors.toList());

                String userHome = System.getProperty("user.home");
                String baseName = "Extrato_MidoriBank_" + periodoSelecionado + "dias.pdf";
                String baseFilePath = userHome + File.separator + "Desktop" + File.separator + baseName;

                String finalFilePath = getAvailableFilePath(baseFilePath);

                PdfGenerationService pdfService = new PdfGenerationService();
                boolean sucesso = pdfService.gerarPdf(currentUser, filtradas, finalFilePath);

                Platform.runLater(() -> {
                    LoadingUtils.hideLoading();
                    if (sucesso) {
                        exibirMensagemInfo("Sucesso", "Extrato PDF gerado com sucesso!\nSalvo em: " + finalFilePath);
                    } else {
                        exibirMensagemErro("Erro", "Não foi possível gerar o extrato em PDF.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    LoadingUtils.hideLoading();
                    exibirMensagemErro("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
                });
            }
        });
    }

    private String getAvailableFilePath(String baseFilePath) {
        File file = new File(baseFilePath);
        if (!file.exists()) {
            return baseFilePath;
        }

        String directory = file.getParent();
        String fileName = file.getName();
        String nameWithoutExt, ext;

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            nameWithoutExt = fileName;
            ext = "";
        } else {
            nameWithoutExt = fileName.substring(0, dotIndex);
            ext = fileName.substring(dotIndex);
        }

        int counter = 1;
        File newFile;
        do {
            String newFileName = String.format("%s (%d)%s", nameWithoutExt, counter, ext);
            newFile = new File(directory, newFileName);
            counter++;
        } while (newFile.exists());

        return newFile.getAbsolutePath();
    }


    private void voltarParaExtrato() {
        try {
            App.setRoot("Extrato");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void voltarParaHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void exibirMensagemErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void exibirMensagemInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}