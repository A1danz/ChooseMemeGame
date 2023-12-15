package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemeCard memeCard = (MemeCard) o;
        return Objects.equals(pathToCard, memeCard.pathToCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathToCard);
    }
}
