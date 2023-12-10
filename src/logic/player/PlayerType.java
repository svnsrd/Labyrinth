package logic.player;

import com.google.gson.annotations.SerializedName;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Aufzählung beinhaltet alle Spielertypen des Spiels.
 * <p>
 * Mögliche Spielertypen sind:
 * <ul>
 *     <li>menschliche</li>
 *     <li>normale KI</li>
 *     <li>erweiterte KI</li>
 * </ul>
 * <p>
 * Spieler.
 *
 * @author svnsrd 
 * @version 01.08.2021
 */
public enum PlayerType {
    @SerializedName("0") HUMAN("menschlich"),
    @SerializedName("1") AI_NORMAL("KI (normal)"),
    @SerializedName("2") AI_EXTENDED("KI (erweitert)");

    /**
     * Name des Spielertypen.
     */
    private final String name;

    /**
     * Konstruktor.
     *
     * @param name Name des Spielertypen
     */
    PlayerType(String name) {
        this.name = name;
    }

    /**
     * Liefert den Namen des Spielertypen.
     *
     * @return String-Repräsentation des Spielertypen.
     */
    @Override
    public String toString() {
        return name;
    }
}
