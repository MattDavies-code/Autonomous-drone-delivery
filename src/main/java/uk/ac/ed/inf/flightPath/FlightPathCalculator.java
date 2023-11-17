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
        // Create the start node
        Node startNode = new Node(start, null, 0,lngLatHandler.distanceTo(start, end),999.0);

        // Initialize open and closed sets
        ArrayList<Node> closedSet = new ArrayList<>();

        closedSet.add(startNode);
        HashMap<Node, Double> openSet = new HashMap<>(generateSuccessors(startNode, end, closedSet));

        // Initialize the start node as the current node
        Node current = startNode;
        int iterations = 0;
        // A* algorithm
        while (!openSet.isEmpty()) {
            // Get the node with the lowest total cost from the open set
            current = Collections.min(openSet.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();

            openSet.remove(current);
            closedSet.add(current);

            // If the goal is reached, reconstruct the path and return it
            if (lngLatHandler.isCloseTo(current.getPosition(), end)) {
                return reconstructPath(current, order);
            }

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


//            if (iterations > 10000) {
//                return reconstructPath(current, order);
//            }
//            iterations++;


        }
        return null;
    }

    /**
     * Generate successors (neighbors) of the current node
     * g = costFromStart = cost from start to current node which is 1 for each move
     * h = heuristicCost = heuristic cost from current node to goal node
     * @param current   the current node
     * @param end  the restaurant location
     * @param closedSet the closed set
     * @return a list of successor nodes
     */
    private HashMap<Node, Double> generateSuccessors(Node current, LngLat end, ArrayList<Node> closedSet) {

        HashMap<Node, Double> successors = new HashMap<>();
        LngLat currentPosition = current.getPosition();
        boolean currentInCentral = lngLatHandler.isInCentralArea(current.getPosition(), centralArea);
        Node successor;

        for (double angle : angles) {
            LngLat nextPosition = lngLatHandler.nextPosition(currentPosition, angle);
            double costFromStart = current.getCostFromStart();
            double heuristicCost = lngLatHandler.distanceTo(nextPosition, end);
            successor = new Node(nextPosition, current, 1, heuristicCost, angle);

            // Skip successor if it is already in the closed set
            if (closedSet.contains(successor)) {
                continue;
            }

//            // Skip successor if it is inside the central area
//            boolean isCentralAreaValid = !currentInCentral && lngLatHandler.isInCentralArea(successor.getPosition(), this.centralArea);
//            if (isCentralAreaValid) {
//                continue;
//            }

            // Skip successor if next position is inside a no-fly zone
            boolean isInsideNoFlyZone = !Arrays.stream(noFlyZones).noneMatch(noFlyZone -> lngLatHandler.isInRegion(nextPosition, noFlyZone));
            if (isInsideNoFlyZone) {
                continue;
            }

            // Add successor to the list of successors
            successors.put(successor, successor.getCostFromStart() * SystemConstants.DRONE_MOVE_DISTANCE + successor.getHeuristicCost());
            //
        }
        return successors;
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