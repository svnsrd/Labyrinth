package logic;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse repräsentiert die Position der Elemente auf dem Spielfeld.
 *
 * @author svnsrd  [Repo: 37]
 * @version 08.08.2021
 */
public class Position {

    /**
     * x-Koordinate einer Position auf dem Spielfeld
     */
    private final int x;

    /**
     * y-Koordinate einer Position auf dem Spielfeld
     */
    private final int y;

    /**
     * Konstruktor zur Erzeugung einer Position
     *
     * @param x x-Koordinate einer Position
     * @param y y-Koordinate einer Position
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Addiert die x- und y-Koordinate dieser Position mit den der übergebenen Position
     * und liefert dahingehend eine neue Position.
     *
     * @param otherPos Übergebene Position
     * @return Summierte Position
     */
    public Position addPos(Position otherPos) {
        int x = this.getX() + otherPos.getX();
        int y = this.getY() + otherPos.getY();

        return new Position(x, y);
    }

    /**
     * Gibt an, ob diese Position diagonal zu der nächsten Position steht.
     *
     * @param other Übergebene Position
     * @return True, wenn sie diagonal zueinander sind, ansonsten false
     */
    public boolean isDiagonalTo(Position other) {
        return Math.abs(this.x - other.x) == Math.abs(this.y - other.y);
    }

    /**
     * Berechnet die <i>Manhattan-Distanz</i> zur übergebenen Position {@code other}.
     *
     * @param other Übergebene Position
     * @return Manhattan-Distanz dieser Position zur übergebenen {@link Position}
     */
    public int distanceTo(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Liefert eine Position im Rahmen des gesamten Spielfeldes inklusive Umrandung.
     *
     * @return Position im gesamten Spielfeld
     */
    public Position getGlobalPos() {
        return new Position(this.x + 1, this.y + 1);
    }

    /**
     * Liefert eine Position im Rahmen des Spielfeldes <b>ohne Umrandung</b>.
     *
     * @return Position im Spielfeld ohne Umrandung
     */
    public Position getLogicalPos() {
        return new Position(this.x - 1, this.y - 1);
    }

    /**
     * Gibt die x-Koordinate einer Position zurück
     *
     * @return x-Koordinate einer Position
     */
    public int getX() {
        return x;
    }

    /**
     * Gibt die y-Koordinate einer Position zurück
     *
     * @return y-Koordinate einer Position
     */
    public int getY() {
        return y;
    }

    /**
     * Liefert eine String-Repräsentation dieser {@link Position} zurück.
     *
     * @return String-Repräsentation dieser Position
     */
    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Vergleicht diese und ein übergebenes Objekt auf Gleichheit.
     *
     * @param o Übergebenes Objekt (Position)
     * @return True, wenn x- und y-Koordinaten gleich sind, ansonsten false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            return false;
        }
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    /**
     * Generiert den HashCode für diese Position.
     *
     * @return HashCode für diese Position.
     */
    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
