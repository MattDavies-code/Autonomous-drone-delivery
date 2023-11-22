package uk.ac.ed.inf.FlightPath;

import org.junit.Test;
import uk.ac.ed.inf.flightPath.PathFinder;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.validator.OrderValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PathFinderTest {

    @Test
    public void findPath_ValidOrderAndLocation_ReturnsValidPath() {
        Order order = createMockOrder();
        Restaurant restaurant = createMockRestaurant();

        OrderValidator orderValidator = new OrderValidator();
        orderValidator.validateOrder(order, new Restaurant[]{restaurant});

        NamedRegion centralArea = createMockCentralArea();
        NamedRegion[] noFlyZones = createMockNoFlyZones();

        PathFinder pathFinder = new PathFinder(noFlyZones, centralArea);
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        ArrayList<Move> result = pathFinder.findPath(appletonTower, restaurant.location(), order);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    private Order createMockOrder() {
        Order order = new Order();
        order.setOrderNo("1");
        order.setCreditCardInformation(new CreditCardInformation("1234567891234567", "03/24", "123"));
        order.setOrderDate(LocalDate.of(2023, 9, 1));
        order.setPriceTotalInPence(1500);
        order.setPizzasInOrder(new Pizza[]{new Pizza("Calzone", 1400)});
        return order;
    }

    private Restaurant createMockRestaurant() {
        return new Restaurant("Civerinos Slice", new LngLat(-3.1912869215011597,55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)});
    }

    private NamedRegion createMockCentralArea() {
        return new NamedRegion("central", new LngLat[]{
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)});
    }

    private NamedRegion[] createMockNoFlyZones() {
        return new NamedRegion[]{
                new NamedRegion("no-fly-zone", new LngLat[]{
                        new LngLat(-3.184319, 55.946233),
                        new LngLat(-3.184319, 55.942617),
                        new LngLat(-3.176165, 55.942617),
                        new LngLat(-3.176165, 55.946233)})
        };
    }
}
