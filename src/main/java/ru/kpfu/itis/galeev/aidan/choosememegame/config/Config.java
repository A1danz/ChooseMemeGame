package ru.kpfu.itis.galeev.aidan.choosememegame.config;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.GameTheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Config {
    Integer PORT = 4555;
    String HOST = "127.0.0.1";
    String START_SCENE = "start-page-view.fxml";
    String MENU_SCENE = "menu-view.fxml";
    int SCENE_WIDTH = 900;
    int SCENE_HEIGHT = 600;
    Map<String, String> GAME_THEMES = initGameThemes();

    private static Map<String, String> initGameThemes() {
        HashMap<String, String> gameThemes = new HashMap<>();
        gameThemes.put("Стандартная", "img/memes/default");

        return gameThemes;
    }
}
