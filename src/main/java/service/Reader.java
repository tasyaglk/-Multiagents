package service;

import java.io.IOException;

public interface Reader {
    void fillInAgents() throws IOException;

    void readJsonOrder(String stringToFile) throws IOException;

    void readJsonMenu(String stringToFile) throws IOException;

    void readJsonDish(String stringToFile) throws IOException;

    void readJsonProducts(String stringToFile) throws IOException;

    void readJsonEquipment(String stringToFile) throws IOException;

    void readJsonCook(String stringToFile) throws IOException;
}
