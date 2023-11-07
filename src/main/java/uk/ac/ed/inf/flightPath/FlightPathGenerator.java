package uk.ac.ed.inf.flightPath;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Used to call the FlightPathCalculator class to calculate the flight paths for the orders
 */
public class FlightPathGenerator {
    public static void main(String[] args) throws JsonProcessingException {
        // Create an instance of FlightPathCalculator class
        FlightPathCalculator flightPathCalculator = new FlightPathCalculator();

        // Call the flightPathList method to calculate the flight paths for the orders
        flightPathCalculator.flightPathList(null);
    }

}
