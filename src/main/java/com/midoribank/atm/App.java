package com.midoribank.atm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static StackPane rootPane;

    @Override
    public void start(Stage stage) throws IOException {
        rootPane = new StackPane();

        rootPane.setStyle("-fx-background-color: #29252D;");
        Parent splashScreen = loadFXML("splash");
        rootPane.getChildren().add(splashScreen);

        scene = new Scene(rootPane, 1050, 750);

        Image icon = new Image(App.class.getResourceAsStream("/com/midoribank/atm/splash/LogoIco.png"));
        stage.getIcons().add(icon);
        stage.setTitle("MidoriBank");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent newScreen = loadFXML(fxml);

        if (rootPane.getChildren().isEmpty()) {
            rootPane.getChildren().add(newScreen);
        } else {
            rootPane.getChildren().set(0, newScreen);
        }
    }

    public static StackPane getRootPane() {
        return rootPane;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        System.out.println(App.class.getResource("/com/midoribank/atm/" + fxml + "/" + fxml + ".fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/midoribank/atm/" + fxml + "/" + fxml + ".fxml"));
        System.out.println(fxml + " carregado.");
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}