package agents.models;

import java.util.ArrayList;
import java.util.List;


public class MenuDishes {
    private static ArrayList<Menu> menu_dishes;

    public MenuDishes() {
        this.menu_dishes = new ArrayList<>();
    }

    public static List<Menu> getMenuDishes() {
        return menu_dishes;
    }

    public void checkNullsInJson () {
        for (Menu element: menu_dishes) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.MENU_DISHES_NULL);
                System. exit(0);
            }
        }
    }

    public void print() {
        for (Menu element: menu_dishes) {
            element.printDishes();
        }
    }
}
