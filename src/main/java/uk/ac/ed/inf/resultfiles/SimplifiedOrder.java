package uk.ac.ed.inf.resultfiles;

import uk.ac.ed.inf.ilp.data.Order;

public class SimplifiedOrder {
    private String orderNo;
    private String orderStatus;
    private String orderValidationCode;
    private int costInPence;

    // Constructors, getters, and setters (if needed)...

    // To create instances of SimplifiedOrder based on Order objects
    public static SimplifiedOrder fromOrder(Order order) {
        SimplifiedOrder simplifiedOrder = new SimplifiedOrder();
        simplifiedOrder.setOrderNo(order.getOrderNo());
        simplifiedOrder.setOrderStatus(order.getOrderStatus().toString());
        simplifiedOrder.setOrderValidationCode(order.getOrderValidationCode().toString());
        simplifiedOrder.setCostInPence(order.getPriceTotalInPence());
        return simplifiedOrder;
    }

    //** Setters **//
    private void setCostInPence(int priceTotalInPence) {
        this.costInPence = priceTotalInPence;
    }

    private void setOrderValidationCode(String validationCode) {
        this.orderValidationCode = validationCode;
    }

    private void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    private void setOrderNo(String orderNo) {
        this.orderNo = orderNo;

    }
}

