package logic;

import logic.tile.FreeWayCard;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Tests, zur Überprüfung der Funktionalität der Klasse {@link Shift}.
 *
 * @author Suwendi Suriadi (WInf104177) [Repo 37]
 */
public class ShiftTest {

    /**
     * Überprüft, ob {@code executeShift} korrekt funktioniert. Hierbei wird die letzte betroffene
     * Gängekarte nach vorne gesetzt. Im Rahmen der Spiellogik würde sie ersetzt werden.
     */
    @Test
    public void correctShift() {
        Field field = new Field("I012,I000,I200", new FreeWayCard(TileShape.T, TileRotation.ROT_0, null));

        FreeWayCard freeWayCard = new FreeWayCard(TileShape.T, TileRotation.ROT_0, null);
        Shift shift = new Shift(Direction.RIGHT, new Position(0, 1), freeWayCard, field.getBoard().length);
        shift.executeShift(field.getBoard());

        Field expField = new Field("I200,I012,I000", new FreeWayCard(TileShape.T, TileRotation.ROT_0, null));

        Assert.assertEquals(Arrays.deepToString(field.getBoard()), Arrays.deepToString(expField.getBoard()));
    }

    /**
     * Überprüft, ob von einer Einschuboperation ausgehend, betroffene Spielerpositionen innerhalb
     * des Spielfeldes (nicht außen) korrekt aktualisiert werden.
     */
    @Test
    public void updatesPlayerPosCorrectly_InField() {
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.T, TileRotation.ROT_0, null);
        Shift shiftRight = new Shift(Direction.RIGHT, new Position(0, 1), freeWayCard, 3);
        Shift shiftLeft = new Shift(Direction.LEFT, new Position(0, 1), freeWayCard, 3);
        Shift shiftDown = new Shift(Direction.DOWN, new Position(0, 1), freeWayCard, 3);

        Position updatedRightPos = shiftRight.updatePlayerPos(new Position(1,0));
        Position expPos = new Position(2,0);
        Assert.assertEquals(expPos, updatedRightPos);

        Position updatedLeftPos = shiftLeft.updatePlayerPos(new Position(1,0));
        Position expLeftPos = new Position(0,0);
        Assert.assertEquals(expLeftPos, updatedLeftPos);
    }


    /**
     * Überprüft, ob von einer Einschuboperation ausgehend, betroffene Spielerpositionen am Rand
     * des Spielfeldes korrekt aktualisiert werden. (Auf andere Seite aktualisiert werden.
     */
    @Test
    public void updatesPlayerPosCorrectly_OppositePosition() {
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.T, TileRotation.ROT_0, null);
        Shift shiftDown = new Shift(Direction.DOWN, new Position(1, 0), freeWayCard, 3);

        Position updatedDownPos = shiftDown.updatePlayerPos(new Position(0,2));
        Position expPos = new Position(0,0);
        Assert.assertEquals(expPos, updatedDownPos);

    }
}
