package uk.ac.ed.inf.validator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.inf.validator.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Unit test for OrderValidator.
 */
public class OrderValidatorTest
{
    @Test
    public void testValidOrder() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setOrderDate((LocalDate.of(2023, 9, 1)));

        order.setPriceTotalInPence(2400); //Total is pizzas + delivery charge of 100
        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms", 900);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

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
        Order validatedOrder = new OrderValidator().validateOrder(order,definedRestaurants);

        // Assert
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }
    @Test
    public void testCardNumberInvalid() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1212",
                "03/24",
                "111"
        );
        order.setCreditCardInformation(creditCardInfo);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testExpiriyDateInvalidNumber() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "011/011",
                "111"
        );
        order.setCreditCardInformation(creditCardInfo);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testExpiriyDateInvalidDate() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "01/22",
                "111"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setOrderDate((LocalDate.of(2023, 9, 1)));
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }


    @Test
    public void testCVVInvalid() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "12345"
        );
        order.setCreditCardInformation(creditCardInfo);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testTotalCorrect() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(9999);

        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms", 900);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }


    @Test
    public void testPizzaDefined() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(2400); //Total is pizzas + delivery charge of 100
        // Wrong Pizza Names
        Pizza pizza1 = new Pizza("Wrong Pizza1", 900);
        Pizza pizza2 = new Pizza("Wrong Pizza2",1400);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

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
        Order validatedOrder = new OrderValidator().validateOrder(order,definedRestaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, validatedOrder.getOrderValidationCode());
    }


    @Test
    public void testMaxPizzaCount(){
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(6300); //Total is pizzas + delivery charge of 100
        // Maximum number of pizzas is 4
        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms", 900);
        Pizza pizza3 = new Pizza("Calzone",1400);
        Pizza pizza4 = new Pizza("Super Cheese", 1400);
        Pizza pizza5 = new Pizza("Vegan Delight", 1100);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2, pizza3, pizza4, pizza5});

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testPizzaInOrder() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(100); //Total is pizzas + delivery charge of 100
        order.setPizzasInOrder(new Pizza[]{});

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[0]);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testMultipleRestaurantPizza() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(2400); //Total is pizzas + delivery charge of 100
        // All Shrooms: Domino's Pizza, Calzone: Civerinos Slice
        Pizza pizza1 = new Pizza("All Shrooms", 900);
        Pizza pizza2 = new Pizza("Calzone",1400);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});

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
        Order validatedOrder = new OrderValidator().validateOrder(order,definedRestaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, validatedOrder.getOrderValidationCode());
    }

    @Test
    public void testRestaurantClosed() {
        Order order = new Order();
        CreditCardInformation creditCardInfo = new CreditCardInformation("1234567891234567",
                "03/24",
                "123"
        );
        order.setCreditCardInformation(creditCardInfo);
        order.setPriceTotalInPence(2400); //Total is pizzas + delivery charge of 100
        Pizza pizza1 = new Pizza("Super Cheese",1400);
        Pizza pizza2 = new Pizza("All Shrooms", 900);
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});
        //Day of month is different to Dominos openings
        order.setOrderDate((LocalDate.of(2023, 10, 2)));

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
        Order validatedOrder = new OrderValidator().validateOrder(order,definedRestaurants);

        // Assert
        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, validatedOrder.getOrderValidationCode());
    }
}
