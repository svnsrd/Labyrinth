package logic.player;


import logic.Field;
import logic.path.PathUtil;
import logic.Position;
import logic.Shift;
import logic.Direction;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.tile.TileRotation;
import logic.tile.TileState;
import logic.Treasure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse beinhaltet Routinen zur Bestimmung eines Zuges für eine KI. Unterschieden wird
 * zwischen einer <i>normalen</i> und <i>erweiterten</i> KI.
 *
 * @author svnsrd  [Repo: 37]
 * @version 08.08.2021
 */
public class AIUtil {

    /**
     * Distanz einer diagonalen Positionierung zum Ziel.
     */
    public static final int DIAGONAL_DIST = 2;

    /**
     * Berechnet den Pfad zum Ziel-Schatz eines KI-Spielers. Insofern das Erreichen des gesuchten
     * Schatzes nicht möglich ist, werden alle möglichen Positionen zu jedem verfügbaren
     * Einschubvorgang zurückgegeben.
     *
     * @param field          Spielfeldinformationen
     * @param currPos        Ausgangsposition von der berechnet werden soll
     * @param treasureToFind Schatzkarte, die bei der Berechnung versucht wird zu finden
     * @return Mapping von Distanzen auf Listen von möglichen KI-Zügen
     */
    private static NavigableMap<Integer, List<AIMove>> getPossiblePos(Field field,
                                                                      Position currPos,
                                                                      Treasure treasureToFind) {

        // Insofern es keine Möglichkeit gibt den gesuchten Schatz zu erreichen, werden alle
        // Positionen mit ihren Pfaden hier gespeichert
        NavigableMap<Integer, List<AIMove>> moves = new TreeMap<>();

        // Zielposition bestimmen
        Position targetPos;

        // Position der freien Gängekarte stellt Einschubposition dar, welche nicht erlaubt ist
        Set<Position> validInsetPositions = new HashSet<>(field.getInsetPositions().keySet());
        validInsetPositions.remove(field.getFreeWayCard().getPosition());

        // Alle möglichen Einschübe durchführen
        for (Position currInsetPos : validInsetPositions) {

            // Alle möglichen Rotationen der freien Gängekarte
            for (int i = 0; i < TileRotation.values().length; i++) {
                // Aktuelle Rotation auf die freie Gängekarte anwenden
                field.getFreeWayCard().setRotated(TileRotation.values()[i]);

                // Aktuelle Einschieberichtung erhalten
                Direction currPushDir = field.getInsetPositions().get(currInsetPos);

                // Kopie des aktuellen Feldes erzeugen
                Tile[][] copyOfField = field.copyOfField();
                Field copyField = new Field(copyOfField, field.getFreeWayCard(), null);

                // Einschiebeoperation erzeugen und ausführen
                int affectedTileSize = copyField.getAffectedTileSizeOfPush(currPushDir);
                Shift shift = new Shift(currPushDir, currInsetPos, field.getFreeWayCard(),
                        affectedTileSize);
                shift.executeShift(copyOfField);

                // Position der KI im Rahmen der Einschuboperation aktualisieren, wenn nötig
                Position test = currPos;
                if (shift.isPositionAffected(currPos)) {
                    test = shift.updatePlayerPos(currPos);
                }

                // Startposition des Einschubs erhalten (neben insetPos)
                Position startPos = shift.getLogicalStartPos();
                copyOfField[startPos.getX()][startPos.getY()] = field.getFreeWayCard();

                // Wurde die Ziel-Schatzkarte rausgeschoben keinen Pfadfindung durchführen,
                // ansonsten Position der Schatzkarte finden
                targetPos = copyField.getTreasurePos(treasureToFind);
                if (targetPos != null) {
                    // Pfad zum Schatz finden
                    Map<Integer, Position> possibleNodes = PathUtil.aStarSearch(copyOfField, test,
                            targetPos);

                    for (int distance : possibleNodes.keySet()) {
                        // Aktuellen Pfadknoten und seine Position erhalten
                        Position nodePos = possibleNodes.get(distance);

                        // Neuen KI-Zug erzeugen
                        AIMove newMove = new AIMove(TileRotation.values()[i], currInsetPos, nodePos,
                                targetPos);

                        // Neuen KI-Zug zu den möglichen Zügen hinzufügen
                        moves.computeIfAbsent(distance, k -> new ArrayList<>()).add(newMove);
                    }
                }
            }
        }

        moves.values().removeIf(List::isEmpty);
        return moves;
    }

