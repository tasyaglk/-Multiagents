package agents.models;

import static java.util.Objects.isNull;

public class QuantityOperations {
    private Integer prod_type;
    private Float prod_quantity;

    public Integer getProdType() {
        return prod_type;
    }

    public Float getProdQuantity() {
        return prod_quantity;
    }

    public boolean checkNullsQuantity() {
        if (isNull(prod_type) || isNull(prod_quantity)) {
            return true;
        }
        return false;
    }

    public void printQuantity() {
        System.out.println(prod_type);
        System.out.println(prod_quantity);
    }
}
