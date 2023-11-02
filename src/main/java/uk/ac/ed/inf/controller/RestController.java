package uk.ac.ed.inf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.flightPath.FlightPathCalculator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.data.Tuple;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.ac.ed.inf.OrderValidator;

/**
 * Rest Controller
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    // Beware down below??
    @Autowired
    private FlightPathCalculator flightPathCalculator;



    private final RestTemplate restTemplate = new RestTemplate();
    private final String restServerUrl = "https://ilp-rest.azurewebsites.net";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderValidator orderValidator = new OrderValidator(); // Create an instance of your OrderValidator class

    public RestController(FlightPathCalculator flightPathCalculator) {
        this.flightPathCalculator = flightPathCalculator;
        //This bit!!!^^^


    }

    /**
     * get a buffered reader for a resource
     a
     * @param jsonResource the JSON resource this reader is required for
     * @return the buffered reader
     */
    private java.io.BufferedReader getBufferedReaderForResource(String jsonResource) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(jsonResource))));
    }

    /*
     * returns the restaurants in the system
     *
     * @return array of restaurants

    @GetMapping("/restaurants")
    public Restaurant[] restaurants() {
        //return new Gson().fromJson(getBufferedReaderForResource("json/restaurants.json"), Restaurant[].class);
    }
    */
    /**
     * returns the restaurants in the system
     *
     * @return array of restaurants
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> restaurants() throws JsonProcessingException {
        String restaurantsUrl = restServerUrl + "/restaurants";
        String response = restTemplate.getForObject(restaurantsUrl, String.class);
        List<Restaurant> restaurants = objectMapper.readValue(response, new TypeReference<List<Restaurant>>() {});
        return ResponseEntity.ok(restaurants);
    }

    /**
     * returns the orders in the system
     *
     * @return array of orders

    @GetMapping("/orders")
    public Order[] orders() {
        return new Gson().fromJson(getBufferedReaderForResource("json/orders.json"), Order[].class);
    }
    */

    /**
     * returns the orders by date in the system
     * @param date the date of the order
     * @return array of orders

    @GetMapping("/orders/{date}")
    public Order[] getOrdersForDay(@PathVariable String date) {
        // Assuming the date is passed in the format "yyyy-MM-dd" (e.g., "2023-09-01")
        LocalDate specifiedDate = LocalDate.parse(date);
        Order[] allOrders = new Gson().fromJson(getBufferedReaderForResource("json/orders.json"), Order[].class);

        // Filter orders for the specified day
        Order[] ordersForDay = Arrays.stream(allOrders)
                .filter(order -> order.getOrderDate().isEqual(specifiedDate))
                .toArray(Order[]::new);

        return ordersForDay;
    }
     */

    @GetMapping("/orders/{day}")
    public ResponseEntity<List<Order>> getOrdersForDay(@PathVariable String day) throws JsonProcessingException {
        String ordersUrl = restServerUrl + "/orders/" + day;
        String response = restTemplate.getForObject(ordersUrl, String.class);
        List<Order> orders = objectMapper.readValue(response, new TypeReference<List<Order>>() {});

        // Validate the fetched orders
        List<Order> validOrdersForTheDay = new ArrayList<>();
        List<Restaurant> restaurants = restaurants().getBody();
        for (Order order : orders) {
            Order validatedOrder = orderValidator.validateOrder(order, restaurants.toArray(new Restaurant[0]));
            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                validOrdersForTheDay.add(validatedOrder);
            }
        }
        return ResponseEntity.ok(validOrdersForTheDay);
    }






    //***************************************************************************************************************************************************
    /**
     * simple test method to test the service's availability
     *
     * @param input an optional input which will be echoed
     * @return the echo
     */
    @GetMapping(value = {"/testPath/{input}", "/testPath"})
    public String test(@PathVariable(required = false) String input) {
        return String.format("Hello from the ILP-Tutorial-REST-Service. Your provided value was: %s", input == null ? "not provided" : input);
    }
    /**
     * a simple alive check
     *
     * @return true (always)
     */
    @GetMapping(value = {"/isAlive"})
    public boolean isAlive() {
        return true;
    }


    /**
     * GET with HTML result
     * @return
     */
    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String testHtml() {
        return "<html>\n" + "<header><title>ILP Tutorial REST Server</title></header>\n" +
                "<body>\n<h1>" + "Hello from the ILP Tutorial REST Server\n" + "</h1></body>\n" + "</html>";
    }

    /**
     * POST with a JSON data structure in the request body
     * @param postAttribute
     * @return
     */
    @PostMapping(value = "/testPostBody",  consumes = {"*/*"})
    public String testPost(@RequestBody Tuple postAttribute) {
        return "You posted: " + postAttribute.toString();
    }

    /**
     * POST with request parameters
     * @param item1
     * @param item2
     * @return
     */
    @PostMapping("/testPostPath")
    public String testPost(@RequestParam("item1") String item1, @RequestParam("item2") String item2) {
        var postAttribute = new Tuple(item1, item2);
        return "You posted: " + postAttribute.toString();
    }

}
