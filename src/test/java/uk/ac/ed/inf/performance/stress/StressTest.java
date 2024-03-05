package uk.ac.ed.inf.performance.stress;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.flightPath.FlightPaths;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.resultfiles.CreateFiles;
import uk.ac.ed.inf.validator.OrderValidator;

import java.util.ArrayList;
import java.util.HashMap;

public class StressTest {

    @Test
    void testSystemStress() {
        // Use a multiple dates to get large amount of varied orders and URL for stress testing
        String[] dates = {
                "2023-11-01", "2023-11-02", "2023-11-03", "2023-11-04", "2023-11-05",
                "2023-11-06", "2023-11-07", "2023-11-08", "2023-11-09", "2023-11-10",
                "2023-11-11", "2023-11-12", "2023-11-13", "2023-11-14", "2023-11-15",
                "2023-11-16", "2023-11-17", "2023-11-18", "2023-11-19", "2023-11-20",
                "2023-11-21", "2023-11-22", "2023-11-23", "2023-11-24", "2023-11-25",
                "2023-11-26", "2023-11-27", "2023-11-28", "2023-11-29", "2023-11-30"
        };
        String restServerUrl = "https://ilp-rest.azurewebsites.net/"; // Modify as needed

        System.out.println("Starting System...");
        long startTime = System.currentTimeMillis();

        modifiedMain(dates, restServerUrl); //Main is modified to accept multiple dates and combine these orders into one dataset

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + " milliseconds");
    }

    private void modifiedMain(String[] dates, String restServerUrl) {

        // iterate through dates to retrieve the orders for each date and combine them into one
        Order[] orders = new Order[0];
        for (String date : dates) {
            // Pass date and restServerUrl to the controller
            RestController restController = new RestController(date, restServerUrl);
            Order[] ordersForDate = null;
            ordersForDate = restController.fetchOrders();

            // Combine orders for each date into one array
            orders = concatenateArrays(orders, ordersForDate);
        }

        // Pass date and restServerUrl to the controller
        RestController restController = new RestController("2023-11-15", restServerUrl);

        Restaurant[] restaurants = null;
        NamedRegion centralArea = null;
        NamedRegion[] noFlyZones = null;
        try {
            restaurants = restController.fetchRestaurants();
            centralArea = restController.fetchCentralArea();
            noFlyZones = restController.fetchNoFlyZones();

        } catch (JsonProcessingException e) {
            System.err.println("Error processing data from the REST-Server: " + e.getMessage());
            System.exit(1);
        }

        // 2. Validate the orders
        System.out.println("Validating orders...");
        OrderValidator orderValidator = new OrderValidator();
        for (Order order : orders) {
            orderValidator.validateOrder(order, restaurants);
        }

        // 3. Calculate the flightpaths for all valid orders in the exact sequence you received them
        System.out.println("Calculating flightpaths...");
        FlightPaths flightPaths = new FlightPaths(orders, restaurants, centralArea, noFlyZones);
        HashMap<String, ArrayList<Move>> flightPathsFiles = flightPaths.flightPathList();

        // 4. Write the 3 result files in a folder resultFiles (create if the directory doesn't exist, overwrite if it does)
        System.out.println("Writing result files...");
        CreateFiles createFiles = new CreateFiles();
        System.out.println("Writing deliveries...");
        createFiles.writeDeliveries("2023-11-15", orders);
        System.out.println("Writing flightpaths...");
        createFiles.writeFlightpath("2023-11-15", flightPathsFiles);
        System.out.println("Writing drones...");
        createFiles.writeDrone("2023-11-15", flightPathsFiles);
    }

    // Helper method to concatenate two arrays
    private Order[] concatenateArrays(Order[] arr1, Order[] arr2) {
        int arr1Length = arr1.length;
        int arr2Length = arr2.length;

        Order[] result = new Order[arr1Length + arr2Length];
        System.arraycopy(arr1, 0, result, 0, arr1Length);
        System.arraycopy(arr2, 0, result, arr1Length, arr2Length);

        return result;
    }
}
