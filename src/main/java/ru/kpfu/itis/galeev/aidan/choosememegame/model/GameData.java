package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import java.util.List;

public class GameData {
    private List<MemeCard> memeImgs;
    private List<Situation> situations;
    private String dataName;

    public GameData(List<MemeCard> memeImgs, List<Situation> situations, String dataName) {
        this.memeImgs = memeImgs;
        this.situations = situations;
        this.dataName = dataName;
    }

    public List<MemeCard> getMemeImgs() {
        return memeImgs;
    }

    public List<Situation> getSituations() {
        return situations;
    }
}
