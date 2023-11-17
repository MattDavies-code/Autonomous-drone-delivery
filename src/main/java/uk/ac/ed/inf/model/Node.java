package uk.ac.ed.inf.model;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Model of a node
 * Utilised in A* pathfinding algorithm
 */
public class Node {
    private final LngLat position;
    public Node parent;
    public double costFromStart;
    public double heuristicCost;
    public double angle;

    /**
     * Constructor for a node
     * @param position
     * @param costFromStart
     * @param heuristicCost
     */
    public Node(LngLat position, Node parent, double costFromStart, double heuristicCost, double angle) {
        this.position = position;
        this.parent = parent;
        this.costFromStart = costFromStart;
        this.heuristicCost = heuristicCost;
        this.angle = angle;
    }

    /**
     * Getter methods
     * @return position, costFromStart, heuristicCost, parent
     */
    public LngLat getPosition() {
        return position;
    }

    public Node getParent() {
        return parent;
    }

    public double getCostFromStart() {
        return costFromStart;
    }

    public double getHeuristicCost() {
        return heuristicCost;
    }

    public double getAngle() {
    	return angle;
    }


    /**
     * Setter methods
     * @param costFromStart, heuristicCost, parent
     */
    public void setCostFromStart(double costFromStart) {
        this.costFromStart = costFromStart;
    }

    public void setHeuristicCost(double heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setAngle(double angle) {
    	this.angle = angle;
    }

}
