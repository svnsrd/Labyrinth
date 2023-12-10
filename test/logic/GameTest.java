package logic;

import logic.player.Player;
import logic.player.PlayerType;
import logic.tile.FreeWayCard;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Tests, für die {@link Game Game-Klasse}
 *
 * @author Suwendi Suriadi (WInf104177) [Repository 37]
 */
public class GameTest {

    // Freie Gängekarte
    FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

    // Erzeugung eines 2x2 Feldes
    Field field = new Field(
            "I100,I100,I100,I100,I100\n" +
                    "I100,L200,I100,I100,I100\n" +
                    "I100,I000,I123,I100,I100\n" +
                    "L000,L300,I100,I100,I100\n" +
                    "I100,I100,I100,I100,I100", freeWayCard);


    Player player0 = new Player(true, "0", PlayerType.HUMAN, new Position(0, 0), new LinkedList<>());
    Player player1 = new Player(true, "1", PlayerType.HUMAN, new Position(0, 0), new LinkedList<>());
    Player player2 = new Player(false, "2", PlayerType.HUMAN, new Position(0, 0), new LinkedList<>());
    Player player3 = new Player(true, "3", PlayerType.HUMAN, new Position(0, 0), new LinkedList<>());

    @Test
    public void nextTurnActiveTest() {
        Player[] players = new Player[]{player0, player1, player2, player3};
        Queue<Treasure> dummyStack = new LinkedList<>();
        dummyStack.add(Treasure.GENIE);

        for (int i = 0; i < 4; i++) {
            players[i] = new Player(true, "0", PlayerType.HUMAN, new Position(0, 0), dummyStack);
        }

        Game game = new Game(new FakeGUI(), field, players, 0);

        game.nextTurn();
        Assert.assertEquals(1, game.getCurrentPlayer());

        game.nextTurn();
        Assert.assertEquals(2, game.getCurrentPlayer());

        game.nextTurn();
        Assert.assertEquals(3, game.getCurrentPlayer());
    }

    @Test
    public void nextTurnInactiveTest() {
        Player[] players = new Player[]{player0, player1, player2, player3};
        Queue<Treasure> dummyStack = new LinkedList<>();
        dummyStack.add(Treasure.GENIE);

        for (int i = 0; i < 4; i++) {
            if (i != 2) {
                players[i] = new Player(true, "0", PlayerType.HUMAN, new Position(0, 0), dummyStack);
            } else {
                players[i] = new Player(false, "0", PlayerType.HUMAN, new Position(0, 0), dummyStack);
            }
        }

        Game game = new Game(new FakeGUI(), field, players, 0);

        game.nextTurn();
        Assert.assertEquals(1, game.getCurrentPlayer());

        game.nextTurn();
        Assert.assertEquals(3, game.getCurrentPlayer());
    }

    @Test
    public void rotateFreeWayCard_Correct() {
        Player[] players = new Player[]{player0, player1, player2, player3};
        Queue<Treasure> dummyStack = new LinkedList<>();
        dummyStack.add(Treasure.GENIE);

        for (int i = 0; i < 4; i++) {
            players[i] = new Player(true, "0", PlayerType.HUMAN, new Position(0, 0), dummyStack);
        }

        Game game = new Game(new FakeGUI(), field, players, 0);

        game.rotateFreeWayCard(RotateDirection.CLOCKWISE);
        Assert.assertEquals(TileRotation.ROT_90, field.getFreeWayCard().getRotated());

    }
}
