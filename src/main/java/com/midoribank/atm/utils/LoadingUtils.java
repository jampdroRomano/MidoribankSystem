package com.midoribank.atm.utils;

import com.midoribank.atm.App;
import com.midoribank.atm.controllers.LoadingController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.concurrent.CompletableFuture;

/**
 * Classe utilitária para exibir e ocultar uma sobreposição de carregamento (loading overlay).

 */
public class LoadingUtils {

    private static Parent loadingNode;
    private static LoadingController controller;

    // Bloco estático para carregar o FXML da tela de loading uma única vez.
    static {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/midoribank/atm/loading/loading.fxml"));
            loadingNode = loader.load();
            controller = loader.getController();
            loadingNode.setMouseTransparent(false); // Garante que a tela de loading bloqueie cliques.

            // Centraliza o painel de loading dentro da sobreposição.
            if (loadingNode instanceof Pane && !((Pane) loadingNode).getChildren().isEmpty() && ((Pane) loadingNode).getChildren().get(0) instanceof Pane) {
                Pane innerPane = (Pane) ((Pane) loadingNode).getChildren().get(0);
                innerPane.layoutXProperty().bind(((Pane) loadingNode).widthProperty().subtract(innerPane.getPrefWidth()).divide(2));
                innerPane.layoutYProperty().bind(((Pane) loadingNode).heightProperty().subtract(innerPane.getPrefHeight()).divide(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
            loadingNode = null;
            controller = null;
        }
    }

    /**
     * Exibe a sobreposição de carregamento com uma mensagem específica.

     */
    public static void showLoading(String message) {
        Platform.runLater(() -> {
            if (loadingNode != null) {
                if (controller != null) {
                    controller.setLoadingText(message);
                }

                if (!App.getRootPane().getChildren().contains(loadingNode)) {
                    // Vincula o tamanho da sobreposição ao tamanho da janela principal.
                    ((Region)loadingNode).prefWidthProperty().bind(App.getRootPane().widthProperty());
                    ((Region)loadingNode).prefHeightProperty().bind(App.getRootPane().heightProperty());
                    App.getRootPane().getChildren().add(loadingNode);
                }
            } else {
                System.err.println("Erro: loadingNode não pôde ser carregado.");
            }
        });
    }

    /**
     * Oculta a sobreposição de carregamento.
     */
    public static void hideLoading() {
        Platform.runLater(() -> {
            if (loadingNode != null) {
                ((Region)loadingNode).prefWidthProperty().unbind();
                ((Region)loadingNode).prefHeightProperty().unbind();
                App.getRootPane().getChildren().remove(loadingNode);
            }
        });
    }

    /**
     * Executa uma tarefa assíncrona que retorna um valor, exibindo uma tela de loading durante a execução.




     */
    public static <T> CompletableFuture<T> runWithLoading(String message, java.util.concurrent.Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            showLoading(message);
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                hideLoading();
            }
        });
    }

    /**
     * Executa uma tarefa assíncrona que não retorna valor, exibindo uma tela de loading durante a execução.



     */
    public static CompletableFuture<Void> runWithLoading(String message, Runnable task) {
        return CompletableFuture.runAsync(() -> {
            showLoading(message);
            try {
                task.run();
            } finally {
                hideLoading();
            }
        });
    }
}
