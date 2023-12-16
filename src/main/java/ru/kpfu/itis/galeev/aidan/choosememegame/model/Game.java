package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.NoMoreCardsException;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private Server server;
    private User creator;
    private ObservableList<GameUser> usersInGame = FXCollections.observableArrayList();
    private Stack<Situation> situations;
    private Stack<MemeCard> memeCards;
    private SimpleIntegerProperty readyCounter = new SimpleIntegerProperty(0);
    private Map<String, ThrownCard> innerMap = new HashMap<>();
    private ObservableMap<String, ThrownCard> observableThrownCardsMap = FXCollections.observableMap(innerMap);
    private int votesCount = 0;
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
            GameUser gameUser = new GameUser(
                    participant,
                    userCards,
                    new SimpleIntegerProperty(0),
                    new SimpleBooleanProperty(false));
            gameUser.pointsProperty().addListener((observableValue, oldValue, newValue) -> {
                server.notifyPointsUpdated(creator.getUsername(), participant.getUser().getUsername(), newValue.intValue());
            });
            usersInGame.add(gameUser);
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
            startGameThread();
        }).start();
    }

    private void startGameThread() {
        boolean run = true;
        observableThrownCardsMap.addListener(new MapChangeListener<String, ThrownCard>() {
            @Override
            public void onChanged(Change<? extends String, ? extends ThrownCard> change) {
                if (change.wasAdded()) {
                    String cardOwner = change.getKey();
                    ThrownCard thrownCard = change.getValueAdded();
                    server.notifyUserThrowCard(creator.getUsername(), change.getKey(), change.getValueAdded());
                    thrownCard.votesProperty().addListener((observableValue, oldValue, newValue) -> {
                        server.notifyUserVoted(creator.getUsername(), cardOwner, newValue.intValue());
                    });
                }
            }
        });
        try {
            while (true) {
                server.notifyNewSituationCard(creator.getUsername(), situations.pop());
                int throwSituationCardViewDelay = 8;
                while (throwSituationCardViewDelay > 0) {
                    Thread.sleep(1000);
                    throwSituationCardViewDelay--;
                }
                server.notifyDropBigSituation(creator.getUsername());
                while (observableThrownCardsMap.keySet().size() != usersInGame.size()) {
                    Thread.sleep(100);
                }
                server.notifyStartVotingProcess(creator.getUsername());
                while (votesCount != usersInGame.size()) {
                    Thread.sleep(100);
                }
                determineWinners();
                Thread.sleep(10000);
                try {
                    prepareForNewRound();
                } catch (NoMoreCardsException ex) {
                    // todo
                }


            }
        } catch (InterruptedException ex) {
            System.out.println("interrupted");
        }
    }

    public void userThrowCard(String username, MemeCard card) {
        observableThrownCardsMap.put(username, new ThrownCard(card));
    }

    public void increaseVotes(String votedFor) {
        observableThrownCardsMap.get(votedFor).increaseVotesValue();
        votesCount++;
    }

    public void determineWinners() {
        Set<Map.Entry<String, ThrownCard>> usersThrownCards = observableThrownCardsMap.entrySet();
        List<Map.Entry<String, ThrownCard>> sortedThrownCards = usersThrownCards.stream()
                .sorted((o1, o2) -> o2.getValue().getVotes() - o1.getValue().getVotes())
                .toList();
        ArrayList<String> winners = new ArrayList<>();
        for (int i = 0; i < sortedThrownCards.size(); i++) {
            Map.Entry<String, ThrownCard> entry = sortedThrownCards.get(i);
            winners.add(entry.getKey());
            if (i != sortedThrownCards.size() - 2) {
                if (entry.getValue().getVotes() != sortedThrownCards.get(i + 1).getValue().getVotes()) {
                    break;
                }
            }
        }

        List<GameUser> winUsers = usersInGame.stream()
                .filter((user) -> winners.contains(user.getUser().getUsername()))
                .toList();

        winUsers.forEach((participant) -> participant.setPoints(participant.getPoints() + Config.WIN_POINTS));
    }

    private void prepareForNewRound() {
        for (GameUser user : usersInGame) {
            try {
                MemeCard card = memeCards.pop();
                user.getClientHandler().addCard(card.getPathToCard());
            } catch (EmptyStackException ex) {
                throw new NoMoreCardsException("no more cards in stack");
            }
        }
        updateMemeCardsCount(memeCards.size());
    }

    private void updateMemeCardsCount(int cardsCount) {
        usersInGame.forEach((participant) -> {
            participant.getClientHandler().updateMemeCardsCount(cardsCount);
        });
    }
}
