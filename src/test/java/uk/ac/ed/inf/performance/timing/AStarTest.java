package uk.ac.ed.inf.performance.timing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import uk.ac.ed.inf.flightPath.PathFinder;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

public class AStarTest {

    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        // Initialize your noFlyZones, centralArea, and PathFinder
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

        pathFinder = new PathFinder(noFlyZones, centralArea);
    }

    @ParameterizedTest
    @CsvSource({"1.0, 1.0", "0.75, 1.25", "1.0, 1.1", "1.25, 1.0","0.9, 1.1", "1.1, 1.1", "1.2, 1.2", "1.0, 1.25", "1.0, 1.2", "1.0, 1.16", "1.0, 1.175"})
    void testFindPath(double gWeight, double hWeight) {

        Restaurant soraLella = new Restaurant(
                "Sora Lella Vegan Restaurant",
                new LngLat(-3.202541470527649, 55.943284737579376),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},
                new Pizza[]{new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)}
        );

        Restaurant[] definedRestaurants = {soraLella};
        // Example order for testing using the furthest away restaurant
        Order order = createSampleOrder("2023-11-15", definedRestaurants);

        // Set up your start and end positions
        LngLat startPosition = new LngLat(-3.186874, 55.944494);
        LngLat endPosition = soraLella.location();

        // Set up your weights
        pathFinder.setWeights(gWeight, hWeight);


        long startTime = System.currentTimeMillis(); // Measure start time

        // Use assertTimeoutPreemptively to set a timeout for path generation
        Assertions.assertTimeoutPreemptively(
                Duration.ofSeconds(20), // Set your desired timeout duration
                () -> {
                    // Run the test with the specified weights
                    ArrayList<Move> path = pathFinder.findPath(startPosition, endPosition, order);

                    // Add your assertions or checks based on the expected behavior
                    Assertions.assertNotNull(path, "Path should not be null");
                    Assertions.assertFalse(path.isEmpty(), "Path should not be empty");
                    // Add more assertions as needed
                }
        );

        long endTime = System.currentTimeMillis(); // Measure end time

        // Log performance metrics
        System.out.println("Weights: g=" + gWeight + ", h=" + hWeight);
        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
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

        sampleOrder.setPriceTotalInPence(2600); //Total is pizzas + delivery charge of 100
        Pizza pizza1 = new Pizza("Meat Lover",1400);
        Pizza pizza2 = new Pizza("Vegan Delight", 1100);
        sampleOrder.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

        return sampleOrder;
    }
}

