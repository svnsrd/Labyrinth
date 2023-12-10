package logic.data;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Ausnahme für einen fehlerhaften Spielstand.
 *
 * @author svnsrd  [Repo 37]
 * @version 08.08.2021
 */
public class InvalidGameDataException extends Exception {

    /**
     * Erzeugt eine Exception und kommuniziert die übergebene {@code message} an <b>stderr</b>.
     *
     * @param message Nachricht, die an <i>stderr</i> kommuniziert wird
     */
    public InvalidGameDataException(String message) {
        super(message);
    }
}
