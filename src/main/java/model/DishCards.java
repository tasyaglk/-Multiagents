package model;

import java.util.ArrayList;
import java.util.List;

public class DishCards {
    private static ArrayList<Dish> dish_cards;

    public DishCards() {
        this.dish_cards = new ArrayList<>();
    }

    public static List<Dish> getDishCards() {
        return dish_cards;
    }

    public void checkNullsInJson () {
        for (Dish element: dish_cards) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.DISH_CARDS_NULL);
                System. exit(0);
            }
        }
    }

    public void print() {
        for (Dish element: dish_cards) {
            element.printDish();
        }
    }
}
