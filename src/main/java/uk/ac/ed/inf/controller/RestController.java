package uk.ac.ed.inf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.web.client.RestTemplate;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.time.LocalDate;

/**
 * Controller that is used to fetch data from the Rest Server
 */
public class RestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String date;
    private final String restServerUrl;

    /**
     * sets the date and restServerUrl
     * @param date the date of the orders to be retrieved
     * @param restServerUrl the URL of the Rest Server
     */
    public RestController(String date, String restServerUrl) {
        this.date = date;
        this.restServerUrl = restServerUrl;
    }

    /**
     * Attempts to access the isAlive endpoint to check if API is dead or alive
     * @return If the API can be accessed correctly
     */
    public boolean isAlive() {
        try {
            String isAliveUrl = restServerUrl + "/isAlive";
            restTemplate.getForObject(isAliveUrl, String.class);
            return true;
        } catch (Exception e) {
            System.err.println("The Rest Service is unreachable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches the orders by date in the system
     * @return List<Order>
     */
    public Order[] fetchOrders() {
        // Create a custom Gson instance with the LocalDateDeserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();

        // Fetch orders from the REST server
        String ordersUrl = restServerUrl + "/orders/" + date;
        String response = restTemplate.getForObject(ordersUrl, String.class);
        // Convert the JSON string to an array of Order objects using LocalDateDeserializer
        return gson.fromJson(response, Order[].class);
    }

    /**
     * Fetches the restaurants from the Rest Server
     * @return List<Restaurant>
     */
    public Restaurant[] fetchRestaurants() throws JsonProcessingException {
        // Fetch the restaurants from the REST server
        String restaurantsUrl = restServerUrl + "/restaurants";
        String response = restTemplate.getForObject(restaurantsUrl, String.class);

        // Deserialize the JSON response into a list of restaurants
        return objectMapper.readValue(response, new TypeReference<>() {});
    }

    /**
     * Fetches the no-fly zones from the Rest Server
     * @return List<NamedRegion>
     */
    public NamedRegion[] fetchNoFlyZones() throws JsonProcessingException {
        // Fetch the no-fly zones from the REST server
        String noFlyZonesUrl = restServerUrl + "/noFlyZones";
        String response = restTemplate.getForObject(noFlyZonesUrl, String.class);

        // Deserialize the JSON response into a list of no-fly zones
        return objectMapper.readValue(response, new TypeReference<>() {});
    }

    /**
     * Fetches the central area from the Rest Server
     * @return NamedRegion
     */
    public NamedRegion fetchCentralArea() throws JsonProcessingException {
        // Fetch the central area from the REST server
        String centralAreaUrl = restServerUrl + "/centralArea";
        String response = restTemplate.getForObject(centralAreaUrl, String.class);

        // Deserialize the JSON response into a CentralArea object
        return objectMapper.readValue(response, new TypeReference<>() {});
    }
}
