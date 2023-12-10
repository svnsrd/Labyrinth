package logic;

import logic.tile.Tile;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import logic.tile.TileState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Testet Methoden der {@link Tile}.
 *
 * @author Suwendi Suriadi (WInf104177)
 */
public class TileTest {

    Tile L_tile_0 = new Tile(TileShape.L, TileState.STATIC, TileRotation.ROT_0);
    Tile L_tile_90 = new Tile(TileShape.L, TileState.STATIC, TileRotation.ROT_90);
    Tile L_tile_180 = new Tile(TileShape.L, TileState.STATIC, TileRotation.ROT_180);
    Tile L_tile_270 = new Tile(TileShape.L, TileState.STATIC, TileRotation.ROT_270);

    Tile T_tile_0 = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_0);
    Tile T_tile_90 = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_90);
    Tile T_tile_180 = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_180);
    Tile T_tile_270 = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_270);

    @Test
    public void getOpenDirections_L() {
        Direction[] expDirs_0 = new Direction[]{Direction.UP, Direction.RIGHT};
        Direction[] expDirs_90 = new Direction[]{Direction.RIGHT, Direction.DOWN};
        Direction[] expDirs_180 = new Direction[]{Direction.DOWN, Direction.LEFT};
        Direction[] expDirs_270 = new Direction[]{Direction.LEFT, Direction.UP};

        Assert.assertArrayEquals(expDirs_0, L_tile_0.getOpenDirections());
        Assert.assertArrayEquals(expDirs_90, L_tile_90.getOpenDirections());
        Assert.assertArrayEquals(expDirs_180, L_tile_180.getOpenDirections());
        Assert.assertArrayEquals(expDirs_270, L_tile_270.getOpenDirections());
    }

    @Test
    public void canBeEnteredFrom_L() {
        Assert.assertTrue(L_tile_0.canBeEnteredFrom(Direction.UP));
        Assert.assertTrue(L_tile_0.canBeEnteredFrom(Direction.RIGHT));
        Assert.assertTrue(L_tile_90.canBeEnteredFrom(Direction.DOWN));
        Assert.assertTrue(L_tile_90.canBeEnteredFrom(Direction.RIGHT));
        Assert.assertTrue(L_tile_180.canBeEnteredFrom(Direction.DOWN));
        Assert.assertTrue(L_tile_180.canBeEnteredFrom(Direction.LEFT));
        Assert.assertTrue(L_tile_270.canBeEnteredFrom(Direction.LEFT));
        Assert.assertTrue(L_tile_270.canBeEnteredFrom(Direction.UP));
    }

    @Test
    public void getOpenDirections_T() {
        Direction[] expDirs_0 = new Direction[]{Direction.LEFT, Direction.RIGHT, Direction.DOWN};
        Direction[] expDirs_90 = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT};
        Direction[] expDirs_180 = new Direction[]{Direction.RIGHT, Direction.LEFT, Direction.UP};
        Direction[] expDirs_270 = new Direction[]{Direction.DOWN, Direction.UP, Direction.RIGHT};

        Assert.assertArrayEquals(expDirs_0, T_tile_0.getOpenDirections());
        Assert.assertArrayEquals(expDirs_90, T_tile_90.getOpenDirections());
        Assert.assertArrayEquals(expDirs_180, T_tile_180.getOpenDirections());
        Assert.assertArrayEquals(expDirs_270, T_tile_270.getOpenDirections());
    }

    @Test
    public void canBeEnteredFrom_T() {
        Assert.assertTrue(T_tile_0.canBeEnteredFrom(Direction.LEFT));
        Assert.assertTrue(T_tile_0.canBeEnteredFrom(Direction.RIGHT));
        Assert.assertTrue(T_tile_0.canBeEnteredFrom(Direction.DOWN));
        Assert.assertTrue(T_tile_90.canBeEnteredFrom(Direction.LEFT));
        Assert.assertTrue(T_tile_90.canBeEnteredFrom(Direction.UP));
        Assert.assertTrue(T_tile_90.canBeEnteredFrom(Direction.DOWN));
        Assert.assertTrue(T_tile_180.canBeEnteredFrom(Direction.UP));
        Assert.assertTrue(T_tile_180.canBeEnteredFrom(Direction.LEFT));
        Assert.assertTrue(T_tile_180.canBeEnteredFrom(Direction.RIGHT));
        Assert.assertTrue(T_tile_270.canBeEnteredFrom(Direction.UP));
        Assert.assertTrue(T_tile_270.canBeEnteredFrom(Direction.DOWN));
        Assert.assertTrue(T_tile_270.canBeEnteredFrom(Direction.RIGHT));
    }
}
