package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;

import java.util.*;

public class Game {
    private Server server;
    private User creator;
    private ObservableList<GameUser> usersInGame = FXCollections.observableArrayList();
    private Stack<Situation> situations;
    private Stack<MemeCard> memeCards;

    public Game(Server server, User creator, ObservableList<GameUser> usersInGame, List<Situation> situations, List<MemeCard> memeCards) {
        this.server = server;
        this.creator = creator;
        this.usersInGame = usersInGame;
        this.situations = null;
        this.memeCards = null;
    }

    public Game(Lobby lobby) {
        this.server = lobby.getServer();
        this.creator = lobby.getCreator();

        List<Situation> shuffledSituations = new ArrayList<>();
        Collections.copy(Config.GAME_THEMES.get(lobby.getTheme()).getSituations(), shuffledSituations);
        Collections.shuffle(shuffledSituations);
        this.situations = new Stack<>();
        situations.addAll(shuffledSituations);

        List<MemeCard> shuffledMemeCards = new ArrayList<>();
        Collections.copy(Config.GAME_THEMES.get(lobby.getTheme()).getMemeImgs(), shuffledMemeCards);
        Collections.shuffle(shuffledMemeCards);
        this.memeCards = new Stack<>();
        memeCards.addAll(shuffledMemeCards);

        for (ClientHandler participant : lobby.getUsersInLobby()) {
            LinkedList<MemeCard> userCards = new LinkedList<>();
            for (int i = 0; i < Config.PLAYER_CARDS_COUNT; i++) {
                userCards.add(memeCards.pop());
            }
            usersInGame.add(new GameUser(
                    participant,
                    userCards,
                    new SimpleIntegerProperty(0),
                    new SimpleBooleanProperty(false)
            ));
        }

    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public ObservableList<GameUser> getUsersInGame() {
        return usersInGame;
    }

    public void setUsersInGame(ObservableList<GameUser> usersInGame) {
        this.usersInGame = usersInGame;
    }

    public Stack<Situation> getSituations() {
        return situations;
    }

    public void setSituations(Stack<Situation> situations) {
        this.situations = situations;
    }


}
