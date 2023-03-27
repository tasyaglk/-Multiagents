package agents.models;

import static java.util.Objects.isNull;

public class Menu {
    private Integer menu_dish_id;
    private Integer menu_dish_card;
    private Integer menu_dish_price;
    private boolean menu_dish_active;

    public Menu() {}

    public Integer getMenuDishId() {
        return menu_dish_id;
    }

    public Integer getMenuDishCard() {
        return menu_dish_card;
    }

    public Integer getMenuDishPrice() {
        return menu_dish_price;
    }

    public boolean getMenuDishActive() {
        return menu_dish_active;
    }

    public boolean checkNulls() {
        if (isNull(menu_dish_id) || isNull(menu_dish_card) || isNull(menu_dish_price)) {
            return true;
        }
        return false;
    }

    public void printDishes() {
        System.out.println(menu_dish_id);
        System.out.println(menu_dish_card);
        System.out.println(menu_dish_price);
        System.out.println(menu_dish_active);
    }
}
