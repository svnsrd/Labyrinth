package logic;

import logic.tile.FreeWayCard;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import org.junit.Assert;
import org.junit.Test;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Spielfeldstests.
 *
 * @author Suwendi Suriadi (WInf104177)
 */
public class FieldTest {
    // Freie Gängekarte
    FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

    // Erzeugung eines 2x2 Feldes
    Field field = new Field(
            "I100,I100,I100,I100,I100\n" +
                    "I100,L200,I100,I100,I100\n" +
                    "I100,I000,I123,I100,I100\n" +
                    "L000,L300,I100,I100,I100\n" +
                    "I100,I100,I100,I100,I100", freeWayCard);
    @Test
    public void findTreasureOnRightPos() {
        Position res = field.getTreasurePos(Treasure.values()[23]);
        Position expPos = new Position(2, 2);

        Assert.assertEquals(expPos, res);
    }

    @Test
    public void findTreasure_NotOnField() {
        Position res = field.getTreasurePos(Treasure.values()[2]);
        Position expPos = new Position(-1, -1);

        Assert.assertEquals(expPos, res);
    }

    @Test
    public void getCorrectSizeOfAffectedTiles() {
        int rightSize = field.getAffectedTileSizeOfPush(Direction.RIGHT);
        int downSize = field.getAffectedTileSizeOfPush(Direction.DOWN);

        Assert.assertEquals(field.getBoard().length, rightSize);
        Assert.assertEquals(field.getBoard().length, downSize);
    }


    @Test
    public void getCorrectSizeOfAffectedTiles_differentLength() {

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,I100\n" +
                        "I100,L200,I100,I100,I100\n" +
                        "I100,I000,I123,I100,I100\n" +
                        "I100,I100,I100,I100,I100", null);

        int rightSize = field.getAffectedTileSizeOfPush(Direction.RIGHT);
        int downSize = field.getAffectedTileSizeOfPush(Direction.DOWN);

        Assert.assertEquals(field.getBoard().length, rightSize);
        Assert.assertEquals(field.getBoard()[0].length, downSize);
    }

}
