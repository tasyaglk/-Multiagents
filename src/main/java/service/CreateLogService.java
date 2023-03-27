package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class CreateLogService {

    private static ArrayList<String> logs;

    public CreateLogService() {}

    public static void addLog(String s) {
        logs.add(s + '\n');
    }
    public static void putLog() throws IOException {
        Path inFilePath = Path.of("src\\main\\resources\\logs.json");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(logs);
        Files.writeString(inFilePath, json);
    }
}
