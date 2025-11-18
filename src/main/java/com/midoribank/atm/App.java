package com.midoribank.atm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static StackPane rootPane;

    /**
     * Ponto de entrada principal para a aplicação JavaFX.
     * Configura o palco (Stage) inicial com a tela de splash.


     */
    @Override
    public void start(Stage stage) throws IOException {
        rootPane = new StackPane();

        rootPane.setStyle("-fx-background-color: #29252D;");
        Parent splashScreen = loadFXML("splash");
        rootPane.getChildren().add(splashScreen);

        scene = new Scene(rootPane, 1050, 750);

        // Define o ícone da aplicação
        Image icon = new Image(App.class.getResourceAsStream("/com/midoribank/atm/splash/LogoIcon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("MidoriBank");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Define a tela principal (root) da cena.


     */
    public static void setRoot(String fxml) throws IOException {
        Parent newScreen = loadFXML(fxml);

        if (rootPane.getChildren().isEmpty()) {
            rootPane.getChildren().add(newScreen);
        } else {
            // Substitui a tela atual pela nova
            rootPane.getChildren().set(0, newScreen);
        }
    }

    /**
     * Obtém o painel raiz (StackPane) da aplicação.

     */
    public static StackPane getRootPane() {
        return rootPane;
    }

    /**
     * Carrega um arquivo FXML e retorna o nó raiz.



     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Constrói o caminho para o arquivo FXML com base na convenção de pastas
        String fxmlPath = "/com/midoribank/atm/" + fxml + "/" + fxml + ".fxml";
        System.out.println("Carregando FXML de: " + fxmlPath);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
        return fxmlLoader.load();
    }

    /**
     * Método principal que inicia a aplicação.

     */
    public static void main(String[] args) {
        launch();
    }
}
