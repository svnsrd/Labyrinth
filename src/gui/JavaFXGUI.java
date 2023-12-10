package gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GUIConnector;
import logic.Game;
import logic.Message;
import logic.Position;
import logic.Shift;
import logic.Treasure;
import logic.path.PathNode;
import logic.player.AIMove;
import logic.player.Player;
import logic.Direction;
import logic.tile.FreeWayCard;
import logic.RotateDirection;
import logic.tile.Tile;
import logic.tile.TileRotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse enthält Methoden zur Steuerung der Benutzeroberfläche
 *
 * @author Suwendi Suriadi (WInf104177) [Repo: 37]
 * @version 06.08.2021
 */
public class JavaFXGUI implements GUIConnector {

    /**
     * Name des Spiels.
     */
    private static final String NAME_OF_GAME = "Das verrückte Labyrinth";

    /**
     * Hinweis, welcher auf der Spieler-Informationsbox präsentiert wird, wenn der Spieler
     * alle Schätze gesammelt hat.
     */
    private static final String WIN_HINT = "Gehe zu deiner Startposition, um zu gewinnen!";

    /**
     * Deckkraft der Spieler-Informationsbox, für inaktive Spieler
     */
    private static final double INACTIVEPLAYER_OPACITY = 0.2;

    /**
     * Halbe Deckkraft.
     */
    private static final double HALF_OPACITY = 0.5;

    /**
     * Ein maximaler Rotationsschritt, wenn der menschliche Spieler die freie Gängekarte rotiert.
     */
    private static final int ROTATION_STEP = 90;

    /**
     * Multipliziert die über {@code shiftDurationSld} und {@code moveDurationSld} übergebenen
     * Werte in Sekunden. (1000 Millisekunde = 1 Sekunde)
     */
    private static final int MILLIS_MULTIPLIER = 1000;

    /**
     * Die Dauer einer Animation ist mindestens 1 Millisekunde.
     * Notwendig, wenn die Dauer der Animationen auf 0 gesetzt ist, damit die
     * {@code onFinishedProperty} ausgeführt wird.
     */
    private static final int DEFAULT_MILLI = 1;

    /**
     * Grafiken der verschiedenen Gängekarten, dessen Indices mit den <i>ordinal</i>-Werten, des
     * Enums {@link logic.tile.TileShape} korrespondieren.
     */
    private static final Image[] IMG_OF_WAYCARDS = new Image[]{
            new Image("gui/images/T.png"),
            new Image("gui/images/L.png"),
            new Image("gui/images/I.png")
    };

    /**
     * Grafik eines Pfeils für eine Einschiebeoperation.
     */
    private static final Image ARROW_IMG = new Image("gui/images/triangle.png");

    /**
     * Grafik, welche einen Pfeil zur Rotation der freien Gängekarte repräsentiert.
     */
    private static final Image CHANGEDIR_ARROW_IMG_LEFT = new Image(
            "gui/images/changeDirArr_left.png");

    private static final Image CHANGEDIR_ARROW_IMG_RIGHT = new Image(
            "gui/images/changeDirArr_right.png");

    /**
     * Hier muss angegeben, in welche Richtung die Pfeil-Grafik zeigt.
     */
    private static final Direction ARROW_IMG_DIR = Direction.DOWN;

    /**
     * Menü-Element zum Speichern des aktuellen Spielstandes.
     */
    private final MenuItem saveGame;

    /**
     * Menü-Element zum Laden des aktuellen Spielstandes.
     */
    private final MenuItem loadGame;

    /**
     * Die Stage.
     */
    private final Stage stage;

    /**
     * Breite des Spielfelds ({@link Double}).
     */
    private final double fieldWidth;

    /**
     * Höhe des Spielfelds ({@link Double}).
     */
    private final double fieldHeight;

    /**
     * Das GridPane in dem alle für das Spiel notwendige Komponenten visualisiert werden.
     */
    private final GridPane gamefield;

    /**
     * Das ImageView-Layer auf welche die verschiedenen Images
     * (Spielfiguren, Gängekarten, Schätze) gesetzt werden.
     */
    private final StackPane[][] field;

    /**
     * Hintergrund der Spieler-Information aus {@code playerBoxes} der an der Reihe ist.
     */
    private static final Background playerOnTurnBG = new Background(new BackgroundFill(Color.WHITE,
            null, null));

    /**
     * Referenz der {@link Rectangle Rectangles}, welche die Positionen, die ein menschlicher
     * Spieler betreten kann, hervorheben.
     */
    private final Rectangle[][] hoverViews;

    /**
     * Repräsentation der freien Gängekarte.
     */
    private final StackPane freeWayCard;

    /**
     * Rechteck zur Hervorhebung der freien Gängekarte
     */
    private final Rectangle freeWayCardHover;

    /**
     * Farben der Spieler.
     */
    private static final Color[] colors = {Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED};

    /**
     * Zeigt die aktuell zu suchenden Schätze der jeweiligen Spieler an.
     */
    private final ImageView[] currTreasures;

    /**
     * Darstellung der Spieler als Kreise.
     */
    private final Circle[] playerCircles;

    /**
     * GridPane, welche die aktuelle freie Gängekarte anzeigt und die Rotation dieser steuert.
     */
    private final GridPane changeDirs;

    /**
     * Animationsgeschwindigkeitsregler für die <b>Einschuboperation</b>.
     */
    private final Slider shiftDurationSld;

    /**
     * Animationsgeschwindigkeitsregler für die <b>Spieler-Bewegung</b>.
     */
    private final Slider moveDurationSld;

