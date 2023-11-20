//package uk.ac.ed.inf.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.web.client.RestTemplate;
//import uk.ac.ed.inf.ilp.data.NamedRegion;
//import uk.ac.ed.inf.ilp.data.Order;
//import uk.ac.ed.inf.ilp.data.Restaurant;
//
//import java.util.Arrays;
//
//import static org.junit.Assert.assertEquals;
//
//public class RestControllerTest {
//
//    private RestController restController;
//
//    @Before
//    public void setUp() {
//        restController = new RestController("2023-11-15", "https://ilp-rest.azurewebsites.net");
//    }
//
//    @Test
//    public void testIsAliveSuccess() {
//        // Simulate a successful response from the isAlive endpoint
//        restController.restTemplate = new RestTemplate() {
//            @Override
//            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
//                return (T) "Alive";
//            }
//        };
//
//        boolean result = restController.isAlive();
//
//        assertEquals(true, result);
//    }
//
//    @Test
//    public void testIsAliveFailure() {
//        // Simulate a failure response from the isAlive endpoint
//        restController.restTemplate = new RestTemplate() {
//            @Override
//            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
//                throw new RuntimeException("Service unreachable");
//            }
//        };
//
//        boolean result = restController.isAlive();
//
//        assertEquals(false, result);
//    }
//
//    @Test
//    public void testFetchOrders() throws JsonProcessingException {
//        Order[] expectedOrders = {new Order(), new Order()};
//        String jsonResponse = "[{\"property\":\"value\"}, {\"property\":\"value\"}]";
//        restController.restTemplate = new MockRestTemplate(jsonResponse);
//
//        Order[] result = restController.fetchOrders();
//
//        assertEquals(Arrays.toString(expectedOrders), Arrays.toString(result));
//    }
//
//    @Test
//    public void testFetchRestaurants() throws JsonProcessingException {
//        Restaurant[] expectedRestaurants = {new Restaurant(), new Restaurant()};
//        String jsonResponse = "[{\"property\":\"value\"}, {\"property\":\"value\"}]";
//        restController.restTemplate = new MockRestTemplate(jsonResponse);
//
//        Restaurant[] result = restController.fetchRestaurants();
//
//        assertEquals(Arrays.toString(expectedRestaurants), Arrays.toString(result));
//    }
//
//    @Test
//    public void testFetchNoFlyZones() throws JsonProcessingException {
//        NamedRegion[] expectedNoFlyZones = {new NamedRegion(), new NamedRegion()};
//        String jsonResponse = "[{\"property\":\"value\"}, {\"property\":\"value\"}]";
//        restController.restTemplate = new MockRestTemplate(jsonResponse);
//
//        NamedRegion[] result = restController.fetchNoFlyZones();
//
//        assertEquals(Arrays.toString(expectedNoFlyZones), Arrays.toString(result));
//    }
//
//    @Test
//    public void testFetchCentralArea() throws JsonProcessingException {
//        NamedRegion expectedCentralArea = new NamedRegion();
//        String jsonReponse = "{\"property\":\"value\"}";
//        restController.restTemplate = new MockRestTemplate(jsonReponse);
//
//        NamedRegion result = restController.fetchCentralArea();
//
//        assertEquals(expectedCentralArea.toString(), result.toString());
//    }
//
//    // Helper class to simulate RestTemplate behavior
//    private static class MockRestTemplate extends RestTemplate {
//        private final String jsonResponse;
//
//        public MockRestTemplate(String jsonResponse) {
//            this.jsonResponse = jsonResponse;
//        }
//
//        @Override
//        public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
//            return (T) jsonResponse;
//        }
//    }
//}
