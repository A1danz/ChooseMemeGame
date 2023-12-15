package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameSimple {
    private User creator;
    private ObservableList<GameUserSimple> usersInGame = FXCollections.observableArrayList();
    private GameUserSimple user;
    private SimpleIntegerProperty memeCardsCount = new SimpleIntegerProperty();
    private SimpleIntegerProperty situationsCount = new SimpleIntegerProperty();
    private SimpleStringProperty situation = new SimpleStringProperty();
    private ObservableList<MemeCard> userCards = FXCollections.observableArrayList();
    private SimpleBooleanProperty gameStarted = new SimpleBooleanProperty();

    public GameSimple(User creator, List<GameUserSimple> usersInGame, int memeCardsCount, int situationsCount, User clientUser) {
        this.creator = creator;
        usersInGame.forEach((participant) -> {
            if (participant.getUser().equals(clientUser)) {
                user = new GameUserSimple(participant.getUser());
            } else {
                this.usersInGame.add(new GameUserSimple(participant.getUser()));
            }
        });
        this.memeCardsCount.set(memeCardsCount);
        this.situationsCount.set(situationsCount);
        this.situation.set("");
        gameStarted.set(false);
    }

    public User getCreator() {
        return creator;
    }

    public ObservableList<GameUserSimple> getUsersInGame() {
        return usersInGame;
    }

    public GameUserSimple getUser() {
        return user;
    }

    public int getMemeCardsCount() {
        return memeCardsCount.get();
    }

    public int getSituationsCount() {
        return situationsCount.get();
    }

    @Override
    public String toString() {
        return "GameSimple{" +
                "creator=" + creator +
                ", usersInGame=" + usersInGame +
                ", user=" + user +
                ", memeCardsCount=" + memeCardsCount +
                ", situationsCount=" + situationsCount +
                ", situation=" + situation +
                ", userCards=" + userCards +
                '}';
    }
}
