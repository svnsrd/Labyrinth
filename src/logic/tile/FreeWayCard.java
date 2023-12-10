package logic.tile;

import logic.Position;
import logic.Treasure;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Diese Klasse repräsentiert eine freie Gängekarte des Spiels.
 *
 * Sie erweitert eine {@link Tile normale Gängekarte} mit einer {@link Position Position}, welche
 * angibt, an welcher Stelle im nächsten Zug nicht eingeschoben werden darf.
 *
 * @author svnsrd  [Repo 37]
 * @version 01.08.2021
 */
public class FreeWayCard extends Tile {

    /**
     * Position der freien Gängekarte, welche angibt an welcher Stelle im Spielzug nicht
     * eingeschoben werden darf.
     */
    private Position position;

    /**
     *
     *
     * @param type
     * @param state
     * @param rotation
     * @param treasure
     * @param position
     */
    public FreeWayCard(TileShape type, TileState state, TileRotation rotation, Treasure treasure, Position position) {
        super(type, state, rotation, treasure);
        this.position = position;
    }

    /**
     *
     * @param tile
     * @param pos
     */
    public FreeWayCard(Tile tile, Position pos) {
        super(tile.getType(), tile.getState(), tile.getRotated(), tile.getTreasure());
        this.position = pos;
    }

    /**
     * Testzwecke.
     *
     * @param type
     * @param rotation
     * @param position
     */
    public FreeWayCard(TileShape type, TileRotation rotation, Position position) {
       super(type, null, rotation, Treasure.EMPTY);
       this.position = position;
    }

    /**
     * Liefert die Position der freien Gängekarte. An dieser Stelle darf <b>nicht</b> eingeschoben
     * werden.
     *
     * @return Position der freien Gängekartea
     */
    public Position getPosition() {
        return position;
    }
}
