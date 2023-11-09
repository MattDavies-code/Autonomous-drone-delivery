package uk.ac.ed.inf.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.flightPath.FlightPathCalculator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
		// Check if the correct number of command-line arguments is provided
		if (args.length != 2) {
			System.err.println("Usage: java -jar PizzaDronz-1.0-SNAPSHOT.jar <date> <URL>");
			System.exit(1);
		}

		ArgumentValidator argumentValidator = new ArgumentValidator();

		// Take date and REST URL, and validate them
		String date = args[0];
		String restServerUrl = args[1];

		try {
			if (argumentValidator.isValidDate(date) && argumentValidator.isValidUrl(restServerUrl)) {
				SpringApplication.run(Main.class, date, restServerUrl);
			} else {
				System.err.println("Invalid date or URL provided.");
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
			System.exit(1);
		}

		// Pass date and restServerUrl to the controller
		RestController restController = new RestController(date, restServerUrl);

		// 1. Read orders for the specified day (and only the day) and restaurants plus other relevant data from the REST-Server
		Order[] orders = restController.fetchOrders();
		Restaurant[] restaurants = restController.fetchRestaurants();
		NamedRegion centralArea = restController.fetchCentralArea();
		NamedRegion[] noFlyZones = restController.fetchNoFlyZones();

		// 2. Validate the orders
		OrderValidator orderValidator = new OrderValidator();

		ArrayList<Order> validOrders = new ArrayList<Order>();
		ArrayList<Order> invalidOrders = new ArrayList<Order>();
		for (Order order : orders) {
			Order currentOrder = orderValidator.validateOrder(order, restaurants);
			if (currentOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
				validOrders.add(currentOrder);
			} else if (currentOrder.getOrderStatus() == OrderStatus.INVALID){
				invalidOrders.add(currentOrder);
			}
		}
		// Convert validOrders and invalidOrders to Order[]
		Order[] validOrders1 = validOrders.toArray(new Order[validOrders.size()]);
		Order[] invalidOrders1 = invalidOrders.toArray(new Order[invalidOrders.size()]);

		// 3. Calculate the flightpaths for all valid orders in the exact sequence you received them
		FlightPathCalculator flightPathCalculator = new FlightPathCalculator(validOrders1, restaurants, centralArea, noFlyZones);
		HashMap<String, ArrayList<Move>> flightPaths = flightPathCalculator.flightPathList();

		// 4. Write the 3 result files in a folder resultfiles (create if not exists)
		CreateFiles createFiles = new CreateFiles();
		createFiles.writeDeliveries(date, validOrders1, invalidOrders1);
		createFiles.writeFlighpath(date, flightPaths);
		createFiles.writeDrone(date, flightPaths);
	}
}







