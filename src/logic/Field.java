package logic;

import gui.FXMLDocumentController;
import logic.tile.*;

import java.util.*;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * In dieser Klasse wird das Spielfeld anhand der Breite/Länge durch das vom FXMLDocument
 * bereitgestellte GridPane erstellt.
 * Insbesondere werden hier die statischen Gängekarten definiert.
 *
 * @author svnsrd  [Repo: 37]
 * @version 04.08.2021
 */
public class Field {

    /**
     * Versatz, mit dem die äußeren Gängekarten übersprungen werden.
     */
    private static final int OUTER_OFFSET = 2;

    /**
     * Start-Ordinal der Rotation für die mittleren statischen Gängekarten.
     */
    private static final int MIDDLESTATIC_STARTROT = 3;

    /**
     * Anzahl der verschiedenen <b>dynamischen</b> Gängekarten.
     */
    private static final int L_CARDSIZE = 15;
    private static final int I_CARDSIZE = 13;
    private static final int T_CARDSIZE = 6;

    /**
     * Das Spielfeld.
     */
    private Tile[][] board;

    /**
     * Einschubpositionen des Spielfeldes, in Korrespondenz zur ihrer Einschubrichtung.
     */
    private final Map<Position, Direction> insetPositions;

    /**
     * Freie Gängekarte, welche eingeschoben wird.
     */
    private FreeWayCard freeWayCard;

    /**
     * Konstruktor zur Erzeugung des Spielfeldes.
     * <p>
     * Hierbei wird das Spielfeld <i>dynamisch</i> erzeugt, sodass, auch wenn über den
     * {@link FXMLDocumentController} ein {@link javafx.scene.layout.GridPane}
     * mit anderen Größen definiert wird, das Spielfeld erzeugt werden kann.
     * <p>
     * Eine entsprechende Anpassung der Anzahl der Gängekarten ({@code T_CARDSIZE},
     * {@code L_CARDSIZE} und {@code I_CARDSIZE} ist hierbei aber notwendig.
     *
     * @param rowcount x-Länge des Spielfeldes
     * @param colcount y-Länge des Spielfeldes
     */
    public Field(int rowcount, int colcount, List<Treasure> treasures) {
        this.board = new Tile[colcount][rowcount];
        this.insetPositions = new HashMap<>();

        // Randomisierter Zahlengenerator
        final Random rnd = new Random();

        // Rotationen
        final TileRotation[] rotations = TileRotation.values();
        final int rotationsLen = rotations.length;

        // Erzeugen der dynamischen Gängekarten
        List<Tile> tiles = new LinkedList<>();

        // Erzeugen der dynamischen Gängekarten
        for (int i = 0; i < L_CARDSIZE; i++) {
            tiles.add(new Tile(TileShape.L, TileState.DYNAMIC,
                    rotations[rnd.nextInt(rotationsLen)]));
        }

        for (int i = 0; i < I_CARDSIZE; i++) {
            tiles.add(new Tile(TileShape.I, TileState.DYNAMIC,
                    rotations[rnd.nextInt(rotationsLen)]));
        }

        for (int i = 0; i < T_CARDSIZE; i++) {
            tiles.add(new Tile(TileShape.T, TileState.DYNAMIC,
                    rotations[rnd.nextInt(rotationsLen)]));
        }

        // Mischen der dynamischen Gängekarten
        Collections.shuffle(tiles);

        // Erzeugen und Zuweisen der mittleren statischen Gängekarten
        initializeMiddleStaticWayCards();

        // Erzeugen und zuweisen der äußeren statischen Gängekarten
        initializeStaticWayCards(colcount, rowcount);

        // Mischen der Schatzkarten
        Collections.shuffle(treasures);

        // Zuweisen der variablen Gängekarten zum Spielfeld
        int tileCounter = 0;
        for (int col = 0; col < colcount; col += 2) {
            for (int row = 1; row < rowcount - 1; row += 2) {
                Tile currWayCard = tiles.get(tileCounter++);
                board[col][row] = currWayCard;
            }
        }

        for (int col = 1; col < rowcount; col += 2) {
            for (int row = 0; row < rowcount; row++) {
                board[col][row] = tiles.get(tileCounter++);
            }
        }

        // Zuweisen der freien Gängekarte
        Tile freeWayCard = tiles.get(tileCounter);
        this.freeWayCard = new FreeWayCard(freeWayCard, new Position(-1, -1));

        // Startpositionen der Spieler an den jeweiligen Ecken, als Schätze zuweisen
        assignLastTreasures(Game.START_POSITIONS);

        int treasureCnt = 0;
        while (treasureCnt < treasures.size()) {
            int x = rnd.nextInt(rowcount);
            int y = rnd.nextInt(colcount);
            Tile currTile = board[y][x];
            if (currTile.getTreasure() == Treasure.EMPTY) {
                Treasure currTreasure = treasures.get(treasureCnt++);
                currTile.setTreasure(currTreasure);
            }
        }
    }

