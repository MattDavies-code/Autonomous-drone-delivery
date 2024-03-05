package uk.ac.ed.inf.scenariobased;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.flightPath.PathFinder;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathFinderTest {

    @Test
    void testBasicPathfinding() {
        PathFinder pathFinder = createPathFinderWithScenario("BasicPathfinding");

        // Updated start coordinates
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat destination = new LngLat(-3.184, 55.942);

        ArrayList<Move> path = pathFinder.findPath(start, destination, createSampleOrder());

        assertLngLatEqual(start, new LngLat(path.get(0).getFromLng(), path.get(0).getFromLat()));
        assertLngLatEqual(destination, new LngLat(path.get(path.size() - 1).getToLng(), path.get(path.size() - 1).getToLat()));
    }

    @Test
    void testObstacleAvoidance() {
        PathFinder pathFinder = createPathFinderWithScenario("ObstacleAvoidance");

        // Updated start coordinates
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat destination = new LngLat(-3.184, 55.942);

        ArrayList<Move> path = pathFinder.findPath(start, destination, createSampleOrder());

        assertLngLatEqual(start, new LngLat(path.get(0).getFromLng(), path.get(0).getFromLat()));
        assertLngLatEqual(destination, new LngLat(path.get(path.size() - 1).getToLng(), path.get(path.size() - 1).getToLat()));
    }

    @Test
    void testFarAwayRestaurant() {
        PathFinder pathFinder = createPathFinderWithScenario("FarAwayRestaurant");

        // Updated start coordinates
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat destination = new LngLat(-3.200, 55.940);

        ArrayList<Move> path = pathFinder.findPath(start, destination, createSampleOrder());

        assertLngLatEqual(start, new LngLat(path.get(0).getFromLng(), path.get(0).getFromLat()));
        assertLngLatEqual(destination, new LngLat(path.get(path.size() - 1).getToLng(), path.get(path.size() - 1).getToLat()));
    }

    @Test
    void testDifferentNoFlyZones() {
        PathFinder pathFinder = createPathFinderWithScenario("DifferentNoFlyZones");

        // Updated start coordinates
        LngLat start = new LngLat(-3.186874, 55.944494);
        LngLat destination = new LngLat(-3.184, 55.942);

        ArrayList<Move> path = pathFinder.findPath(start, destination, createSampleOrder());

        assertLngLatEqual(start, new LngLat(path.get(0).getFromLng(), path.get(0).getFromLat()));
        assertLngLatEqual(destination, new LngLat(path.get(path.size() - 1).getToLng(), path.get(path.size() - 1).getToLat()));
    }

    private PathFinder createPathFinderWithScenario(String scenario) {
        NamedRegion[] noFlyZones;
        NamedRegion centralArea;

        switch (scenario) {
            case "BasicPathfinding":
                // Set up a basic scenario with no no-fly zones and a standard central area
                noFlyZones = new NamedRegion[0];
                centralArea = new NamedRegion("Standard Central Area", new LngLat[]{
                        new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.942617),
                        new LngLat(-3.184319, 55.942617),
                        new LngLat(-3.184319, 55.946233)
                });
                break;

            case "ObstacleAvoidance":
                // Set up a scenario with randomly generated no-fly zones and a standard central area
                noFlyZones = generateRandomNoFlyZones(3); // Adjust the number of no-fly zones as needed
                centralArea = new NamedRegion("Standard Central Area", new LngLat[]{
                        new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.942617),
                        new LngLat(-3.184319, 55.942617),
                        new LngLat(-3.184319, 55.946233)
                });
                break;

            case "FarAwayRestaurant":
                // Set up a scenario with a restaurant far away from the central area
                noFlyZones = new NamedRegion[0];
                centralArea = new NamedRegion("Standard Central Area", new LngLat[]{
                        new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.942617),
                        new LngLat(-3.184318, 55.942617),
                        new LngLat(-3.184318, 55.946233)
                });
                break;

            case "DifferentNoFlyZones":
                // Set up a scenario with randomly generated different no-fly zones
                noFlyZones = generateRandomNoFlyZones(8); // Adjust the number of no-fly zones as needed
                centralArea = new NamedRegion("Standard Central Area", new LngLat[]{
                        new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.942617),
                        new LngLat(-3.184319, 55.942617),
                        new LngLat(-3.184319, 55.946233)
                });
                break;

            default:
                throw new IllegalArgumentException("Unsupported scenario: " + scenario);
        }

        return new PathFinder(noFlyZones, centralArea);
    }

    private NamedRegion[] generateRandomNoFlyZones(int numberOfZones) {
        NamedRegion[] noFlyZones = new NamedRegion[numberOfZones];
        for (int i = 0; i < numberOfZones; i++) {
            // Generate random vertices for each no-fly zone
            LngLat[] vertices = new LngLat[4];
            for (int j = 0; j < 4; j++) {
                // Generate random coordinates (modify the range as needed)
                double lng = Math.random() * (-3.18 - (-3.20)) + (-3.20);
                double lat = Math.random() * (55.94 - 55.92) + 55.92;
                vertices[j] = new LngLat(lng, lat);
            }
            noFlyZones[i] = new NamedRegion("No-Fly Zone " + (i + 1), vertices);
        }
        return noFlyZones;
    }


    private void assertLngLatEqual(LngLat expected, LngLat actual) {
        assertEquals(expected.lng(), actual.lng(), 0.0001);
        assertEquals(expected.lat(), actual.lat(), 0.0001);
    }

    // Helper method to create a sample order for testing
    private Order createSampleOrder() {
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
