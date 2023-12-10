package logic;

import com.google.gson.JsonSyntaxException;
import logic.data.Data;
import logic.data.InvalidGameDataException;
import logic.path.PathNode;
import logic.path.PathUtil;
import logic.player.AIUtil;
import logic.player.AIMove;
import logic.player.Player;
import logic.player.PlayerType;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.tile.TileRotation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse enthält Methoden zur Repräsentierung der Spiellogik und anweisen bzw. steuern der
 * Benutzeroberfläche.
 *
 * @author svnsrd  [Repo: 37]
 * @version 06.08.2021
 */
public class Game {

    /**
     * Maximale Anzahl an Spieler die teilnehmen können.
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * Startpositionen der Spieler.
     */
    public final static Position[] START_POSITIONS = new Position[]{
            new Position(0, 0),
            new Position(6, 0),
            new Position(6, 6),
            new Position(0, 6)
    };

    /**
     * Anzahl der verfügbaren Schätze.
     */
    public static final int MAX_TREASURESIZE = 24;

    /**
     * Die Steuerung der Benutzeroberfläche
     */
    private final GUIConnector gui;

    /**
     * Das Spielfeld.
     */
    private final Field field;

    /**
     * Spieler, welcher derzeit am Zug ist.
     */
    private int currentPlayer;

    /**
     * Die Spieler
     */
    private Player[] players;

    /**
     * Die in einem Spiel aktuell betretbaren Positionen
     */
    private PathNode[][] possiblePositions;

    /**
     * Zur Ablaufsteuerung muss festgehalten werden, ob bereits ein Einschub erfolgt ist
     */
    private boolean pushed;

    /**
     * Gibt, an ob das Spiel unterbrochen wurde.
     */
    private boolean interrupted;

    /**
     * Konstruktor.
     *
     * @param gui         Die Benutzeroberfläche, über welche Aktualisierungen an dieser stattfinden
     * @param cardSize    Anzahl der Schatzkarten, die jeder Spieler hat
     * @param playerTypes Spielertypen der Spieler (menschlich, normale KI, erweiterte KI)
     * @param x           Breite des Spielfeldes
     * @param y           Höhe des Spielfeldes
     */
    public Game(GUIConnector gui, List<String> playerNames, int cardSize, int cardSizePerPlayer,
                List<PlayerType> playerTypes, List<Boolean> activePlayer, int x, int y) {

        assert cardSize <= MAX_TREASURESIZE;

        this.gui = gui;
        this.possiblePositions = new PathNode[x][y];

        // Hinzufügen aller Karten
        List<Treasure> treasureCards = new ArrayList<>(
                Arrays.asList(Treasure.values()).subList(1, MAX_TREASURESIZE + 1));
        gui.createTreasureImg();

        // Mischen der Karten
        Collections.shuffle(treasureCards);

        // Spielerzeugung und Zuteilung von Schatzkarten
        createPlayer(playerNames, activePlayer, treasureCards, playerTypes, cardSizePerPlayer);

        // Erzeugen des Spielfeldes
        this.field = new Field(x, y, treasureCards);

        // Initialisieren des Spielfeldes auf der GUI
        gui.initializeField(field.getBoard(), field.getInsetPositions(), field.getFreeWayCard());

        // Spiel beginnen
        currentPlayer = MAX_PLAYERS - 1;
        interrupted = false;
        nextTurn();
    }

    /**
     * TODO
     * @param gui
     * @param field
     * @param players
     * @param currentPlayer
     */
    Game(GUIConnector gui, Field field, Player[] players, int currentPlayer) {
        this.gui = gui;
        this.field = field;
        this.players = players;
        this.currentPlayer = currentPlayer;
    }


