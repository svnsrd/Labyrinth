package logic.path;

import logic.Position;
import logic.Direction;
import logic.tile.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 * <p>
 * Diese Klasse stellt Operationen zur Pfadfindung bereit.
 *
 * @author svnsrd  [Repo 37]
 * @version 08.08.2021
 */
public class PathUtil {



    /**
     * Pfadfindung für die KI.
     * <p>
     * Diese Methode sucht einen Pfad von der übergebenen Startposition ({@code startPos}) zu
     * einer übergebenen Endposition ({@code targetPos}) auf dem übergebenen Array ({@code field}).
     * Dabei ist nur relevant, ob die Position {@code targetPos} erreicht werden kann.
     * <p>
     * Der Algorithmus zur Pfadfindung basiert auf dem <i>A*-Algorithmus</i>, welche mit einer
     * Heuristik arbeitet.
     *
     * <b>Heuristik: (p1.x - p2.x) + (p1.y - p2.y)</b>
     *
     * @param field     Das Feld auf dem die Pfadfindung stattfindet
     * @param startPos  Startposition von der die Pfadfindung beginnt
     * @param targetPos Zielposition
     * @return Mapping von Distanzen auf entsprechende Positionen des Feldes {@code field}
     */
    public static Map<Integer, Position> aStarSearch(Tile[][] field, Position startPos,
                                                     Position targetPos) {

        // Ergebnis-Map, welche sortierte Distanz-Positions-Paare enthält
        NavigableMap<Integer, Position> distances = new TreeMap<>();

        // Bereits betrachtete Positionen des Spielfeldes
        Set<Position> closedList = new HashSet<>();

        // Comparator damit die Positionen gewählt werden die bereits nahe der Zielposition sind
        final Comparator<Position> distanceComparator = Comparator.comparingInt(
                p -> p.distanceTo(targetPos));

        // Noch zu betrachtende Positionen
        Queue<Position> openList = new PriorityQueue<>(distanceComparator);

        // Startposition zur offenen Liste hinzufügen
        openList.add(startPos);

        // Solange noch zu betrachtende Knoten existieren
        while (!openList.isEmpty()) {

            // Knoten mit der geringsten Distanz zum Ziel erhalten
            Position currentPos = openList.poll();

            // Knoten zur closedList hinzufügen
            closedList.add(currentPos);

            // Distanz berechnen und zur Ergebnis-Map hinzufügen
            int distance = currentPos.distanceTo(targetPos);
            distances.put(distance, currentPos);

            // Haben wir das Ziel erreicht geben wir die closedList zurück
            //if (currentPos.equals(targetPos)) {
            //    return distances;
            //}

            // Generierung der benachbarten Positionen (Kinder)
            List<Position> neighbours = generateValidNeighbours(field, currentPos, closedList);

            // Jeden generierten (validen) Nachbar-Knoten durchlaufen und Werte aktualisieren
            for (Position neighbourPos : neighbours) {

                // Nachbar schon erkundet worden?
                if (!closedList.contains(neighbourPos)) {

                    // Knoten zu der offenen Liste hinzufügen
                    openList.add(neighbourPos);
                }
            }
        }

        return distances;
    }

