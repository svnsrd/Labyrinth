package logic;

import logic.path.PathNode;
import logic.path.PathUtil;
import logic.tile.FreeWayCard;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Pfadfindungstest.
 *
 * @author Suwendi Suriadi (WInf104177)
 */
public class PathTest {


    @Test
    public void checkIfPosOverCornerIsReachable_uninformedSearch() {

        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,L200\n" +
                        "I100,L200,I100,I100,I000\n" +
                        "I100,I000,I123,I100,I000\n" +
                        "L000,L300,I100,I100,I000\n" +
                        "I100,I100,I100,I100,L300", freeWayCard);

        // Berechnung der möglichen Positionen
        PathNode[][] nodes = PathUtil.getPossiblePositions(field.getBoard(), new Position(0, 0));

        // Position über zwei Ecken ist erreichbar
        Assert.assertNotNull(nodes[3][4]);

        // Eigene Position ist nicht null
        Assert.assertNotNull(nodes[0][0]);
    }

    @Test
    public void checkIfPosOverCornerIsReachable_informedSearch() {

        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,L200\n" +
                        "I100,L200,I100,I100,I000\n" +
                        "I100,I000,I123,I100,I000\n" +
                        "L000,L300,I100,I100,I000\n" +
                        "I100,I100,I100,I100,L300", freeWayCard);

        Map<Integer, Position> result = PathUtil.aStarSearch(field.getBoard(), new Position(0, 0), new Position(3, 4));

        Assert.assertTrue(result.containsValue(new Position(3, 4)));
        Assert.assertFalse(result.containsValue(new Position(1, 1)));
    }


    @Test
    public void targetPosNotReachable() {

        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,L200\n" +
                        "I100,L200,I100,I100,I000\n" +
                        "I100,I000,I123,I100,I000\n" +
                        "L000,L300,I100,I100,I000\n" +
                        "I100,I100,I100,I100,L300", freeWayCard);

        Map<Integer, Position> result = PathUtil.aStarSearch(field.getBoard(), new Position(0, 0), new Position(2, 2));

        Assert.assertFalse(result.containsValue(new Position(2, 2)));
    }
}
