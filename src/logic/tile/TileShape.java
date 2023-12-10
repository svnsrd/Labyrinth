package logic.tile;

import com.google.gson.annotations.SerializedName;
import logic.Direction;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Aufzählung beinhaltet die verschiedenen Formen, die eine Gängekarte haben kann.
 *
 * @author svnsrd  [Repo: 37]
 * @version 06.08.2021
 */
public enum TileShape {
    @SerializedName("0") T(new Direction[]{Direction.LEFT, Direction.RIGHT, Direction.DOWN}),
    @SerializedName("1") L(new Direction[]{Direction.UP, Direction.RIGHT}),
    @SerializedName("2") I(new Direction[]{Direction.UP, Direction.DOWN});

    /**
     * Die Ausgänge der Form
     */
    private final Direction[] openDirections;

    /**
     * Erzeugt eine Form mit ihren entsprechenden Ausgängen.
     *
     * @param openDirections Offene Ausgänge dieser Form
     */
    TileShape(Direction[] openDirections) {
        this.openDirections = openDirections;
    }

    /**
     * Liefert die offenen Ausgänge dieser Form.
     *
     * @return Offene Ausgänge
     */
    public Direction[] getOpenDirections() {
        return openDirections;
    }

}