    /**
     * Ermittlung aller möglich guten Positionierung für einen KI-gesteuerten Spieler.
     * <p>
     * Die <i>normale KI</i> sucht bei <b>nicht-Erreichbarkeit</b> des Schatzes nach der dichtesten
     * Position zum Ziel.
     * <p>
     * Die <i>erweiterte KI</i> sucht bei <b>nicht-Erreichbarkeit</b> des Schatzes zunächst nach
     * diagonalen Positionierungen, dessen Gängekarte optimalerweise passende Öffnungen zu
     * der Ziel-Gängekarte hat.
     * <p>
     * Sollte keine diagonale Positionierung möglich sein, so wird nach den dichtesten Positionen
     * zum Ziel gesucht.
     *
     * @param field      Spielfeldinformationen
     * @param currPos    Ausgangsposition von der aus berechnet wird
     * @param treasure   Zu erreichender Schatz
     * @param playerType KI-Typ
     * @return Liste von gleichwertig guten KI-Spielzügen, gemäß des KI-Typs
     */
    private static List<AIMove> getBestAiMoves(Field field, Position currPos, Treasure treasure,
                                               PlayerType playerType) {

        // Mögliche KI-Züge berechnen
        NavigableMap<Integer, List<AIMove>> possibleMoves = getPossiblePos(field, currPos, treasure);

        // Liste der, für die erweiterte KI, besten KI-Züge
        List<AIMove> bestPossibleMoves = new ArrayList<>();

        // Aktuelle Distanz zum Ziel berechnen
        int currDistance = -1;
        Position treasureToFindPos = field.getTreasurePos(treasure);

        // Befindet sich der Schatz nicht auf der freien Gängekarte, Distanz berechnen
        if (!treasureToFindPos.equals(field.getFreeWayCard().getPosition())) {
            currDistance = currPos.distanceTo(treasureToFindPos);
        }

        // Schatz ist erreichbar
        if (possibleMoves.containsKey(0)) {
            bestPossibleMoves = possibleMoves.get(0);
        } else if (playerType == PlayerType.AI_EXTENDED) { /* Nach diagonaler Positionierung suchen */
            if (possibleMoves.containsKey(DIAGONAL_DIST)) {
                if (treasureToFindPos.equals(field.getFreeWayCard().getPosition())) {
                    bestPossibleMoves = bestDiagonalPos(possibleMoves.get(DIAGONAL_DIST),
                            field.getFreeWayCard());
                } else {
                    bestPossibleMoves = bestDiagonalPos(possibleMoves.get(DIAGONAL_DIST),
                            field.getBoard()[treasureToFindPos.getX()][treasureToFindPos.getY()],
                            field);
                }
            }
        }

        if (bestPossibleMoves.isEmpty()) {
            bestPossibleMoves = calcDensestMove(field, possibleMoves, currPos, treasureToFindPos,
                    currDistance);
        }

        return bestPossibleMoves;
    }

    /**
     * Ermittelt einen KI-Zug für einen KI-gesteuerten Spieler.
     *
     * @param field      Spielfeldinformationen
     * @param currPos    Aktuelle Position des KI-Spielers
     * @param treasure   Zu erreichender Schatz des Spielers
     * @param playerType KI-Typ
     * @return Den für den KI-Typ besten Spielzug
     */
    public static AIMove calcAIMove(Field field, Position currPos, Treasure treasure,
                                    PlayerType playerType) {

        List<AIMove> bestPossibleMoves = getBestAiMoves(field, currPos, treasure, playerType);
        return randomMove(bestPossibleMoves);

    }

