package model;

import static java.util.Objects.isNull;

public class Cook {
    private Integer cook_id;
    private String cook_name;
    private boolean cook_active;

    public Integer getCookId() {
        return cook_id;
    }

    public String getCookName() {
        return cook_name;
    }

    public boolean getCookActive() {
        return cook_active;
    }

    public void changeCookActive() {
        cook_active = !cook_active;
    }


    public boolean checkNulls() {
        return isNull(cook_id) || isNull(cook_name) || isNull(cook_active);
    }

    public void printCook() {
        System.out.println(cook_id);
        System.out.println(cook_name);
        System.out.println(cook_active);
    }
}
