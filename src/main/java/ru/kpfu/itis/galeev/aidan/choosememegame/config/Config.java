package ru.kpfu.itis.galeev.aidan.choosememegame.config;

import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;

import java.io.*;
import java.util.*;

public interface Config {
    Integer PORT = 4555;
    String HOST = "127.0.0.1";
    String START_SCENE = "start-page-view.fxml";
    String MENU_SCENE = "menu-view.fxml";
    String LOBBY_SCENE = "waiting-lobby.fxml";
    String GAME_SCENE = "game-view.fxml";
    List<String> defaultAvatarPaths = List.of(
            "default/1.png", "default/2.png", "default/anaconda.png", "default/armadillo.png", "default/bear.png",
            "default/bird.png", "default/butterfly.png", "default/deer.png", "default/ganesha.png", "default/jacutinga.png",
            "default/jaguar.png", "default/turtle.png"
            );
    int SCENE_WIDTH = 900;
    int SCENE_HEIGHT = 600;
    Map<String, String> MATCH_THEMES = new HashMap<>();
    Map<String, GameData> GAME_THEMES = initGameThemes();
    int PLAYER_CARDS_COUNT = 6;
    int TIME_BEFORE_START = 12;
    int WIN_POINTS = 100;
    int SECONDS_FOR_THROW_CARD = 4;
    int SECONDS_FOR_VOTE = 4;
    int SECONDS_BEFORE_NEW_ROUND = 2;

    private static Map<String, GameData> initGameThemes() {
        HashMap<String, GameData> gameThemes = new HashMap<>();
        File allMemes = new File(MainApplication.class.getResource("memes").getPath());

        // default memes theme
        ArrayList<MemeCard> defaultMemeCardsPaths = new ArrayList<>();
        File defaultMemes = new File(allMemes.getPath() + "/default");
        int count = 0;
        if (defaultMemes.listFiles() != null) {
            for (File file : defaultMemes.listFiles()) {
                count++;
                if (count > 14) {
                    break;
                }
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

        String serverThemeName = "default";
        String clientThemeName = "Стандартная";
        gameThemes.put(serverThemeName, new GameData(defaultMemeCardsPaths, defaultSituations, clientThemeName));
        MATCH_THEMES.put(clientThemeName, serverThemeName);
        return gameThemes;
    }
}
