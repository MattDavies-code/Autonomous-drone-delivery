package uk.ac.ed.inf.flightPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.FlightPath;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.List;

/**
 * Flight Path Calculator
 */
@Service
public class FlightPathCalculator {
    //create instance of rest controller
    @Autowired
    private RestController restController;
    List<Order> orders = restController.fetchOrders();
    List<Restaurant> restaurants = restController.fetchRestaurants();
    List<NamedRegion> noFlyZones = restController.fetchNoFlyZones();
    NamedRegion centralArea = restController.fetchCentralArea();
    LngLat appletonTower = new LngLat(-3.186874, 55.944494);

    private final LngLat startPosition = appletonTower; //change this

    public FlightPathCalculator() throws JsonProcessingException {
    }

    /**
     * Calculate flight paths for orders in the exact sequence as they are received
     *
     * @param validOrdersForDay valid orders for the day
     * @return list of flight paths
     */
    public List<FlightPath> flightPathList(List<Order> validOrdersForDay) {
        List<FlightPath> flightPathList = null;
        // Calculate the flightpath for all valid orders in the exact sequence you receive them
        for (Order order : validOrdersForDay) {
            // Calculate the flight path for a single order
            FlightPath flightPath = calculateFlightPath(order);
            flightPathList.add(flightPath);
        }
        return flightPathList;
    }

    /*
    // Create method for calculating flight path for single order
    public FlightPath calculateFlightPath(Order order) {
        // Get the location of the restaurant for the order
        for (Restaurant restaurant : restaurants) {
            // if restaurant menu matches a pizza in the order
            if (restaurant.menu().contains(order.getPizzasInOrder())) {
                LngLat endPosition = restaurant.location();
            }
        }


        // Calculate the flight path for a single order

        // Return a FlightPath object

        return null;
    }

     */
}

