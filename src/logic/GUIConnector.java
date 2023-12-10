package logic;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Alert;
import javafx.scene.shape.Rectangle;
import logic.path.PathNode;
import logic.player.AIMove;
import logic.player.Player;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.tile.TileRotation;

import java.util.List;
import java.util.Map;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Interface, welche die GUI mit der Logik verbindet und alle Routinen zur Aktualisierung der
 * Oberfläche enthält.
 *
 * @author svnsrd  [Repository 37]
 * @version 08.08.2021
 */
public interface GUIConnector {

    /**
     * Erzeugt alle relevanten Schatzkarten-Bilder für das Spiel.
     */
    void createTreasureImg();

    /**
     * Setzt die Spieler-Informationen zurück.
     */
    void resetPlayerInfo();

    /**
     * Erzeugt einen Spieler-Kreis und weist diesen an die übergebene Startposition {@code startPos}
     * zu.
     *
     * @param playerIdx Index des Spielers, dessen Kreis erstellt werden soll
     * @param player    Korrespondierendes Spieler-Objekt zur {@code playerIdx}
     */
    void createPlayer(int playerIdx, Player player);


    /**
     * Initialisiert das Feld.
     *
     * @param board          Das Spielfeld (ohne Umrandung)
     * @param insetPositions Einschubpositionen
     * @param freeWayCard    Freie Gängekarte
     */
    void initializeField(Tile[][] board, Map<Position, Direction> insetPositions,
                         FreeWayCard freeWayCard);


    /**
     * Erzeugt und dreht eine Einschub-Pfeilgrafik.
     *
     * @param col       Spalte
     * @param row       Zeile
     * @param direction Richtung in die der Pfeil zeigen soll
     */
    void displayInsetArrow(int col, int row, Direction direction);


    /**
     * Animation für den von einem menschlichen Spieler getätigte Einschuboperation.
     *
     * @param shift             Einschuboperation, die alle Informationen zum Einschubvorgang
     *                          enthält
     * @param possiblePositions Die möglichen Positionen nach dem Einschub
     */
    void animateHumanShift(Shift shift, PathNode[][] possiblePositions);


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
     * @param shift        Einschuboperation
     * @param aiMove       KI-Zug
     * @param playerIdx    Spieler-Index innerhalb des Spiels
     * @param nextTreasure Nächster Schatz oder {@code null}, wenn kein Schatz eingesammelt wurde
     * @param path         Pfad zur Zielposition des KI-Zuges
     * @param game         Referenz des Spiels, um diese zum für den weiteren Ablauf anzuweisen
     */
    void aiMove(Shift shift, AIMove aiMove, int playerIdx, Treasure collected, Treasure nextTreasure,
                List<Position> path, Game game);


    /**
     * Hebt die Gängekarte an der übergebenen Position hervor, indem der das dazugehörige
     * {@link Rectangle} aus {@code hoverViews} mit einer {@link FadeTransition} hervorgehoben und
     * beim Loslassen der Taste {@code KeyCode.H} mittels einer anderen {@link FadeTransition}
     * die Hervorhebung zurückgesetzt wird.
     *
     * @param posToHighlight Position die hervorgehoben werden soll
     * @param playerIdx      Spieler-Index
     */
    void highlightTreasureToFind(Position posToHighlight, int playerIdx);

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
    void nextPlayer(int playerIdxBefore, int playerIdx);


    /**
     * Ersetzt die aktuelle zu erreichende Schatz-Grafik der Spieler-Informationsbox
     * (gemäß {@code playerIdx}) mit einem Hinweis, dass der Spieler zu seiner Startposition gehen
     * soll, um zu gewinnen.
     *
     * @param playerIdx Index des Spielers, dessen Informationsbox aktualisiert wird
     */
    void showPlayerWinHint(int playerIdx);


    /**
     * Lädt das GUI-Feld, wenn ein Speicherstand geladen wird.
     *
     * @param board          Das Spielfeld
     * @param insetPositions Einschubpositionen
     * @param freeWayCard    Freie Gängekarte
     */
    void loadField(Tile[][] board, Map<Position, Direction> insetPositions,
                   FreeWayCard freeWayCard);


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
    void setPossiblePos(PathNode[][] nodes);

    /**
     * Erzeugt eine Spieler-Bewegung-Animation und führt diese aus.
     *
     * @param player    Spieler-Index innerhalb des Spiels
     * @param positions Pfad der Bewegungsanimation
     * @param game      Referenz des Spiels, um dieses anzuweisen, weiterzumachen
     */
    void movePlayer(int player, List<Position> positions, Treasure collected, Treasure nextTreasure,
                    Game game);

    /**
     * Erzeugt eine {@link RotateTransition} und führt diese aus.
     *
     * @param rotation Rotation in die rotiert werden soll
     * @param rotDir   Rotationsrichtung
     */
    void rotateFreeWayCard(TileRotation rotation, RotateDirection rotDir);

    /**
     * Erzeugt und zeigt die neue freie Gängekarte an.
     *
     * @param tile Die neue freie Gängekarte
     */
    void setFreeWayCard(Tile tile);

    /**
     * Zeigt eine {@link Alert Warnung}, bei einer fehlerhaften Benutzereingabe.
     *
     * @param message Nachricht die dem Benutzer angezeigt wird
     */
    void showAlert(String message);

    /**
     * Zeigt eine Fehlermeldung mit der übergebenen Nachricht an.
     *
     * @param message Nachricht die ausgegeben wird
     */
    void showErrorAlert(String message);


    /**
     * Zeigt den Gewinner des Spiels an.
     *
     * @param playerName Name des Spielers, welcher gewonnen hat
     */
    void showWinner(String playerName);

    /**
     * Deaktiviert das Spielfeld für Benutzereingaben.
     *
     * @param value true, für das deaktivieren; false, für das Aktivieren
     */
    void disableField(boolean value);


}
