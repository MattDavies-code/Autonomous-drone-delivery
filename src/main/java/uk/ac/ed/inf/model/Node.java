package uk.ac.ed.inf.model;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Model of a node
 * Utilised in A* pathfinding algorithm
 */
public class Node {
    private final LngLat position;
    public double costFromStart;
    public double heuristicCost;
    private Node parent;

    /**
     * Constructor for a node
     * @param position
     * @param costFromStart
     * @param heuristicCost
     */
    public Node(LngLat position, double costFromStart, double heuristicCost) {
        this.position = position;
        this.costFromStart = costFromStart;
        this.heuristicCost = heuristicCost;
    }

    /**
     * Getter methods
     * @return position, costFromStart, heuristicCost, parent
     */
    public LngLat getPosition() {
        return position;
    }

    public double getCostFromStart() {
        return costFromStart;
    }

    public double getHeuristicCost() {
        return heuristicCost;
    }

    public double getTotalCost() {
        return costFromStart + heuristicCost;
    }

    public Node getParent() {
        return parent;
    }

    /**
     * Setter method for the parent node
     * @param parent The parent node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
}
