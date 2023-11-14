package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
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
    //private final Double Hover = 999.0;

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
                        //System.out.println("Flightpath to restaurant already exists");
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
                        System.out.println("Order Complete");

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
        // Must spend one move hovering (with angle 999) when closeTo(restaurantLocation)
        // Must spend one move hovering (with angle 999) when closeTo(endPosition)
        // Once back inside the central area the drone must not leave again until it has delivered the pizzas to the start position (appleton Tower)
        // The drone must not fly over any no-fly zones
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

        // Set max depth
        int maxDepth = 50;
        int depth = 0;
        // A* algorithm
        while (!openSet.isEmpty() && depth < maxDepth) {
            // Get the node with the lowest total cost from the open set
            current = getLowestCostNode(openSet);

            // Remove the current node from the open set and add it to the closed set
            openSet.remove(current);
            closedSet.add(current);

            // If the goal is reached, reconstruct the path and return it
            if (lngLatHandler.isCloseTo(current.getPosition(), goalNode.getPosition())) {
                System.out.println("Goal reached");
                ArrayList<Move> path = reconstructPath(current, order);
                // Add a hover move at the end of the path
                LngLat lastPosition = current.getPosition();
                path.add(new Move(order.getOrderNo(), lastPosition.lng(), lastPosition.lat(), 999, lastPosition.lng(), lastPosition.lat()));

                return path;
            }

            // Generate successors (neighbors) of the current node
            List<Node> successors = generateSuccessors(current, goalNode, closedSet, openSet);

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
            depth++;
        }

        // If open set is empty and goal is not reached, return an empty path
        return new ArrayList<>();
    }

    /**
     * Generate successors (neighbors) of the current node
     * g = costFromStart = cost from start to current node
     * h = heuristicCost = heuristic cost from current node to goal node
     * @param current   the current node
     * @param goalNode  the goal node
     * @return a list of successor nodes
     */
    private List<Node> generateSuccessors(Node current, Node goalNode, Set<Node> closedSet, Set<Node> openSet) {

        List<Node> successors = new ArrayList<>();
        LngLat currentPos = current.getPosition();

        // Adjust these angles based on your drone's movement capabilities
        //double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
        double[] angles = {0, 45, 90, 135, 180, 225, 270, 315};
        //int successorsGenerated

        for (double angle : angles) {
            LngLat nextPos = lngLatHandler.nextPosition(currentPos, angle);

            Node successor = new Node(nextPos, 0, 0);
            // Skip successor if visited before
            if (closedSet.contains(successor) || openSet.contains(successor)) {
                continue;
            }

            // Check if the next position is inside any no-fly zones
            boolean isInsideNoFlyZone = false;
            for (NamedRegion noFlyZone : noFlyZones) {
                if (lngLatHandler.isInRegion(nextPos, noFlyZone)) {
                    isInsideNoFlyZone = true;
                    break;
                }
            }
            // Skip successor if next position is inside a no-fly zone
            if (isInsideNoFlyZone) {
                continue;
            }

            // Skip successor if current position is in central area and next position out of central area
            //if (lngLatHandler.isInRegion(currentPos, centralArea) && !lngLatHandler.isInRegion(nextPos, centralArea)) {
            //    continue;
            //}

            double costFromStart = current.getCostFromStart() + SystemConstants.DRONE_MOVE_DISTANCE;
            double heuristicCost = lngLatHandler.distanceTo(nextPos, goalNode.getPosition());

            successors.add(new Node(nextPos, costFromStart, heuristicCost));

        }
        return successors;
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
     * Reconstruct the path from the goal node to the start node
     *
     * @param goalNode the goal node
     * @return the reconstructed path as a list of moves
     */
    private ArrayList<Move> reconstructPath(Node goalNode, Order order) {
        System.out.println("Reconstructing path");
        ArrayList<Move> path = new ArrayList<>();
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