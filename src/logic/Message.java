package logic;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Aufzählung beinhaltet Nachrichten, welche
 *
 * <ul>
 *    <li>im Rahmen einer {@link logic.data.InvalidGameDataException} an die Ausgabe
 *        kommuniziert werden</li>
 *    <li>die menschlichen Spielern bei falschen Benutzereingaben in der {@link gui.JavaFXGUI GUI}
 *        präsentiert werden</li>
 *    <li>die den Gewinner des Spiels in der {@link gui.JavaFXGUI GUI} präsentiert</li>
 * </ul>
 *
 * @author svnsrd 
 * @version 01.08.2021
 */
public enum Message {

    /**
     * Nachrichten, welche im Rahmen einer {@link logic.data.InvalidGameDataException} an den
     * Benutzer und die Ausgabe kommuniziert werden.
     */
    INVALID_CURRENTPLAYER("Der Spielerindex %o existiert nicht."),
    INVALID_FIELDLENGTH("Die Spielfeldgröße %ox%o ist invalide. Gefordert ist %ox%o."),
    INVALID_TREASURE("Der Schatz mit dem Index, existiert nicht."),
    INVALID_FREEWAYCARD("Die freie Gängekarte ist invalide."),
    INVALID_GAMEDATA("Es wurde kein Spielstand geladen."),
    DUPLICATE_TREASURE_FIELD("Der Schatz %o (%s) ist mehrfach im Spielfeld vorhanden."),

    /**
     * Nachricht, welche im Rahmen einer {@link java.io.FileNotFoundException} an den Benutzer und
     * die Ausgabe kommuniziert wird.
     */
    FILE_NOT_FOUND("Die Datei konnte nicht gefunden werden."),

    /**
     * Nachrichten, die dem Benutzer bei <b>falschen Benutzereingaben</b> präsentiert werden.
     */
    FIRST_SHIFT("Schieben Sie zunächst die freie Gängekarte ein!"),
    INVALID_SHIFTPOS("Von dieser Position kann nicht eingeschoben werden!"),
    INVALID_MOVEPOS("Zu dieser Position können sie sich nicht bewegen"),
    CANNOT_INTERACT("Den Spielstand können sie nur speichern, wenn sie dran sind und noch" +
            "nicht eingeschoben haben."),

    /**
     * Gewinner-Nachricht.
     */
    WINNER_MESSAGE_TEMPLATE("%s hat gewonnen!");

    /**
     * Die Nachricht.
     */
    private final String message;

    /**
     * Konstruktor.
     *
     * @param message Nachricht
     */
    Message(String message) {
        this.message = message;
    }

    /**
     * Liefert die Nachricht zu der Aufzählung.
     *
     * @return Die Nachricht
     */
    public String getMessage() {
        return message;
    }
}
