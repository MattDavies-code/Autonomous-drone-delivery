package uk.ac.ed.inf.functional.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import static org.junit.jupiter.api.Assertions.*;

public class RESTIntegrationTest {
    // Mocked data for testing
    private final String testDate = "2023-11-15";
    private final String testRestServerUrl = "https://ilp-rest.azurewebsites.net/";

    @Test
    public void testOrderProcessingAndDelivery() throws JsonProcessingException {
        // Arrange
        RestController restController = new RestController(testDate, testRestServerUrl);

        // Act
        boolean isAlive = restController.isAlive();
        Order[] orders = restController.fetchOrders();
        Restaurant[] restaurants = restController.fetchRestaurants();
        NamedRegion[] noFlyZones = restController.fetchNoFlyZones();
        NamedRegion centralArea = restController.fetchCentralArea();

        // Assert
        assertTrue(isAlive, "The Rest Service should be alive.");

        assertNotNull(orders, "Orders should not be null.");

        assertTrue(orders.length > 0, "There should be at least one order.");

        assertNotNull(restaurants, "Restaurants should not be null.");
        assertTrue(restaurants.length > 0, "There should be at least one restaurant.");

        assertNotNull(noFlyZones, "No-fly zones should not be null.");

        assertNotNull(centralArea, "Central area should not be null.");
    }
}