    /**
     * Berechnet bei mehreren gleichwertigen Zügen einen möglichen <b>Anti-Zug</b>.
     * <p>
     * Sollte der nächste Spieler eines erweiterten KI-Spielers nur noch zu einer Startposition
     * gelangen müssen, dann wird bei mehreren gleichwertigen guten KI-Zügen, derjenige berechnet,
     * den es dem nächsten Spieler unmöglich macht, sein Ziel zu erreichen.
     *
     * @param field              Spielfeldinformationen
     * @param currPos            Aktuelle Position des KI-Spielers
     * @param treasure           Der aktuell zu erreichende Schatz des Spielers
     * @param nextPlayerPos      Position des Spielers der nach dem KI-Spieler an der Reihe ist
     * @param nextPlayerStartPos Startposition des nächsten Spielers
     * @return AIMove, der möglicherweise den nächsten Spieler daran hindert, seine Startposition
     * zu erreichen
     */
    public static AIMove calcAIMove(Field field, Position currPos, Treasure treasure,
                                    Position nextPlayerPos, Treasure nextPlayerStartPos) {

        List<AIMove> bestPossibleMoves = getBestAiMoves(field, currPos, treasure,
                PlayerType.AI_EXTENDED);

        if (bestPossibleMoves.size() > 1) {
            bestPossibleMoves = calcAntiPattern(bestPossibleMoves, field, nextPlayerPos,
                    nextPlayerStartPos);
        }

        return randomMove(bestPossibleMoves);
    }

    /**
     * Liefert zufälligen {@link AIMove KI-Zug} aus der übergebenen Liste aus KI-Zügen
     * {@code moves}.
     * <p>
     * Relevant ist diese Methode, wenn im Rahmen der Ermittlung eines KI-Zuges nach allen in
     * Betracht gezogenen Optionen weiterhin mehr als ein KI-Zug zur Verfügung steht.
     *
     * @param moves KI-Züge
     * @return Zufällig ausgewählter KI-Zug aus {@code moves}
     */
    private static AIMove randomMove(List<AIMove> moves) {
        // (Zufällig) ausgewählter KI-Zug
        AIMove res;

        // Enthält die übergebene Liste mehr als einen KI-Zug → zufällige Auswahl
        if (moves.size() > 1) {
            final Random rnd = new Random();
            int move = rnd.nextInt(moves.size());
            res = moves.get(move);
        } else {
            res = moves.get(0);
        }

        return res;
    }

    /**
     * Berechnet aus einer übergebenen Liste von Positionen ({@code possibleMoves}), die dichteste
     * Position zum Ziel ({@code treasureToFindPos}).
     *
     * @param field             Spielfeldinformationen
     * @param possibleMoves     Mapping der Distanzen auf Listen von KI-Zügen
     * @param currPos           Ausgangsposition von der berechnet wird
     * @param treasureToFindPos Position des zu erreichenden Schatzes
     * @param currDistance      Aktuelle Distanz zum Erreichen des zu erreichenden Schatzes
     * @return Die dichteste Positionierung zum Ziel
     */
    private static List<AIMove> calcDensestMove(Field field,
                                                NavigableMap<Integer, List<AIMove>> possibleMoves,
                                                Position currPos, Position treasureToFindPos,
                                                int currDistance) {

        // KI-Züge der berechnet und geliefert werden
        List<AIMove> res;

        int distance = possibleMoves.ceilingKey(1);

        // Keine Verbesserung der Distanz zum Ziel möglich
        if (currDistance == distance) {
            List<Position> insetPositions = new ArrayList<>(field.getInsetPositions().keySet());

            // Überprüfen, ob Spielstein bewegt werden kann
            if (field.getBoard()[currPos.getX()][currPos.getY()].getState() == TileState.DYNAMIC) {
                insetPositions.removeIf(e -> e.getX() != currPos.getX() + 1
                        && e.getY() != currPos.getY() + 1);
            } else if (field.getBoard()[treasureToFindPos.getX()][treasureToFindPos.getY()].getState() == TileState.DYNAMIC) {
                insetPositions.removeIf(e -> e.getX() != treasureToFindPos.getX() + 1
                        && e.getY() != treasureToFindPos.getY() + 1);
            }

            // Zufällige Auswahl aus einer der verfügbaren Einschubpositionen
            Random rnd = new Random();
            Position pos = insetPositions.get(rnd.nextInt(insetPositions.size()));
            res = Collections.singletonList(new AIMove(TileRotation.ROT_0, pos));
        } else {
            res = possibleMoves.get(distance);
        }

        return res;
    }

