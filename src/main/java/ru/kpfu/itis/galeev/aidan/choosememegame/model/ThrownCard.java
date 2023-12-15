package ru.kpfu.itis.galeev.aidan.choosememegame.model;

public class ThrownCard {
    private MemeCard memeCard;
    private int votes;

    public MemeCard getMemeCard() {
        return memeCard;
    }

    public int getVotes() {
        return votes;
    }

    public ThrownCard(MemeCard memeCard) {
        this.memeCard = memeCard;
        this.votes = 0;
    }
}
