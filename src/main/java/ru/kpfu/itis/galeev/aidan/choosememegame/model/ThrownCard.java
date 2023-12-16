package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleIntegerProperty;

public class ThrownCard {
    private MemeCard memeCard;
    private SimpleIntegerProperty votes = new SimpleIntegerProperty(0);

    public MemeCard getMemeCard() {
        return memeCard;
    }

    public int getVotes() {
        return votes.getValue();
    }

    public SimpleIntegerProperty votesProperty() {
        return votes;
    }

    public ThrownCard(MemeCard memeCard) {
        this.memeCard = memeCard;
    }

    public void increaseVotesValue() {
        votes.set(votes.intValue() + 1);
    }

    public void setVotes(int votes) {
        this.votes.set(votes);
    }
}