    /**
     * Konstruktor zur Erzeugung eines <i>fiktiven</i> Spielfeldes für die Berechnung im Rahmen
     * der Berechnung eines KI-Zuges in {@link logic.player.AIUtil}.
     *
     * @param board Das Spielfeld {@link Tile Tile-Array}
     * @param freeWayCard {@link FreeWayCard Die freie Gängekarte}
     * @param insetPositions Mapping der Einschubposition zu ihren korrespondierenden
     *                       Einschubrichtungen
     */
    public Field(Tile[][] board, FreeWayCard freeWayCard, Map<Position, Direction> insetPositions) {
        this.board = board;
        this.freeWayCard = freeWayCard;
        this.insetPositions = insetPositions;
    }

    /**
     * Liefer eine <i>tiefe</i> Kopie des Spielfeldes {@code board}.
     * @return
     */
    public Tile[][] copyOfField() {
        Tile[][] copyField = new Tile[getColCount()][getRowCount()];
        for (int col = 0; col < getColCount(); col++) {
            for (int row = 0; row < getRowCount(); row++) {
                copyField[col][row] = board[col][row];
            }
        }
        return copyField;
    }

    /**
     * Lädt das übergebene Feld und freie Gängekarte auf dieses Feld und die freie Gängekarte.
     *
     * @param field       Das übergebene Feld
     * @param freeWayCard Übergebene freie Gängekarte
     */
    public void loadField(Tile[][] field, FreeWayCard freeWayCard) {
        this.board = new Tile[board.length][board[0].length];

        for (int col = 0; col < getColCount(); col++) {
            for (int row = 0; row < getRowCount(); row++) {
                this.board[col][row] = field[col][row];
            }
        }

        this.freeWayCard = freeWayCard;
    }

    /**
     * Entfernt die Startposition-Schätze von dem Spielfeld.
     *
     * @param startPos Startpositionen
     */
    public void removeStartPosTreasures(Position[] startPos) {
        for (Position pos : startPos) {
            board[pos.getX()][pos.getY()].setTreasure(Treasure.EMPTY);
        }
    }

    /**
     * Factory-Methode zur Erzeugung eines benutzerdefinierten Spielfeldes.
     *
     * @param board String-Repräsentation des benutzerdefinierten Spielfeldes
     * @return Spielfeld gemäß der benutzerdefinierten String-Darstellung des
     * Spielfeldes
     */
    Field(String board, FreeWayCard freeWayCard) {
        String[] rows = board.split("\n");
        String[] aCol = rows[0].split(",");

        final int rowLen = rows.length;
        final int colLen = aCol.length;

        this.board = new Tile[aCol.length][rowLen];

        for (int row = 0; row < rowLen; row++) {
            String[] tiles = rows[row].split(",");
            for (int col = 0; col < tiles.length; col++) {
                String[] info = tiles[col].split("");
                TileShape shape = TileShape.valueOf(info[0]);
                TileRotation rot = TileRotation.values()[Integer.parseInt(info[1])];
                Treasure treasure = Treasure.values()[Integer.parseInt(info[2] + info[3])];
                this.board[col][row] = new Tile(shape, rot, treasure);
            }
        }

        // Einschubpositionen erzeugen ohne statische Felder
        this.insetPositions = new HashMap<>();
        for (int col = 0; col < colLen + 2; col += colLen + 1) {
            for (int row = 1; row < rowLen + 1; row++) {
                if (col == 0) {
                    this.insetPositions.put(new Position(col, row), Direction.RIGHT);
                } else {
                    this.insetPositions.put(new Position(col, row), Direction.LEFT);
                }
            }
        }

        for (int row = 0; row < rowLen + 2; row += rowLen + 1) {
            for (int col = 1; col < colLen + 1; col++) {
                if (row == 0) {
                    this.insetPositions.put(new Position(col, row), Direction.DOWN);
                } else {
                    this.insetPositions.put(new Position(col, row), Direction.UP);
                }
            }
        }


        this.freeWayCard = freeWayCard;
    }

