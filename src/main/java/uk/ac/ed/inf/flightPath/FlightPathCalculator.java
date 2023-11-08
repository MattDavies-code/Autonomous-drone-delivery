package uk.ac.ed.inf.flightPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Drone;
import uk.ac.ed.inf.model.Move;

import java.util.List;

/**
 * Flight Path Calculator
 */
@Service
public class FlightPathCalculator {
    //create instance of rest controller
    @Autowired
    private RestController restController;
    //List<Order> orders = restController.fetchOrders();
    List<Restaurant> restaurants = restController.fetchRestaurants();
    //List<NamedRegion> noFlyZones = restController.fetchNoFlyZones();
    //NamedRegion centralArea = restController.fetchCentralArea();

    //Constants
    private final LngLat appletonTower = new LngLat(-3.186874, 55.944494);


    public FlightPathCalculator() throws JsonProcessingException {
    }

    /**
     * Make flight paths for orders in the exact sequence as they are received
     *
     * @param validOrdersForDay valid orders for the day
     * @return list of flight paths
     */
    public List<Move> flightPathList(List<Order> validOrdersForDay) {
        List<Move> flightPathList = null;
        // Calculate the flightpath for all valid orders in the exact sequence you receive them
        for (Order order : validOrdersForDay) {
            // Calculate the flight path for a single order
            Move flightPath = calculateFlightPath(order);
            flightPathList.add(flightPath);
        }
        return flightPathList;
    }

    /**
     * Gets the restaurants location for the order
     * @param order the current order being delivered
     * @return restaurantLocation
     */
    private LngLat getRestaurantLocation(Order order) {
        LngLat restaurantLocation = null;
        for (Restaurant restaurant : restaurants) {
            // check if pizza in restaurant.menu() is in order.pizzas() using disjoint
            if (CollectionUtils.containsAny(List.of(restaurant.menu()), List.of(order.getPizzasInOrder()))) {
                // Get the location of the restaurant for the order
                restaurantLocation = restaurant.location();
            }
        }
        return restaurantLocation;
    }

    /**
     * Calculate flight path for single order
     * @param order the current order being delivered
     //* @param drone
     * @return Move
     */
    public Move calculateFlightPath(Order order) { //drone
        LngLat endPosition = getRestaurantLocation(order);
        // Calculate the flight path for a single order
        // using A* algorithm
        // Must spend one move when closeTo(restaurantLocation)
        // Must spend one move when closeTo(endPosition)
        // Once back inside the central area the drone must not leave again unitl it has delivered the pizzas to the start position (appleton Tower)
        // code here
        return null;

    }
}

