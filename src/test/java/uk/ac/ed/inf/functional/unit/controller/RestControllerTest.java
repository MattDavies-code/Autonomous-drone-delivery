package uk.ac.ed.inf.functional.unit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RestControllerTest {

    private RestController restController;

    @BeforeEach
    void setUp() {
        // Initialize the RestController with mock values
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        restController = new RestController("2023-11-15", "https://ilp-rest.azurewebsites.net/");
    }

    @Test
    void testIsAlive() {
        // Mock RestTemplate to simulate a successful call
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        when(restTemplateMock.getForObject("https://ilp-rest.azurewebsites.net/isAlive", String.class)).thenReturn("Alive");

        restController.setRestTemplate(restTemplateMock);

        assertTrue(restController.isAlive());
    }

    @Test
    void testFetchOrders() {
        // Mock RestTemplate to simulate fetching orders
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        when(restTemplateMock.getForObject("https://ilp-rest.azurewebsites.net/2023-11-15", String.class)).thenReturn("[{\"orderId\":1,\"status\":\"Pending\"}]");

        restController.setRestTemplate(restTemplateMock);

        Order[] orders = restController.fetchOrders();
        assertNotNull(orders);
        assertEquals(1, orders.length);
        assertEquals(1, orders[0].getOrderNo());
        assertEquals("Pending", orders[0].getOrderStatus());
    }

    @Test
    void testFetchRestaurants() throws JsonProcessingException {
        // Mock RestTemplate to simulate fetching restaurants
        RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
        when(restTemplateMock.getForObject("https://ilp-rest.azurewebsites.net/restaurants", String.class)).thenReturn("[{\"name\":\"Restaurant A\"}]");

        restController.setRestTemplate(restTemplateMock);

        Restaurant[] restaurants = restController.fetchRestaurants();
        assertNotNull(restaurants);
        assertEquals(1, restaurants.length);
        assertEquals("Restaurant A", restaurants[0].name());
    }
}