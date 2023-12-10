package logic;

import javafx.geometry.Pos;
import logic.player.AIUtil;
import logic.player.AIMove;
import logic.player.PlayerType;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.tile.TileRotation;
import logic.tile.TileShape;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Tests, für das {@link AIUtil KI-Verhalten}.
 *
 * @author Suwendi Suriadi (WInf104177) [Repo: 37]
 * @version 05.08.2021
 */
public class AITest {

    private Set<Position> createExpSet(Position... positions) {
        return new HashSet<>(Arrays.asList(positions));
    }

    /**
     * Testet, ob die <b>erweiterte KI</b> auf einem 2x2-Feld die freie Gängekarte korrekt
     * einschiebt und seinen zu erreichenden Schatz findet.
     * <p>
     * Der Schatz befindet sich im Feld der Spiellogik auf (0, 0) und die KI auf (1, 1).
     */
    @Test
    public void AIExt_calculateRightPush() {

        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.T, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field("L100,I000\nI000,I000", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.GOLD;
        field.getBoard()[0][0].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(1, 1);

        // Berechnung des erweiterten KI-Zuges
        AIMove move = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_EXTENDED);

        // Erzeugung einer Einschiebeoperation
        Direction dir = field.getInsetPositions().get(move.getInsetPos());
        Shift shift = new Shift(dir, move.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dir));
        shift.executeShift(field.getBoard());

