package uk.ac.ed.inf.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.data.Tuple;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Rest Controller
 */
@RestController
public class RestController {

    /**
     * get a buffered reader for a resource
     a
     * @param jsonResource the JSON resource this reader is required for
     * @return the buffered reader
     */
    private java.io.BufferedReader getBufferedReaderForResource(String jsonResource) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(jsonResource))));
    }

    /**
     * returns the restaurants in the system
     *
     * @return array of suppliers
     */
    @GetMapping("/restaurants")
    public Restaurant[] restaurants() {
        return new Gson().fromJson(getBufferedReaderForResource("json/restaurants.json"), Restaurant[].class);
    }

    /**
     * returns the orders in the system
     *
     * @return array of orders
     */
    @GetMapping("/orders")
    public Order[] orders() {
        return new Gson().fromJson(getBufferedReaderForResource("json/orders.json"), Order[].class);
    }

    /**
     * returns the orders by date in the system
     * @param date the date of the order
     * @return array of orders
     */

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





    //************************************************************************************************************************
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
