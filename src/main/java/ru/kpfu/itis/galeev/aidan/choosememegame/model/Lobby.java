package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;


public class Lobby {
    private String name;
    private User creator;
    private ObservableList<ClientHandler> usersInLobby;
    private final int lobbyCapacity;
    private String theme;
    private Server server;
    final int[] participantsCountWrapper = new int[]{0};
    private int timer = timeBeforeStart;
    private final static int minPlayersForStart = 2;
    private final static int timeBeforeStart = Config.TIME_BEFORE_START;


    public Lobby(User creator, Server server, int lobbyCapacity, String theme, int participantsCount, String name) {
        this.creator = creator;
        this.lobbyCapacity = lobbyCapacity;
        this.theme = theme;
        usersInLobby = FXCollections.observableArrayList();
        this.participantsCountWrapper[0] = participantsCount;
        this.name = name;
        this.server = server;
        Thread timerThread = new Thread(this::startTimer);
        timerThread.start();
        usersInLobby.addListener(new ListChangeListener<ClientHandler>() {
            @Override
            public void onChanged(Change<? extends ClientHandler> change) {
                while(change.next()) {
                    System.out.println("LOBBY CHANGE");
                    if (change.wasAdded()) {
                        System.out.println("added user");
                        System.out.println(change.getAddedSubList());
                        server.notifyChangeInLobby(true, creator.getUsername(), change.getAddedSubList().get(0).getUser());
                    } else if (change.wasRemoved()) {
                        System.out.println("removed user");
                        server.notifyChangeInLobby(false, creator.getUsername(), change.getRemoved().get(0).getUser());
                    }
                    participantsCountWrapper[0] = usersInLobby.size();
                }
            }
        });
    }


    public ObservableList<ClientHandler> getUsersInLobby() {
        return usersInLobby;
    }

    public void setUsersInLobby(ObservableList<ClientHandler> usersInLobby) {
        this.usersInLobby = usersInLobby;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getLobbyCapacity() {
        return lobbyCapacity;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getParticipantsCount() {
        return participantsCountWrapper[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public List<ClientHandler> getParticipants() {
        return usersInLobby;
    }

    public boolean isFull() {
        return lobbyCapacity == usersInLobby.size();
    }

    public void addUser(ClientHandler user) {
        usersInLobby.add(user);
    }

    public void removeUser(ClientHandler user) {
        usersInLobby.remove(user);
    }

    private void startTimer() {
        try {
            while (timer > 0) {
                Thread.sleep(1000);
                timer -= 1;
                server.notifyTimerUpdate(timer, creator);
            }
            if (getParticipantsCount() >= minPlayersForStart) {
                server.notifyStartGame(creator);
            } else {
                timer = timeBeforeStart;
                server.notifyNeedMorePlayersForStart(creator, getParticipantsCount(), minPlayersForStart);
                startTimer();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
