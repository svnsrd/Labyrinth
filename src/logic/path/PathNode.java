package logic.path;

import logic.Position;

import java.util.List;
import java.util.Stack;

/**
 * Fachhochschule Wedel, Sommersemester 2021
 * Programmierpraktikum - Labyrinth
 *
 * Diese Klasse repräsentiert einen Pfadknoten, welche im Rahmen der Pfadfindung zur Bewegung
 * einer Spielerfigur in der {@link gui.JavaFXGUI GUI} genutzt wird.
 *
 * @author svnsrd 
 * @version 01.08.2021
 */
public class PathNode {

    /**
     * Position des Pfadknotens.
     */
    private final Position position;

    /**
     * Elternknoten dieses Pfadknotens.
     * Dieser entspricht der Position, von der eine Spielerfigur kommt, wenn sie diesen Pfadknoten
     * betritt.
     */
    private final PathNode parent;

    /**
     * Erzeugt einen Pfadknoten.
     *
     * @param position Position des Pfadknotens
     * @param parent   Elternknoten des Pfadknotens
     */
    public PathNode(Position position, PathNode parent) {
        this.position = position;
        this.parent = parent;
    }

    /**
     * Erzeugt einen Pfad, welcher aus einer {@link List Liste} von {@link Position Positionen}
     * besteht.
     *
     * @return Liste aus Positionen die den Pfad repräsentiert (Rückwärts)
     */
    public List<Position> createPath() {

        // Liste, welche den Pfad beinhaltet
        List<Position> path = new Stack<>();

        // Dieser Knoten ist die erste Position
        path.add(this.getPosition());

        // Durchlaufen der jeweiligen Elternknoten jedes Pfadknotens, bis kein Elternknoten
        // gefunden werden kann
        PathNode currNode = this.parent;
        while (currNode != null) {
            path.add(currNode.getPosition());
            currNode = currNode.parent;
        }

        return path;
    }

    /**
     * Liefert die Position des Pfadknotens.
     *
     * @return Position dieses Pfadknotens
     */
    public Position getPosition() {
        return position;
    }
}
