package model;

import model.QuantityOperations;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class Operations {
    private Integer oper_type;
    private Integer equip_type;
    private Float oper_time;
    private static ArrayList<QuantityOperations> oper_products;

    public Operations() {
        this.oper_products = new ArrayList<>();
    }

    public static List<QuantityOperations> getOperProducts() {
        return oper_products;
    }

    public Integer getEquipType() {
        return equip_type;
    }

    public boolean checkNullsOperations() {
        if (isNull(oper_type) || isNull(equip_type) || isNull(oper_time)) {
            return true;
        }
        for (QuantityOperations n : oper_products) {
            return n.checkNullsQuantity();
        }
        return false;
    }

    public void printOperation() {
        System.out.println(oper_type);
        System.out.println(equip_type);
        System.out.println(oper_time);
        for (QuantityOperations n : oper_products) {
            n.printQuantity();
        }
    }
}
