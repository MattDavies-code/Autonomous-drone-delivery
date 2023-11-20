package uk.ac.ed.inf.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.flightPath.FlightPaths;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.resultfiles.CreateFiles;
import uk.ac.ed.inf.validator.ArgumentValidator;
import uk.ac.ed.inf.validator.OrderValidator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class that is used to run the program
 */
public class Main {
	public static void main(String[] args) {

		System.out.println("Starting System...");
		long startTime = System.currentTimeMillis();

		// Check if the correct number of command-line arguments is provided
		if (args.length != 2) {
			System.err.println("Usage: java -jar PizzaDronz-1.0-SNAPSHOT.jar <date> <URL>");
			System.exit(1);
		}

		// Take date and REST URL, and validate them
		String date = args[0];
		String restServerUrl = args[1];
		ArgumentValidator argumentValidator = new ArgumentValidator();

		try {
			if (!argumentValidator.isValidDate(date) || !argumentValidator.isValidUrl(restServerUrl)) {
				System.err.println("Invalid date or URL provided.");
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("An error occurred validating the date and Rest URL: " + e.getMessage());
			System.exit(1);
		}

		// Pass date and restServerUrl to the controller
		RestController restController = new RestController(date, restServerUrl);

		// 1. Read orders for the specified day (and only the day) and restaurants plus other relevant data from the REST-Server
		System.out.println("Fetching orders...");

		// Check if the REST service is alive before proceeding
		if (!restController.isAlive()) {
			System.err.println("The Rest Service is not alive. Exiting.");
			System.exit(1);
		}

		Order[] orders = null;
		Restaurant[] restaurants = null;
		NamedRegion centralArea = null;
		NamedRegion[] noFlyZones = null;
		try {
			orders = restController.fetchOrders();
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
		createFiles.writeDeliveries(date, orders);
		System.out.println("Writing flightpaths...");
		createFiles.writeFlightpath(date, flightPathsFiles);
		System.out.println("Writing drones...");
		createFiles.writeDrone(date, flightPathsFiles);

		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		System.out.println("Execution time: " + executionTime + " milliseconds");
	}
}