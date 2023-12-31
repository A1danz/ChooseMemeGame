package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSimple {
    private User creator;
    private ObservableList<GameUserSimple> usersInGame = FXCollections.observableArrayList();
    private GameUserSimple user;
    private SimpleIntegerProperty memeCardsCount = new SimpleIntegerProperty();
    private SimpleIntegerProperty situationsCount = new SimpleIntegerProperty();
    private SimpleStringProperty situation = new SimpleStringProperty();
    private ObservableList<MemeCard> userCards = FXCollections.observableArrayList();
    private SimpleBooleanProperty votingStarted = new SimpleBooleanProperty(false);
    private SimpleStringProperty addedCard = new SimpleStringProperty("");
    private SimpleBooleanProperty newRoundBegin = new SimpleBooleanProperty(false);
    private SimpleIntegerProperty timerUpdates = new SimpleIntegerProperty(0);
    private ObservableList<Map.Entry<User, Integer>> winner = FXCollections.observableArrayList();

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

    public String getAddedCard() {
        return addedCard.get();
    }

    public SimpleStringProperty addedCardProperty() {
        return addedCard;
    }

    public void setAddedCard(String addedCard) {
        this.addedCard.set(addedCard);
    }

    public void setMemeCardsCount(int memeCardsCount) {
        this.memeCardsCount.set(memeCardsCount);
    }

    public SimpleIntegerProperty memeCardsCountProperty() {
        return memeCardsCount;
    }

    public boolean isNewRoundBegin() {
        return newRoundBegin.get();
    }

    public SimpleBooleanProperty newRoundBeginProperty() {
        return newRoundBegin;
    }

    public void setNewRoundBegin(boolean newRoundBegin) {
        this.newRoundBegin.set(newRoundBegin);
    }

    public int getTimerUpdates() {
        return timerUpdates.get();
    }

    public SimpleIntegerProperty timerUpdatesProperty() {
        return timerUpdates;
    }

    public void setTimerUpdates(int timerUpdates) {
        this.timerUpdates.set(timerUpdates);
    }

    public ObservableList<Map.Entry<User, Integer>> getWinner() {
        return winner;
    }

    public void addWinner(User user, int points) {
        winner.add(new AbstractMap.SimpleEntry<>(user, points));
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
