package agents.models;

import java.util.List;

import static java.util.Objects.isNull;

public class Dish {
    private Integer card_id;
    private Float card_time;
    private static List<Operations> operations;

    public Dish() {}

    public Integer getCardId() {
        return card_id;
    }

    public Float getCardTime() {
        return card_time;
    }

    public static List<Operations> getOperations() {
        return operations;
    }

    public Integer timeOnDish() {
        return Math.round(card_time * 60);
    }

    public boolean checkNulls() {
        if (isNull(card_id) || isNull(card_time)) {
            return true;
        }
        for (Operations n : operations) {
            return n.checkNullsOperations();
        }
        return false;
    }

    public void printDish() {
        System.out.println(card_id);
        System.out.println(card_time);
        for (Operations n : operations) {
            n.printOperation();
        }
    }
}
