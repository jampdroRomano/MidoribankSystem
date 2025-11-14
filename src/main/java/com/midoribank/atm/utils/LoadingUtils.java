package com.midoribank.atm.utils;

import com.midoribank.atm.App;
import com.midoribank.atm.controllers.LoadingController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.concurrent.CompletableFuture;

public class LoadingUtils {

    private static Parent loadingNode;
    private static LoadingController controller;

    static {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/midoribank/atm/loading/loading.fxml"));
            loadingNode = loader.load();
            controller = loader.getController();
            loadingNode.setMouseTransparent(false);

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

    public static void showLoading(String message) {
        Platform.runLater(() -> {
            if (loadingNode != null) {
                if (controller != null) {
                    controller.setLoadingText(message);
                }

                if (!App.getRootPane().getChildren().contains(loadingNode)) {
                    ((Region)loadingNode).prefWidthProperty().bind(App.getRootPane().widthProperty());
                    ((Region)loadingNode).prefHeightProperty().bind(App.getRootPane().heightProperty());
                    App.getRootPane().getChildren().add(loadingNode);
                }
            } else {
                System.err.println("Erro: loadingNode não pôde ser carregado.");
            }
        });
    }

    public static void hideLoading() {
        Platform.runLater(() -> {
            if (loadingNode != null) {
                ((Region)loadingNode).prefWidthProperty().unbind();
                ((Region)loadingNode).prefHeightProperty().unbind();
                App.getRootPane().getChildren().remove(loadingNode);
            }
        });
    }

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