package logic;

import logic.tile.TileRotation;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Aufzählung repräsentiert Richtungen.
 * <p>
 * Im Rahmen des Spiels kann eine Richtung die <i>Einschieberichtung</i> oder die Richtung
 * in die ein Spieler gehen kann definieren.
 *
 * @author svnsrd  [Repo 37]
 * @version 08.08.2021
 */
public enum Direction {
    UP(new Position(0, -1)),
    RIGHT(new Position(1, 0)),
    DOWN(new Position(0, 1)),
    LEFT(new Position(-1, 0));

    /**
     * Richtung als Position.
     * <p>
     * Beispiel an einem Koordinatensystem, in welcher (0,0) die obere linke Ecke ist:
     * <p>
     * Um von einer {@link Position} auf die obere benachbarte Position zu kommen, muss man die
     * Ausgangsposition mit (0, -1) addieren.
     */
    private final Position dir;

    /**
     * Konstruktor.
     *
     * @param dir Position die, die Richtung repräsentiert
     */
    Direction(Position dir) {
        this.dir = dir;
    }

    /**
     * Liefert die {@link Position}, welche diese Richtung repräsentiert.
     *
     * @return Position, die diese Richtung repräsentiert
     */
    public Position getDirPos() {
        return dir;
    }

    /**
     * Liefert zu dieser Richtung die <b>entgegengesetzte Richtung</b>.
     *
     * @return Entgegengesetzte Richtung der übergebenen Richtung
     */
    public Direction getOppositeDir() {
        return Direction.values()[(this.ordinal() + TileRotation.ROT_180.ordinal())
                % Direction.values().length];
    }
}
