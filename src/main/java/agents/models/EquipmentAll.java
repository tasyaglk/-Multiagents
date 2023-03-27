package agents.models;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAll {
    private static ArrayList<Equipment> equipment;

    public EquipmentAll() {
        this.equipment = new ArrayList<>();
    }

    public static List<Equipment> getEquipment() {
        return equipment;
    }

    public void checkNullsInJson () {
        for (Equipment element: equipment) {
            if (element.checkNulls()) {
                System.out.println(MessageStorage.EQUIPMENT_NULL);
                System. exit(0);
            }
        }
    }

    public void print() {
        for (Equipment element: equipment) {
            element.printEquipment();
        }
    }
}
