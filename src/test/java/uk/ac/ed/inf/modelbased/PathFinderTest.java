package uk.ac.ed.inf.modelbased;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.flightPath.FlightPaths;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.resultfiles.CreateFiles;
import uk.ac.ed.inf.validator.ArgumentValidator;
import uk.ac.ed.inf.validator.OrderValidator;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class PathFinderTest {


    @Test
    void testModelBased() {
        try {
            // State 1: Validating arguments
            String date = "2023-11-15";
            String restServerUrl = "https://ilp-rest.azurewebsites.net/";
            ArgumentValidator argumentValidator = new ArgumentValidator();
            assertTrue(argumentValidator.isValidDate(date));
            assertTrue(argumentValidator.isValidUrl(restServerUrl));

            // State 2: Retrieving Data from REST server
            RestController restController = new RestController(date, restServerUrl);
            assertTrue(restController.isAlive());
            Order[] orders = restController.fetchOrders();
            Restaurant[] restaurants = restController.fetchRestaurants();
            NamedRegion centralArea = restController.fetchCentralArea();
            NamedRegion[] noFlyZones = restController.fetchNoFlyZones();
            assertNotNull(orders);
            assertNotNull(restaurants);
            assertNotNull(centralArea);
            assertNotNull(noFlyZones);

            // State 3: Validating Orders
            OrderValidator orderValidator = new OrderValidator();
            for (Order order : orders) {
                orderValidator.validateOrder(order, restaurants);
                assertTrue(order.getOrderStatus() == OrderStatus.INVALID || order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED);
            }

            // State 4: Calculating flight paths
            FlightPaths flightPaths = new FlightPaths(orders, restaurants, centralArea, noFlyZones);
            HashMap<String, ArrayList<Move>> flightPathsFiles = flightPaths.flightPathList();
            assertNotNull(flightPathsFiles);
            assertFalse(flightPathsFiles.isEmpty());

            // State 5: Writing result files
            CreateFiles createFiles = new CreateFiles();

        } catch (Exception e) {
            // Handle exceptions or errors here
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testSystemConstraints() {
        // Additional test for system constraints
        try {
            assertTrue(SystemConstants.DRONE_MOVE_DISTANCE <= SystemConstants.DRONE_MAX_MOVES);

        } catch (Exception e) {
            fail("Constraint violation: " + e.getMessage());
        }
    }
}
