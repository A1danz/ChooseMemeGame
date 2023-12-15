package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private SimpleIntegerProperty readyCounter = new SimpleIntegerProperty(0);

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

        readyCounter.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == usersInGame.size()) {
                    server.notifyAllReady(creator.getUsername());
                    startTimer(creator.getUsername());
                }
            }
        });


        List<Situation> shuffledSituations = new ArrayList<>(Config.GAME_THEMES.get(lobby.getTheme()).getSituations());
        Collections.shuffle(shuffledSituations);
        this.situations = new Stack<>();
        situations.addAll(shuffledSituations);

        List<MemeCard> shuffledMemeCards = new ArrayList<>(Config.GAME_THEMES.get(lobby.getTheme()).getMemeImgs());
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

    public Stack<MemeCard> getMemeCards() {
        return memeCards;
    }

    public void setMemeCards(Stack<MemeCard> memeCards) {
        this.memeCards = memeCards;
    }
    public void increaseReadyPlayersCount() {
        readyCounter.set(readyCounter.get() + 1);
    }

    private void startTimer(String gameOwner) {
        new Thread(() -> {
            int t = 10;
            while (t > 0) {
                try {
                    Thread.sleep(1000);
                    t--;
                    server.notifyStartGameTimer(gameOwner, t);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
