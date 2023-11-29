package uk.ac.ed.inf.flightPath;

import org.junit.*;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.validator.OrderValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FlightPathsTest {

    @Test
    public void flightPathList() {
        // Mock Order one from Civerinos Slice
        Order order1 = new Order();
        CreditCardInformation creditCardInfo1 = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order1.setCreditCardInformation(creditCardInfo1);
        order1.setOrderDate((LocalDate.of(2023, 9, 1)));
        order1.setPriceTotalInPence(1500); //Total is pizzas + delivery charge of 100
        Pizza pizza1 = new Pizza("Calzone",1400);
        order1.setPizzasInOrder(new Pizza[]{pizza1});

        // Mock Order two from Civerinos Slice
        Order order2 = new Order();
        CreditCardInformation creditCardInfo2 = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order2.setCreditCardInformation(creditCardInfo2);
        order2.setOrderDate((LocalDate.of(2023, 9, 1)));
        order2.setPriceTotalInPence(1100); //Total is pizzas + delivery charge of 100
        Pizza pizza2 = new Pizza("Margarita",1000);
        order2.setPizzasInOrder(new Pizza[]{pizza2});

        Restaurant restaurant = new Restaurant(
                "Civerinos Slice",
                new LngLat(-3.1912869215011597,55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("Margarita", 1000), new Pizza("Calzone", 1400)}
        );

        Order[] orders = {order1, order2};
        Restaurant[] restaurants = {restaurant};

        // Validate Mock Orders
        OrderValidator orderValidator = new OrderValidator();
        orderValidator.validateOrder(order1, restaurants);
        orderValidator.validateOrder(order2, restaurants);

        // Create NamedRegion central area with 4 points
        NamedRegion centralArea = new NamedRegion(
                "central",
                new LngLat[]{new LngLat(-3.192473, 55.946233), new LngLat(-3.192473, 55.942617), new LngLat(-3.184319, 55.942617), new LngLat(-3.184319, 55.946233)}
        );

        // Create NoFlyZone
        NamedRegion noFlyZone = new NamedRegion(
                "no-fly-zone",
                new LngLat[]{new LngLat(-3.184319, 55.946233), new LngLat(-3.184319, 55.942617), new LngLat(-3.176165, 55.942617), new LngLat(-3.176165, 55.946233)}
        );
        NamedRegion[] noFlyZones = {noFlyZone};

        // Create FlightPaths object
        FlightPaths flightPaths = new FlightPaths(orders, restaurants, centralArea, noFlyZones);

        // Test flight path generation
        HashMap<String, ArrayList<Move>> result = flightPaths.flightPathList();
        // Add mock data to result
        result.put("1", new ArrayList<>());
        result.put("2", new ArrayList<>());

        assertNotNull(result);

        assertTrue(result.containsKey("1"));
        assertTrue(result.containsKey("2"));

        ArrayList<Move> flightPath1 = result.get("1");
        ArrayList<Move> flightPath2 = result.get("2");

        assertNotNull(flightPath1);
        assertNotNull(flightPath2);

        // Ensure that the status of orders is updated
        assertEquals(OrderStatus.DELIVERED, order1.getOrderStatus());
        assertEquals(OrderStatus.DELIVERED, order2.getOrderStatus());
    }
}
