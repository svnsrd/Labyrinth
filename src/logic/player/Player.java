package logic.player;

import logic.Position;
import logic.Treasure;

import java.util.Queue;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse enthält Informationen zu einem Spieler.
 *
 * @author svnsrd  [Repo 37]
 * @version 01.08.2021
 */
public class Player {

    /**
     * Gibt an ob der Spieler im Spiel involviert ist.
     */
    private final boolean involved;

    /**
     * Name des Spielers.
     */
    private final String name;

    /**
     * Spielertyp des Spielers
     */
    private final PlayerType directedBy;

    /**
     * Position des Spielers.
     */
    private Position position;

    /**
     * Die zugeteilten Schatzkarten des Spielers
     */
    private final Queue<Treasure> treasureCards;

    /**
     * Konstruktor, welche einen Spieler erzeugt mit den übergebenen Informationen erzeugt.
     *
     * @param name       Name des Spielers
     * @param directedBy Spielertyp des Spielers
     * @param position   Position des Spielers
     * @param treasures  Schätze die der Spieler finden muss
     * @param involved   Gibt an, ob der Spieler im Spiel involviert ist
     */
    public Player(boolean involved, String name, PlayerType directedBy, Position position,
                  Queue<Treasure> treasures) {

        this.treasureCards = treasures;
        this.directedBy = directedBy;
        this.position = position;
        this.name = name;
        this.involved = involved;
    }

    /**
     * Liefert den Namen dieses Spielers.
     *
     * @return Name dieses Spielers
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert den Spielertypen dieses Spielers
     *
     * @return Spielertyp
     */
    public PlayerType getDirectedBy() {
        return directedBy;
    }

    /**
     * Liefert die Position dieses Spielers im Spielfeld <b>ohne Umrandung</b>.
     *
     * @return Position des Spielers im Spielfeld ohne Umrandung
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Liefert die <b>globale</b> Position dieses Spielers, d.h. im Spielfeld inklusive Umrandung.
     *
     * @return Globale Position dieses Spielers.
     */
    public Position getGlobalPos() {
        return position.getGlobalPos();
    }

    /**
     * Ändert die Position des Spielers im Spielfeld <b>ohne Umrandung</b> auf die übergebene
     * {@code position Position}.
     *
     * @param position Übergebene Position auf welche, dieser Spieler gesetzt werden soll.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Liefert den <b>aktuell zu suchenden Schatz</b>.
     *
     * @return Den aktuell zu suchenden Schatz
     */
    public Treasure getCurrTreasure() {
        return treasureCards.peek();
    }

    /**
     * Liefert die {@link Queue Warteschlange} der Schätze, die dieser Spieler noch suchen muss.
     *
     * @return Warteschlange der Schätze, die dieser Spieler noch suchen muss
     */
    public Queue<Treasure> getTreasureCards() {
        return treasureCards;
    }

    /**
     * Gibt an ob der Spieler im Spiel involviert ist.
     *
     * @return True, wenn dieser Spieler im Spiel involviert ist, ansonsten false.
     */
    public boolean isInvolved() {
        return involved;
    }
}