    /**
     * Führt den Einschiebevorgang aus und aktualisiert die freie Gängekarte.
     *
     * @param shift Informationen des Einschiebevorganges
     */
    public void shift(Shift shift) {
        // Einschubvorgang ausführen
        Tile freeWayCards = shift.executeShift(board);

        // Startposition im Rahmen dieses Spielfeldes ermitteln
        Position pos = shift.getLogicalStartPos();

        // An erste Stelle die aktuelle freie Gängekarte setzen
        board[pos.getX()][pos.getY()] = new Tile(freeWayCard.getType(), freeWayCard.getState(),
                freeWayCard.getRotated(), freeWayCard.getTreasure());

        // Freie Gängekarte aktualisieren
        Position newFreeWayCardPos = shift.getLastAffectedPosition()
                .addPos(shift.getPushDir().getDirPos());
        this.freeWayCard = new FreeWayCard(freeWayCards, newFreeWayCardPos);
    }

    /**
     * Liefert zu einer {@link Direction Richtung} die Breite/Länge des Spielfeldes.
     *
     * @param pushDir Richtung
     * @return Länge der zur übergebenen Richtung korrespondierenden Länge/Breite
     */
    public int getAffectedTileSizeOfPush(Direction pushDir) {
        int affectedTilesSize;
        if (pushDir == Direction.RIGHT || pushDir == Direction.LEFT) { /* Horizontal */
            affectedTilesSize = board.length;
        } else { /* Vertikal */
            affectedTilesSize = board[0].length;
        }
        return affectedTilesSize;
    }

    /**
     * Gibt das Spielfeld zurück
     *
     * @return Das Spielfeld
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * Liefert die Anzahl der Spalten des Spielfeldes <b>ohne Umrandung</b>.
     *
     * @return Anzahl der Spalten ohne Umrandung
     */
    public int getColCount() {
        return board.length;
    }

    /**
     * Liefert die Anzahl der Reihen des Spielfeldes <b>ohne Umrandung</b>.
     *
     * @return Anzahl der Reihen ohne Umrandung
     */
    public int getRowCount() {
        return board[0].length;
    }

    /**
     * Gibt die aktuelle freie Gängekarte zurück
     *
     * @return Liefert die aktuelle freie Gängekarte
     */
    public FreeWayCard getFreeWayCard() {
        return freeWayCard;
    }

    /**
     * Gibt die Positionen zurück, von denen aus eine Gängekarte eingeschoben
     * werden kann.
     *
     * @return Positionen über welche eine Gängekarte eingeschoben werden kann
     */
    public Map<Position, Direction> getInsetPositions() {
        return insetPositions;
    }

    /**
     * Gibt die Position des übergebenen Schatzes auf dem Spielfeld zurück.
     *
     * @param treasure Übergebener Schatz
     * @return Position (Spiellogik) des Schatzes auf dem Spielfeld
     */
    public Position getTreasurePos(Treasure treasure) {
        for (int col = 0; col < board.length; col++) {
            for (int row = 0; row < board[0].length; row++) {
                if (board[col][row].getTreasure() == treasure) {
                    return new Position(col, row);
                }
            }
        }

        // Ist der Schatz nicht gefunden worden, so muss er auf der freien Gängekarte liegen
        return new Position(-1, -1);
    }

