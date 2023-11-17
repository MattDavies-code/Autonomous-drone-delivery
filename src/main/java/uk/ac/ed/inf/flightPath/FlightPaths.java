package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class FlightPaths {

    // Constants
    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);

    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final Order[] orders;
    private final Restaurant[] restaurants;
    private final PathFinder pathFinder;

    public FlightPaths(Order[] validOrders, Restaurant[] restaurants, NamedRegion centralArea, NamedRegion[] noFlyZones) {
        this.orders = validOrders;
        this.restaurants = restaurants;
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
        this.pathFinder = new PathFinder(noFlyZones, centralArea);
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
                        ArrayList<Move> flightPath = pathFinder.findPath(appletonTower, endPosition, order);

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