    /**
     * Berechnet von den verfügbaren Positionen die beste diagonale Positionierung aus.
     * <p>
     * Eine diagonale Positionierung ist optimal, wenn die Öffnungen der Gängekarte zueinander
     * passen, sodass im nächsten Zug nur einmal verschoben werden muss.
     *
     * @param moves  Mögliche KI-Züge
     * @param target Ziel Gängekarte, welche diagonal zu den übergebenen Zielpositionen der
     *               KI-Züge {@code moves} ist.
     * @return Beste diagonale Positionierung
     */
    private static List<AIMove> bestDiagonalPos(List<AIMove> moves, Tile target, Field field) {
        // Berechnung der offenen Positionen von dem Ziel aus
        Direction[] openDirs = target.getOpenDirections();

        // Gibt die aktuell höchste Anzahl an Möglichkeiten an, die der Spieler im nächsten
        // Zug nutzen kann, um seinen zu erreichenden Schatz einsammeln zu können
        int possibilities = 0;

        // Speichert die aktuell besten KI-Züge
        List<AIMove> bestMoves = new ArrayList<>();

        for (AIMove move : moves) {
            if (move.getTargetPos().isDiagonalTo(move.getTreasurePos())) {
                int possibilityCnt = 0;
                Position posToMove = move.getTargetPos();

                // Durchlaufen aller Ausgänge der Gängekarte auf der sich die Schatzkarte befindet
                for (Direction dir : openDirs) {

                    // Ist nach einer Bewegung zum aktuellen Ausgang, die Distanz zur Position,
                    // zu der sich die KI bewegen wird = 1, wird geprüft, ob die Ausgänge der
                    // Gängekarte zu der Gängekarte auf dem sich der Schatz befindet passt
                    if (posToMove.distanceTo(move.getTreasurePos().addPos(dir.getDirPos())) == 1) {
                        possibilityCnt = calcDiagonalPossibilities(move, dir, field);
                    }
                }

                // Sind die Möglichkeiten im nächsten Zug höher als die aktuellen Möglichkeiten
                // wird die Liste angepasst
                if (possibilityCnt > possibilities) {
                    bestMoves.clear();
                    bestMoves.add(move);
                    possibilities = possibilityCnt;
                } else if (!bestMoves.isEmpty() && possibilityCnt == possibilities) { /* Gleiche Anzahl an Möglichkeiten */
                    bestMoves.add(move);
                }
            }
        }

        return bestMoves;
    }


    /**
     * Berechnet die beste diagonale Positionierung, wenn das Ziel auf der freien Gängekarte liegt.
     *
     * @param moves  KI-Züge deren Zielposition diagonal zur Position mit dem zu erreichenden Schatz
     *               ist
     * @param target Ziel-Gängekarte
     * @return Beste Diagonale Positionierung des möglichen diagonalen Positionierungen
     */
    private static List<AIMove> bestDiagonalPos(List<AIMove> moves, FreeWayCard target) {
        // Berechnung der offenen Positionen von dem Ziel aus
        int possibilities = 0;
        List<AIMove> bestMoves = new ArrayList<>();
        Direction[] openDirs;

        for (AIMove move : moves) {
            int possibilityCnt = 0;
            Position posToMove = move.getTargetPos();
            target.setRotated(move.getFreeWayCardRot());
            openDirs = target.getOpenDirections();
            for (Direction dir : openDirs) {
                if (posToMove.distanceTo(move.getTargetPos().addPos(dir.getDirPos())) == 1) {
                    possibilityCnt++;
                }
            }

            if (possibilityCnt > possibilities) {
                bestMoves.clear();
                bestMoves.add(move);
                possibilities = possibilityCnt;
            } else if (possibilityCnt == possibilities) {
                bestMoves.add(move);
            }
        }

        return bestMoves;
    }