    /**
     * Hilfsmethode, welche die <b>statischen</b> Gängekarten erzeugt.
     */
    private void initializeStaticWayCards(int colcount, int rowcount) {

        /* -------------------- Links --------------------- */
        for (int row = OUTER_OFFSET; row < rowcount - 1; row += 2) {
            board[0][row] = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_270);
            insetPositions.put(new Position(0, row), Direction.RIGHT);
        }
        insetPositions.put(new Position(0, rowcount - 1), Direction.RIGHT);

        /* -------------------- Rechts --------------------- */
        for (int row = OUTER_OFFSET; row < rowcount - 1; row += 2) {
            board[colcount - 1][row] = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_90);
            insetPositions.put(new Position(colcount + 1, row), Direction.LEFT);
        }
        insetPositions.put(new Position(colcount + 1, rowcount - 1), Direction.LEFT);

        /* -------------------- Oben --------------------- */
        for (int col = OUTER_OFFSET; col < rowcount - 1; col += 2) {
            board[col][0] = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_0);
            insetPositions.put(new Position(col, 0), Direction.DOWN);
        }
        insetPositions.put(new Position(colcount - 1, 0), Direction.DOWN);

        /* -------------------- Unten --------------------- */
        for (int col = OUTER_OFFSET; col < colcount - 1; col += 2) {
            board[col][rowcount - 1] = new Tile(TileShape.T, TileState.STATIC, TileRotation.ROT_180);
            insetPositions.put(new Position(col, rowcount + 1), Direction.UP);
        }
        insetPositions.put(new Position(colcount - 1, rowcount + 1), Direction.UP);


        /* -------------------- Ecken --------------------- */
        board[0][0] = new Tile(
                TileShape.L, TileState.STATIC, TileRotation.ROT_90);
        board[colcount - 1][0] = new Tile(
                TileShape.L, TileState.STATIC, TileRotation.ROT_180);
        board[colcount - 1][rowcount - 1] = new Tile(
                TileShape.L, TileState.STATIC, TileRotation.ROT_270);
        board[0][rowcount - 1] = new Tile(
                TileShape.L, TileState.STATIC, TileRotation.ROT_0);
    }

    /**
     * Setzt die Startpositionen der Spieler, als "Schätze" an die entsprechenden Startpositionen
     * der Spieler.
     *
     * @param startPositions Startpositionen der Spieler
     */
    public void assignLastTreasures(Position[] startPositions) {
        int treasuresLength = Treasure.values().length;
        int startPositionsLen = startPositions.length;

        for (int startPosIdx = 0; startPosIdx < startPositionsLen; startPosIdx++) {
            Position startPos = startPositions[startPosIdx];
            board[startPos.getX()][startPos.getY()]
                    .setTreasure(Treasure.values()[treasuresLength - startPositionsLen + startPosIdx]);
        }
    }

    /**
     * Initialisiert der mittleren statischen Gängekarten.
     */
    private void initializeMiddleStaticWayCards() {
        final TileRotation[] rotations = TileRotation.values();
        final int rotationLen = rotations.length;

        int tRotation = MIDDLESTATIC_STARTROT;
        int rowCounter = 0;
        for (int row = 2; row < getRowCount() - 1; row += 2) {
            if (rowCounter % 2 == 0) {
                for (int col = OUTER_OFFSET; col < getColCount() - 1; col += 2) {
                    board[col][row] = new Tile(TileShape.T, TileState.STATIC, rotations[tRotation]);
                    tRotation++;
                    tRotation %= rotationLen;
                }
            } else {
                for (int col = getColCount() - 1 - OUTER_OFFSET; col >= 2; col -= 2) {
                    board[col][row] = new Tile(TileShape.T, TileState.STATIC, rotations[tRotation]);
                    tRotation++;
                    tRotation %= rotationLen;
                }
            }
            rowCounter++;
        }
    }
}
