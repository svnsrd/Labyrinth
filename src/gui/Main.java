package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Hauptklasse, welche den Startpunkt der JavaFX-Applikation darstellt.
 * Sie lädt das Spieleinstellungsfenster, aus welcher das Spiel erzeugt wird.
 *
 * @author Suwendi Suriadi (WInf10477) [Repository 37]
 * @version 08.08.2021
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Anwendung beenden, wenn eine Ausnahme nicht abgefangen worden ist
        Thread.currentThread().setUncaughtExceptionHandler((Thread th, Throwable ex) -> {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unerwarteter Fehler");
            alert.setContentText("Entschuldigung, das hätte nicht passieren dürfen!");
            alert.showAndWait();
        });

        // Resource der Spieleinstellungsfenster laden und erzeugen
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        Scene settingsScene = new Scene(settingsLoader.load(), 1280, 800);

        // Spieleinstellungsfenster laden und öffnen
        primaryStage.setTitle("Labyrinth Einstellungen");
        primaryStage.setScene(settingsScene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