    /**
     * {@link HBox Spielerinformationsboxen}, welche den Namen, die Anzahl der verbleibenden Schätze
     * und den aktuell zu suchenden Schatz anzeigen.
     */
    private final HBox[] playerBoxes;

    /**
     * Labels, welche für jeden Spieler anzeigen, wie viele Schätze er noch finden muss.
     */
    private final Label[] leftTreasuresLbls;

    /**
     * Label der Spielernamen
     */
    private final Label[] playerNameLbls;

    /**
     * Grafische Repräsentation der Schätze die in diesem Spiel relevant sind.
     */
    private ImageView[] treasureViews;

    /**
     * Horizontale Positionierungen der Spieler-Kreise innerhalb einer Gängekarte.
     */
    private static final HPos[] HPOS_OF_CIRCLES = {HPos.LEFT, HPos.RIGHT, HPos.RIGHT, HPos.LEFT};

    /**
     * Vertikale Positionierungen der Spieler-Kreise innerhalb einer Gängekarte.
     */
    private static final VPos[] VPOS_OF_CIRCLES = {VPos.TOP, VPos.TOP, VPos.BOTTOM, VPos.BOTTOM};

    /**
     * Mapping der Positionen der Einschubpfeile auf ihre dazugehörigen {@link ImageView ImageViews}
     */
    private final Map<Position, ImageView> insetArrows;

    /**
     * {@link ImageView} der aktuell <b>verbotenen</b> Einschubposition.
     */
    private ImageView currForbiddenInsetPos;

    /**
     * @param grdPn             {@link GridPane}, welches die Zellen des Spielfeldes repräsentiert
     * @param imgs              {@link StackPane StackPane-Array}, welche die Bilder des Spielfeldes
     *                          repräsentiert. Dazu gehören die Gängekarten-, Schatz-,
     *                          Einschubpfeil- und Rotationspfeil-Bilder.
     * @param hoverViews        {@link Rectangle Rechtecke}, welche auf jedem Feld positioniert
     *                          sind, um dem menschlichen Spieler seine begehbaren Positionen
     *                          aufzuzeigen.
     * @param freeTile          {@link StackPane}, welches die freie Gängekarte visualisiert
     * @param freeRect          {@link Rectangle}, welche zur Hervorhebung der freien Gängekarte
     *                          dient
     * @param currTreasures     {@link ImageView ImageViews} der Schatzkarten, welche die Spieler
     *                          aktuell suchen müssen
     * @param changeDir         {@link GridPane}, welches die Rotationspfeile und die freie
     *                          Gängekarte {@code freeTile} beinhaltet
     * @param changeDirArrows   {@link ImageView ImageViews} der verschiedenen Rotationspfeile
     * @param playerNameLbls    {@link Label Labels} für die Spielernamen
     * @param leftTreasuresLbls {@link Label Labels}, welche die Anzahl der verbleibenden Schätze
     *                          anzeigen
     */
    public JavaFXGUI(MenuItem saveGame, MenuItem loadGame, GridPane grdPn, StackPane[][] imgs,
                     Rectangle[][] hoverViews, StackPane freeTile, Rectangle freeRect,
                     ImageView[] currTreasures, GridPane changeDir, ImageView[] changeDirArrows,
                     Label[] playerNameLbls, Label[] leftTreasuresLbls, HBox[] playerBoxes,
                     Slider an, Slider mv) {

        this.saveGame = saveGame;
        this.loadGame = loadGame;
        this.gamefield = grdPn;
        this.field = imgs;
        this.hoverViews = hoverViews;
        this.freeWayCard = freeTile;
        this.freeWayCardHover = freeRect;
        this.currTreasures = currTreasures;
        this.changeDirs = changeDir;
        this.leftTreasuresLbls = leftTreasuresLbls;
        this.playerCircles = new Circle[Game.MAX_PLAYERS];
        this.playerNameLbls = playerNameLbls;
        this.insetArrows = new HashMap<>();
        this.currForbiddenInsetPos = new ImageView();
        this.playerBoxes = playerBoxes;
        this.shiftDurationSld = an;
        this.moveDurationSld = mv;
        this.fieldHeight = gamefield.getHeight() / gamefield.getRowCount();
        this.fieldWidth = gamefield.getWidth() / gamefield.getColumnCount();
        this.stage = (Stage) gamefield.getScene().getWindow();

        // Stage-Namen auf
        this.stage.setTitle(NAME_OF_GAME);

        // Initialisieren der Pfeile zur Rotation der freien Gängekarte
        initializeRotArrow(changeDirArrows);

        disableField(true);
    }

    /**
     * Erzeugt alle relevanten Schatzkarten-Bilder für das Spiel.
     */
    @Override
    public void createTreasureImg() {
        // Erzeugen der Schatzkartenbilder und dazugehörigen ImageViews
        // +1 für das leere Image (0) und +4 (MAX_PLAYERS), für die letzten fiktiven Schätze
        final int treasuresSize = Game.MAX_TREASURESIZE + Game.MAX_PLAYERS + 1;
        this.treasureViews = new ImageView[treasuresSize];

        // Erzeugen der Bilder von 1 bis maxTreasureSize
        this.treasureViews[0] = new ImageView();
        for (int i = 1; i < Game.MAX_TREASURESIZE + 1; i++) {
            Image img = new Image("gui/images/s" + i + ".png");
            this.treasureViews[i] = new ImageView(img);
        }

        // Startpositionen werden als Schätze dargestellt, haben aber keine Grafik
        for (int startIdx = Game.MAX_TREASURESIZE + 1; startIdx < treasuresSize; startIdx++) {
            this.treasureViews[startIdx] = new ImageView();
        }
    }

