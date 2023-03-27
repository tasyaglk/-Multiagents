package service;

import agents.models.*;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReaderService implements Reader {

    @SneakyThrows
    @Override
    public void fillInAgents() {
        String ordersString = "src\\main\\resources\\visitors_orders.json";
        readJsonOrder(ordersString);
        String menuString = "src\\main\\resources\\menu_dishes.json";
        readJsonMenu(menuString);
        String dishString = "src\\main\\resources\\dish_cards.json";
        readJsonDish(dishString);
        String productsString = "src\\main\\resources\\products.json";
        readJsonProducts(productsString);
        String equipmentString = "src\\main\\resources\\equipment.json";
        readJsonEquipment(equipmentString);
        String cookString = "src\\main\\resources\\cookers.json";
        readJsonCook(cookString);
    }

    @Override
    public void readJsonOrder(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.VISITOR_ORDERS_ERROR);
        Order orders = new Gson().fromJson(json, Order.class);
        orders.checkNullsInJson();
    }

    @Override
    public void readJsonMenu(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.MENU_DISHES_ERROR);
        MenuDishes menu = new Gson().fromJson(json, MenuDishes.class);
        menu.checkNullsInJson();
    }

    @Override
    public void readJsonDish(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.DISH_CARDS_ERROR);
        DishCards dishes = new Gson().fromJson(json, DishCards.class);
        dishes.checkNullsInJson();
    }

    @Override
    public void readJsonProducts(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.PRODUCTS_ERROR);
        Stock products = new Gson().fromJson(json, Stock.class);
        products.checkNullsInJson();
    }

    @Override
    public void readJsonEquipment(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.EQUIPMENT_ERROR);
        EquipmentAll equipment = new Gson().fromJson(json, EquipmentAll.class);
        equipment.checkNullsInJson();
    }

    @Override
    public void readJsonCook(String stringToFile) throws IOException {
        String json = pathToString(stringToFile);
        checkCorrectnessJson(json, MessageStorage.COOKERS_ERROR);
        CookersAll cook = new Gson().fromJson(json, CookersAll.class);
        cook.checkNullsInJson();
    }

    private void checkCorrectnessJson(String json, String message) {
        try {
            new Gson().fromJson(json, Object.class);
        } catch (com.google.gson.JsonSyntaxException ex) {
            System.out.println(message);
            System. exit(0);
        }
    }

    private String pathToString(String stringToFile) throws IOException {
        Path path = Path.of(stringToFile).normalize();
        return Files.readString(path);
    }
}
