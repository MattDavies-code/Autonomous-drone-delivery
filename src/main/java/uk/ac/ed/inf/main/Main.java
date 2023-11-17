package uk.ac.ed.inf.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.flightPath.FlightPathCalculator;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.resultfiles.CreateFiles;
import uk.ac.ed.inf.validator.ArgumentValidator;
import uk.ac.ed.inf.validator.OrderValidator;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	public static void main(String[] args) throws JsonProcessingException {

		System.out.println("Starting System...");
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
			System.err.println("An error occurred: " + e.getMessage());
			System.exit(1);
		}

		// Pass date and restServerUrl to the controller
		RestController restController = new RestController(date, restServerUrl);

		System.out.println("Fetching orders...");
		// 1. Read orders for the specified day (and only the day) and restaurants plus other relevant data from the REST-Server
		Order[] orders = restController.fetchOrders();
		Restaurant[] restaurants = restController.fetchRestaurants();
		NamedRegion centralArea = restController.fetchCentralArea();
		NamedRegion[] noFlyZones = restController.fetchNoFlyZones();

		System.out.println("Validating orders...");
		// 2. Validate the orders
		OrderValidator orderValidator = new OrderValidator();
		for (Order order : orders) {
			orderValidator.validateOrder(order, restaurants);
		}

		long startTime = System.currentTimeMillis();
		System.out.println("Calculating flightpaths...");
		// 3. Calculate the flightpaths for all valid orders in the exact sequence you received them
		FlightPathCalculator flightPathCalculator = new FlightPathCalculator(orders, restaurants, centralArea, noFlyZones);
		HashMap<String, ArrayList<Move>> flightPaths = flightPathCalculator.flightPathList();

		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		System.out.println("Execution time: " + executionTime + " milliseconds");

		System.out.println("Writing result files...");
		// 4. Write the 3 result files in a folder resultfiles (create if not exists)
		CreateFiles createFiles = new CreateFiles();
		System.out.println("Writing deliveries...");
		createFiles.writeDeliveries(date, orders);
		System.out.println("Writing flightpaths...");
		createFiles.writeFlightpath(date, flightPaths);
		System.out.println("Writing drones...");
		createFiles.writeDrone(date, flightPaths);



	}
}