    /**
     * Setzt die Spieler-Informationen zurück.
     */
    @Override
    public void resetPlayerInfo() {
        for (int i = 0; i < Game.MAX_PLAYERS; i++) {
            leftTreasuresLbls[i].setText(""); /* Verbleibende Schätze-Zähler */
            currTreasures[i].setImage(null); /* Aktuell zu suchender Schatz */
            playerNameLbls[i].setText(""); /* Spielername */
            gamefield.getChildren().remove(playerCircles[i]); /* Spieler-Kreise entfernen */
        }
    }

    /**
     * Erzeugt einen Spieler-Kreis und weist diesen an die übergebene Startposition {@code startPos}
     * zu.
     *
     * @param playerIdx Index des Spielers, dessen Kreis erstellt werden soll
     * @param player    Korrespondierendes Spieler-Objekt zur {@code playerIdx}
     */
    @Override
    public void createPlayer(int playerIdx, Player player) {
        if (player.isInvolved()) {
            playerBoxes[playerIdx].setOpacity(1);

            // Spieler-Kreis erzeugen
            Circle circle = new Circle();
            circle.setFill(colors[playerIdx]);
            circle.radiusProperty().bind(gamefield.widthProperty().divide(50));
            circle.setStroke(Color.BLACK);
            playerCircles[playerIdx] = circle;

            // Spieler-Kreis zentral innerhalb seiner GridPane-Zelle positionieren
            GridPane.setHalignment(circle, HPos.CENTER);

            // Spieler-Kreis an seine Startposition im GridPane zuweisen
            Position startPos = player.getGlobalPos();
            gamefield.add(circle, startPos.getX(), startPos.getY());

            // Den Hover-Rectangle nach oben setzen, indem nacheinander die Gängekarte und der
            // Spielerkreis nach hinten gesetzt werden
            movePlayerCircle(circle, startPos);

            // Spielernamen anzeigen
            playerNameLbls[playerIdx].setText(player.getName());

            // Anzahl der suchenden Schätze anzeigen (-1 wegen Startposition)
            leftTreasuresLbls[playerIdx].setText(String.valueOf(player.getTreasureCards().size() - 1));

            // Aktuell zu suchenden Schatz des Spielers anzeigen
            currTreasures[playerIdx].setImage(treasureViews[player.getCurrTreasure().ordinal()].getImage());
        } else {
            playerBoxes[playerIdx].setOpacity(INACTIVEPLAYER_OPACITY);
        }
    }

