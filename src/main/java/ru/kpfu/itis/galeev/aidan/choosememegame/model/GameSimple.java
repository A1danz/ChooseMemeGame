package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private SimpleBooleanProperty votingStarted = new SimpleBooleanProperty(false);

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

    public SimpleStringProperty getSituation() {
        return situation;
    }

    public SimpleStringProperty situationProperty() {
        return situation;
    }

    public int getMemeCardsCount() {
        return memeCardsCount.get();
    }

    public int getSituationsCount() {
        return situationsCount.get();
    }

    public void setSituationText(String textSituation) {
        situation.set(textSituation);
    }

    public boolean isVotingStarted() {
        return votingStarted.get();
    }

    public SimpleBooleanProperty votingStartedProperty() {
        return votingStarted;
    }

    public void setVotingStarted(boolean votingStarted) {
        this.votingStarted.set(votingStarted);
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