    /**
     * Gibt die Anzahl der Möglichkeiten, die die KI im nächsten Zug zum Einschub zur Erreichung
     * des Ziels zur Verfügung hat, zurück.
     *
     * @param move  KI-Zug
     * @param dir   Ausgangsrichtung der Gängekarte auf dem sich die zu erreichende Schatzkarte
     *              befindet
     * @param field Spielfeldinformationen
     * @return Anzahl der Möglichkeiten im nächsten Zug, die Ziel Gängekarte zu erreichen
     */
    private static int calcDiagonalPossibilities(AIMove move, Direction dir, Field field) {
        // Gibt die aktuell höchste Anzahl an Möglichkeiten an
        int possibilityCnt = 0;

        // Freie Gängekarte gemäß dem KI-Zug drehen
        field.getFreeWayCard().setRotated(move.getFreeWayCardRot());

        // Einschubrichtung ermitteln
        Direction shiftDir = field.getInsetPositions().get(move.getInsetPos());

        // Einschuboperation erzeugen
        Shift shift = new Shift(shiftDir, move.getInsetPos(),
                field.getFreeWayCard(), field.getAffectedTileSizeOfPush(shiftDir));

        // Spielfeld kopieren, Einschuboperation auf diesem ausführen und die Gängekarte
        // ermitteln auf der sich die KI dann bewegt
        Tile[][] copy = field.copyOfField();
        shift.executeShift(copy);
        Tile playerTile = copy[move.getTargetPos().getX()][move.getTargetPos().getY()];

        // Passen die Ausgänge, zählt dies als Möglichkeit
        if (playerTile.canBeEnteredFrom(dir.getOppositeDir())) {
            possibilityCnt++;
        }

        return possibilityCnt;
    }

    /**
     * Bei mehreren gleichwertig berechneten KI-Zügen wird im Rahmen der Berechnung für die
     * <i>erweiterte KI</i> versucht, denjenigen Zug zu berechnen, der dem nächsten Spieler das
     * Erreichen seines gesuchten Schatzes verhindert, wenn dieser nur noch zu seiner
     * Startposition gelangen muss,
     *
     * @param moves                Mögliche KI-Züge
     * @param field                Spielfeldinformationen
     * @param nextPlayerPos        Position des Spielers, der nach dem Zug an der Reihe ist
     * @param startPosOfNextPlayer Schatz des Spielers, der nach dem Zug an der Reihe ist
     * @return Einen möglichen Anti-Zug, der dem nächsten Spieler das Erreichen seines aktuell zu
     * erreichenden Schatzes verhindert
     */
    private static List<AIMove> calcAntiPattern(List<AIMove> moves, Field field, Position
            nextPlayerPos, Treasure startPosOfNextPlayer) {

        List<AIMove> result = new ArrayList<>();

        // Alle möglichen KI-Züge durchlaufen
        for (AIMove move : moves) {

            // Einschuboperation mit den Informationen aus dem aktuellen KI-Zug erzeugen
            int affectedTileSize = field.getAffectedTileSizeOfPush(field.getInsetPositions()
                    .get(move.getInsetPos()));
            FreeWayCard freeWayCard = field.getFreeWayCard();
            freeWayCard.setRotated(move.getFreeWayCardRot());
            Shift shift = new Shift(field.getInsetPositions().get(move.getInsetPos()),
                    move.getInsetPos(), freeWayCard, affectedTileSize);

            // Einschuboperation ausführen und die Position des nächsten Spielers ggf. aktualisieren
            Tile[][] gameField = field.copyOfField();
            Tile newFreeWayCard = shift.executeShift(gameField);
            if (shift.isPositionAffected(nextPlayerPos)) {
                nextPlayerPos = shift.updatePlayerPos(nextPlayerPos);
            }

            // Einschubpositionen aktualisieren
            Map<Position, Direction> insetPos = new HashMap<>(field.getInsetPositions());
            Position newFWCPos = shift.getLastAffectedPosition().addPos(shift.getPushDir().getDirPos());
            FreeWayCard freeWayCard1 = new FreeWayCard(newFreeWayCard, newFWCPos);

            // Neues Field ausgehend von dem KI-Zug erzeugen
            Field possibleNewField = new Field(gameField, freeWayCard1, insetPos);

            // Möglichkeiten des nächsten Spielers berechnen
            Map<Integer, List<AIMove>> possiblePos = getPossiblePos(possibleNewField, nextPlayerPos,
                    startPosOfNextPlayer);

            // Ist die Startposition nicht erreichbar, wird dieser Spielzug gespeichert
            if (!possiblePos.containsKey(0)) {
                result.add(move);
            }
        }

        return result.isEmpty() ? moves : result;
    }
}
