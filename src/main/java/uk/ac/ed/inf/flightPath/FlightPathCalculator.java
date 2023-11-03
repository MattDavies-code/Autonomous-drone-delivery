package uk.ac.ed.inf.flightPath;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.FlightPath;

import java.util.List;

/**
 * Flight Path Calculator
 */
@Service
public class FlightPathCalculator {
    /**
     * Calculate flight paths for orders in the exact sequence as they are received
     * @param validOrdersForDay valid orders for the day
     * @return list of flight paths
     */
    public List<FlightPath> calculateFlightPaths(List<Order> validOrdersForDay) {
        return null;
    }
}
