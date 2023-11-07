package uk.ac.ed.inf.cw1;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates the order
 * Implements order validation interface
 */
public class OrderValidator implements OrderValidation {
    /**
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        /**
         * Validate Card Number
         */
        String creditCardNumber = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        if (creditCardNumber == null || !(creditCardNumber.matches("\\d{16}")) || !creditCardNumber.matches("\\d+")) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }
        /**
         * Validate Card Expiry Date (format)
         */
        String expiryDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        String[] expiryDateSplit = expiryDate.split("/");
        int month = Integer.parseInt(expiryDateSplit[0]);
        int year = Integer.parseInt(expiryDateSplit[1]);
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}") ||  !(month >0 && month <13 && year >22)){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        /**
         * Validate Expiration Date (date)
         */
        int orderMonth = orderToValidate.getOrderDate().getMonthValue();
        int orderYear = orderToValidate.getOrderDate().getYear()%1000;
        if (year < orderYear || (year == orderYear && month < orderMonth)){
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                return orderToValidate;
        }

        /**
         * Validate CVV
         */
        if (orderToValidate.getCreditCardInformation().getCvv() == null || !(orderToValidate.getCreditCardInformation().getCvv().matches("\\d{3}")) || !creditCardNumber.matches("\\d+")) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        /**
         * Validate if order has no pizzas
         */
        if (orderToValidate.getPizzasInOrder().length == 0) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        /**
         * Validate if total is correct
         */
        // The total includes an order charge
        int addedPrice = SystemConstants.ORDER_CHARGE_IN_PENCE;
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            addedPrice += pizza.priceInPence();
        }
        // Compares price of pizzas added together to total price defined in the pizza
        if (orderToValidate.getPriceTotalInPence() != addedPrice) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        /**
         * Validate if order exceeds max pizzas per order
         */
        if (orderToValidate.getPizzasInOrder().length > SystemConstants.MAX_PIZZAS_PER_ORDER) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        /**
         * Validate if Pizza Defined in Menu
         */
        // List of all pizzas from any restaurant
        List<String> allPizza = new ArrayList<>();
        for (Restaurant restaurant : definedRestaurants) {
            for (Pizza pizza : restaurant.menu()) {
                allPizza.add(pizza.name());
            }
        }
        // Check if list of all pizzas available contains the pizzas within the order
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            if (!(allPizza.contains(pizza.name()))) {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                return orderToValidate;
            }
        }

        /**
         * Validate Pizza From Multiple Restaurants
         */
        // Create a list of the restaurants in which the pizzas in the order
        List<String> restaurantNamesOfPizzas = new ArrayList<>();
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            for (Restaurant restaurant : definedRestaurants) {
                List<Pizza> pizzaOfRestaurant = new ArrayList<>(Arrays.asList(restaurant.menu()));
                if (pizzaOfRestaurant.contains(pizza)) {
                    restaurantNamesOfPizzas.add(restaurant.name());
                }
            }
        }
        // Check if all restaurants in the list are the same
        boolean differentRestaurants = false;
        for (String name : restaurantNamesOfPizzas) {
            if (!(name.equals(restaurantNamesOfPizzas.get(0)))) {
                differentRestaurants = true;
                break;
            }
        }
        if (differentRestaurants) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }

        /**
         * Validate Restaurant Open
         */
        // Can use restaurantNamesOfPizzas as previous validation eliminates multiple types
        LocalDate orderDate = orderToValidate.getOrderDate();
        DayOfWeek orderDay = orderDate.getDayOfWeek();
        String restaurantToCheck = restaurantNamesOfPizzas.get(0);
        boolean isRestaurantOpenOnDate = false;
        for (Restaurant restaurant : definedRestaurants) {
            if (restaurantToCheck.equals(restaurant.name())) {
                for (DayOfWeek openingDay : restaurant.openingDays()) {
                    if (openingDay == orderDay) {
                        isRestaurantOpenOnDate = true;
                        break;
                    }
                }
            }
        }

        if (!isRestaurantOpenOnDate){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }
}