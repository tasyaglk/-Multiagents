package agents.models;

import static java.util.Objects.isNull;

public class Equipment {
    private Integer equip_id;
    private Integer equip_type;
    private String equip_name;
    private boolean equip_active;

    public Integer getEquipId() {
        return equip_id;
    }

    public Integer getEquipType() {
        return equip_type;
    }

    public String getEquipName() {
        return equip_name;
    }

    public boolean getEquipActive() {
        return equip_active;
    }

    public boolean checkNulls() {
        return isNull(equip_id) || isNull(equip_type) || isNull(equip_name);
    }

    public void changeEquipActivity() {
        equip_active = !equip_active;
    }

    public void printEquipment() {
        System.out.println(equip_id);
        System.out.println(equip_type);
        System.out.println(equip_name);
        System.out.println(equip_active);
    }
}
