package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Situation situation = (Situation) o;
        return Objects.equals(situationText, situation.situationText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(situationText);
    }
}
