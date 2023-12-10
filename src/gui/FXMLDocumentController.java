package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Game;
import logic.player.PlayerType;
import logic.RotateDirection;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse repräsentiert Routinen zur direkten Oberflächensteuerung.
 * <p>
 *
 * @author Suwendi Suriadi (WInf104177) [Repository: 37]
 * @version 08.08.2021
 */
public class FXMLDocumentController implements Initializable {

    /**
     * Bogenlänge für die {@link Rectangle Rechtecke}.
     */
    private static final int RECT_ARC = 40;

    @FXML
    private MenuItem saveGame;

    @FXML
    private MenuItem loadGame;

    /**
     * Das Hauptfenster, welches alle FX-Komponenten beinhaltet
     */
    @FXML
    private AnchorPane ap;

    /**
     * Komponenten, um das GridPane quadratisch zu halten
     */
    @FXML
    private HBox hBox;
    @FXML
    private VBox vBox;

    /**
     * {@link GridPane}, welches das Spielfeld repräsentiert.
     */
    @FXML
    private GridPane gamefieldGrd;

    /**
     * Das GridPane (1x3), welche die Drehung der freien Gängekarte handhaben soll.
     */
    @FXML
    private GridPane changDir;


    /**
     * Die Menüleiste.
     */
    @FXML
    private MenuBar menuBar;

    /**
     * Slider für die Einstellung der Animationsgeschwindigkeiten einer Einschuboperation und
     * der Bewegung einer Spielerfigur.
     */
    @FXML
    private Slider animationDurationShift;

    /**
     * Slider für die Einstellung der Animationsgeschwindigkeit der Bewegung einer Spielerfigur.
     * Hierbei ist die Geschwindigkeit für einen Schritt gemeint, nicht für die gesamte Bewegung.
     */
    @FXML
    private Slider moveDurationSlider;

    /**
     * StackPane für die Grafiken (Gängekarte und Schätze) der freien Gängekarte.
     */
    @FXML
    private StackPane freeWayCardStkPn;

    /**
     * Rechteck für die freie Gängekarte zur Hervorhebung, falls sich ein zu erreichender Schatz
     * auf dieser befindet.
     */
    @FXML
    private Rectangle freeWayCardRect;

    /**
     * Pfeil-Grafiken mit welcher der Benutzer die freie Gängekarte drehen kann.
     */
    @FXML
    private ImageView turnFreeTileLeft;
    @FXML
    private ImageView turnFreeTileRight;

    /**
     * ImageView-Array, welche die Referenzen der {@link ImageView Pfeil-Grafiken} für die Drehung
     * der freien Gängekarte hat.
     */
    private ImageView[] turnFreeTileArrows;

    /**
     * Spieler-Informationsboxen, welche den Namen, die verbleibenden Schätze und eine Grafik
     * des derzeit zu erreichenden Schatzes (oder Startposition) anzeigen.
     */
    @FXML
    private HBox player0_box;
    @FXML
    private HBox player1_box;
    @FXML
    private HBox player2_box;
    @FXML
    private HBox player3_box;

    /**
     * HBox-Array, welche die Referenzen der {@link HBox Horizontalen Boxen} für die Anzahl der
     * verbleibenden Schätze jedes Spielers beinhaltet.
     */
    private HBox[] playerBoxes;

    /**
     * ImageViews, welche die aktuell zu erreichende Schatzkarte anzeigt
     */
    @FXML
    private ImageView player1_currTreasure;
    @FXML
    private ImageView player2_currTreasure;
    @FXML
    private ImageView player3_currTreasure;
    @FXML
    private ImageView player4_currTreasure;

    /**
     * ImageView-Array, welche die Referenzen der {@link Label ImageViews} für die Anzahl der
     * verbleibenden Schätze jedes Spielers beinhaltet.
     */
    private ImageView[] currTreasures;

    /**
     * Labels, welche die Spielernamen repräsentieren
     */
    @FXML
    private Label player1_name;
    @FXML
    private Label player2_name;
    @FXML
    private Label player3_name;
    @FXML
    private Label player4_name;

    /**
     * Array, welches die Referenzen der {@link Label Labels} für die verschiedenen Spielernamen
     * beinhaltet.
     */
    private Label[] playerNamesLbs;

    /**
     * Label, welche die verbleibenden Schätze eines Spielers repräsentieren
     */
    @FXML
    private Label player1_leftTreasures;
    @FXML
    private Label player2_leftTreasures;
    @FXML
    private Label player3_leftTreasures;
    @FXML
    private Label player4_leftTreasures;