        // Überprüfen, ob nach dem Einschub, die KI an die Position des Schatzes geht
        Assert.assertEquals(field.getTreasurePos(treasure), move.getTargetPos());
    }

    /**
     * Testet, ob die <b>erweiterte KI</b> auf einem 2x2-Feld die freie Gängekarte korrekt
     * einschiebt und seinen zu erreichenden Schatz findet.
     * <p>
     * Der Schatz befindet sich im Feld der Spiellogik auf (0, 0) und die KI auf (1, 1).
     */
    @Test
    public void AINormal_calculateRightPush() {

        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.T, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field("L100,I000\nI000,I000", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.GOLD;
        field.getBoard()[0][0].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(1, 1);

        // Berechnung des erweiterten KI-Zuges
        AIMove move = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_NORMAL);

        // Erzeugung einer Einschiebeoperation
        Direction dir = field.getInsetPositions().get(move.getInsetPos());
        Shift shift = new Shift(dir, move.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dir));
        shift.executeShift(field.getBoard());

        // Überprüfen, ob nach dem Einschub, die KI an die Position des Schatzes geht
        Assert.assertEquals(field.getTreasurePos(treasure), move.getTargetPos());
    }

    @Test
    public void AIExt_calculateDiagPos() {
        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I000,I100\n" +
                        "I100,I100,I100\n" +
                        "I100,I000,L001", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.values()[1];
        field.getBoard()[2][2].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(1, 0);

        // Berechnung des erweiterten KI-Zuges
        AIMove move = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_EXTENDED);

        // Erzeugung einer Einschiebeoperation
        Direction dir = field.getInsetPositions().get(move.getInsetPos());
        Shift shift = new Shift(dir, move.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dir));
        shift.executeShift(field.getBoard());

        // Überprüfen, ob nach dem Einschub, die KI an die Position des Schatzes geht
        Position expPos = new Position(1, 1);
        Position expPos1 = new Position(1, 0);
        Set<Position> expSet = createExpSet(expPos, expPos1);
        Assert.assertTrue(expSet.contains(move.getTargetPos()));
    }


    /**
     * Überprüft, ob die erweiterte KI bei mehreren möglichen Positionierungen die beste wählt.
     *
     */
    @Test
    public void AIExt_calculateBESTDiagPos() {
        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,I100\n" +
                        "I100,L200,I100,I100,I100\n" +
                        "I100,I000,I123,I100,I100\n" +
                        "L000,L300,I100,I100,I100\n" +
                        "I100,I100,I100,I100,I100", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.values()[23];
        field.getBoard()[2][2].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(0, 1);

        // Berechnung des erweiterten KI-Zuges
        AIMove move = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_EXTENDED);

        // Erzeugung einer Einschiebeoperation
        Direction dir = field.getInsetPositions().get(move.getInsetPos());
        Shift shift = new Shift(dir, move.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dir));
        shift.executeShift(field.getBoard());

        // Überprüfen, ob nach dem Einschub, die KI an die Position des Schatzes geht
        Position expPos = new Position(1, 1);
        Position expPos1 = new Position(3, 1);
        Position expPos2 = new Position(0, 1);
        Position expPos3 = new Position(1, 3);
        Set<Position> expPositions = Set.of(expPos, expPos1, expPos2, expPos3);

        Assert.assertTrue(move.toString(), expPositions.contains(move.getTargetPos()));
        System.out.println(move);
        Assert.assertEquals(2, move.getTargetPos().distanceTo(move.getTreasurePos()));
    }

    /**
     * Überprüft, ob die erweiterte KI bei mehreren möglichen Positionierungen die beste wählt.
     */
    @Test
    public void AIExt_calculatePosLikeNormalAI() {
        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,I100\n" +
                        "L200,I000,I100,I100,I000\n" +
                        "L000,L300,I123,I100,I000\n" +
                        "I100,I000,I100,I100,I000\n" +
                        "I100,I100,I100,I100,I100", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.values()[23];
        field.getBoard()[2][2].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(0, 1);

        // Berechnung des erweiterten KI-Zuges
        AIMove move = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_EXTENDED);

        // Erzeugung einer Einschiebeoperation
        Direction dir = field.getInsetPositions().get(move.getInsetPos());
        Shift shift = new Shift(dir, move.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dir));
        shift.executeShift(field.getBoard());

        // Überprüfen, ob nach dem Einschub, die KI an die Position des Schatzes geht
        Position expPos = new Position(1, 2);
        Position expPos1 = new Position(0, 2);
        Set<Position> expPositions = Set.of(expPos, expPos1);

        Assert.assertTrue(expPositions.contains(move.getTargetPos()));
    }

    /**
     * Überprüft, die KI's einen bereits vorhandenen Weg nicht zerstören.
     */
    @Test
    public void AI_dontDestroyExistentWayToTreasure() {
        // Freie Gängekarte
        FreeWayCard freeWayCard = new FreeWayCard(TileShape.I, TileRotation.ROT_0, new Position(-1, -1));

        // Erzeugung eines 2x2 Feldes
        Field field = new Field(
                "I100,I100,I100,I100,I100\n" +
                        "L200,I000,I100,I100,I000\n" +
                        "L000,L300,I123,I100,I000\n" +
                        "I100,I000,I100,I100,I000\n" +
                        "I100,I100,I100,I100,I100", freeWayCard);

        // Zu erreichender Schatz und Zuweisung zu der Position (0, 0)
        Treasure treasure = Treasure.values()[23];
        field.getBoard()[2][0].setTreasure(treasure);

        // Aktuelle Position (Spiellogik)
        Position currAIPos = new Position(0, 0);

        // Berechnung des erweiterten KI-Zuges
        AIMove moveExtended = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_EXTENDED);
        AIMove moveNormal = AIUtil.calcAIMove(field, currAIPos, treasure, PlayerType.AI_NORMAL);

        // Erzeugung einer Einschiebeoperation
        Direction dirExt = field.getInsetPositions().get(moveExtended.getInsetPos());
        Direction dirNor = field.getInsetPositions().get(moveNormal.getInsetPos());

        Shift shiftExt = new Shift(dirExt, moveNormal.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dirNor));
        Shift shiftNor = new Shift(dirExt, moveNormal.getInsetPos(), freeWayCard, field.getAffectedTileSizeOfPush(dirNor));

        Tile[][] fieldExt = field.copyOfField();
        Tile[][] fieldNorm = field.copyOfField();

        shiftExt.executeShift(fieldExt);
        shiftNor.executeShift(fieldNorm);

        Assert.assertEquals(new Position(2, 0), moveExtended.getTargetPos());
        Assert.assertEquals(new Position(2, 0), moveNormal.getTargetPos());
    }
}
