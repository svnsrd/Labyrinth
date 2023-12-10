package logic.tile;

import logic.Direction;
import logic.Treasure;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse repräsentiert ein Feld auf dem Spielfeld
 *
 * @author svnsrd  [Repository 37]
 * @version 08.08.2021
 */
public class Tile {

    /**
     * Die Form der Gängekarte.
     */
    private final TileShape type;

    /**
     * Die Rotation der Gängekarte.
     */
    private TileRotation rotated;

    /**
     * Zustand der Gängekarte auf dem Spielfeld.
     */
    private transient final TileState state;

    /**
     * Schatz der sich auf der Gängekarte befindet.
     */
    private Treasure treasure;

    /**
     * Konstruktor zur Erzeugung einer Gängekarte ohne Schatz.
     *
     * @param type    Gangtyp (I, L, oder T)
     * @param rotated Rotation der Karte
     * @pre Übergebener Gängekarten-Typ, muss in den bereits bekannten Typen vorhanden sein
     */
    public Tile(TileShape type, TileState state, TileRotation rotated) {
        this.type = type;
        this.state = state;
        this.rotated = rotated;
        this.treasure = Treasure.EMPTY;
    }

    /**
     * Konstruktor zur Erzeugung einer Gängekarte und zusätzliche Zuweisung eines Schatzes.
     *
     * @param type     Gängekarten-Typ
     * @param state    Zustand der Gängekarte
     * @param rotated  Rotation der Gängekarte
     * @param treasure Schatz der sich auf der Gängekarte befindet
     */
    public Tile(TileShape type, TileState state, TileRotation rotated, Treasure treasure) {
        this(type, state, rotated);
        this.treasure = treasure;
    }

    /**
     * Konstruktor zur Erzeugung einer Gängekarte ohne Zustand.
     *
     * @param type     Gängekarten-Typ
     * @param rotated  Rotation der Gängekarte
     * @param treasure Schatz der sich auf der Gängekarte befindet
     */
    public Tile(TileShape type, TileRotation rotated, Treasure treasure) {
        this.type = type;
        this.rotated = rotated;
        this.treasure = treasure;
        this.state = null;
    }

    /**
     * Liefert die offenen Ausgänge dieser Gängekarte unter Berücksichtigung ihrer derzeitigen
     * Rotation.
     *
     * @return Offenen Ausgänge der Gängekarte
     */
    public Direction[] getOpenDirections() {
        // Anzahl der möglichen Ausgänge dieser Gängekarte
        final int openDirLen = this.type.getOpenDirections().length;

        // Ergebnis-Array, welches die offenen Ausgänge in Relation zur Rotation beinhaltet
        Direction[] openDirs = new Direction[openDirLen];

        // Initiale offene Ausgänge der Gängekarte, wenn sie 0° rotiert ist
        Direction[] initialDirs = this.type.getOpenDirections();

        // Ermittlung der offenen Ausgänge
        if (this.rotated == TileRotation.ROT_0) { /* Keine Rotation */
            openDirs = initialDirs;
        } else { /* Rotation vorhanden */
            for (int i = 0; i < openDirLen; i++) {
                openDirs[i] = Direction.values()[(initialDirs[i].ordinal()
                        + rotated.ordinal()) % Direction.values().length];
            }
        }

        return openDirs;
    }

    /**
     * Gibt an, ob von einer übergebenen Richtung aus in diese Gängekarte eingetreten werden kann.
     *
     * @param dir Richtung von der eingetreten werden soll
     * @return true, wenn das Eintreten funktioniert, ansonsten false
     */
    public boolean canBeEnteredFrom(Direction dir) {
        Direction[] openDirs = this.getOpenDirections();
        int openDirLen = openDirs.length;

        int i = 0;
        while (i < openDirLen && openDirs[i] != dir) {
            i++;
        }

        return i < openDirLen;
    }

    /**
     * Gibt die Rotation der Gängekarte zurück
     *
     * @return Rotation der Gängekarte
     */
    public TileRotation getRotated() {
        return rotated;
    }

    /**
     * Setzt die Rotation dieser Gängekarte.
     *
     * @param rotated Rotation die dieser Gängekarte zugewiesen werden soll
     */
    public void setRotated(TileRotation rotated) {
        this.rotated = rotated;
    }

    /**
     * Liefert den Zustand der Gängekarte auf dem Spielfeld.
     *
     * @return Zustand der Gängekarte
     */
    public TileState getState() {
        return state;
    }

    /**
     * Gibt den Schatz (oder keiner) der Gängekarte zurück
     *
     * @return Schatz der sich auf der Gängekarte befindet
     */
    public Treasure getTreasure() {
        return treasure;
    }

    /**
     * Legt einen Schatz auf die Gängekarte
     *
     * @param treasure Schatz, welcher auf die Gängekarte gelegt werden soll
     */
    public void setTreasure(Treasure treasure) {
        this.treasure = treasure;
    }

    /**
     * Liefert die Form der Gängekarte, wie sie in {@link TileShape}.
     *
     * @return Form der Gängekarte
     */
    public TileShape getType() {
        return type;
    }

    /**
     * Erzeugt eine String-Repräsentation der Gangkarte und gibt an welche Rotation und welchen Schatz sie hat.
     *
     * @return String-Repräsentation aller Informationen zu dieser Gängekarte.
     */
    @Override
    public String toString() {
        return String.valueOf(type) + rotated + treasure;
    }
}
