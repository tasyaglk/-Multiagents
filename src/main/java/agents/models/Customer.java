package agents.models;

import java.util.ArrayList;

import static java.util.Objects.isNull;

public class Customer {
    private String vis_name;
    private Integer vis_ord_total;
    private ArrayList<Integer> vis_ord_dishes;

    public Customer() {
        this.vis_ord_dishes = new ArrayList<>();
    }

    public String getVisName() {
        return vis_name;
    }

    public Integer getVisOrdTotal() {
        return vis_ord_total;
    }

    public ArrayList<Integer> getVisOrdDishes() {
        return vis_ord_dishes;
    }

    public boolean checkNulls() {
        if (isNull(vis_name) || isNull(vis_ord_total)) {
            return true;
        }
        for (Integer n : vis_ord_dishes) {
            if (isNull(n)) {
                return true;
            }
        }
        return false;
    }
    public void printCustomer() {
        System.out.println(vis_name);
        System.out.println(vis_ord_total);
        for (Integer n : vis_ord_dishes) {
            System.out.println(n);
        }
    }
}