    /**
     * Label-Array, welche die Referenzen der {@link Label Labels} für die Anzahl der verbleibenden
     * Schätze jedes Spielers beinhaltet.
     */
    private Label[] leftTreasuresOfPlayers;

    /**
     * StackPane-Array, welche die Referenzen der {@link Label StackPanes} für die verschiedenen
     * Grafiken (Gängekarten und Schätze) des Spielfeldes beinhaltet.
     */
    private StackPane[][] stackPanes;

    /**
     * Referenz der Rechtecke auf allen Zellen des GridPanes ({@code gridPane}), welcher zur
     * Hervorhebung dienen, wenn sich ein zu erreichender Schatz auf einen der entsprechenden
     * Zellen (Gängekarte) befindet und um hervorzuheben, ob ein Spieler sich dorthin bewegen kann.
     */
    private Rectangle[][] hoverViews;

    /**
     * Die Spiellogik.
     */
    private Game game;

    /**
     * Initialisierung von Grid und Logik.
     *
     * @param url            ungenutzt
     * @param resourceBundle ungenutzt
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ap.setDisable(true);

        // Spielfeld (gridPane) quadratisch halten
        gamefieldGrd.prefWidthProperty().bind(vBox.widthProperty());
        gamefieldGrd.prefHeightProperty().bind(vBox.widthProperty());
        vBox.prefWidthProperty().bind(hBox.heightProperty());

        // Erzeugen eines Label-Arrays, welche die Labels der Spielernamen hält
        playerNamesLbs = new Label[]{player1_name, player2_name, player3_name, player4_name};

        // Erzeugen eines Label-Arrays, welche die Labels die
        // verbleibenden Schätze eines Spielers hält
        leftTreasuresOfPlayers = new Label[]{player1_leftTreasures, player2_leftTreasures,
                player3_leftTreasures, player4_leftTreasures};

        // Erzeugen eines ImageView-Arrays, welche die aktuell zu suchenden Schätze der Spieler
        // anzeigt
        currTreasures = new ImageView[]{player1_currTreasure, player2_currTreasure,
                player3_currTreasure, player4_currTreasure};

        // Erzeugen eines ImageView-Arrays, welche die Pfeile zur Rotation der freien Gängekarte
        // anzeigt
        turnFreeTileArrows = new ImageView[]{turnFreeTileLeft, turnFreeTileRight};

        // Spieler-Informationsboxen
        playerBoxes = new HBox[]{player0_box, player1_box, player2_box, player3_box};
    }

    /**
     * Initialisiert die Logik und die GUI.
     *
     * @param playerSize  Anzahl der Spieler
     * @param playerTypes Spielertypen (menschlich, KI)
     */
    public void init(int playerSize, int treasuresSizePerPlayer, List<String> playerNames,
                     List<PlayerType> playerTypes, List<Boolean> activePlayer) {

        // Erzeugen eines StackPane-Arrays für die Gängekarten
        initStackPanes();

        // Erzeugen der Pfeile zur Rotation der freien Gängekarte
        initChangeDirs();

        // Berechnen der maximalen Anzahl an Karten
        final int cardsSize = playerSize * treasuresSizePerPlayer;

        // KeyListener erzeugen der auf <H> hört
        Scene scene = gamefieldGrd.getScene();
        scene.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.H) {
                this.game.highlightTreasureToFind();
            }
        });

        for (ImageView view : currTreasures) {
            view.setOpacity(0);
        }

        // Erzeugen der GUI
        JavaFXGUI gui = new JavaFXGUI(saveGame, loadGame, gamefieldGrd, stackPanes, hoverViews,
                freeWayCardStkPn, freeWayCardRect, currTreasures, changDir, turnFreeTileArrows,
                playerNamesLbs, leftTreasuresOfPlayers, playerBoxes, animationDurationShift,
                moveDurationSlider);

        // Erzeugen der Spiellogik und übergeben der GUI
        this.game = new Game(gui, playerNames, cardsSize, treasuresSizePerPlayer, playerTypes,
                activePlayer, gamefieldGrd.getRowCount() - 2,
                gamefieldGrd.getColumnCount() - 2);
        ap.setDisable(false);
    }


    /**
     * Handhabt einen Klick auf das GridPane.
     *
     * @param event Der Klick
     */
    @FXML
    private void handleGrdPnOnMouseClicked(MouseEvent event) {
        int x = -1;
        int y = -1;
        boolean leftClicked = event.getButton() == MouseButton.PRIMARY;

        //determine the imageview of the grid that contains the coordinates of the mouseclick
        //to determine the board-coordinates

        // Bestimmen des Elementes, dass die Koordinaten des Mausklicks enthält, um die
        // Spielfeldkoordinaten zu ermitteln
        for (Node node : gamefieldGrd.getChildren()) {
            if (node.getBoundsInParent().contains(event.getX(), event.getY())) {
                x = GridPane.getColumnIndex(node);
                y = GridPane.getRowIndex(node);
            }
        }

        assert (x >= 0 && y >= 0) : "dem Klick ist keine Koordinate zuzuordnen";

        if (leftClicked) {
            game.handleHumanMove(x, y);
        }
    }

    /**
     * Handhabung die Betätigung einer Taste.
     * <p>
     * Handelt es sich um die Taste <i>H</i>, dann führt die {@link Game} eine Routine aus.
     *
     * @param keyEvent Eingabe einer Taste
     */
    @FXML
    private void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.H) {
            game.highlightTreasureToFind();
        }
    }

    /**
     * Handhabt den Klick auf das {@link MenuItem} <i>Neues Spiel</i>.
     *
     * @throws IOException Fehler bei I/O-Verarbeitung
     */
    @FXML
    private void handleStartNewGame() throws Exception {
        this.game.interruptGame();
        Stage stage = (Stage) gamefieldGrd.getScene().getWindow();
        stage.close();

        // Neustart
        Main main = new Main();
        main.start(new Stage());
    }

    /**
     * Handhabt den Klick auf das MenuItem {@code saveGame}.
     */
    @FXML
    private void handleSaveGame() {
        if (game.checkForInvalidInteraction(true)) {
            //Step 1: Figure out where we are currently are, so we can open the dialog in
            //the same directory the jar is located. See also
            //https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
            File currDir = null;
            try {
                currDir = new File(FXMLDocumentController.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
            } catch (URISyntaxException ex) {
                //oops... ¯\_(ツ)_/¯
                //guess we won't be opening the dialog in the right directory
            }
            //Step 2: Put it together
            FileChooser fileChooser = new FileChooser();
            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "JSON files (*.JSON)", "*.JSON");
            fileChooser.getExtensionFilters().add(extFilter);

            if (currDir != null) {
                //ensure the dialog opens in the correct directory
                fileChooser.setInitialDirectory(currDir.getParentFile());
            }
            fileChooser.setTitle("Save the game");

            //Step 3: Open the Dialog (set window owner, so nothing in the original window
            //can be changed)
            File savedFile = fileChooser.showSaveDialog(gamefieldGrd.getScene().getWindow());
            if (savedFile != null) {
                game.saveGame(savedFile);
            }
        }
    }

    /**
     * Handhabt den Klick auf den Button "Laden"
     */
    @FXML
    private void handleLoadGame() {
        if (game.checkForInvalidInteraction(false)) {
            // Aktuelles Verzeichnis in dem sich dieses Programm befindet finden
            File currDir = null;
            try {
                currDir = new File(FXMLDocumentController.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
            } catch (URISyntaxException ex) {
                //oops... ¯\_(ツ)_/¯
                //guess we won't be opening the dialog in the right directory
            }

            //Step 2: Put it together
            FileChooser fileChooser = new FileChooser();
            if (currDir != null) {
                //ensure the dialog opens in the correct directory
                fileChooser.setInitialDirectory(currDir.getParentFile());
            }

            fileChooser.setTitle("Open JSON Graph-File");
            //Step 3: Open the Dialog (set window owner, so nothing in the original window
            //can be changed)
            File selectedFile = fileChooser.showOpenDialog(gamefieldGrd.getScene().getWindow());

            if (selectedFile != null) {
                game.loadGame(selectedFile);
            }
        }
    }

    /**
     * Handhabt einen Klick auf das {@link MenuItem} <i>Spiel beenden</i>, indem die Applikation
     * geschlossen wird
     */
    @FXML
    private void handleCloseGame() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Handhabt den Klick auf die linke Pfeil-Grafik im Bereich der freien Gängekarte.
     */
    @FXML
    private void rotateClockwise() {
        game.rotateFreeWayCard(RotateDirection.CLOCKWISE);
    }

    /**
     * Handhabt den Klick auf die rechte Pfeil-Grafik im Bereich der freien Gängekarte.
     */
    @FXML
    private void rotateAntiClockwise() {
        game.rotateFreeWayCard(RotateDirection.ANTICLOCKWISE);
    }

    /**
     * Erzeugt ein 2-Dimensionales StackPane-Array, dessen Elemente an den jeweiligen
     * GridPane-Zellen zugewiesen sind.
     */
    private void initStackPanes() {
        int colcount = gamefieldGrd.getColumnConstraints().size();
        int rowcount = gamefieldGrd.getRowConstraints().size();
        stackPanes = new StackPane[colcount][rowcount];
        hoverViews = new Rectangle[colcount][rowcount];

        // Anbinden jedes erzeugten ImageView an die jeweilige GridPane-Zelle
        for (int x = 0; x < rowcount; x++) {
            for (int y = 0; y < colcount; y++) {
                // Erzeugen eines StackPane
                StackPane stkPn = new StackPane();

                // Erzeugen eines Rechtecks für den Hover-Effekt
                Rectangle rect = createRect();
                bindRectToGrdPn(rect, gamefieldGrd);
                hoverViews[y][x] = rect;

                // Zuweisen des StackPanes an das StackPane-Array
                stackPanes[y][x] = stkPn;

                // Hinzufügen des StackPane zu der GridPane-Zelle
                // Zuweisen der korrekten Indices für die ImageView, sodass man später GridPane.getColumnIndex(Node)
                // nutzen kann
                gamefieldGrd.add(stkPn, y, x);
                gamefieldGrd.add(rect, y, x);
            }
        }

        // Freie Gängekarte
        freeWayCardRect = createRect();
        freeWayCardRect.widthProperty().bind(freeWayCardStkPn.widthProperty());
        freeWayCardRect.heightProperty().bind(freeWayCardStkPn.heightProperty());
        freeWayCardRect.setFill(Color.GREEN);
        changDir.add(freeWayCardRect, 1, 0);
    }

    /**
     * Hilfsmethode, welche die ImageViews für die Pfeile im Rahmen der Rotation der freien
     * Gängekarte erzeugt.
     * <p>
     * {@code changDir} ist hierbei das GridPane, welche die ImageViews für die Pfeile und das
     * StackPane zur Abbildung der freien Gängekarte repräsentiert.
     */
    private void initChangeDirs() {
        // Spalten-Index Definition, damit die Benutzereingabe (Klick) zugeordnet werden kann
        // Binden der ImageViews für die Pfeile am entsprechenden GridPane
        turnFreeTileRight.fitHeightProperty().bind(freeWayCardStkPn.heightProperty().divide(1.5));
        turnFreeTileRight.fitWidthProperty().bind(freeWayCardStkPn.widthProperty().divide(1.5));
        turnFreeTileLeft.fitHeightProperty().bind(freeWayCardStkPn.heightProperty().divide(1.5));
        turnFreeTileLeft.fitWidthProperty().bind(freeWayCardStkPn.widthProperty().divide(1.5));

        // Zentrale Positionierung der ImageViews für die Pfeile innerhalb ihrer GridPane-Zelle
        GridPane.setHalignment(turnFreeTileRight, HPos.CENTER);
        GridPane.setHalignment(turnFreeTileLeft, HPos.CENTER);
        turnFreeTileLeft.toFront();
        turnFreeTileRight.toFront();
    }

    /**
     * Erzeugt ein {@link Rectangle Rechteck} und bindet dessen Größe an eine GridPane-Zelle von ({@code gridPane}).
     * Dient zur Hervorhebung von Gängekarten.
     *
     * @return Rechteck
     */
    private Rectangle createRect() {
        Rectangle rect = new Rectangle(0, 0);

        // Ausblenden
        rect.setOpacity(0);

        // Bogenlänge anpassen
        rect.setArcHeight(RECT_ARC);
        rect.setArcWidth(RECT_ARC);

        return rect;
    }

    /**
     * Bindet die Größe des übergebenen {@link Rectangle Rechtecks} an eine GridPane-Zelle.
     *
     * @param rect Übergebenes Rechteck
     */
    private void bindRectToGrdPn(Rectangle rect, GridPane grdPn) {
        rect.widthProperty().bind(grdPn.widthProperty().divide(grdPn.getColumnCount()));
        rect.heightProperty().bind(grdPn.heightProperty().divide(grdPn.getRowCount()));
    }
}
