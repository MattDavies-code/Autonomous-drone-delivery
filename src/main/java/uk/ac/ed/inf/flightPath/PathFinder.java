package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.model.Node;

import java.util.*;

/**
 * PathFinder class that calculates the flight path for a single order using the A* algorithm
 */
public class PathFinder {

    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final LngLatHandler lngLatHandler;

    public PathFinder(NamedRegion[] noFlyZones, NamedRegion centralArea) {
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.lngLatHandler = new LngLatHandler();
    }

    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    private static final double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
    static PriorityQueue<Node> openSet; // frontier
    static HashSet<Node> closedSet; // visited

    /**
     * Calculate flight path for single order using A* algorithm
     * @param startPosition the start position of the drone (Appleton Tower)
     * @param endPosition the end position of the drone (Restaurant)
     * @return List of moves from start to end position
     */
    public ArrayList<Move> findPath(LngLat startPosition, LngLat endPosition, Order order) {
        openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
        closedSet = new HashSet<>();

        Node start = new Node(startPosition);
        start.angle = 999.0;
        Node end = new Node(endPosition);
        end.angle = 999.0;

        openSet.add(start);
        while (!openSet.isEmpty()) {

            // Get the node with the lowest f value from the priority queue
            Node current = openSet.poll();
            if (current == null) {
                // Handles the case where openSet.poll() returns null (openSet is empty)
                continue;
            }
            closedSet.add(current);

            // If goal is reached, reconstruct the path and return it
            if (lngLatHandler.isCloseTo(current.position, end.position)){
                return reconstructPath(current, order);
            }

            // Generate successors (neighbors) of the current node
            for (double angle : angles) {
                LngLat successorPosition = lngLatHandler.nextPosition(current.position, angle);
                Node successor = new Node(successorPosition);

                if ((!closedSet.contains(successor))
                        && (!isInNoFlyZone(successorPosition))
                        && (!goingBackIntoCentral(successorPosition, current.position))){

                    // Calculate the tentative g value which is the actual distance the drone has travelled
                    // Weighted by 0.75 to encourage the drone to move in a straight line
                    double tentativeG = current.getG() + SystemConstants.DRONE_MOVE_DISTANCE;

                    Node successorExists = findSuccessor(successor);

                    if (successorExists != null){
                        if (tentativeG < successorExists.g){
                            // Update the costs and parent node
                            successorExists.setParent(current);
                            successorExists.setG(tentativeG);
                            successorExists.setH(lngLatHandler.distanceTo(successorPosition, end.position));
                            // Heuristic weighted by 1.25 to encourage the drone to move towards the goal
                            successorExists.setF(successorExists.g + successorExists.h * 1.25);
                            successorExists.setAngle(angle);
                        }
                    } else {
                        // Update the costs and parent node
                        successor.setParent(current);
                        successor.setG(tentativeG);
                        successor.setH(lngLatHandler.distanceTo(successorPosition, end.position));
                        // Heuristic weighted by 1.25 to encourage the drone to move towards the goal
                        successor.setF(successor.g + successor.h * 1.25);
                        successor.setAngle(angle);

                        openSet.add(successor);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if the successor node is in a no-fly zone
     * @param successorPosition the position of the successor node
     * @return true if the successor node is in a no-fly zone, false otherwise
     */
    private boolean isInNoFlyZone(LngLat successorPosition) {
        if (noFlyZones == null) {
            return false;
        }

        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(successorPosition, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that once the drone has left the central area, it does not go back into it
     * @param successorPosition the position of the successor node
     * @param currentPosition the position of the current node
     * @return true if the successor node is in the central area and has not left yet, false otherwise
     */
    private boolean goingBackIntoCentral(LngLat successorPosition, LngLat currentPosition) {
        boolean isCurrentOutside = !lngLatHandler.isInRegion(currentPosition, centralArea);
        boolean isSuccessorInside = lngLatHandler.isInRegion(successorPosition, centralArea);
        return isCurrentOutside && isSuccessorInside;
    }

    /**
     * Find a successor node in the open set
     * @param successor the successor node
     * @return the successor node
     */
    private Node findSuccessor(Node successor) {
        if (openSet == null) {
            return null;
        }
        for (Node node : openSet) {

            // Utilises 'equals' method in Node class
            if (node.equals(successor)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Reconstruct the path from the goal node to the start node
     * @param goalNode the goal node
     * @return the reconstructed path as a list of moves
     */
    private ArrayList<Move> reconstructPath(Node goalNode, Order order) {
        ArrayList<Move> path = new ArrayList<>();

        // Add a hover move at the end of the path
        path.add(new Move(order.getOrderNo(), goalNode.getPosition().lng(), goalNode.getPosition().lat(), 999, goalNode.getPosition().lng(), goalNode.getPosition().lat()));
        Node current = goalNode;

        while (current.getParent() != null) {

            // Construct a Move object from the current node and its parent
            LngLat from = current.getParent().getPosition();
            LngLat to = current.getPosition();

            // Create a Move object and add it to the path
            path.add(new Move(order.getOrderNo(), from.lng(), from.lat(), current.getAngle(), to.lng(), to.lat()));

            // Move to the parent node
            current = current.getParent();
        }

        // Add a hover move at the start of the path
        path.add(new Move(order.getOrderNo(), appletonTower.lng(), appletonTower.lat(), 999, appletonTower.lng(), appletonTower.lat()));
        Collections.reverse(path);
        return path;
    }
}
