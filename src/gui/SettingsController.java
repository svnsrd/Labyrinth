package gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.Game;
import logic.player.PlayerType;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Einstellungsfenster, welches vor Beginn des Spiels angezeigt wird, um alle notwendigen
 * Einstellungen zu definieren.
 *
 * @author Suwendi Suriadi (WInf104177) [Repo: 37]
 * @version 06.08.2021
 */
public class SettingsController implements Initializable {

    /**
     * Nachricht für Fehlermeldung am Benutzer, wenn kein Spieler teilnimmt
     */
    private final static String ALERT_NOACTICVEPLAYER = "Mindestens ein Spieler muss teilnehmen";

    /**
     * Nachricht für Fehlermeldung an Benutzer, wenn aktive Spieler keinen Namen zugewiesen
     * bekommen haben
     */
    private final static String ALERT_EMPTYNAME = "Aktive Spieler müssen einen Namen haben und dürfen" +
            " nicht länger als 15 Zeichen sein!";

    /**
     * ComboBox, ueber welche man die Anzahl der Schaetze, die ein Spieler zu
     * Spielbeginn zugewiesen bekommt, setzt.
     */
    public ComboBox<Integer> treasuresPerPlayerSize_Box;

    /**
     * Textfelder, über welcher der Benutzer die Namen der teilnehmenden Spieler
     * bestimmen kann.
     */
    @FXML
    private TextField player1_name;
    @FXML
    private TextField player2_name;
    @FXML
    private TextField player3_name;
    @FXML
    private TextField player4_name;

    /**
     * {@link TextField TextField-Array}, welches die Referenzen der Textfelder für die
     * Spielernamen hat.
     */
    private TextField[] playerNames;

    /**
     * CheckBoxen, über welcher der Benutzer angeben kann, ob ein Spieler teilnimmt
     */
    @FXML
    private CheckBox player1_active;
    @FXML
    private CheckBox player2_active;
    @FXML
    private CheckBox player3_active;
    @FXML
    private CheckBox player4_active;

    /**
     * {@link TextField TextField-Array}, welches die Referenzen der CheckBoxen beinhaltet, die
     * kennzeichnen, ob ein Spieler an der Runde teilnimmt.
     */
    private CheckBox[] activePlayers;

    /**
     * ComboBoxen, über welche der Benutzer die Arten (menschlich/KI) der
     * teilnehmenden Spieler bestimmen kann.
     */
    @FXML
    private ComboBox<PlayerType> player1_type;
    @FXML
    private ComboBox<PlayerType> player2_type;
    @FXML
    private ComboBox<PlayerType> player3_type;
    @FXML
    private ComboBox<PlayerType> player4_type;

    /**
     * ComboBoxen, in einer Liste gespeichert.
     */
    public List<ComboBox<PlayerType>> playerTypes;

    /**
     * Button, welcher das Spiel mit den aktuellen Einstellungen startet.
     */
    public Button startGame_Button;

