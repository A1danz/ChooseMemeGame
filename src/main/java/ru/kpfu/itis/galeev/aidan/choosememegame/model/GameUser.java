package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;

import java.util.LinkedList;

public class GameUser {
    private ClientHandler clientHandler;
    private LinkedList<MemeCard> cards;
    private SimpleIntegerProperty points;
    private SimpleBooleanProperty mustThrowCard;

    public GameUser(ClientHandler clientHandler, LinkedList<MemeCard> cards, SimpleIntegerProperty points, SimpleBooleanProperty mustThrowCard) {
        this.clientHandler = clientHandler;
        this.cards = cards;
        this.points = points;
        this.mustThrowCard = mustThrowCard;
    }


}
