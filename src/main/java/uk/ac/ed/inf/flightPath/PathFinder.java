package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.model.Node;

import java.util.*;

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
            Node current = openSet.poll();
            closedSet.add(current);
            if (lngLatHandler.isCloseTo(current.position, end.position)){
                return reconstructPath(current, order);
            }
            // Generate successors (neighbors) of the current node
            for (double angle : angles) {
                LngLat successorPosition = lngLatHandler.nextPosition(current.position, angle);

                if ((!closedSet.contains(new Node(successorPosition)))
                        && (!isInNoFlyZone(successorPosition))
                        ){
                    double tentativeG = current.g + SystemConstants.DRONE_MOVE_DISTANCE;

                    Node successorExists = findSuccessor(successorPosition);
                    if (successorExists != null){
                        if (tentativeG < successorExists.g){
                            // Update the costs and parent node
                            successorExists.parent = current;
                            successorExists.g = tentativeG;
                            successorExists.h = lngLatHandler.distanceTo(successorExists.position, end.position);
                            successorExists.f = successorExists.g + successorExists.h;
                            successorExists.angle = angle;
                        }
                    } else {
                        Node successor = new Node(successorPosition);
                        successor.parent = current;
                        successor.g = tentativeG;
                        successor.h = lngLatHandler.distanceTo(successorPosition, end.position);
                        successor.f = successor.g + successor.h;
                        successor.angle = angle;

                        openSet.add(successor);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if the successor node is in a no fly zone
     * @param successorPosition the position of the successor node
     * @return true if the successor node is in a no fly zone, false otherwise
     */
    private boolean isInNoFlyZone(LngLat successorPosition) {
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(successorPosition, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCentralValid(LngLat successorPosition, LngLat currentPosition) {
        return (!lngLatHandler.isInRegion(currentPosition, centralArea)) && (lngLatHandler.isInRegion(successorPosition, centralArea));
    }

    /**
     * Find a successor node in the open set
     * @param position the position of the successor node
     * @return the successor node
     */
    private Node findSuccessor(LngLat position) {
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Node> iterator = openSet.iterator();

        Node find = null;
        while (iterator.hasNext()) {
            Node next = iterator.next();
            if(next.getPosition() == position){
                find = next;
                break;
            }
        }
        return find;
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