    /**
     * Initialisiert das die JavaFX-Komponenten des Spieleinstellungsfensters.
     *
     * @param url Ungenutzt
     * @param resourceBundle Ungenutzt
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Arrays erzeugen
        playerNames = new TextField[]{player1_name, player2_name, player3_name, player4_name};
        activePlayers = new CheckBox[]{player1_active, player2_active, player3_active,
                player4_active};


        // Initial ist zum Start der erste Spieler bereits vorausgewählt
        player1_active.setSelected(true);

        // ChangeListener für die CheckBoxen, damit dynamisch die Anzahl der teilnehmenden
        // Spieler ermittelt werden kann
        ChangeListener<Boolean> activeBoxListener = createChangeListener();

        // Hinzufügen des Listeners zu jeder CheckBox
        for (CheckBox box : activePlayers) {
            box.selectedProperty().addListener(activeBoxListener);
        }

        // Spielertypen zu den ComboBoxen hinzufügen (Initial menschlich)
        playerTypes = Arrays.asList(player1_type, player2_type, player3_type, player4_type);
        for (ComboBox<PlayerType> playerType : playerTypes) {
            playerType.setItems(FXCollections.observableArrayList(PlayerType.values()));
            playerType.setValue(PlayerType.HUMAN);
        }

        // Hinzufügen der Anzahl an möglichen Schätzen pro Spieler für den Initialwert
        addValsToTreasureComboBox(24, 1);
    }


    /**
     * Handhabt den Klick auf den Button 'Start Game!', welche einen Szenenwechsel
     * auf das Spielfeld auslöst.
     *
     * @throws IOException, wenn das FXML-Dokument nicht gefunden werden konnte
     */
    @FXML
    private void handleStartGameButton() throws IOException {
        // Anzahl der involvierten Spieler
        int playerSize = 0;

        // Liste erzeugen, welche angibt, welche Spieler aktiv sind
        List<Boolean> activePlayerList = new ArrayList<>();
        for (CheckBox box : activePlayers) {
            activePlayerList.add(box.isSelected());
        }

        // Gibt an, ob ein Spielername nicht den Voraussetzungen entspricht
        boolean invalid = false;

        // Durchlaufen der Spieler
        List<String> playerNamesList = new LinkedList<>();
        for (int i = 0; i < Game.MAX_PLAYERS; i++) {

            // Aktuelle Spielername
            String currPlayerName = playerNames[i].getText().trim();

            // Nimmt der Spieler teil?
            if (activePlayerList.get(i)) {
                playerSize++;
                if (currPlayerName.isEmpty() || currPlayerName.length() > 15) {
                    invalid = true;
                    playerNames[i].setStyle("-fx-control-inner-background: #E55A5A");
                } else {
                    playerNames[i].setStyle(null);
                }
            } else {
                currPlayerName = "Spieler " + (i + 1);
            }
            playerNamesList.add(currPlayerName);
        }

        // Auf invalide Benutzereingaben prüfen, ansonsten Spiel erzeugen
        if (invalid) { /* Fehlermeldung → Leerer Spielernamen */
            showAlert(ALERT_EMPTYNAME);
        } else if (playerSize == 0) { /* Fehlermeldung → keine aktiven Spieler */
            showAlert(ALERT_NOACTICVEPLAYER);
        } else {
            // Die Stage durch den Button erhalten
            Stage stage = (Stage) startGame_Button.getScene().getWindow();

            // Das FXMLDocument laden, welche das Spielfeld etc. repräsentiert
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("./FXMLDocument.fxml")
            );

            // Die Szene auf das Spiel wechseln
            stage.setScene(new Scene(loader.load(), 1280, 800));

            // Den Controller des FXMLDocuments erhalten
            FXMLDocumentController gameFieldController = loader.getController();

            // Spielertypen
            List<PlayerType> settedPlayerTypes = new LinkedList<>();
            for (int i = 0; i < Game.MAX_PLAYERS; i++) {
                settedPlayerTypes.add(playerTypes.get(i).getValue());
            }

            // Werte dem FXMLDocumentController übergeben
            gameFieldController.init(playerSize, treasuresPerPlayerSize_Box.getValue(),
                    playerNamesList, settedPlayerTypes, activePlayerList);
        }
    }

    /**
     * Erzeugt einen {@link ChangeListener}, welcher bei Ausführung die Einträge der
     * {@code treasuresPerPlayerSize_Box} anpasst.
     *
     * @return ChangeListener
     */
    private ChangeListener<Boolean> createChangeListener() {
        return (observableValue, aBoolean, t1) -> {
            int activeCounter = 0;
            for (CheckBox box : activePlayers) {
                if (box.isSelected()) {
                    activeCounter++;
                }
            }
            if (activeCounter != 0) {
                addValsToTreasureComboBox(treasuresPerPlayerSize_Box.getValue(), activeCounter);
            }
        };
    }

    /**
     * Hilfsmethode, welche die Werte für die Anzahl der möglichen Schätze pro Spieler hinzufügt.
     *
     * @param activePlayerSize Anzahl der Spieler die aktiv am Spiel teilnehmen
     */
    private void addValsToTreasureComboBox(int oldVal, int activePlayerSize) {
        treasuresPerPlayerSize_Box.getItems().clear();
        int max = Game.MAX_TREASURESIZE / activePlayerSize;
        int counter = 1;
        while (counter <= max) {
            treasuresPerPlayerSize_Box.getItems().add(counter);
            counter++;
        }

        if (oldVal > counter) {
            treasuresPerPlayerSize_Box.getSelectionModel().selectLast();
        } else {
            treasuresPerPlayerSize_Box.getSelectionModel().select(oldVal - 1);
        }
    }

    /**
     * Ruft eine Fehlermeldung auf, die dem Benutzer mit der übergebenen Nachricht angezeigt wird.
     *
     * @param message Nachricht die der Benutzer mit der Fehlermeldung erhält
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText("Fehler");
        alert.setResizable(true); // Muss rein, da KDE Bug https://bugs.openjdk.java.net/browse/JDK-8179073
        alert.setContentText(message);
        alert.showAndWait();
    }
}
