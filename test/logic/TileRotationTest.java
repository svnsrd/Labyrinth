package logic;

import logic.tile.TileRotation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Gängekarten-Drehungstests.
 *
 * @author Suwendi Suriadi (WInf104177)
 */
public class TileRotationTest {

    @Test
    public void getCorrectNextRotation() {
        Assert.assertEquals(TileRotation.ROT_90, TileRotation.ROT_0.getNextRotation(RotateDirection.CLOCKWISE));
        Assert.assertEquals(TileRotation.ROT_270, TileRotation.ROT_0.getNextRotation(RotateDirection.ANTICLOCKWISE));

        Assert.assertEquals(TileRotation.ROT_0, TileRotation.ROT_270.getNextRotation(RotateDirection.CLOCKWISE));
        Assert.assertEquals(TileRotation.ROT_90, TileRotation.ROT_180.getNextRotation(RotateDirection.ANTICLOCKWISE));
    }
}
