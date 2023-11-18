package uk.ac.ed.inf.model;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

/**
 * Model of a node
 * Utilised in A* pathfinding algorithm
 */
public class Node implements Comparable<Node> {
    public LngLat position;
    public Node parent;
    public double g;
    public double h;
    public double f;
    public double angle;

    /**
     * Constructor for a node
     * @param position
     */
    public Node(LngLat position) {
        this.position = position;
        parent = null;
        g = 0;
        h = 0;
        f = 0;
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }

        if(!(obj instanceof Node node)){
            return false;
        }
        return node.position.equals(position);
    }

    public int hashCode() {
        return Objects.hash(position, g, parent);
    }

    public int compareTo(Node node) {
        if (this.f < node.f) {
            return -1;
        } else if (this.f > node.f) {
            return 1;
        } else {
            return 0;
        }

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

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public double getAngle() {
    	return angle;
    }


    /**
     * Setter methods
     * @param costFromStart, heuristicCost, parent
     */
    public void setG(double g) {
        this.g = g;
    }
    public void setH(double h) {
        this.h = h;
    }
    public void setF(double f) {
        this.f = f;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setAngle(double angle) {
    	this.angle = angle;
    }

}
