package logic.tile;

import com.google.gson.annotations.SerializedName;
import logic.RotateDirection;

/**
 * Diese Aufzählung repräsentiert alle möglichen Rotationen einer Gängekarte.
 * <p>
 * Möglich hierbei sind:
 * <ul>
 *     <li>0°</li>
 *     <li>90°</li>
 *     <li>180°</li>
 *     <li>270°</li>
 * </ul>
 * <p>
 * Eine 0°-Rotation entspricht der Form, so wie sie geschrieben wird, das heißt eine T-Gängekarte,
 * hat also bei 0° ihre Ausgänge links, rechts und unten.
 * <p>
 *
 * @author svnsrd  [Repo 37]
 * @version 08.08.2021
 */
public enum TileRotation {

    @SerializedName("0") ROT_0(0),
    @SerializedName("1") ROT_90(90),
    @SerializedName("2") ROT_180(180),
    @SerializedName("3") ROT_270(270);

    /**
     * Die Rotation als Grad repräsentiert.
     */
    private final int rotationDeg;

    /**
     * Konstruktor.
     *
     * @param rotationDeg Der Grad-Wert der Rotation
     */
    TileRotation(int rotationDeg) {
        this.rotationDeg = rotationDeg;
    }

    /**
     * Liefert den Grad-Wert der Rotation
     *
     * @return Grad-Wert dieser Rotation
     */
    public int getDeg() {
        return rotationDeg;
    }

    /**
     * Liefert die nächste Rotation anhand einer gegebenen Rotationsrichtung.
     *
     * @param dir Richtung in die rotiert werden soll
     * @return Rotierte Rotation
     */
    public TileRotation getNextRotation(RotateDirection dir) {
        TileRotation resultDir;
        int possibleRotLen = values().length;
        int idx;

        if (dir == RotateDirection.CLOCKWISE) {
            idx = (this.ordinal() + 1) % possibleRotLen;
            resultDir = values()[idx];
        } else {
            resultDir = this.ordinal() == 0
                    ? values()[possibleRotLen - 1] : values()[this.ordinal() - 1];
        }

        return resultDir;
    }

    /**
     * Liefert eine String-Repräsentation des Ordinal-Wertes dieser Rotation.
     *
     * @return String-Repräsentation des Ordinal-Wertes
     */
    @Override
    public String toString() {
        return String.valueOf(this.ordinal());
    }
}
