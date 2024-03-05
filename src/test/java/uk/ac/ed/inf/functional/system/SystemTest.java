package uk.ac.ed.inf.functional.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.flightPath.FlightPaths;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.resultfiles.CreateFiles;
import uk.ac.ed.inf.validator.ArgumentValidator;
import uk.ac.ed.inf.validator.OrderValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemTest {

    @Test
    void testSystem() {

        // No-fly zones taken from the REST server
        NamedRegion[] noFlyZones = {
                new NamedRegion("George Square Area", new LngLat[]{
                        new LngLat(-3.190578818321228, 55.94402412577528),
                        new LngLat(-3.1899887323379517, 55.94284650540911),
                        new LngLat(-3.187097311019897, 55.94328811724263),
                        new LngLat(-3.187682032585144, 55.944477740393744),
                        new LngLat(-3.190578818321228, 55.94402412577528)
                }),
                new NamedRegion("Dr Elsie Inglis Quadrangle", new LngLat[]{
                        new LngLat(-3.1907182931900024, 55.94519570234043),
                        new LngLat(-3.1906163692474365, 55.94498241796357),
                        new LngLat(-3.1900262832641597, 55.94507554227258),
                        new LngLat(-3.190133571624756, 55.94529783810495),
                        new LngLat(-3.1907182931900024, 55.94519570234043)
                }),
                new NamedRegion("Bristo Square Open Area", new LngLat[]{
                        new LngLat(-3.189543485641479, 55.94552313663306),
                        new LngLat(-3.189382553100586, 55.94553214854692),
                        new LngLat(-3.189259171485901, 55.94544803726933),
                        new LngLat(-3.1892001628875732, 55.94533688994374),
                        new LngLat(-3.189194798469543, 55.94519570234043),
                        new LngLat(-3.189135789871216, 55.94511759833873),
                        new LngLat(-3.188138008117676, 55.9452738061846),
                        new LngLat(-3.1885510683059692, 55.946105902745614),
                        new LngLat(-3.1895381212234497, 55.94555918427592),
                        new LngLat(-3.189543485641479, 55.94552313663306)
                }),
                new NamedRegion("Bayes Central Area", new LngLat[]{
                        new LngLat(-3.1876927614212036, 55.94520696732767),
                        new LngLat(-3.187555968761444, 55.9449621408666),
                        new LngLat(-3.186981976032257, 55.94505676722831),
                        new LngLat(-3.1872327625751495, 55.94536993377657),
                        new LngLat(-3.1874459981918335, 55.9453361389472),
                        new LngLat(-3.1873735785484314, 55.94519344934259),
                        new LngLat(-3.1875935196876526, 55.94515665035927),
                        new LngLat(-3.187624365091324, 55.94521973430925),
                        new LngLat(-3.1876927614212036, 55.94520696732767)
                })
        };

        NamedRegion centralArea = new NamedRegion(
                "central",
                new LngLat[]{new LngLat(-3.192473, 55.946233), new LngLat(-3.192473, 55.942617), new LngLat(-3.184319, 55.942617), new LngLat(-3.184319, 55.946233)}
        );
        Restaurant civerinosSlice = new Restaurant(
                "Civerinos Slice",
                new LngLat(-3.1912869215011597,55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)}
        );
        Restaurant soraLella = new Restaurant(
                "Sora Lella Vegan Restaurant",
                new LngLat(-3.202541470527649, 55.943284737579376),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},
                new Pizza[]{new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)}
        );
        Restaurant dominos = new Restaurant(
                "Domino's Pizza - Edinburgh - Southside",
                new LngLat(-3.1838572025299072, 55.94449876875712),
                new DayOfWeek[]{DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("Super Cheese", 1400), new Pizza("All Shrooms", 900)}
        );
        Restaurant sodebergPavilion = new Restaurant(
                "Sodeberg Pavilion",
                new LngLat(-3.1940174102783203, 55.94390696616939),
                new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("Proper Pizza", 1400), new Pizza("Pineapple & Ham & Cheese", 900)}
        );
        Restaurant[] definedRestaurants = {civerinosSlice, soraLella, dominos, sodebergPavilion};

        // Create an instance of ArgumentValidator
        ArgumentValidator argumentValidator = new ArgumentValidator();

        // Validate date (assuming a valid date)
        String date = "2023-11-15";
        assertEquals(true, argumentValidator.isValidDate(date));

        // Validate URL (assuming a valid URL)
        String validUrl = "https://ilp-rest.azurewebsites.net/";
        String invalidUrl = "not_a_valid_url";
        assertEquals(true, argumentValidator.isValidUrl(validUrl));
        assertEquals(false, argumentValidator.isValidUrl(invalidUrl));

        // Create an instance of OrderValidator
        OrderValidator orderValidator = new OrderValidator();

        // Create a sample order for testing
        Order sampleOrder = createSampleOrder(date, definedRestaurants);

        // Validate the sample order
        Order validatedOrder = orderValidator.validateOrder(sampleOrder, definedRestaurants);

        // Assert the expected order status and validation code
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());

        // Create an instance of FlightPaths
        FlightPaths flightPaths = new FlightPaths(new Order[]{validatedOrder}, definedRestaurants, centralArea, noFlyZones);
        HashMap<String, ArrayList<Move>> flightPathsFiles = flightPaths.flightPathList();

        // Create an instance of CreateFiles
        CreateFiles createFiles = new CreateFiles();

        // Write deliveries file
        createFiles.writeDeliveries(date, new Order[]{validatedOrder});

        // Assert order details in the generated deliveries file
        Path filePath2 = Paths.get(System.getProperty("user.dir"), "resultFiles", "deliveries-" + date + ".json");
        assertTrue("Deliveries file should have been created", Files.exists(filePath2));

        // Read the content of the deliveries file
        // Read and parse the content of the deliveries file using Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(filePath2.toFile());

            // Assuming there's only one entry in the deliveries file
            JsonNode deliveryEntry = jsonNode.get(0);

            // Check individual values
            assertEquals("123456", deliveryEntry.get("orderNo").asText());
            assertEquals("DELIVERED", deliveryEntry.get("orderStatus").asText());
            assertEquals("NO_ERROR", deliveryEntry.get("orderValidationCode").asText());
            assertEquals(2400, deliveryEntry.get("costInPence").asInt());

        } catch (Exception e) {
            // Handle exceptions, e.g., IOException or JsonProcessingException
            e.printStackTrace();
            Assert.fail("Exception occurred while reading or parsing the deliveries file.");
        }

        // Write flightpath file
        createFiles.writeFlightpath(date, flightPathsFiles);

        // Assert the expected starting and ending locations in flight paths
        Path filePath = Paths.get(System.getProperty("user.dir"), "resultFiles", "flightpath-" + date + ".json");
        assertTrue("Flightpath file should have been created", Files.exists(filePath));

        // Read and parse the content of the flight path file using Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(filePath.toFile());

            // Assuming there's only one entry in the flight path file
            JsonNode flightPathEntry = jsonNode.get(0);

            // Check individual values for the starting location
            assertEquals(-3.186874, flightPathEntry.get("fromLongitude").asDouble(), 0.0001); // Specify a delta for double comparison
            assertEquals(55.944494, flightPathEntry.get("fromLatitude").asDouble(), 0.0001);

            // Assuming there's at least one entry in the flight path file
            int lastIndex = jsonNode.size() - 1;
            JsonNode finalMove = jsonNode.get(lastIndex);

            // Check individual values for the final move's ending location (back at start point)
            assertEquals(-3.186874, finalMove.get("toLongitude").asDouble(), 0.0001); // Specify a delta for double comparison
            assertEquals(55.944494, finalMove.get("toLatitude").asDouble(), 0.0001);


        } catch (Exception e) {
            // Handle exceptions, e.g., IOException or JsonProcessingException
            e.printStackTrace();
            Assert.fail("Exception occurred while reading or parsing the flight path file.");
        }

        // Write drone file
        createFiles.writeDrone(date, flightPathsFiles);

        // Assert drone details in the generated drone file
        Path filePath1 = Paths.get(System.getProperty("user.dir"), "resultFiles", "drone-" + date + ".geojson");
        assertTrue("Drone file should have been created", Files.exists(filePath1));

    }

    // Helper method to create a sample order for testing
    private Order createSampleOrder(String date, Restaurant[] definedRestaurants) {
        Order sampleOrder = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        sampleOrder.setOrderNo("123456");
        sampleOrder.setCreditCardInformation(creditCardInfo);
        sampleOrder.setOrderDate((LocalDate.of(2023, 9, 1)));

        sampleOrder.setPriceTotalInPence(2400); //Total is pizzas + delivery charge of 100
        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms", 900);
        sampleOrder.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

        return sampleOrder;
    }
}

