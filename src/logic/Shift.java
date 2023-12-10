package logic;

import logic.tile.FreeWayCard;
import logic.tile.Tile;

import java.util.ArrayList;
import java.util.List;


/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse enthält alle Informationen und Funktionalitäten zur Ausführung eines
 * Einschiebevorgangs.
 * <p>
 * Die Verarbeitung beschränkt sich auf die Verschiebung der in der Logik, als auch in der GUI
 * entsprechenden Instanzen (Tile oder StackPane).
 *
 * @author svnsrd  [Repo 37]
 * @version 01.08.2021
 */
public class Shift {

    /**
     * Richtung in die dieser Einschubvorgang geht.
     */
    private final Direction pushDir;

    /**
     * Position des Pfeiles, von der die freie Gängekarte eingeschoben werden soll.
     */
    private final Position insetPos;

    /**
     * Betroffene Positionen dieser Einschuboperation.
     */
    private final Position[] affectedPositions;

    /**
     * Betroffene Spieler.
     */
    private final List<Integer> affectedPlayer;

    /**
     * Aktualisierte Positionen der betroffenen Spieler.
     */
    private final List<Position> updatedPlayerPos;

    /**
     * Aktuelle freie Gängekarte mit welche eingeschoben wird.
     */
    private final FreeWayCard freeWayCard;

    /**
     * Die neue freie Gängekarte.
     */
    private Tile newFreeWayCard;


    /**
     * Erzeugt eine Einschub-Operation mit allen notwendigen Informationen.
     *
     * @param pushDir           Richtung des Einschubes
     * @param insetPos          Einschub-Position (Position in der Umrandung)
     * @param affectedPositions Betroffene Positionen dieser Einschiebeoperation
     * @param affectedPlayer    Indices der betroffenen Spieler
     * @param affectedPlayerPos Positionen der betroffenen Spieler
     * @param newFreeWayCard    Die aus der Einschuboperation resultierende neue freie Gängekarte
     * @param freeWayCard       Freie Gängekarte mit der eingeschoben wird
     */
    Shift(Direction pushDir, Position insetPos, Position[] affectedPositions,
          List<Integer> affectedPlayer, List<Position> affectedPlayerPos, FreeWayCard freeWayCard,
          Tile newFreeWayCard) {

        this.pushDir = pushDir;
        this.insetPos = insetPos;
        this.affectedPositions = affectedPositions;
        this.affectedPlayer = affectedPlayer;
        this.freeWayCard = freeWayCard;
        this.newFreeWayCard = newFreeWayCard;

        this.updatedPlayerPos = new ArrayList<>();
        for (Position pos : affectedPlayerPos) {
            this.updatedPlayerPos.add(updatePlayerPos(pos));
        }
    }

    /**
     * Konstruktor zur Erzeugung einer Einschuboperation im Rahmen der Berechnung eines KI-Zuges.
     *
     * @param pushDir           Richtung des Einschubes
     * @param insetPos          Einschub-Position (Position in der Umrandung)
     * @param freeWayCard       Freie Gängekarte mit der eingeschoben wird
     * @param affectedTileSize  Anzahl der betroffenen Gängekarten
     */
    public Shift(Direction pushDir, Position insetPos, FreeWayCard freeWayCard,
                 int affectedTileSize) {

        this.pushDir = pushDir;
        this.insetPos = insetPos;
        this.freeWayCard = freeWayCard;
        this.affectedPositions = new Position[affectedTileSize];
        this.updatedPlayerPos = null;
        this.affectedPlayer = null;

        Position currPos = insetPos;
        for (int i = 0; i < affectedTileSize; i++) {
            // Nächste Position
            currPos = currPos.addPos(pushDir.getDirPos());

            // Position hinzufügen
            affectedPositions[i] = currPos;
        }
    }

    /**
     * Führt den Einschubvorgang aus.
     * <p>
     * Diese Methode mit parametrisierten Typen, d.h. entscheidend für den Typ der im Rahmen
     * dieser Methode verarbeitet wird, ist der Parameter <i>board</i>.
     * <p>
     * Ein Einschubvorgang muss sowohl in der {@link Field} als auch in der
     * {@link gui.JavaFXGUI} ausgeführt werden, so wird im Rahmen der Logik ein {@link Tile}-Array
     * übergeben und im Rahmen der GUI ein {@link javafx.scene.layout.StackPane}-Array.
     *
     * @param board Das Feld auf dem die Verschiebung ausgeführt wird
     * @param <T>   Typ der Gängekarten, im Rahmen der {@link gui.JavaFXGUI} StackPane und in der
     *              Logic {@link Tile}
     * @return Die neue freie Gängekarte
     */
    public <T> T executeShift(T[][] board) {
        Position adjustPos = new Position(0, 0);
        Position startPos = affectedPositions[0];
        Position lastPos = getLastAffectedPosition();
        int affectedTilesSize = affectedPositions.length;

        // Sollte ein Spielfeld verwendet werden, das eine unterschiedliche Anzahl von Zeilen
        // und Spalten hat, ist eine Unterscheidung der Zeile/Reihen-Länge erforderlich
        int lineLength;
        if (this.pushDir == Direction.LEFT || this.pushDir == Direction.RIGHT) {
            lineLength = board.length;
        } else {
            lineLength = board[0].length;
        }

        // Positionen müssen angepasst werden, wenn das logische Feld verarbeitet wird
        if (lineLength == affectedTilesSize) {
            adjustPos = adjustPos.getLogicalPos();
            startPos = startPos.addPos(adjustPos);
            lastPos = lastPos.addPos(adjustPos);
        }

        T newFreeWayCard = board[lastPos.getX()][lastPos.getY()];
        for (int i = affectedTilesSize - 1; i > 0; i--) {
            Position pos = affectedPositions[i].addPos(adjustPos);
            Position nextPos = affectedPositions[i - 1].addPos(adjustPos);
            board[pos.getX()][pos.getY()] = board[nextPos.getX()][nextPos.getY()];
        }


        board[startPos.getX()][startPos.getY()] = newFreeWayCard;

        return newFreeWayCard;
    }