    /**
     * Bestimmen des nächsten Spielers und Steuerung dieses Zuges.
     */
    public void nextTurn() {

        // Überprüfen ob der Spieler gewonnen hat
        boolean won = checkIfPlayerWon();

        // Wenn das Spiel nicht unterbrochen wurde und noch kein Gewinner feststeht nächsten
        // Spieler ermitteln und Spielzug ausführen lassen
        if (!interrupted && !won) {
            // Alte SpielerID zwischenspeichern
            int oldPlayerIdx = currentPlayer;

            // Nächsten aktiven Spieler ermitteln
            currentPlayer = nextActivePlayer();
            Player nextPlayer = players[currentPlayer];

            // Auf der GUI den nächsten Spieler anzeigen
            gui.nextPlayer(oldPlayerIdx, currentPlayer);

            // Logging
            Logger.getInstance().log(Logger.PLAYER_ONTURN, currentPlayer);

            // Überprüfen ob nächster Spieler menschlich oder KI ist
            PlayerType currPlayerType = nextPlayer.getDirectedBy();
            if (currPlayerType == PlayerType.AI_NORMAL
                    || currPlayerType == PlayerType.AI_EXTENDED) {
                gui.disableField(true);
                handleAIMove(currPlayerType);
            } else { /* Menschlicher Zug */
                // Spielfeld aktivieren
                gui.disableField(false);

                // Dem menschlichen Spieler seine derzeitigen betretbaren Positionen anzeigen
                gui.setPossiblePos(PathUtil.getPossiblePositions(field.getBoard(),
                        nextPlayer.getPosition()));
            }
        }
    }

    /**
     * Steuert eine Benutzereingabe (Klick auf das Spielfeld inklusive Einschubpfeile)
     * <p>
     * Der x- und y-Wert entspringt nicht dem hier dargestellten Feld von 7x7, sondern des
     * gesamten Spielfeldes, welches die Einschubpfeile mit beinhaltet (9x9).
     * <p>
     * Demnach werden diese Werte auch so gehandhabt.
     *
     * @param x-Koordinate der Benutzereingabe
     * @param y-Koordinate der Benutzereingabe
     */
    public void handleHumanMove(int x, int y) {
        // Spielfeld für weitere Benutzereingabe zunächst deaktivieren
        gui.disableField(true);

        // Position der Benutzereingabe erzeugen
        Position userInputPos = new Position(x, y);

        if (!pushed) { /* Phase: Einschiebevorgang */
            // Überprüfen ob Benutzereingabe einer Einschubposition entspricht
            Position freeWayCardPos = field.getFreeWayCard().getPosition();

            if (field.getInsetPositions().containsKey(userInputPos)
                    && !userInputPos.equals(freeWayCardPos)) {

                // Einschiebeoperation erzeugen
                Shift shift = createShift(userInputPos, field.getFreeWayCard());

                // Einschiebeoperation durchführen
                shift(shift);

                // Einschubanimation erzeugen und ausführen
                gui.animateHumanShift(shift, possiblePositions);

                // Einschubzustand ändern
                this.pushed = true;
            } else {
                // Je nach angegebener Position, entsprechende Fehlermeldung an den Benutzer liefern
                handleInvalidShiftInput(userInputPos);
            }
        } else { /* Phase: Spieler-Bewegung */

            if (isInsideField(userInputPos) && this.possiblePositions[x - 1][y - 1] != null) {

                // Spieler zu neuer Position zuweisen
                Treasure collectedTreasure = movePlayer(currentPlayer,
                        userInputPos.getLogicalPos());
                Treasure nextTreasure = null;

                if (collectedTreasure != null) {
                    nextTreasure = players[currentPlayer].getCurrTreasure();
                }

                // Animation ausführen
                gui.movePlayer(currentPlayer, this.possiblePositions[x - 1][y - 1].createPath(),
                        collectedTreasure, nextTreasure, this);

                // Eingeschoben-Zustand zurücksetzen
                pushed = false;
            } else {
                // Invalide Position angegeben, zu welcher sich der Spieler bewegen soll
                gui.showAlert(Message.INVALID_MOVEPOS.getMessage());
                gui.disableField(false);
            }
        }
    }

