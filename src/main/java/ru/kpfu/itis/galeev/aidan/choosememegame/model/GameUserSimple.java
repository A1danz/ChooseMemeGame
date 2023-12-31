package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;

public class GameUserSimple {
    private User user;
    private SimpleIntegerProperty points = new SimpleIntegerProperty();
    private SimpleBooleanProperty mustThrowCard = new SimpleBooleanProperty();
    private SimpleStringProperty throwableCard = new SimpleStringProperty();
    private SimpleIntegerProperty cardVotes = new SimpleIntegerProperty();
    private SimpleIntegerProperty cardsCount = new SimpleIntegerProperty();

    public GameUserSimple(User user) {
        this.user = user;
        points.set(0);
        mustThrowCard.set(false);
        throwableCard.set("");
        cardVotes.set(0);
        cardsCount.set(Config.PLAYER_CARDS_COUNT);
    }

    public User getUser() {
        return user;
    }

    public int getPoints() {
        return points.get();
    }

    public SimpleIntegerProperty pointsProperty() {
        return points;
    }

    public boolean isMustThrowCard() {
        return mustThrowCard.get();
    }

    public SimpleBooleanProperty mustThrowCardProperty() {
        return mustThrowCard;
    }

    public String getThrowableCard() {
        return throwableCard.get();
    }

    public SimpleStringProperty throwableCardProperty() {
        return throwableCard;
    }

    public int getCardVotes() {
        return cardVotes.get();
    }

    public int getCardsCount() {
        return cardsCount.get();
    }

    public SimpleIntegerProperty cardsCountProperty() {
        return cardsCount;
    }

    @Override
    public String toString() {
        return "GameUserSimple{" +
                "user=" + user +
                ", points=" + points +
                ", mustThrowCard=" + mustThrowCard +
                ", throwableCard=" + throwableCard +
                ", cardPoints=" + cardVotes +
                ", cardsCount=" + cardsCount +
                '}';
    }
}
