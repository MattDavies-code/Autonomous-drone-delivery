package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.model.Node;

import java.util.*;

/**
 * Flight Path Calculator
 */
//@Service
public class FlightPathCalculator {

    // Constants
    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);
    private final Double Hover = 999.0;

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
                    if (flightPathToRestaurant.containsKey(currentRestaurant)) {
                        ArrayList<Move> flightPath = flightPathToRestaurant.get(currentRestaurant);

                        // Add flightpath to restaurant to flightpath list
                        flightPaths.put(order.getOrderNo(), flightPath);
                        // Reverse and add the flightpath
                        ArrayList<Move> reverseFlightPath = new ArrayList<>(flightPath);
                        Collections.reverse(reverseFlightPath);
                        flightPaths.put(order.getOrderNo(), reverseFlightPath);
                    } else {
                        // Get start and end position for the order
                        LngLat startPosition = appletonTower;
                        LngLat endPosition = currentRestaurant.location();

                        // Calculate the flight path for a single order
                        ArrayList<Move> flightPath = calculateFlightPath(order, startPosition, endPosition);

                        // Add the flightpath and reverse flightpath to the flightpaths list
                        flightPaths.put(order.getOrderNo(), flightPath);
                        ArrayList<Move> reverseFlightPath = new ArrayList<>(flightPath);
                        Collections.reverse(reverseFlightPath);
                        flightPaths.put(order.getOrderNo(), reverseFlightPath);

                        // Add flightpath to restaurant to flightpath list
                        flightPathToRestaurant.put(currentRestaurant, flightPath);
                    }
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
        // Must spend one move hovering (with angle 999) when closeTo(restaurantLocation)
        // Must spend one move hovering (with angle 999) when closeTo(endPosition)
        // Once back inside the central area the drone must not leave again until it has delivered the pizzas to the start position (appleton Tower)
        // The drone must not fly over any no-fly zones
        // utilise isCloseTo() method in LngLatHandler
        // utilise distanceTo() method in LngLatHandler
        // utilise isInRegion() method in LngLatHandler
        // utilise nextPosition() method in LngLatHandler
        // can move in 16 directions, angle is in degrees
        // hover is 999
        // Initialize open and closed sets
        Set<Node> openSet = new HashSet<>();
        Set<Node> closedSet = new HashSet<>();


        // Heuristic function is Euclidean distance as this is admissible
        // Create the start and goal nodes
        Node startNode = new Node(start, 0, lngLatHandler.distanceTo(start, end));
        Node goalNode = new Node(end, 0, 0);

        // Add the start node to the open set
        openSet.add(startNode);

        // Initialize the start node as the current node
        Node current = startNode;

        // A* algorithm
        while (!openSet.isEmpty()) {
            // Get the node with the lowest total cost from the open set
            current = getLowestCostNode(openSet);

            // Remove the current node from the open set and add it to the closed set
            openSet.remove(current);
            closedSet.add(current);

            // If the goal is reached, reconstruct the path and return it
            if (current.getPosition().equals(goalNode.getPosition())) {
                return reconstructPath(current);
            }

            // Generate successors (neighbors) of the current node
            List<Node> successors = generateSuccessors(current, goalNode, order);

            for (Node neighbor : successors) {
                // Skip if the neighbor is in the closed set
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // Calculate tentative cost from start to neighbor
                double tentativeCost = current.getCostFromStart() + lngLatHandler.distanceTo(current.getPosition(), neighbor.getPosition());

                // If neighbor is not in the open set or the tentative cost is lower
                if (!openSet.contains(neighbor) || tentativeCost < neighbor.getCostFromStart()) {
                    // Set the parent of the neighbor to the current node
                    neighbor.setParent(current);

                    // Update cost information
                    neighbor.costFromStart = tentativeCost;
                    neighbor.heuristicCost = lngLatHandler.distanceTo(neighbor.getPosition(), end);

                    // Add the neighbor to the open set if not already present
                    openSet.add(neighbor);
                }
            }
        }

        // If open set is empty and goal is not reached, return an empty path
        return new ArrayList<>();
    }

    /**
     * Generate successors (neighbors) of the current node
     * @param current   the current node
     * @param goalNode  the goal node
     * @param order     the order being delivered
     * @return a list of successor nodes
     */
    private List<Node> generateSuccessors(Node current, Node goalNode, Order order) {

        List<Node> successors = new ArrayList<>();
        LngLat currentPos = current.getPosition();

        // Adjust these angles based on your drone's movement capabilities
        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

        for (double angle : angles) {
            LngLat nextPos = lngLatHandler.nextPosition(currentPos, angle);
            double costFromStart = current.getCostFromStart() + lngLatHandler.distanceTo(current.getPosition(), nextPos);
            double heuristicCost = lngLatHandler.distanceTo(nextPos, goalNode.getPosition());

            // Check additional rules
            if (shouldHover(currentPos, nextPos, goalNode.getPosition(), order)) {
                // Add a move with angle 999 (hovering)
                successors.add(new Node(nextPos, costFromStart + 1, heuristicCost));
            } else {
                successors.add(new Node(nextPos, costFromStart, heuristicCost));
            }
        }
        return successors;
    }

    /**
     * Check if the drone should hover at the current position
     *
     * @param currentPos the current position of the drone
     * @param nextPos    the next position the drone is considering
     * @param goalPos    the goal position of the drone
     * @param order      the order being delivered
     * @return true if the drone should hover, false otherwise
     */
    private boolean shouldHover(LngLat currentPos, LngLat nextPos, LngLat goalPos, Order order) {
        // Must spend one move hovering (with angle 999) when closeTo(restaurantLocation)
        if (lngLatHandler.isCloseTo(nextPos, getRestaurant(order).location())) {
            return true;
        }
        // Must spend one move hovering (with angle 999) when closeTo(endPosition)
        if (lngLatHandler.isCloseTo(nextPos, goalPos)) {
            return true;
        }
        // Once back inside the central area, the drone must not leave again until it has delivered the pizzas to the start position (appleton Tower)
        if (!lngLatHandler.isInRegion(nextPos, centralArea) && lngLatHandler.isInRegion(currentPos, centralArea)) {
            // Drone is leaving the central area
            return true;
        }
        // The drone must not fly over any no-fly zones
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(nextPos, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the node with the lowest total cost from the set
     *
     * @param nodes the set of nodes to search
     * @return the node with the lowest total cost
     */
    private Node getLowestCostNode(Set<Node> nodes) {
        return nodes.stream()
                .min(Comparator.comparingDouble(Node::getTotalCost))
                .orElse(null);
    }

    /**
     * Gets the restaurants location for the order
     * @param order the current order being delivered
     * @return restaurantLocation
     */
    private Restaurant getRestaurant(Order order) {
        Restaurant restaurant = null;
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