    /**
     * Steuerung eines KI-Zuges. Je nach Spielertyp werden unterschiedliche Vorgehensweisen
     * zur Berechnung eines Pfades zum gesuchten Schatz genutzt.
     *
     * @param playerType KI-Spielertyp (normal oder erweitert)
     */
    public void handleAIMove(PlayerType playerType) {
        // Aktueller Spieler
        Player currPlayer = players[currentPlayer];
        Position currPlayerPos = currPlayer.getPosition();
        Treasure currPlayerTrs = currPlayer.getCurrTreasure();
        PlayerType currPlayerType = currPlayer.getDirectedBy();

        AIMove aiMove;

        // Überprüfen ob der nächste Spieler zu seiner Startposition muss
        if (players[nextActivePlayer()].getTreasureCards().size() == 1
                && currPlayerType == PlayerType.AI_EXTENDED) {

            // Position und Schatz des nächsten Spielers
            int nextPlayer = nextActivePlayer();
            if (nextPlayer != currentPlayer) {
                Player nextPlayerObj = players[nextPlayer];
                Position nextPlayerPos = nextPlayerObj.getPosition();
                Treasure nextPlayerTreasure = nextPlayerObj.getCurrTreasure();
                aiMove = AIUtil.calcAIMove(field, currPlayerPos, currPlayerTrs, nextPlayerPos, nextPlayerTreasure);
            } else {
                aiMove = AIUtil.calcAIMove(field, currPlayerPos, currPlayerTrs, currPlayerType);
            }
        } else {
            // Berechnung des KI-Zuges
            aiMove = AIUtil.calcAIMove(field, currPlayerPos, currPlayerTrs, playerType);
        }

        // Freie Gängekarte auf die berechnete Rotation setzen
        TileRotation rotation = aiMove.getFreeWayCardRot();
        field.getFreeWayCard().setRotated(rotation);

        // Einschiebeoperation erzeugen
        Shift shift = createShift(aiMove.getInsetPos(), field.getFreeWayCard());

        // Einschuboperation ausführen
        shift(shift);

        // Spieler in der Logik bewegen
        Position targetPos = aiMove.getTargetPos();
        if (targetPos == null) {
            targetPos = currPlayer.getPosition();
        }

        Treasure collectedTreasure = movePlayer(currentPlayer, targetPos);
        Treasure nextTreasure = null;

        if (collectedTreasure != null) {
            nextTreasure = players[currentPlayer].getCurrTreasure();
        }

        // Pfad erzeugen
        List<Position> path = possiblePositions[targetPos.getX()][targetPos.getY()].createPath();

        // Animationen ausführen
        gui.aiMove(shift, aiMove, currentPlayer, collectedTreasure, nextTreasure, path, this);
    }

    /**
     * Dreht die freie Gängekarte.
     *
     * @param rotDir Richtung, in die, die freie Gängekarte rotiert werden soll
     */
    public void rotateFreeWayCard(RotateDirection rotDir) {
        gui.disableField(true);
        // Nächste Rotation, anhand der ermittelten Rotationsrichtung bestimmten
        TileRotation nextRotation = field.getFreeWayCard().getRotated().getNextRotation(rotDir);

        // Rotation ausführen
        gui.rotateFreeWayCard(nextRotation, rotDir);
        field.getFreeWayCard().setRotated(nextRotation);
    }

    /**
     * Berechnet die {@link Position} auf dem sich der aktuell zu erreichende
     * {@link Treasure Schatz} des Spielers, der aktuell an der Reihe ist.
     */
    public void highlightTreasureToFind() {
        // Aktueller Spieler
        final Player player = players[currentPlayer];

        if (player.getDirectedBy() == PlayerType.HUMAN) {
            // Position des aktuell zu erreichenden Schatzes des Spielers der an der Reihe ist
            Position treasurePos = field.getTreasurePos(player.getCurrTreasure()).getGlobalPos();

            // GUI anweisen die entsprechende Gängekarte mit dem Schatz hervorzuheben
            gui.highlightTreasureToFind(treasurePos, currentPlayer);
        }
    }

