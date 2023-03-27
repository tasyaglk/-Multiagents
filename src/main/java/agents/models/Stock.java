package agents.models;

import java.util.ArrayList;

public class Stock {
    private static ArrayList<Products> products;

    public Stock() {
        this.products = new ArrayList<>();
    }

    public static ArrayList<Products> getProducts() {
        return products;
    }

    public void checkNullsInJson () {
        for (Products element: products) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.PRODUCTS_NULL);
                System. exit(0);
            }
        }
    }

    public void print() {
        for (Products element: products) {
            element.printProducts();
        }
    }
}
