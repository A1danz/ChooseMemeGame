package ru.kpfu.itis.galeev.aidan.choosememegame.model;

public class GameTheme {
    private String name;
    private String pathToImgs;

    public GameTheme(String name, String pathToImgs) {
        this.name = name;
        this.pathToImgs = pathToImgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathToImgs() {
        return pathToImgs;
    }

    public void setPathToImgs(String pathToImgs) {
        this.pathToImgs = pathToImgs;
    }
}
