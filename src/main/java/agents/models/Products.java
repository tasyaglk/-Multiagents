package agents.models;

import static java.util.Objects.isNull;

public class Products {
    private Integer prod_item_id;
    private Integer prod_item_type;
    private String prod_item_name;
    private String prod_item_company;
    private String prod_item_unit;
    private Float prod_item_quantity;
    private Float prod_item_cost;
    private String prod_item_delivered;
    private String prod_item_valid_until;

    public Integer getProdItemId() {
        return prod_item_id;
    }

    public Integer getProdItemType() {
        return prod_item_type;
    }

    public String getProdItemName() {
        return prod_item_name;
    }

    public String getProdItemCompany() {
        return prod_item_company;
    }

    public String getProdItemUnit() {
        return prod_item_unit;
    }

    public Float getProdItemQuantity() {
        return prod_item_quantity;
    }

    public Float getProdItemCost() {
        return prod_item_cost;
    }

    public String getProdItemDelivered() {
        return prod_item_delivered;
    }

    public String getProdItemValidUntil() {
        return prod_item_valid_until;
    }

    public void changeProdItemQuantity(Float cost) {
        prod_item_quantity -= cost;
    }


    public boolean checkNulls() {
        return isNull(prod_item_id) || isNull(prod_item_type) || isNull(prod_item_name) || isNull(prod_item_company)
                || isNull(prod_item_unit) || isNull(prod_item_quantity) || isNull(prod_item_cost)
                || isNull(prod_item_delivered) || isNull(prod_item_valid_until);
    }

    public void printProducts() {
        System.out.println(prod_item_id);
        System.out.println(prod_item_type);
        System.out.println(prod_item_name);
        System.out.println(prod_item_company);
        System.out.println(prod_item_unit);
        System.out.println(prod_item_quantity);
        System.out.println(prod_item_cost);
        System.out.println(prod_item_delivered);
        System.out.println(prod_item_valid_until);
    }
}
