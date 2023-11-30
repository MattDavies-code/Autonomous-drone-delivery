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
     * @param position of the node
     */
    public Node(LngLat position) {
        this.position = position;
        parent = null;
        g = 0;
        h = 0;
        f = 0;
    }

    /**
     * Overriding equals method to compare nodes
     * @param obj the other node to compare the node to
     * @return true if the node is equal to the object
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }

        if(!(obj instanceof Node node)){
            return false;
        }
        return node.position.equals(position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, g, parent);
    }

    /**
     * Used to order priority queue
     * @param node the other node to compare the node to
     * @return -1 if the node is less than the object, 1 if the node is greater than the object, 0 if they are equal
     */
    @Override
    public int compareTo(Node node) {
        return Double.compare(this.f, node.f);
    }

    /**
     * Getter methods
     * @return position, parent, g, h, f
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

    public double getAngle() {
    	return angle;
    }


    /**
     * Setter methods
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
