package uk.ac.ed.inf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import uk.ac.ed.inf.flightPath.FlightPathCalculator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.cw1.OrderValidator;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Rest Controller
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    //restServerUrl = "https://ilp-rest.azurewebsites.net";
    //date = "2023-11-15";

    private String restServerUrl;
    private String date;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderValidator orderValidator = new OrderValidator(); // Create an instance of OrderValidator class
    private final FlightPathCalculator flightPathCalculator = new FlightPathCalculator();

    public RestController() throws JsonProcessingException {
    }

    public void setConfiguration(String date, String restServerUrl) {
        this.date = date;
        this.restServerUrl = restServerUrl;
    }


    /**
     * returns the orders by date in the system and validates them using OrderValidator
     * @return List<Order>
     */
    @GetMapping("/orders")
    public List<Order> fetchOrders() throws JsonProcessingException {
        // Create a custom Gson instance with the LocalDateDeserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();

        // Fetch orders from the REST server
        String ordersUrl = restServerUrl + "/orders/" + date;
        String response = restTemplate.getForObject(ordersUrl, String.class);
        // Convert the JSON string to an array of Order objects using LocalDateDeserializer
        Order[] orders = gson.fromJson(response, Order[].class);

        // Validate the fetched orders
        List<Order> validOrdersForTheDay = new ArrayList<>();
        List<Restaurant> restaurants = fetchRestaurants();
        for (Order order : orders) {
            Order validatedOrder = orderValidator.validateOrder(order, restaurants.toArray(new Restaurant[0]));
            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                validOrdersForTheDay.add(validatedOrder);
            /*
              else to be implemented here
              non valid orders to still be in output files!!
              */
            }
        }
        return validOrdersForTheDay;
    }

    /**
     * returns the restaurants from the Rest Server
     * @return List<Restaurant>
     */
    public List<Restaurant> fetchRestaurants() throws JsonProcessingException {
        String restaurantsUrl = restServerUrl + "/restaurants";
        String response = restTemplate.getForObject(restaurantsUrl, String.class);

        List<Restaurant> definedRestaurants = objectMapper.readValue(response, new TypeReference<>() {});
        return definedRestaurants;
    }



    /**
     * returns the no-fly zones from the Rest Server
     * @return List<NamedRegion>
     */
    @GetMapping("/noFlyZones")
    public List<NamedRegion> fetchNoFlyZones() throws JsonProcessingException {
        // Fetch noFlyZones from the REST server
        String noFlyZonesUrl = restServerUrl + "/noFlyZones";
        String response = restTemplate.getForObject(noFlyZonesUrl, String.class);

        // Deserialize the JSON response into a list of no-fly zones
        List<NamedRegion> noFlyZones = objectMapper.readValue(response, new TypeReference<>() {});
        return noFlyZones;

    }

    /**
     * returns the central area from the Rest Server
     * @return NamedRegion
     */
    @GetMapping("/centralArea")
    public NamedRegion fetchCentralArea() throws JsonProcessingException {
        // Fetch the central area from the REST server
        String centralAreaUrl = restServerUrl + "/centralArea";
        String response = restTemplate.getForObject(centralAreaUrl, String.class);

        // Deserialize the JSON response into a CentralArea object
        NamedRegion centralArea = objectMapper.readValue(response, new TypeReference<>() {});
        return centralArea;
    }
}
