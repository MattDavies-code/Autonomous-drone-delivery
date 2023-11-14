package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.model.Node;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Flight Path Calculator
 */
//@Service
public class FlightPathCalculator {

    // Constants
    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    private static final double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};


    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final Order[] orders;
    private final Restaurant[] restaurants;
    private final LngLatHandler lngLatHandler;

    public FlightPathCalculator(Order[] validOrders, Restaurant[] restaurants, NamedRegion centralArea, NamedRegion[] noFlyZones) {
        this.orders = validOrders;
        this.restaurants = restaurants;
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
        this.lngLatHandler = new LngLatHandler();
    }

    /**
     * Make flight paths for orders in the exact sequence as they are received
     * @return HashMap of flight paths
     */
    public HashMap<String, ArrayList<Move>> flightPathList() {
        HashMap<String, ArrayList<Move>> flightPaths = new HashMap<>();
        // Flightpaths to be stored and reused
        HashMap<Restaurant, ArrayList<Move>> flightPathToRestaurant = new HashMap<>();
        // Calculate the flightpath for all valid orders in the exact sequence you receive them
        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                try {
                    // Check if flightpath to restaurant already exists
                    Restaurant currentRestaurant = getRestaurant(order);
                    //System.out.println("Current Restaurant: " + currentRestaurant.name());
                    if (flightPathToRestaurant.containsKey(currentRestaurant)) {
                        ArrayList<Move> flightPath = flightPathToRestaurant.get(currentRestaurant);

                        // Add the flightpath and reverse flightpath to the flightpaths list
                        flightPaths.put(order.getOrderNo(), flightPath);
                        Collections.reverse(flightPath);
                        flightPaths.put(order.getOrderNo(), flightPath);

                    } else {
                        // Get end position for the order
                        LngLat endPosition = currentRestaurant.location();

                        // Calculate the flight path for a single order
                        ArrayList<Move> flightPath = calculateFlightPath(order, appletonTower, endPosition);

                        // Add the flightpath and reverse flightpath to the flightpaths list
                        flightPaths.put(order.getOrderNo(), flightPath);
                        Collections.reverse(flightPath);
                        flightPaths.put(order.getOrderNo(), flightPath);

                        // Add flightpath to restaurant to flightpath list
                        flightPathToRestaurant.put(currentRestaurant, flightPath);

                    }
                    order.setOrderStatus(OrderStatus.DELIVERED);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
        return flightPaths;
    }


    /**
     * Calculate flight path for single order using A* algorithm
     * @param start the start position of the drone (Appleton Tower)
     * @param end the end position of the drone (Restaurant)
     * @return List of moves from start to end position
     */
    private ArrayList<Move> calculateFlightPath(Order order, LngLat start, LngLat end) {
        // Heuristic function is Euclidean distance as this is admissible
        // Create the start and goal nodes
        Node startNode = new Node(start, 0, lngLatHandler.distanceTo(start, end));

        // Initialize open and closed sets
        ArrayList<Node> closedSet = new ArrayList<>();
        HashMap<Node, Double> openSet = new HashMap<>(generateSuccessors(startNode, end, closedSet));
        // Initialize the start node as the current node
        Node current = startNode;

        // A* algorithm
        while (!lngLatHandler.isCloseTo(current.getPosition(), end)) {
            // Get the node with the lowest total cost from the open set
            current = getLowestCostNode(openSet);

            // Remove the current node from the open set and add it to the closed set
            openSet.remove(current);
            closedSet.add(current);

            // Generate successors (neighbors) of the current node
            HashMap<Node, Double> successors = generateSuccessors(current, end, closedSet);
            if (successors.isEmpty()) {
                // exit the loop if no successors are generated
                break;
            }
            for (Node successor : successors.keySet()) {
                // Add successor to open set if not already present or update if better
                openSet.compute(successor, (key, value) -> (value == null || successors.get(successor) < value) ? successors.get(successor) : value);
            }
        }
        // If the goal is reached, reconstruct the path and return it
        return reconstructPath(current, order);
    }

    /**
     * Generate successors (neighbors) of the current node
     * g = costFromStart = cost from start to current node
     * h = heuristicCost = heuristic cost from current node to goal node
     * @param current   the current node
     * @param end  the restaurant location
     * @param closedSet the closed set
     * @return a list of successor nodes
     */
    private HashMap<Node, Double> generateSuccessors(Node current, LngLat end, ArrayList<Node> closedSet) {

        HashMap<Node, Double> successors = new HashMap<>();
        LngLat currentPos = current.getPosition();

        for (double angle : angles) {
            LngLat nextPos = lngLatHandler.nextPosition(currentPos, angle);

            Node successor = new Node(nextPos, 0, 0);
            // Skip successor if visited before
            if (closedSet.contains(successor))  {
                continue;
            }

            // Skip successor if current position is in central area and next position out of central area
            //if (lngLatHandler.isInCentralArea(currentPos, centralArea) || !lngLatHandler.isInCentralArea(nextPos, centralArea)) {
            //    continue;
            //}

            // Skip successor if next position is inside a no-fly zone
            boolean isInsideNoFlyZone = !Arrays.stream(noFlyZones).noneMatch(noFlyZone -> lngLatHandler.isInRegion(nextPos, noFlyZone));
            if (isInsideNoFlyZone) {
                continue;
            }
            double costFromStart = current.costFromStart + SystemConstants.DRONE_MOVE_DISTANCE;
            double heuristicCost = lngLatHandler.distanceTo(nextPos, end);
            //successor.costFromStart = costFromStart;
            //System.out.println("Cost from start: " + costFromStart);
            successor.heuristicCost = heuristicCost;
            successor.setParent(current);
            successors.put(successor, costFromStart + heuristicCost);
        }
        return successors;
    }

    /**
     * Get the node with the lowest total cost from the set
     *
     * @param nodes the Hashmap of nodes to search
     * @return the node with the lowest total cost
     */
    private Node getLowestCostNode(HashMap<Node, Double> nodes) {
        return Collections.min(nodes.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
    }

    /**
     * Reconstruct the path from the goal node to the start node
     *
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
            int angle = calculateAngle(from, to);

            // Create a Move object and add it to the path
            path.add(new Move(order.getOrderNo(), from.lng(), from.lat(), angle, to.lng(), to.lat()));

            // Move to the parent node
            current = current.getParent();
        }

        // Reverse the path to get the correct order
        Collections.reverse(path);
        // Add a hover move at the start of the path
        path.add(new Move(order.getOrderNo(), appletonTower.lng(), appletonTower.lat(), 999, appletonTower.lng(), appletonTower.lat()));
        return path;
    }

    /**
     * Calculate the angle between two positions
     *
     * @param from the starting position
     * @param to   the ending position
     * @return the angle in degrees
     */
    private int calculateAngle(LngLat from, LngLat to) {
        // Calculate the angle based on the difference in longitude and latitude
        double deltaX = to.lng() - from.lng();
        double deltaY = to.lat() - from.lat();
        return (int) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    /**
     * Gets the restaurants' location for the order
     * @param order the current order being delivered
     * @return restaurantLocation
     */
    private Restaurant getRestaurant(Order order) {
        Restaurant restaurant;
        for (Restaurant currentRestaurant : restaurants) {
            // Check if the restaurant has the first pizza in the order
            if (Arrays.asList(currentRestaurant.menu()).contains(order.getPizzasInOrder()[0])) {
                // Get the location of the restaurant for the order
                restaurant = currentRestaurant;
                return restaurant;
            }
        }
        return null;
    }


}