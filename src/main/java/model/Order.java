package model;

import java.util.ArrayList;

public class Order {
    private static ArrayList<Customer> visitors_orders;

    public Order() {
        this.visitors_orders = new ArrayList<>();
    }

    public static ArrayList<Customer> getVisitorsOrders() {
        return visitors_orders;
    }

    public void checkNullsInJson () {
        for (Customer element: visitors_orders) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.VISITOR_ORDERS_NULL);
                System. exit(0);
            }
        }
    }
    public void print() {
        for (Customer element: visitors_orders) {
            element.printCustomer();
        }
    }
}
