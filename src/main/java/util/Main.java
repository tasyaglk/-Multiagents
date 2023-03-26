package util;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        MainController mainController = new MainController();
        mainController.initAgents("agents");
    }
}