package logic.player;

import logic.Position;
import logic.tile.TileRotation;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse repräsentiert die berechneten Informationen eines KI-Zuges.
 *
 * @author svnsrd 
 * @version 01.08.2021
 */
public class AIMove {

    /**
     * Rotation der freien Gängekarte dieses KI-Zuges.
     */
    private final TileRotation freeWayCardRot;

    /**
     * Einschubposition dieses KI-Zuges.
     */
    private final Position insetPos;

    /**
     * Position zu der die KI sich bewegt.
     */
    private final Position targetPos;

    /**
     * Position des zu erreichenden Schatzes.
     */
    private final Position treasurePos;

    /**
     * Erzeugt einen KI-Zug.
     *
     * @param freeWayCardRot Rotation der freien Gängekarte
     * @param insetPos       Einschubposition
     * @param targetPos      Ziel dieses KI-Zuges
     */
    public AIMove(TileRotation freeWayCardRot, Position insetPos, Position targetPos, Position treasurePos) {
        this.freeWayCardRot = freeWayCardRot;
        this.insetPos = insetPos;
        this.targetPos = targetPos;
        this.treasurePos = treasurePos;
    }

    /**
     * Konstruktor eines KI-Zuges, in welcher die KI stehen bleibt.
     *
     * @param freeWayCardRot Rotation der freien Gängekarte
     * @param insetPos Einschubposition
     */
    public AIMove(TileRotation freeWayCardRot, Position insetPos) {
        this(freeWayCardRot, insetPos, null, null);
    }

    /**
     * Liefert, welche Rotation die freie Gängekarte bei dem Einschub haben soll.
     *
     * @return Rotation der freien Gängekarte
     */
    public TileRotation getFreeWayCardRot() {
        return freeWayCardRot;
    }

    /**
     * Liefert die Zielposition, zu welcher sich die KI nach dem Einschub bewegen soll.
     *
     * @return Position zu der sich die KI, nach Einschub bewegt
     */
    public Position getTargetPos() {
        return targetPos;
    }

    /**
     * Liefert die Position, in welche die KI die freie Gängekarte einschiebt.
     *
     * @return Position des Einschubs
     */
    public Position getInsetPos() {
        return insetPos;
    }

    /**
     * Liefert die Position des zu suchenden Schatzes dieses KI-Zuges.
     *
     * @return Position des zu suchenden Schatzes in diesem KI-Zug
     */
    public Position getTreasurePos() {
        return treasurePos;
    }

    @Override
    public String toString() {
        return "AIMove{" +
                "freeWayCardRot=" + freeWayCardRot +
                ", insetPos=" + insetPos +
                ", targetPos=" + targetPos +
                ", treasurePos=" + treasurePos +
                '}';
    }
}
