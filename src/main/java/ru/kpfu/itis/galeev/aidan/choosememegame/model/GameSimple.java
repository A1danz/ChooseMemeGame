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
    private SimpleIntegerProperty cardsCount = new SimpleIntegerProperty();
    private SimpleIntegerProperty situationsCount = new SimpleIntegerProperty();
    private SimpleStringProperty situation = new SimpleStringProperty();
    private ObservableList<MemeCard> userCards = FXCollections.observableArrayList();

    public GameSimple(User creator, List<GameUserSimple> usersInGame, int cardsCount, int situationsCount, User ownerUser) {
        this.creator = creator;
        usersInGame.forEach((participant) -> {
            if (participant.getUser().equals(ownerUser)) {
                user = new GameUserSimple(participant.getUser());
            } else {
                usersInGame.add(new GameUserSimple(participant.getUser()));
            }
        });
        this.cardsCount.set(cardsCount);
        this.situationsCount.set(situationsCount);
        this.situation.set("");
    }
}
