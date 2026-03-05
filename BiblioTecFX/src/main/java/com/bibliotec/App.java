package com.bibliotec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLogin();

        stage.setTitle("BiblioTec");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            scene.getStylesheets().add(App.class.getResource("css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMain() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);
            scene.getStylesheets().add(App.class.getResource("css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
