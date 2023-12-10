package logic;

import com.google.gson.annotations.SerializedName;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Aufzählung repräsentiert alle Schätze des Spiels.
 * <p>
 * Zusätzlich zu den Schätzen gibt es 5 weitere <i>Schätze</i>, welche zur Ablaufsteuerung dienen.
 *
 * <ul>
 *     <li>{@code EMPTY}, welche eine Gängekarte besitzt, wenn sie keine Schatzkarte hat</li>
 *     <li>Startpositionen, welche die Spieler nach Finden aller ihrer Schätze erreichen müssen</li>
 * </ul>
 *
 * @author svnsrd  [Repo: 37]
 * @version 31.07.2021
 */
public enum Treasure {
    @SerializedName("0") EMPTY,
    @SerializedName("1") GHOST,
    @SerializedName("2") DIAMOND,
    @SerializedName("3") INSECT,
    @SerializedName("4") SWORD,
    @SerializedName("5") KEYS,
    @SerializedName("6") MAP,
    @SerializedName("7") LIZARD,
    @SerializedName("8") SPIDER,
    @SerializedName("9") CROWN,
    @SerializedName("10") WOMAN,
    @SerializedName("11") GENIE,
    @SerializedName("12") BAT,
    @SerializedName("13") GOBLIN,
    @SerializedName("14") OWL,
    @SerializedName("15") CANDLE,
    @SerializedName("16") BUTTERFLY,
    @SerializedName("17") SKULL,
    @SerializedName("18") GOLD,
    @SerializedName("19") MOUSE,
    @SerializedName("20") DRAGON,
    @SerializedName("21") BOX,
    @SerializedName("22") RING,
    @SerializedName("23") ARMOR,
    @SerializedName("24") BOOK,

    STARTPOS_PLAYER0,
    STARTPOS_PLAYER1,
    STARTPOS_PLAYER2,
    STARTPOS_PLAYER3;

    /**
     * Liefert eine String-Repräsentation einer bestimmten Aufzählung.
     * <p>
     * Geliefert wird ein zweistelliger Ordinal-Wert, welcher bei 1-stelligen Zahlen z.B. "04" ist.
     *
     * @return String-Repräsentation einer bestimmten Aufzählung
     */
    @Override
    public String toString() {
        String res = "";
        if (this.ordinal() < 10) {
            res = "0";
        }

        res += this.ordinal();
        return res;
    }
}