    /**
     * Diese Operation generiert von einer Startposition ({@code startPos}) alle möglichen Pfade
     * inklusive der jeweiligen Speicherung von Elternknoten, sodass eine Konstruktion eines
     * Pfades über {@link PathNode} möglich ist.
     *
     * @param field Das Spielfeld (ohne Umrandung)
     * @param startPos Position von der aus alle möglichen Positionen berechnet werden
     * @return 2D-Array, welches alle
     */
    public static PathNode[][] getPossiblePositions(Tile[][] field, Position startPos) {

        // Breite/Länge des Spielfeldes
        final int colcount = field.length;
        final int rowcount = field[0].length;

        // Speichern aller bereits besuchten Gängekarten während der Berechnung
        Set<Position> visited = new HashSet<>();

        // Speichern von Knoten an validen Positionen
        PathNode[][] validPositions = new PathNode[colcount][rowcount];

        // Aktuelle Spielerposition als "bereits besucht" markieren
        visited.add(startPos);

        // Spieler darf auch stehen bleiben
        validPositions[startPos.getX()][startPos.getY()] = new PathNode(startPos, null);

        // Warteschlange, welche alle Gängekarten enthält, die noch geprüft werden müssen
        Queue<PathNode> tiles = new LinkedList<>();

        // Position, auf der der Spieler steht, ist valide
        tiles.add(validPositions[startPos.getX()][startPos.getY()]);


        while (!tiles.isEmpty()) {
            // Aktuellen Knoten zuweisen und gleichzeitig entfernen
            PathNode currNode = tiles.poll();

            // Nachbars-Knoten generieren
            List<Position> neighbours = generateValidNeighbours(field, currNode.getPosition(), visited);

            // Durchlaufen aller generierten Nachbarn zur aktuellen Position
            for (Position neighbourPos : neighbours) {
                // Pfad-Knoten erzeugen
                PathNode neighbourNode = new PathNode(neighbourPos, currNode);

                // Nachbarn als bereits besucht markieren
                visited.add(neighbourPos);

                // Knoten zu den erreichbaren Positionen hinzufügen
                validPositions[neighbourPos.getX()][neighbourPos.getY()] = neighbourNode;

                // Knoten zu der Warteschlange hinzufügen
                tiles.add(neighbourNode);
            }
        }

        return validPositions;
    }

    /**
     * Generiert eine Liste von <b>betretbaren</b> benachbarten Positionen.
     *
     * @param field   Das Spielfeld
     * @param currPos Position von der aus die betretbaren benachbarten Positionen ermittelt werden
     *                sollen
     * @param visited Array, welche die bereits betrachteten Positionen beinhaltet
     * @return Liste von <b>betretbaren</b> benachbarten Positionen
     */
    private static List<Position> generateValidNeighbours(Tile[][] field, Position currPos,
                                                          Set<Position> visited) {
        List<Position> neighbours = new ArrayList<>();

        // Das Gängekarten-Objekt auf der Position erhalten
        Tile currTile = field[currPos.getX()][currPos.getY()];

        // Offene Richtungen der aktuellen Gängekarten ermitteln
        Direction[] openDirsOfCurrTile = currTile.getOpenDirections();

        for (Direction dir : openDirsOfCurrTile) {
            // x- und y-Koordinate des aktuellen Nachbarn ermitteln
            int x = currPos.getX() + dir.getDirPos().getX();
            int y = currPos.getY() + dir.getDirPos().getY();

            // Überprüfen, ob die Koordinaten noch im Spielfeld sind
            if (isValid(x, y, field.length, field[0].length)) {
                // Gängekarten-Objekt des Nachbarn erhalten
                Tile toTile = field[x][y];

                // Ermitteln der entgegengesetzten Richtung von der aktuellen Richtung aus
                Direction oppositeDir = dir.getOppositeDir();

                // Position erzeugen
                Position currNeighbourPos = new Position(x, y);

                // Kann der Nachbar betreten werden und ist noch nicht besucht
                if (toTile.canBeEnteredFrom(oppositeDir) && !visited.contains(currNeighbourPos)) {
                    neighbours.add(currNeighbourPos);
                }
            }
        }

        return neighbours;
    }

    /**
     * Überprüft, ob die Koordinaten sich noch im Spielfeld befinden.
     *
     * @param row Reihe
     * @param col Spalte
     * @return True, wenn die Position sich im Spielfeld befindet, ansonsten false.
     */
    private static boolean isValid(int row, int col, int maxCols, int maxRows) {
        return row >= 0 && row < maxRows && col >= 0 && col < maxCols;
    }
}
