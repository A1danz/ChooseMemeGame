package ru.kpfu.itis.galeev.aidan.choosememegame.model;

public class Situation {
    private String situationText;

    public String getSituationText() {
        return situationText;
    }

    public void setSituationText(String situationText) {
        this.situationText = situationText;
    }

    public Situation(String situationText) {
        this.situationText = situationText;
    }

    @Override
    public String toString() {
        return "Situation{" +
                "situationText='" + situationText + '\'' +
                '}';
    }
}
