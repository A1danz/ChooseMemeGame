package ru.kpfu.itis.galeev.aidan.choosememegame.config;

import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Config {
    Integer PORT = 4555;
    String HOST = "127.0.0.1";
    String START_SCENE = "start-page-view.fxml";
    String MENU_SCENE = "menu-view.fxml";
    String LOBBY_SCENE = "waiting-lobby.fxml";
    int SCENE_WIDTH = 900;
    int SCENE_HEIGHT = 600;
    Map<String, GameData> GAME_THEMES = initGameThemes();
    int PLAYER_CARDS_COUNT = 6;

    private static Map<String, GameData> initGameThemes() {
        HashMap<String, GameData> gameThemes = new HashMap<>();
        File allMemes = new File(MainApplication.class.getResource("memes").getPath());

        // default memes theme
        ArrayList<MemeCard> defaultMemeCardsPaths = new ArrayList<>();
        File defaultMemes = new File(allMemes.getPath() + "/default");
        if (defaultMemes.listFiles() != null) {
            for (File file : defaultMemes.listFiles()) {
                defaultMemeCardsPaths.add(new MemeCard("memes/default/" + file.getName()));
            }
        }

        ArrayList<Situation> defaultSituations = new ArrayList<>();
        InputStream inputStream = MainApplication.class.getResourceAsStream("situations/default.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                defaultSituations.add(new Situation(line));
            }
        } catch (IOException ex) {
            throw new RuntimeException();
        }

        gameThemes.put("default", new GameData(defaultMemeCardsPaths, defaultSituations, "Стандартная"));
        return gameThemes;
    }
}
