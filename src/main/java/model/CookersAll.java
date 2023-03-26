package model;

import java.util.ArrayList;

public class CookersAll {
    private static ArrayList<Cook> cookers;

    public CookersAll() {
        this.cookers = new ArrayList<>();
    }

    public static ArrayList<Cook> getCookers() {
        return cookers;
    }

    public void checkNullsInJson () {
        for (Cook element: cookers) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.COOKERS_NULL);
                System. exit(0);
            }
        }
    }

    public void print() {
        for (Cook element: cookers) {
            element.printCook();
        }
    }
}
