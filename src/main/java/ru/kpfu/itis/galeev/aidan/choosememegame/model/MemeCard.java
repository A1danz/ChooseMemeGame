package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;

public class MemeCard {
    private String pathToCard;

    public MemeCard(String pathToCard) {
        this.pathToCard = pathToCard;
    }

    public String getPathToCard(String memesThemeName) {
        return pathToCard;
    }

    public void setPathToCard(String pathToCard) {
        this.pathToCard = pathToCard;
    }

    @Override
    public String toString() {
        return "MemeCard{" +
                "pathToCard='" + pathToCard + '\'' +
                '}';
    }
}
