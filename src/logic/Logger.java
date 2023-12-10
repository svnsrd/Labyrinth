package logic;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse repräsentiert einen Logger, welcher den Spielverlauf an die Ausgabe kommuniziert und
 * diese Einträge in eine Datei <i>log.txt</i> speichert. Das Verzeichnis wird beim Programmstart
 * gegebenenfalls erzeugt und befindet sich dann am vom Verzeichnis aus, aus welchem das Programm
 * gestartet ist, im <i>./log</i>-Verzeichnis.
 * <p>
 * Ein Logger hat nur eine Instanz gemäß dem <i>Singleton-Pattern</i> und kann innerhalb der Klassen
 * mit <i>getInstance()</i> aufgerufen werden.
 *
 * @author svnsrd  [Repo 37]
 * @version 06.08.2021
 */
public class Logger {

    /**
     * Instanz des Loggers.
     */
    private static final Logger logger = new Logger();

    /**
     * Schreiber des Loggers.
     */
    private PrintWriter writer;

    /**
     * Aktuelles Verzeichnis aus dem dieses Programm heraus gestartet wurde.
     */
    private final String userDir = System.getProperty("user.dir");

    /**
     * Pfad der Datei und Name der Datei.
     */
    private final String logName = userDir + '/' + "log/" + "log.txt";

    /**
     * Vorlagen für die verschiedenen Logging-Einträge.
     */
    public static final String PLAYER_CREATION = "player%o is %s, has treasures %s";
    public static final String PLAYER_NOTINVOLVED = "player%o %s is not involved";
    public static final String PLAYER_ONTURN = "player%o is on the turn";
    public static final String SHIFT = "player%o shifts %s %s with waycard {%s}";
    public static final String MOVE = "player%o moves from %s to %s";
    public static final String PLAYER_WON = "player%o has won!";
    public static final String TREASURE_COLLECTED = "player%o collected Treasure %o (%s), next " +
            "treasures %s";
    public static final String SHIFT_PLAYER_AFFECTED = "player%o is affected of shift, changes " +
            "from %s to %s";

    /**
     * Privater Konstruktor, welcher die <i>./log/log.txt</i> erzeugt.
     */
    private Logger() {
        createLogFile();
    }

    /**
     * Liefert die einzige Instanz des Loggers.
     *
     * @return Instanz des Loggers
     */
    public static Logger getInstance() {
        return logger;
    }

    /**
     * Erzeugt eine Nachricht mit einer übergebenen Vorlage mit übergebenen Parametern.
     *
     * Hierbei handelt es sich bei der Nachricht {@code message}, um eine Vorlage, welche im
     * Rahmen von {@code String.format} mit den übergebenen Parametern {@code objects} gefüllt wird.
     *
     * @param message String-Vorlage
     * @param objects Variable Anzahl von Parametern für die String-Vorlage
     */
    public void log(String message, Object... objects) {
        String generatedMessage = String.format(message, objects);

        logToFileAndStdout(generatedMessage);
    }

    /**
     *
     * @param message Übergebene Nachricht
     */
    public void log(String message) {
        logToFileAndStdout(message);
    }

    /**
     * Erzeugt eine Komma-separierte Liste der übergebenen Schätze {@code treasures}.
     *
     * @param treasures Schätze
     * @return Komma-separierte Liste der übergebenen Schätze
     */
    public String treasuresToString(Queue<Treasure> treasures) {
        final String separator = ", ";
        StringBuilder sb = new StringBuilder();

        if (!treasures.isEmpty()) {
            for (Treasure treasure : treasures) {
                sb.append(treasure.ordinal()).append(", ");
            }

            sb.setLength(sb.length() - separator.length());
        }

        return sb.toString();
    }

    /**
     * Loggt die übergebene Nachricht {@code message} in die Datei <i>./log/log.txt</i> und
     * kommuniziert diesen Eintrag an die Ausgabe.
     *
     * @param message Übergebene Nachricht
     */
    private void logToFileAndStdout(String message) {
        createWriter();
        System.out.println(message);
        writer.println(message);
        writer.flush();
        writer.close();
    }

    /**
     * Erzeugt einen {@link PrintWriter} und weist ihm {@code writer} zu.
     */
    private void createWriter() {
        try {
            FileWriter fileWriter = new FileWriter(logName, true);
            writer = new PrintWriter(fileWriter, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hilfsmethode, welche eine <i>log.txt</i> Datei im Verzeichnis <i>./log</i> erzeugt.
     */
    private void createLogFile() {
        File logFolder = new File(userDir + '/' + "log");
        if (!logFolder.exists()) {
            System.out.println("Creating new log directory in " + userDir);
            logFolder.mkdir();
        } else {
            File file = new File(logName);
            file.delete();
        }
    }
}
