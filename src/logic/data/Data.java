package logic.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.Game;
import logic.Message;
import logic.player.Player;
import logic.tile.FreeWayCard;
import logic.tile.Tile;
import logic.Treasure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse enthält Methoden zum Laden und Speicher von Spielstanddateien.
 *
 * @author svnsrd  [Repo: 37]
 * @version 01.08.2021
 */
public class Data {

    /**
     * Spielfeld des Spielstandes.
     */
    private final Tile[][] field;

    /**
     * Freie Gängekarte des Spielstandes.
     */
    private final FreeWayCard freeWayCard;

    /**
     * Aktueller Spieler des Spielstandes.
     */
    private final int currentPlayer;

    /**
     * Spieler des Spielstandes.
     */
    private final Player[] players;

    /**
     * @param field         Spielfeld
     * @param freeWayCard   Freie Gängekarte
     * @param currentPlayer Index des aktuellen Spielers
     * @param players       Spieler
     */
    public Data(Tile[][] field, FreeWayCard freeWayCard, int currentPlayer, Player[] players) {
        this.field = field;
        this.freeWayCard = freeWayCard;
        this.currentPlayer = currentPlayer;
        this.players = players;
    }

    /**
     * Konstruktor zum Laden eines Spielstandes.
     *
     * @param file Spielstanddatei
     * @throws FileNotFoundException Datei konnte nicht gefunden werden
     */
    public Data(File file, int colcount, int rowcount) throws FileNotFoundException, InvalidGameDataException {
        InputStream stream = new FileInputStream(file);
        Reader reader = new InputStreamReader(stream);
        Gson gson = new Gson();

        try {
            Data data = gson.fromJson(reader, Data.class);

            // Nachricht die im Falle eines fehlerhaften Ladens, an die InvalidGameDataException
            // erzeugt und übergeben wird
            String message;

            // Prüfen, ob ein Spielstand geladen wurde
            if (data == null || data.getField() == null || data.getPlayers() == null
                    || data.getFreeWayCard() == null) {
                throw new InvalidGameDataException(Message.INVALID_GAMEDATA.getMessage());
            }

            // Prüfung ob Spieler einen invaliden Schatz beinhalten
            Set<Treasure> alreadyUsed = new HashSet<>(); /* Zur Prüfung von Duplikaten */
            for (Player player : data.getPlayers()) {
                for (Treasure treasure : player.getTreasureCards()) {
                    if (treasure == null || alreadyUsed.contains(treasure)) {
                        throw new InvalidGameDataException(Message.INVALID_TREASURE.getMessage());
                    }

                    alreadyUsed.add(treasure);
                }
            }

            // Prüfen, ob die freie Gängekarte invalide ist
            FreeWayCard loadedFreeWayCard = data.getFreeWayCard();
            if (loadedFreeWayCard == null) {
                throw new InvalidGameDataException(Message.INVALID_FREEWAYCARD.getMessage());
            }

            // Prüfen, ob das Spielfeld eine valide Größe hat
            Tile[][] loadedField = data.getField();
            if (loadedField.length != colcount || loadedField[0].length != rowcount) {
                message = String.format(Message.INVALID_FIELDLENGTH.getMessage(), loadedField.length,
                        loadedField[0].length, colcount, rowcount);
                throw new InvalidGameDataException(message);
            }

            // Prüfen, ob das Spielfeld valide ist
            Set<Treasure> alreadyUsedInField = new HashSet<>();
            for (Tile[] tiles : data.getField()) {
                for (Tile tile : tiles) {

                    if (tile == null || tile.getTreasure() == null) { /* Prüfung auf null */
                        throw new InvalidGameDataException(Message.INVALID_TREASURE.getMessage());
                    } else if (alreadyUsedInField.contains(tile.getTreasure())) { /* Duplikate */
                        message = String.format(Message.DUPLICATE_TREASURE_FIELD.getMessage(),
                                tile.getTreasure().ordinal(), tile.getTreasure());
                        throw new InvalidGameDataException(message);
                    } else if (tile.getTreasure() != Treasure.EMPTY) {
                        alreadyUsedInField.add(tile.getTreasure());
                    }
                }
            }

            // Prüfen, ob der aktuelle Spieler innerhalb der validen Spieler-Indices ist
            if (data.getCurrentPlayer() < 0 || data.getCurrentPlayer() > Game.MAX_PLAYERS) {
                message = String.format(
                        Message.INVALID_CURRENTPLAYER.getMessage(), data.getCurrentPlayer());
                throw new InvalidGameDataException(message);
            }


            this.field = data.field;
            this.freeWayCard = data.freeWayCard;
            this.currentPlayer = data.currentPlayer;
            this.players = data.players;

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Schreibt die Informationen dieses Spielstandes in die über {@code file} referenzierte Datei.
     *
     * @param file Stream zum Schreiben auf die referenzierte Datei
     * @throws IOException Fehler beim Schreiben in die Datei
     */
    public void saveGame(FileOutputStream file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        file.write(gson.toJson(this).getBytes());
        file.close();
    }

    /**
     * Liefert das aktuelle Spielfeld des Spielstandes.
     *
     * @return Spielfeld
     */
    public Tile[][] getField() {
        return field;
    }

    /**
     * Liefert die aktuelle freie Gängekarte des Spielstandes.
     *
     * @return Aktuelle freie Gängekarte
     */
    public FreeWayCard getFreeWayCard() {
        return freeWayCard;
    }

    /**
     * Liefert den Spieler, welcher gemäß dem Spielstand aktuell an der Reihe ist.
     *
     * @return Spieler der aktuell an der Reihe ist
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Liefert die Spieler des Spielstandes.
     *
     * @return Spieler
     */
    public Player[] getPlayers() {
        return players;
    }
}
