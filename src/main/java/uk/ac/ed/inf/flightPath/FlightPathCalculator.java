package uk.ac.ed.inf.flightPath;

import org.springframework.util.CollectionUtils;
import org.w3c.dom.Node;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;

import java.lang.reflect.Array;
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

    public FlightPathCalculator(Order[] validOrders, Restaurant[] restaurants, NamedRegion centralArea, NamedRegion[] noFlyZones) {
        this.orders = validOrders;
        this.restaurants = restaurants;
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
    }

    /**
     * Make flight paths for orders in the exact sequence as they are received
     */
    public HashMap<String, ArrayList<Move>> flightPathList() {
        HashMap<String, ArrayList<Move>> flightPaths = new HashMap<>();
        // Flightpaths to be stored and reused
        HashMap<Restaurant, ArrayList<Move>> flightPathToRestaurant = new HashMap<>();
        // Calculate the flightpath for all valid orders in the exact sequence you receive them
        for (Order order : orders) {
            try{
                // Check to see if flightpath to restaurant already exists
                Restaurant currentRestaurant = getRestaurant(order);
                if (flightPathToRestaurant.containsKey(currentRestaurant)){
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

                    // Add flightpath to restaurant to flighPathToRestaurant to be reused
                    flightPathToRestaurant.put(currentRestaurant, flightPath);
                }
            } catch (Exception e){
                System.err.println(e);
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
        LngLatHandler lngLatHandler = new LngLatHandler();
        // Must spend one move when closeTo(restaurantLocation)
        // Must spend one move when closeTo(endPosition)
        // Once back inside the central area the drone must not leave again until it has delivered the pizzas to the start position (appleton Tower)
        // The drone must not fly over any no-fly zones
        // utilise isCloseTo() method in LngLatHandler
        // utilise distanceTo() method in LngLatHandler
        // utilise isInRegion() method in LngLatHandler
        // utilise nextPosition() method in LngLatHandler
        // can move in 16 directions, angle is in degrees
        // hover is 999
        ArrayList<Move> flightPath = new ArrayList<>();
        // Add the first move to the flightpath
        flightPath.add(new Move(order.getOrderNo(), start.lng(), start.lat(), 0, start.lng(), start.lat()));

        // Calculate the flightpath from start to end position
        // If drone is not at the end position then keep calculating the flightpath
        while (!lngLatHandler.isCloseTo(end, start)) {
            // use A* algorithm to calculate the flightpath
            // Create the start node
            // Create the end node
            // Create the open and closed lists
            List<Node> openList = new ArrayList<>();
            List<Node> closedList = new ArrayList<>();
            // Add the start node to the open list
            //openList.add(startNode);


        }
        return flightPath;
    }
    /**
     * Calculate heuristic for A* algorithm
     */
    private Double calculateHeuristic(LngLat start, LngLat end) {
        // Calculate the heuristic
        // h = distance from current node to end node
        //return start.distanceTo(end);
        return 0.0;
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