    /**
     * Liefert eine aktualisierte Position der übergebenen Position im Rahmen der Einschuboperation.
     * <p>
     * Neben dem einheitlichen Verschieben durch die Einschuboperation, wird zu dem geprüft, ob die
     * Position nun außerhalb des Spielfeldes ist. Ist dies der Fall, wird die Position gegenüber
     * zugewiesen.
     *
     * @param currPlayerPos Übergebene Position
     * @return Aktualisierte Position ausgehend von der {@code currPlayerPos}
     */
    public Position updatePlayerPos(Position currPlayerPos) {
        currPlayerPos = currPlayerPos.addPos(pushDir.getDirPos());

        // Befindet sich der Spieler nun außerhalb des Spielfeldes, so wurde er
        // dieser rausgeschoben und wird gegenüber platziert
        if (currPlayerPos.getX() < 0 || currPlayerPos.getY() < 0
                || currPlayerPos.getX() == affectedPositions.length
                || currPlayerPos.getY() == affectedPositions.length) {


            currPlayerPos = getLogicalStartPos();
        }

        return currPlayerPos;
    }

    /**
     * Liefert die Startposition des Einschubvorganges im Rahmen des logischen Spielfeldes, welches
     * keine Umrandung hat.
     *
     * @return Startposition im logischen Spielfeld
     */
    public Position getLogicalStartPos() {
        return this.insetPos.addPos(pushDir.getDirPos()).getLogicalPos();
    }

    /**
     * Gibt an, ob die übergebene Position in den betroffenen Positionen dieser Einschuboperation
     * liegt.
     *
     * @param pos Übergebene Position
     * @return True, wenn die Position betroffen ist, ansonsten false
     */
    public boolean isPositionAffected(Position pos) {
        for (Position currPos : affectedPositions) {
            if (currPos.getLogicalPos().equals(pos)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Liefert die <b>Richtung</b> der Einschiebung.
     *
     * @return Einschub-Richtung
     */
    public Direction getPushDir() {
        return pushDir;
    }

    /**
     * Liefert die Position von der im Rahmen der {@link gui.JavaFXGUI} (inklusive Umrandung)
     * eingeschoben wurde.
     *
     * @return Position des Einschubs (im Rahmen des kompletten Spielfeldes inklusive Umrandung)
     */
    public Position getInsetPos() {
        return insetPos;
    }

    /**
     * Liefert die Position der betroffenen Gängekarten im Rahmen des kompletten Spielfeldes, d.h.
     * inklusive Umrandung.
     *
     * @return Betroffene Positionen dieses Einschiebevorganges
     */
    public Position[] getAffectedPositions() {
        return affectedPositions;
    }

    /**
     * Liefert die <b>freie Gängekarte</b>, mit welcher in diesem Einschubvorgang eingeschoben wird.
     *
     * @return Freie Gängekarte mit der eingeschoben wird.
     */
    public FreeWayCard getFreeWayCard() {
        return freeWayCard;
    }

    /**
     * Liefert die letzte betroffene Position, von der {@code pushDir Einschubrichtung} ausgehend.
     *
     * @return Letzte betroffene Position
     */
    public Position getLastAffectedPosition() {
        return this.affectedPositions[affectedPositions.length - 1];
    }

    /**
     * Liefert die betroffenen Spieler.
     *
     * @return Liste der betroffenen Spieler
     */
    public List<Integer> getAffectedPlayer() {
        return affectedPlayer;
    }

    /**
     * Liefert die aktualisierten Spielerpositionen, der Spieler die betroffen sind.
     *
     * @return Liste der aktualisierten Positionen der betroffenen Spieler
     */
    public List<Position> getUpdatedPlayerPos() {
        return updatedPlayerPos;
    }

    /**
     * Liefert die neue freie Gängekarte.
     *
     * @return Die durch die Einschuboperation ergebene neue freie Gängekarte
     */
    public Tile getNewFreeWayCard() {
        return newFreeWayCard;
    }
}