    /**
     * Initialisiert das Feld.
     *
     * @param board          Das Spielfeld (ohne Umrandung)
     * @param insetPositions Einschubpositionen
     * @param freeWayCard    Freie Gängekarte
     */
    @Override
    public void initializeField(Tile[][] board, Map<Position, Direction> insetPositions,
                                FreeWayCard freeWayCard) {
        int cols = board.length;
        int rows = board[0].length;

        // Erzeugen der Gängekarten
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                createAndDisplayTile(field[col + 1][row + 1], board[col][row]);
            }
        }

        // Erzeugen der Einschubpfeile
        for (Position insetPos : insetPositions.keySet()) {
            displayInsetArrow(insetPos.getX(), insetPos.getY(), insetPositions.get(insetPos));

        }

        // Erzeugen der freien Gängekarte
        createAndDisplayTile(this.freeWayCard, freeWayCard);
    }

    /**
     * Erzeugt und dreht eine Einschub-Pfeilgrafik.
     *
     * @param col       Spalte
     * @param row       Zeile
     * @param direction Richtung in die der Pfeil zeigen soll
     */
    @Override
    public void displayInsetArrow(int col, int row, Direction direction) {
        // Erzeugen der Pfeil-Grafik-Repräsentation
        ImageView arrow = new ImageView(ARROW_IMG);

        // Berechnung der Rotation
        int rotation = (direction.ordinal() - ARROW_IMG_DIR.ordinal()) * 90;
        arrow.setRotate(rotation);

        // Pfeil-Grafik an GridPane-Größe binden
        arrow.fitHeightProperty().bind(gamefield.heightProperty().divide(gamefield.getColumnCount() + 7));
        arrow.fitWidthProperty().bind(gamefield.widthProperty().divide(gamefield.getRowCount() + 3));

        // Mapping der Positionen der Einschubpfeile auf das entsprechende ImageView
        insetArrows.put(new Position(col, row), arrow);

        // Pfeil-Grafik hinzufügen
        gamefield.add(arrow, col, row);
        GridPane.setHalignment(arrow, HPos.CENTER);
        GridPane.setValignment(arrow, VPos.CENTER);
        arrow.toBack();
    }

    /**
     * Animation für den von einem menschlichen Spieler getätigte Einschuboperation.
     *
     * @param shift             Einschuboperation, die alle Informationen zum Einschubvorgang
     *                          enthält
     * @param possiblePositions Die möglichen Positionen nach dem Einschub
     */
    @Override
    public void animateHumanShift(Shift shift, PathNode[][] possiblePositions) {
        // Einschub-Animation erzeugen
        SequentialTransition shiftAnimation = new SequentialTransition();
        shiftAnimation.getChildren().add(createShiftAnimation(shift));

        shiftAnimation.setOnFinished(e -> {
            // Dem menschlichen Spieler seine möglichen Positionen aufzeigen
            setPossiblePos(possiblePositions);

            // Der Benutzer darf mit der GUI wieder interagieren
            disableField(false);
        });

        // Animation ausführen
        shiftAnimation.play();
    }

    /**
     * Erzeugt die Animationen eines KI-Zuges.
     *
     * <ul>
     *     <li>{@link RotateTransition} für die Rotation der freien Gängekarte</li>
     *     <li>
     *         {@link SequentialTransition} für die verschiedenen Animationen der Einschuboperation
     *         und der darauf anschließenden Spieler-Bewegung
     *     </li>
     * </ul>
     *
     * @param shift       Einschuboperation
     * @param aiMove      KI-Zug
     * @param playerIdx   Spieler-Index innerhalb des Spiels
     * @param nexTreasure Nächster Schatz oder {@code null}, wenn kein Schatz eingesammelt wurde
     * @param path        Pfad zur Zielposition des KI-Zuges
     * @param game        Referenz des Spiels, um diese zum für den weiteren Ablauf anzuweisen
     */
    @Override
    public void aiMove(Shift shift, AIMove aiMove, int playerIdx, Treasure collectedTreasure,
                       Treasure nexTreasure, List<Position> path, Game game) {

        // SequentialTransition erzeugen
        SequentialTransition sq = new SequentialTransition();

        // Animation: Rotation der freien Gängekarte
        RotateTransition rotateAnimation = createRotateTransition(aiMove.getFreeWayCardRot().getDeg());

        // Animation: Einschub der freien Gängekarte
        SequentialTransition shiftAnimation = createShiftAnimation(shift);

        // Alle Animationen zur SequentialTransition hinzufügen
        sq.getChildren().addAll(rotateAnimation, shiftAnimation);

        // Sobald die Einschubanimation beendet ist, kann die Spiele-Bewegung-Animation erzeugt
        // und ausgeführt werden
        sq.setOnFinished(e -> createMoveAnimation(playerIdx, path, collectedTreasure, nexTreasure,
                game).play());

        // SequentialTransition ausführen
        sq.playFromStart();
    }


    /**
     * Hebt die Gängekarte an der übergebenen Position hervor, indem der das dazugehörige
     * {@link Rectangle} aus {@code hoverViews} mit einer {@link FadeTransition} hervorgehoben und
     * beim Loslassen der Taste {@code KeyCode.H} mittels einer anderen {@link FadeTransition}
     * die Hervorhebung zurückgesetzt wird.
     *
     * @param posToHighlight Position die hervorgehoben werden soll
     * @param playerIdx Spieler-Index
     */
    @Override
    public void highlightTreasureToFind(Position posToHighlight, int playerIdx) {
        // Scene erhalten
        final Scene scene = gamefield.getScene();

        // Das Hover-Layer der betroffenen Gängekarte
        final Rectangle hoverRect = posToHighlight.getX() > 0
                ? hoverViews[posToHighlight.getX()][posToHighlight.getY()] : freeWayCardHover;

        // Den Hover-Layer mittels FadeTransition einblenden
        createFade(hoverRect, 0.5, 0.5).play();
        createFade(currTreasures[playerIdx], 1, 0.5).play();

        // Lässt man die <h>-Taste los, wird die Hervorhebung mit einer FadeTransition zurückgesetzt
        scene.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.H) {
                createFade(hoverRect, 0, 0.5).play();
                createFade(currTreasures[playerIdx], 0, 0.5).play();
            }
        });
    }

    /**
     * Setzt die Hervorhebung des alten Spielers zurück und weist sie dem aktuellen Spieler
     * (der nun an der Reihe ist) zu.
     * <p>
     * Zudem wird durch ein 3-Faches <i>Blinken</i> der entsprechenden Spieler-Information-Box,
     * dass dieser Spieler an der Reihe ist.
     *
     * @param playerIdxBefore Spieler-Index des vorherigen Spielers
     * @param playerIdx       Spieler-Index des Spielers der nun an der Reihe ist
     */
    @Override
    public void nextPlayer(int playerIdxBefore, int playerIdx) {
        // Weißen Hintergrund des vorherigen Spielers entfernen
        HBox hBoxOfOldPlayer = playerBoxes[playerIdxBefore];
        hBoxOfOldPlayer.setBackground(null);

        // Weißen Hintergrund für den Spieler der nun an der Reihe ist
        playerBoxes[playerIdx].setBackground(playerOnTurnBG);

        // FadeTransitions, welche ein Blinken simulieren, damit gezeigt wird das dieser Spieler
        // an der Reihe ist
        FadeTransition fadeIn = createFade(playerBoxes[playerIdx], 1, 0.5);
        fadeIn.play();
    }

    /**
     * Ersetzt die aktuelle zu erreichende Schatz-Grafik der Spieler-Informationsbox
     * (gemäß {@code playerIdx}) mit einem Hinweis, dass der Spieler zu seiner Startposition gehen
     * soll, um zu gewinnen.
     *
     * @param playerIdx Index des Spielers, dessen Informationsbox aktualisiert wird
     */
    @Override
    public void showPlayerWinHint(int playerIdx) {
        Label lbl = new Label(WIN_HINT);
        lbl.setWrapText(true);
        lbl.prefWidthProperty().bind(playerBoxes[playerIdx].widthProperty().divide(2));
        lbl.setStyle("-fx-padding: 5px");
        playerBoxes[playerIdx].getChildren().remove(currTreasures[playerIdx]);
        playerBoxes[playerIdx].getChildren().add(lbl);
    }

    /**
     * Lädt das GUI-Feld, wenn ein Speicherstand geladen wird.
     *
     * @param board          Das Spielfeld
     * @param insetPositions Einschubpositionen
     * @param freeWayCard    Freie Gängekarte
     */
    @Override
    public void loadField(Tile[][] board, Map<Position, Direction> insetPositions,
                          FreeWayCard freeWayCard) {

        // Gängekarte- und Schatzkartenbilder löschen
        for (StackPane[] stackPanes : field) {
            for (StackPane stackPane : stackPanes) {
                stackPane.getChildren().clear();
            }
        }

        // Initialisieren des geladenen Feldes
        for (ImageView view : insetArrows.values()) {
            gamefield.getChildren().remove(view);
        }

        // Erzeugen des Spielfeldes in der GUI
        initializeField(board, insetPositions, freeWayCard);

        if (insetPositions.containsKey(freeWayCard.getPosition())) {
            currForbiddenInsetPos = insetArrows.get(freeWayCard.getPosition());
            currForbiddenInsetPos.setOpacity(HALF_OPACITY);
        }
    }

    /**
     * Die {@link Rectangle} der Felder auf dem Feld entsprechende Farben zuweisen.
     *
     * <ul>
     *      <li>Grün → betretbar</li>
     *      <li>Rot → nicht betretbar</li>
     * </ul>
     *
     * @param nodes Felder, welche die verschiedenen betretbaren Position repräsentieren
     */
    @Override
    public void setPossiblePos(PathNode[][] nodes) {
        for (int x = 0; x < gamefield.getColumnCount() - 2; x++) {
            for (int y = 0; y < gamefield.getRowCount() - 2; y++) {
                Rectangle rect = hoverViews[x + 1][y + 1];
                if (nodes[x][y] != null) {
                    rect.setFill(Color.LIGHTGREEN);
                } else {
                    rect.setFill(Color.RED);
                }
                createFadeInTrans(rect);
            }
        }
    }

    /**
     * Erzeugt eine Spieler-Bewegung-Animation und führt diese aus.
     *
     * @param player    Spieler-Index innerhalb des Spiels
     * @param positions Pfad der Bewegungsanimation
     * @param game      Referenz des Spiels, um dieses anzuweisen, weiterzumachen
     */
    public void movePlayer(int player, List<Position> positions, Treasure collected,
                           Treasure nextTreasure, Game game) {
        createMoveAnimation(player, positions, collected, nextTreasure, game).play();
    }

    /**
     * Erzeugt eine {@link RotateTransition} und führt diese aus.
     *
     * @param rotation Rotation in die rotiert werden soll
     * @param rotDir   Rotationsrichtung
     */
    @Override
    public void rotateFreeWayCard(TileRotation rotation, RotateDirection rotDir) {
        // Rotations-Animation erzeugen und die freie Gängekarte hinzufügen
        RotateTransition rt = new RotateTransition();
        rt.setNode(freeWayCard);

        // Aktuelle Rotation der freien Gängekarte in der GUI erhalten
        double deg = freeWayCard.getRotate();

        if (rotDir == RotateDirection.CLOCKWISE) {
            deg += ROTATION_STEP;
        } else {
            deg -= ROTATION_STEP;
        }

        // Rotation soll auf den berechneten Grad erfolgen
        rt.setToAngle(deg);

        // Sobald die Rotationsanimation beendet ist, die freie Gängekarte der GUI auf die
        // übergebene Rotation setzen, da sie negativ sein kann
        rt.setOnFinished(e -> {
            freeWayCard.setRotate(rotation.getDeg());
            disableField(false);
        });

        // Ausführen der Rotations-Animation
        rt.play();
    }

    /**
     * Erzeugt und zeigt die neue freie Gängekarte an.
     *
     * @param tile Die neue freie Gängekarte
     */
    @Override
    public void setFreeWayCard(Tile tile) {
        createAndDisplayTile(freeWayCard, tile);
    }

    /**
     * Zeigt eine {@link Alert Warnung}, bei einer fehlerhaften Benutzereingabe.
     *
     * @param message Nachricht die dem Benutzer angezeigt wird
     */
    @Override
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setResizable(true);
        alert.showAndWait();
        disableField(false);

    }

    /**
     * Zeigt eine Fehlermeldung mit der übergebenen Nachricht an.
     *
     * @param message Nachricht die ausgegeben wird
     */
    @Override
    public void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText("Fehler beim Laden des Spielstandes");
        alert.show();
    }

    /**
     * Zeigt den Gewinner des Spiels an.
     *
     * @param playerName Name des Spielers, welcher gewonnen hat
     */
    @Override
    public void showWinner(String playerName) {
        String winnerText = String.format(Message.WINNER_MESSAGE_TEMPLATE.getMessage(), playerName);
        Alert winnerAlert = new Alert(Alert.AlertType.NONE, winnerText, ButtonType.OK);
        winnerAlert.setHeaderText(null);
        winnerAlert.show();
    }

    /**
     * Deaktiviert das Spielfeld für Benutzereingaben.
     *
     * @param value true, für das deaktivieren; false, für das Aktivieren
     */
    public void disableField(boolean value) {
        gamefield.setDisable(value);
        changeDirs.setDisable(value);
        saveGame.setDisable(value);
        loadGame.setDisable(value);
    }

    /**
     * Weist den Pfeil-ImageViews zur Rotation der freien Gängekarte, die entsprechenden Grafiken
     * zu.
     *
     * @param changeDirArrows ImageViews
     */
    private void initializeRotArrow(ImageView[] changeDirArrows) {
        changeDirArrows[0].setImage(CHANGEDIR_ARROW_IMG_LEFT);
        changeDirArrows[1].setImage(CHANGEDIR_ARROW_IMG_RIGHT);
    }

    /**
     * Erzeugt eine RotationTransition für die Rotation der freien Gängekarte.
     *
     * @param deg Grad in welche die freie Gängekarte gedreht werden soll
     * @return RotateTransition
     */
    private RotateTransition createRotateTransition(int deg) {
        RotateTransition rt = new RotateTransition(Duration.seconds(1));
        rt.setNode(freeWayCard);

        rt.setOnFinished(e -> freeWayCard.setRotate(deg));

        rt.setToAngle(deg);

        return rt;
    }

    /**
     * Hilfsmethode, welche eine Einschub-Animation erzeugt.
     * <p>
     * Zu einer Einschub-Animation gehören:
     *
     * <ul>
     *     <li>FadeTransitions für das Ein- und Ausblenden der alten freien Gängekarte</li>
     *     <li>FadeTransitions für das Ein- und Ausblenden der neuen freien Gängekarte</li>
     *     <li>ParallelTransitions für die den Einschub und Bewegung aller betroffenen
     *         Gängekarten</li>
     * </ul>
     *
     * @param shift Einschuboperation, die alle Informationen und Methoden beinhaltet
     * @return SequentialTransition mit den genannten Animationen
     */
    private SequentialTransition createShiftAnimation(Shift shift) {
        // Größe der Stage darf während einer Animation nicht angepasst werden
        stage.setResizable(false);

        SequentialTransition sq = new SequentialTransition();

        // Startposition der Einschiebeoperation
        Position insetPos = shift.getInsetPos();

        // Letzte betroffene Position der Einschiebeoperation
        Position lastPos = shift.getLastAffectedPosition();

        // Werte für die Verschiebung der Gängekarten auf dem GridPane
        double widthDbl = gamefield.getWidth() / gamefield.getColumnCount();
        double heightDbl = gamefield.getHeight() / gamefield.getRowCount();

        // Erzeugen einer parallelen Transition für die Verschiebung der Gängekarte
        ParallelTransition shiftAnimation = new ParallelTransition();

        // Neues StackPane erzeugen, welche die freie Gängekarte beinhaltet
        StackPane newTile = new StackPane();
        createAndDisplayTile(newTile, shift.getFreeWayCard());
        newTile.setOpacity(0);
        newTile.toFront();

        double shiftSpeed = this.shiftDurationSld.getValue();

        // Fade-Animationen für die alte freie Gängekarte
        FadeTransition fadeInOldFWC = createFade(newTile, 1, shiftSpeed);
        FadeTransition fadeOutOldFWC = createFade(freeWayCard, 0, shiftSpeed);
        FadeTransition fadeInInsetArrow = createFade(currForbiddenInsetPos, 1, shiftSpeed);
        ParallelTransition oldFWCAnimation = new ParallelTransition();
        oldFWCAnimation.getChildren().addAll(fadeInOldFWC, fadeOutOldFWC, fadeInInsetArrow);

        // Aktualisieren des Einschubpfeils, welcher nicht im nächsten Zug benutzt werden darf
        Position newForbiddenInsPos = lastPos.addPos(shift.getPushDir().getDirPos());
        currForbiddenInsetPos = insetArrows.get(newForbiddenInsPos);

        // Fade-Animationen für die neue freie Gängekarte
        ParallelTransition newFWCAnimation = new ParallelTransition();
        FadeTransition fadeInNewFWC = createFade(freeWayCard, 1, shiftSpeed);
        FadeTransition fadeOutNewFWC = createFade(field[lastPos.getX()][lastPos.getY()], 0, shiftSpeed);
        FadeTransition fadeOutInsetArrow = createFade(currForbiddenInsetPos, HALF_OPACITY, shiftSpeed);
        newFWCAnimation.getChildren().addAll(fadeInNewFWC, fadeOutNewFWC, fadeOutInsetArrow);

        // Hinzufügen der neuen Gängekarte auf dem Pfeil, von dem eingeschoben werden soll
        field[insetPos.getX()][insetPos.getY()].getChildren().add(newTile);

        // Erzeugen einer Liste von Nodes, in welche jede betroffene Node im Rahmen des
        // Einschubvorgangs hinzugefügt wird
        ObservableList<Node> list = FXCollections.observableArrayList();
        list.add(newTile);

        // Jedes betroffenes StackPane zu einer Liste zusammenführen
        Position[] affectedPositions = shift.getAffectedPositions();
        for (Position pos : affectedPositions) {
            list.addAll(field[pos.getX()][pos.getY()]);
        }

        // Herausgeschobene Gängekarte soll über den Einschiebepfeil verlaufen
        field[lastPos.getX()][lastPos.getY()].toFront();

        // Spieler(-Kreise) ebenfalls zu Liste hinzufügen, wenn diese betroffen sind
        List<Integer> affectedPlayers = shift.getAffectedPlayer();
        for (int playerIdx : affectedPlayers) {
            list.add(playerCircles[playerIdx]);
            playerCircles[playerIdx].toFront();
        }

        // Iteration durch alle Nodes um jeweils eine TranslateTransition für diesen zu erzeugen
        Duration test = new Duration(shiftSpeed * 1000 + 1);
        for (Node node : list) {
            TranslateTransition shifting = new TranslateTransition(test, node);

            switch (shift.getPushDir()) {
                case RIGHT:
                    shifting.byXProperty().set(widthDbl);
                    break;
                case LEFT:
                    shifting.byXProperty().set(-widthDbl);
                    break;
                case UP:
                    shifting.byYProperty().set(-heightDbl);
                    break;
                case DOWN:
                    shifting.byYProperty().set(heightDbl);
                    break;
            }

            // TranslateTransition der ParallelTransition hinzufügen
            shiftAnimation.getChildren().add(shifting);
        }

        // Nach der Einschub-Animation wird die freie Gängekarte aktualisiert (GUI)
        shiftAnimation.setOnFinished(e -> {
            setFreeWayCard(shift.getNewFreeWayCard());

            // Nach der Animation darf die Größe der Stage wieder angepasst werden
            stage.setResizable(true);
        });

        sq.setOnFinished(actionEvent -> {

            // Freie Gängekarte von der Einschubposition entfernen
            field[shift.getInsetPos().getX()][shift.getInsetPos().getY()].getChildren()
                    .remove(newTile);

            // Freie Gängekarte zu ihrer eingeschobenen Position hinzufügen
            createAndDisplayTile(field[lastPos.getX()][lastPos.getY()], shift.getFreeWayCard());
            field[lastPos.getX()][lastPos.getY()].toBack();

            // Einschiebeoperation am StackPane-Array durchführen
            shift.executeShift(field);

            // Alle betroffenen StackPane-Positionen aktualisieren
            for (Position currPos : shift.getAffectedPositions()) {
                setStackPaneToPos(field[currPos.getX()][currPos.getY()], currPos.getX(),
                        currPos.getY());
            }

            // Positionen der Spielerkreise aktualisieren
            int playerIdx = 0;
            while (playerIdx < affectedPlayers.size()) {

                // Erzeugen der neuen Position des aktuellen Spielerkreises
                Position newPosition = shift.getUpdatedPlayerPos().get(playerIdx);

                // Spielerkreis zu neuer Position zuweisen
                movePlayerCircle(playerCircles[affectedPlayers.get(playerIdx)],
                        newPosition.getGlobalPos());
                playerIdx++;
            }

            field[lastPos.getX()][lastPos.getY()].toBack();

        });

        oldFWCAnimation.setOnFinished(e -> field[lastPos.getX()][lastPos.getY()].setOpacity(1));
        newFWCAnimation.setOnFinished(e -> field[lastPos.getX()][lastPos.getY()].setOpacity(1));

        sq.getChildren().addAll(oldFWCAnimation, shiftAnimation, newFWCAnimation);
        return sq;
    }

    /**
     * Erzeugt für eine Spielerfigur eine Bewegungsanimation.
     * <p>
     * Die Animation wird mit einer {@link SequentialTransition} erzeugt, in welcher jeder Schritt
     * zum nächsten Feld eingefügt wird. Der Pfad, also alle Positionen (einschließlich der
     * Startposition), bis zum Ziel ist über {@code positions} gegeben und mit welcher eine
     * horizontale oder vertikalen Bewegung jeweils berechnet wird.
     * <p>
     * Über {@code collectedTreasure} wird der eingesammelte Schatz angegeben oder {@code null},
     * wenn kein Schatz eingesammelt wurde. Ähnlich wird {@code nexTreasure} behandelt.
     * <p>
     * Mit der Referenz des Spiels {@code game}, wird das Spiel zum Weitermachen angewiesen.
     *
     * @param playerIdx         Spieler-Index, dessen Figur bewegt wird
     * @param positions         Pfad (Liste aus {@link Position}) zum Ziel
     * @param collectedTreasure Schatz, welcher eingesammelt wurde oder {@code null}, wenn
     *                          kein Schatz eingesammelt wurde
     * @param nextTreasure      Nächster Schatz, welcher erreicht werden muss oder {@code null}, wenn
     *                          kein Schatz eingesammelt wurde
     * @param game              Referenz des Spiels
     * @return SequentialTransition, die eine Spielfigur-Bewegung enthält
     */
    private SequentialTransition createMoveAnimation(int playerIdx, List<Position> positions,
                                                     Treasure collectedTreasure,
                                                     Treasure nextTreasure, Game game) {

        // Größenänderung der Stage verhindern
        stage.setResizable(false);

        final double sliderVal = this.moveDurationSld.getValue();
        final Duration duration = new Duration(sliderVal * MILLIS_MULTIPLIER + DEFAULT_MILLI);
        final int lastPos = positions.size() - 1;

        Circle currCircle = playerCircles[playerIdx];
        currCircle.toFront();

        GridPane.setHalignment(currCircle, HPos.CENTER);
        GridPane.setValignment(currCircle, VPos.CENTER);

        // Erstellt für jede Linie eine Animation figureStep und fasst alle figureSteps zusammen in
        // einem figureMove
        SequentialTransition figureMove = new SequentialTransition();

        for (int i = lastPos - 1; i >= 0; i--) {
            Position currPos = positions.get(i).getGlobalPos();
            Position posBefore = positions.get(i + 1).getGlobalPos();

            final double xMove = (currPos.getX() - posBefore.getX()) * fieldWidth;
            final double yMove = (currPos.getY() - posBefore.getY()) * fieldHeight;

            TranslateTransition figureStep = new TranslateTransition(duration, currCircle);
            figureStep.byXProperty().set(xMove);
            figureStep.byYProperty().set(yMove);
            figureMove.getChildren().add(figureStep);
            field[currPos.getX() + 1][currPos.getY() + 1].toBack();
        }

        figureMove.onFinishedProperty().set((ActionEvent actionEvent1) -> {
            Position targetPos = positions.get(0).getGlobalPos();

            if (nextTreasure != null) {
                field[targetPos.getX()][targetPos.getY()].getChildren()
                        .remove(treasureViews[collectedTreasure.ordinal()]);
                currTreasures[playerIdx].setImage(treasureViews[nextTreasure.ordinal()].getImage());
                int oldCount = Integer.parseInt(leftTreasuresLbls[playerIdx].getText());
                leftTreasuresLbls[playerIdx].setText(String.valueOf(oldCount - 1));

                if (oldCount == 1) {
                    showPlayerWinHint(playerIdx);
                }
            }


            // Spieler-Kreis aktualisieren
            movePlayerCircle(currCircle, targetPos);

            // Kreis innerhalb der GridPane-Zelle platzieren
            GridPane.setHalignment(currCircle, HPOS_OF_CIRCLES[playerIdx]);
            GridPane.setValignment(currCircle, VPOS_OF_CIRCLES[playerIdx]);

            // Hier muss das Spiel angestoßen werden, um den nächsten Zug durchführen zu können
            game.nextTurn();

            // Größenänderung der Stage erlauben
            stage.setResizable(true);
        });

        return figureMove;
    }


    /**
     * Erzeugt eine {@link FadeTransition} für ein bestimmtes Element.
     *
     * @param node     Element auf die, die {@link FadeTransition} angewandt wird
     * @param to       Deckkraft zu der die {@link FadeTransition} auf-/ausblenden soll
     * @param duration Dauer dieser {@link FadeTransition}
     * @return FadeTransition
     */
    private FadeTransition createFade(Node node, double to, double duration) {
        // FadeTransition erzeugen und Dauer setzen
        FadeTransition fade = new FadeTransition(Duration.seconds(duration));

        // Element setzen
        fade.setNode(node);

        // Deckkraft setzen
        fade.setToValue(to);

        return fade;
    }

    /**
     * Erzeugt eine temporäre Gängekarte, für die Einschubanimation.
     *
     * @param tile Gängekarte, welche in der GUI abgebildet werden soll
     */
    private void createAndDisplayTile(StackPane stkPn, Tile tile) {
        // Gängekarten-Bild zuweisen
        ImageView tileView = new ImageView(IMG_OF_WAYCARDS[tile.getType().ordinal()]);

        // Schatz-Bild zuweisen
        ImageView treasure = treasureViews[tile.getTreasure().ordinal()];

        // Größen der ImageViews an die GridPane-Zellen binden
        bindIvsToGrdPn(tileView, treasure);

        // Rotation zuweisen
        stkPn.setRotate(tile.getRotated().getDeg());

        // ImageViews hinzufügen
        stkPn.getChildren().addAll(tileView, treasure);
    }

    /**
     * Setzt das übergebene {@link StackPane}, auf die übergebenen Koordinaten im {@code gamefield}.
     *
     * @param tile StackPane, welches eine Gängekarte (ggf. mit Schatz) repräsentiert
     * @param x    x-Koordinate des Spielfelds
     * @param y    y-Koordinate des Spielfelds
     */
    private void setStackPaneToPos(StackPane tile, int x, int y) {
        GridPane.setColumnIndex(tile, x);
        GridPane.setRowIndex(tile, y);
        tile.toBack();
        tile.setTranslateX(0);
        tile.setTranslateY(0);
    }


    /**
     * Weist den übergebenen Spieler-Kreis auf die übergebene Position zu.
     *
     * @param circle   Spielerkreis
     * @param position Neue Position
     */
    private void movePlayerCircle(Circle circle, Position position) {
        // Nach der Bewegung wird die Figur der neuen Zelle zugeordnet, damit sie sauber positioniert bleibt.
        // Die Verschiebung muss wieder auf 0 gesetzt werden.
        GridPane.setColumnIndex(circle, position.getX());
        GridPane.setRowIndex(circle, position.getY());

        // Reihenfolge der GridPane-Zelle wieder herstellen
        circle.toBack();
        field[position.getX()][position.getY()].toBack();

        // Verschiebung zurücksetzen
        circle.setTranslateX(0);
        circle.setTranslateY(0);
    }

    /**
     * Bindet die übergebenen {@link ImageView} Argumente an das GridPane {@code gamefield}.
     *
     * @param imageViews ImageViews, dessen Größen, an die GridPane-Zellen angepasst werden sollen
     */
    private void bindIvsToGrdPn(ImageView... imageViews) {
        for (ImageView iv : imageViews) {
            iv.fitWidthProperty().bind(gamefield.widthProperty().divide(gamefield.getRowCount()));
            iv.fitHeightProperty().bind(gamefield.heightProperty().divide(gamefield.getColumnCount()));
        }
    }

    /**
     * Erzeugt für die übergebene FX-Komponente ({@code node} einen <i>Hover-Effekt</i> zur
     * Hervorhebung der Gängekarte.
     *
     * @param node FX-Komponente
     */
    private void createFadeInTrans(Node node) {
        // Einblenden, wenn mit der Maus die FX-Komponente betreten wird
        FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
        fadeIn.setNode(node);
        fadeIn.setToValue(HALF_OPACITY);
        node.setOnMouseEntered(e -> fadeIn.playFromStart());

        // Ausblenden, wenn die Maus die FX-Komponente verlässt
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        fadeOut.setNode(node);
        fadeOut.setToValue(0);
        node.setOnMouseExited(e -> fadeOut.playFromStart());
    }
}
