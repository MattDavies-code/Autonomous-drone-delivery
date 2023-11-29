package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;

import java.util.*;

/**
 * Class to calculate flight paths for all orders for the day
 */
public class FlightPaths {

    // Constants
    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);

    private final Order[] orders;
    private final Restaurant[] restaurants;
    private final PathFinder pathFinder;

    public FlightPaths(Order[] validOrders, Restaurant[] restaurants, NamedRegion centralArea, NamedRegion[] noFlyZones) {
        this.orders = validOrders;
        this.restaurants = restaurants;
        this.pathFinder = new PathFinder(noFlyZones, centralArea);
    }

    /**
     * Make flight paths for orders in the exact sequence as they are received
     * Utilises the PathFinder class to calculate the flight paths
     * @return HashMap of flight paths
     */
    public HashMap<String, ArrayList<Move>> flightPathList() {
        LinkedHashMap<String, ArrayList<Move>> flightPaths = new LinkedHashMap<>();

        // Flight-paths to be stored and reused if the restaurant has already been travelled to
        HashMap<Restaurant, ArrayList<Move>> flightPathToRestaurant = new HashMap<>();

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                try {

                    // Check if flightpath to restaurant already exists
                    Restaurant currentRestaurant = getRestaurant(order);

                    //System.out.println("Current Restaurant: " + currentRestaurant.name());
                    if (flightPathToRestaurant.containsKey(currentRestaurant)) {

                        // Get the flightpath to the restaurant if it exists and add it to the flightpaths list
                        ArrayList<Move> flightPath = flightPathToRestaurant.get(currentRestaurant);

                        // Create a return path by reversing the moves in the original path
                        ArrayList<Move> returnPath = new ArrayList<>(flightPath);
                        Collections.reverse(returnPath);

                        // Remove the first hover move at the restaurant from the return path
                        if (!returnPath.isEmpty()) {
                            returnPath.remove(0);
                        }

                        // Join the return path to the original path
                        ArrayList<Move> fullPath = new ArrayList<>(flightPath);
                        fullPath.addAll(returnPath);

                        // Remove the first hover move at appleton tower from the full path
                        if (!fullPath.isEmpty()) {
                            fullPath.remove(0);
                        }

                        // Add the full flightpath to the flightpaths list
                        flightPaths.put(order.getOrderNo(), fullPath);


                    } else {
                        // Get end position for the order
                        LngLat endPosition = null;
                        if (currentRestaurant != null) {
                            endPosition = currentRestaurant.location();
                        }

                        // Calculate the flight path for a single order
                        ArrayList<Move> flightPath = pathFinder.findPath(appletonTower, endPosition, order);

                        // Add flightpath to restaurant to flightpath list
                        flightPathToRestaurant.put(currentRestaurant, flightPath);

                        // Create a return path by reversing the moves in the original path
                        ArrayList<Move> returnPath = new ArrayList<>(flightPath);
                        Collections.reverse(returnPath);

                        // Remove the first hover move at the restaurant from the return path
                        if (!returnPath.isEmpty()) {
                            returnPath.remove(0);
                        }

                        // Join the return path to the original path
                        ArrayList<Move> fullPath = new ArrayList<>(flightPath);
                        fullPath.addAll(returnPath);

                        // Remove the first hover move at appleton tower from the full path
                        if (!fullPath.isEmpty()) {
                            fullPath.remove(0);
                        }

                        // Add the full flightpath to the flightpaths list
                        flightPaths.put(order.getOrderNo(), fullPath);
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