    /**
     * Speichert die aktuelle Spielsituation in der übergebene {@link File Datei}.
     *
     * @param savedFile Datei
     */
    public void saveGame(File savedFile) {
        // Entfernen der letzten Schätze, welche die Startpositionen darstellen
        for (int playerIdx = 0; playerIdx < MAX_PLAYERS; playerIdx++) {
            players[playerIdx].getTreasureCards()
                    .remove(Treasure.values()[MAX_TREASURESIZE + playerIdx + 1]);
        }

        // Entfernen der letzten Schätze, auf dem Spielfeld
        field.removeStartPosTreasures(START_POSITIONS);

        // Speichervorgang
        Data data = new Data(field.getBoard(), field.getFreeWayCard(), currentPlayer, players);

        try {
            data.saveGame(new FileOutputStream(savedFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Schätze wieder hinzufügen
        for (int playerIdx = 0; playerIdx < MAX_PLAYERS; playerIdx++) {
            players[playerIdx].getTreasureCards()
                    .add(Treasure.values()[MAX_TREASURESIZE + playerIdx + 1]);
        }
    }

    /**
     * Laden eines Spielstandes aus der übergebenen {@link File {@code file}}.
     *
     * @param file Spielstand-Datei ({@code null}, wenn das Laden vom Benutzer abgebrochen wurde)
     */
    public void loadGame(File file) {
        try {
            Data data = new Data(file, field.getColCount(), field.getRowCount());

            // Spieler-Informationen zurücksetzen
            gui.resetPlayerInfo();

            // Spielfeld laden
            field.loadField(data.getField(), data.getFreeWayCard());
            field.assignLastTreasures(START_POSITIONS);
            gui.loadField(field.getBoard(), field.getInsetPositions(), field.getFreeWayCard());

            // Spieler aktualisieren
            players = data.getPlayers();
            for (int playerIdx = 0; playerIdx < MAX_PLAYERS; playerIdx++) {
                Player currPlayer = players[playerIdx];
                if (currPlayer.isInvolved()) {
                    currPlayer.getTreasureCards().add(Treasure.values()[MAX_TREASURESIZE + playerIdx + 1]);
                }

                // Spieler-Informationsbox erzeugen
                gui.createPlayer(playerIdx, players[playerIdx]);

                // Überprüfung ob der Spieler bereits alle Schätze gefunden hat
                if (currPlayer.getTreasureCards().size() == 1) {
                    gui.showPlayerWinHint(playerIdx);
                }
            }

            // Den momentan, am Zug, spielenden Spieler setzen
            this.currentPlayer = data.getCurrentPlayer() == 0 ? 3 : data.getCurrentPlayer() - 1;

            // Einschub-Zustand zurücksetzen, da nur aus diesem heraus ein Spiel gespeichert werden
            // kann
            pushed = false;

            // Spiel starten
            nextTurn();

        } catch (Exception e) {
            if (e instanceof InvalidGameDataException) {
                gui.showErrorAlert(e.getMessage());
            } else if (e instanceof JsonSyntaxException) {
                gui.showErrorAlert("Spielstandsdatei entspricht nicht der json-Syntax." +
                        " Überprüfen Sie ob es sich um eine .json-Datei handelt oder ob sie" +
                        " modifiziert wurde. Stellen Sie sie ggf. auf eine vorherige Version" +
                        " zurück.");
            } else if (e instanceof NullPointerException) {
                gui.showErrorAlert("Spielstand enthält Werte die nicht existent sind.");
            } else {
                gui.showErrorAlert("Datei konnte nicht gefunden werden.");
            }

        }
    }

    /**
     * Überprüft ob der Spieler derzeit mit der GUI interagieren darf.
     */
    public boolean checkForInvalidInteraction(boolean save) {
        if (players[currentPlayer].getDirectedBy() != PlayerType.HUMAN) {
            gui.showErrorAlert(Message.CANNOT_INTERACT.getMessage());
            return false;
        } else if (save && pushed) {
            return false;
        }

        return true;
    }

    /**
     * Setzt das {@code interrupted}-Flag auf {@code true}, um das Spiel im nächsten
     * <i>nextTurn()</i>-Methodenaufruf zu unterbrechen.
     */
    public void interruptGame() {
        this.interrupted = true;
    }

    /**
     * Hilfsmethode, welche die Spieler und ihre zu suchenden Schatzkarten erzeugt.
     * <p>
     * Die
     *
     * @param playerNames       Namen der Spieler
     * @param activePlayer      Liste die angibt, welche Spieler im Spiel involviert sind
     * @param cards             Alle Schatzkarten
     * @param playerTypes       {@link PlayerType Spielertypen} der Spieler
     * @param cardSizePerPlayer Anzahl der Karten pro Spieler
     */
    private void createPlayer(List<String> playerNames, List<Boolean> activePlayer,
                              List<Treasure> cards, List<PlayerType> playerTypes,
                              int cardSizePerPlayer) {

        // Erzeugen eines Arrays, welche alle Spieler-Objekte beinhaltet
        players = new Player[MAX_PLAYERS];

        // Stapel an Schatzkarten, die den Spieler zugewiesen werden
        Queue<Treasure> currStack;

        // Liefert den Index der aktuellen Karte
        int cardPointer = 0;

        // Spieler-Erzeugung
        for (int playerIdx = 0; playerIdx < MAX_PLAYERS; playerIdx++) {

            // Name des aktuellen Spielers
            String currPlayerName = playerNames.get(playerIdx);

            // Gibt an, ob der aktuelle Spieler involviert ist
            boolean active = activePlayer.get(playerIdx);

            Position playerStartPos; /* Startposition des zu erzeugenden Spielers */
            currStack = new LinkedList<>(); /* Schatzkarten des zu erzeugenden Spielers */

            // Überprüfen ob Spieler involviert ist
            if (active) { /* involviert */
                playerStartPos = START_POSITIONS[playerIdx];
                for (int j = 0; j < cardSizePerPlayer; j++) {
                    currStack.add(cards.get(cardPointer++)); /* Inkrementieren des Karten-Zeigers */
                }

                // Logging
                String treasuresToStr = Logger.getInstance().treasuresToString(currStack);
                Logger.getInstance().log(Logger.PLAYER_CREATION, playerIdx,
                        playerTypes.get(playerIdx), treasuresToStr);

                currStack.add(Treasure.values()[MAX_TREASURESIZE + playerIdx + 1]);
            } else { /* nicht involviert */
                playerStartPos = new Position(0, 0);
                Logger.getInstance().log(Logger.PLAYER_NOTINVOLVED, playerIdx, currPlayerName);
            }

            // Aktuellen Spieler erzeugen und zur Spieler-Liste hinzufügen
            Player currPlayer = new Player(active, currPlayerName, playerTypes.get(playerIdx),
                    playerStartPos, currStack);
            players[playerIdx] = currPlayer;

            // Ist der aktuell betrachtete Spieler involviert, so wird er in der GUI erzeugt
            gui.createPlayer(playerIdx, currPlayer);
        }
    }

    /**
     * Liefert den Index des nächsten <i>aktiven</i> Spielers.
     *
     * @return Index des Spielers im Spieler-Array {@code players}, welcher aktiv ist
     */
    private int nextActivePlayer() {
        int nextActivePlayerIdx = currentPlayer;
        boolean isInvolved = false;
        while (!isInvolved) {
            nextActivePlayerIdx = (nextActivePlayerIdx + 1) % (MAX_PLAYERS);
            isInvolved = players[nextActivePlayerIdx].isInvolved();
        }

        return nextActivePlayerIdx;
    }

    /**
     * Erzeugt eine Einschiebeoperation.
     * <p>
     * Von der Einschubposition als Ausgangspunkt, werden alle betroffenen Gängekarten und Spieler,
     * insofern sie sich auf einer der betroffenen Gängekarten befinden, in die
     * {@link Shift Einschuboperation} eingebunden.
     *
     * @param insetPos    Einschubposition (Globale Position)
     * @param freeWayCard Freie Gängekarte
     * @return Einschuboperation
     */
    private Shift createShift(Position insetPos, FreeWayCard freeWayCard) {
        // Die Richtung des Einschubvorgangs
        Direction pushDir = field.getInsetPositions().get(insetPos);

        // Anzahl der betroffenen Gängekarten ermitteln
        int affectedTilesSize = field.getAffectedTileSizeOfPush(pushDir);

        // Indices der betroffenen Spieler
        List<Integer> affectedPlayer = new ArrayList<>();

        // Positionen der betroffenen Spieler
        List<Position> affectedPlayerPos = new ArrayList<>();

        // Betroffene Gängekarten
        Position[] affectedPositions = new Position[affectedTilesSize];

        // Spieler die bereits auf einer betroffenen Gängekarte gefunden wurden
        Set<Player> alreadyAffected = new HashSet<>();

        // Iteration aller betroffenen Gängekarten und Überprüfung ob Spieler betroffen sind
        Position currPos = insetPos;
        Player currPlayer;
        for (int tileIdx = 0; tileIdx < affectedTilesSize; tileIdx++) {
            // Nächste Position
            currPos = currPos.addPos(pushDir.getDirPos());

            // Überprüfen ob einer der (aktiven) Spieler auf der Position ist
            for (int playerIdx = 0; playerIdx < MAX_PLAYERS; playerIdx++) {
                currPlayer = players[playerIdx];
                Position currPlayerPos = currPlayer.getPosition();

                if (currPlayer.isInvolved() && !alreadyAffected.contains(currPlayer)
                        && currPlayer.getPosition().equals(currPos.getLogicalPos())) {

                    // Spieler zu den Betroffenen-Listen hinzufügen
                    affectedPlayer.add(playerIdx);
                    affectedPlayerPos.add(currPlayerPos);
                    alreadyAffected.add(currPlayer);
                }
            }

            // Aktuelle Position zu den betroffenen Position hinzufügen
            affectedPositions[tileIdx] = currPos;
        }

        // Neue freie Gängekarte bestimmen
        Position lastPos = affectedPositions[affectedTilesSize - 1];
        Tile newFreeWayCard = field.getBoard()[lastPos.getX() - 1][lastPos.getY() - 1];

        // Einschub erzeugen
        return new Shift(pushDir, insetPos, affectedPositions, affectedPlayer, affectedPlayerPos,
                freeWayCard, newFreeWayCard);
    }

    /**
     * Führt die übergebene Einschuboperation sowohl in der Logik als auch der GUI aus.
     *
     * @param shift Übergebene Informationen zur Einschuboperation
     */
    private void shift(Shift shift) {
        // Logging
        Position insetPos = shift.getInsetPos();
        String rowOrCol = shift.getPushDir() == Direction.LEFT
                || shift.getPushDir() == Direction.RIGHT ?
                "row " + (insetPos.getY() - 1) : "col " + (insetPos.getX() - 1);
        String test = shift.getPushDir().toString();
        FreeWayCard fwc = field.getFreeWayCard();
        String wayCard = String.format("%s, rot %o, treasure %o", fwc.getType(),
                fwc.getRotated().ordinal(), fwc.getTreasure().ordinal());
        Logger.getInstance().log(Logger.SHIFT, currentPlayer, rowOrCol, test, wayCard);

        // Einschuboperation am logischen Feld ausführen
        field.shift(shift);

        // Die Positionen der betroffenen Spieler aktualisieren
        List<Integer> affectedPlayers = shift.getAffectedPlayer();
        Logger.getInstance().log("Folgende Spieler sind betroffen: " + affectedPlayers.toString());
        List<Position> updatedPlayerPos = shift.getUpdatedPlayerPos();

        for (int i = 0; i < affectedPlayers.size(); i++) {
            // Aktuell betrachteter betroffener Spieler
            Player affectedPlayer = players[affectedPlayers.get(i)];

            // Für Logging
            Position oldPos = affectedPlayer.getPosition();

            // Position des betroffenen Spielers gemäß der Einschuboperation aktualisieren
            affectedPlayer.setPosition(updatedPlayerPos.get(i));

            // Logging
            Logger.getInstance().log(Logger.SHIFT_PLAYER_AFFECTED, affectedPlayers.get(i), oldPos,
                    updatedPlayerPos.get(i));
        }

        // Die für den aktuellen Spieler möglichen Positionen sowie entsprechende Pfade berechnen
        Position currPos = players[currentPlayer].getPosition();
        this.possiblePositions = PathUtil.getPossiblePositions(field.getBoard(), currPos);
    }

    /**
     * Bewegt einen {@link Player Spieler} zu einer übergebenen {@link Position}.
     * <p>
     * Die übergebene {@link Position Positionsangabe} ist im Rahmen des gesamten Spielfeldes
     * inklusive Umrandung, weshalb hier eine Anpassung notwendig ist.
     *
     * @param playerIdx Spieler-Index der zu der übergebenen Position zugewiesen werden soll
     * @param toPos     Position zu welcher der Spieler zugewiesen werden soll
     */
    private Treasure movePlayer(int playerIdx, Position toPos) {

        // Spieler der eine neue Position zugewiesen bekommt
        Player player = players[playerIdx];

        // Logging: Spieler-Bewegung
        Logger.getInstance().log(Logger.MOVE, currentPlayer, player.getPosition().toString(),
                toPos.toString());

        // Zuweisung der neuen (übergebenen) Position
        player.setPosition(toPos);

        // Insofern ein Schatz durch die Spieler-Bewegung gefunden wird, wird hier der nächste zu
        // suchende Schatz zugewiesen
        Treasure collectedTreasure = null;

        // Erreicht der Spieler seinen zu suchenden Schatz
        if (field.getBoard()[toPos.getX()][toPos.getY()].getTreasure() == player.getCurrTreasure()) {
            collectedTreasure = collectTreasure(player);
            field.getBoard()[toPos.getX()][toPos.getY()].setTreasure(Treasure.EMPTY);
        }

        return collectedTreasure;
    }

    /**
     * Der übergebene Spieler sammelt einen Schatz ein.
     *
     * @param player Spieler der seinen aktuell zu suchenden Schatz einsammelt
     * @return Den nachfolgenden zu suchenden Schatz des übergebenen Spielers
     */
    private Treasure collectTreasure(Player player) {
        assert !player.getTreasureCards().isEmpty();

        // Derzeitigen Schatz entfernen
        Treasure oldTreasure = player.getTreasureCards().poll();

        // Logging: Schatz eingesammelt
        String treasureToStr = Logger.getInstance().treasuresToString(player.getTreasureCards());
        Logger.getInstance().log(Logger.TREASURE_COLLECTED, currentPlayer,
                oldTreasure.ordinal(), oldTreasure.toString(), treasureToStr);

        // Neuen Schatz liefern
        return oldTreasure;
    }

    /**
     * Prüft, ob die übergebene <b>globale</b> Position eine Position im Rahmen des Spielfeldes
     * darstellt.
     *
     * @param globalPos Zu überprüfende (globale) Position
     * @return true, wenn Koordinate eine Ecke der Umrandung ist.
     */
    private boolean isInsideField(Position globalPos) {
        int c = globalPos.getX();
        int r = globalPos.getY();

        return (c <= field.getColCount() && c > 0) && (r > 0 && r <= field.getRowCount());
    }

    /**
     * Handhabt im Rahmen eines menschlichen Spielzuges eine invalide Positionsangabe.
     *
     * @param userInputPos Benutzereingabe
     */
    private void handleInvalidShiftInput(Position userInputPos) {
        if (isInsideField(userInputPos)) {
            gui.showAlert(Message.FIRST_SHIFT.getMessage());
        } else {
            gui.showAlert(Message.INVALID_SHIFTPOS.getMessage());
        }
    }

    /**
     * Überprüft, ob der übergebene Spieler gewonnen hat.
     *
     * @return True, wenn der aktuelle Spieler gewonnen hat, ansonsten false.
     */
    private boolean checkIfPlayerWon() {
        Player currPlayer = players[currentPlayer];
        if (currPlayer.isInvolved() && currPlayer.getTreasureCards().size() == 0
                && currPlayer.getPosition().equals(START_POSITIONS[currentPlayer])) {
            gui.showWinner(players[currentPlayer].getName());
            gui.disableField(false);

            // Logging: Aktueller Spieler hat gewonnen
            Logger.getInstance().log(Logger.PLAYER_WON, currentPlayer);

            return true;
        }

        return false;
    }

    /**
     * Getter zu Testzwecken.
     *
     * @return Liefert den Index des aktuellen Spielers
     */
    int getCurrentPlayer() {
        return currentPlayer;
    }
}
