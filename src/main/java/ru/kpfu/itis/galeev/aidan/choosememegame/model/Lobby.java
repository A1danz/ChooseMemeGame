package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;


public class Lobby {
    private User creator;
    private ObservableList<ClientHandler> usersInLobby;
    private final int lobbyCapacity;
    private String theme;
    final int[] participantsCountWrapper = new int[]{0};


    public Lobby(User creator, Server server, int lobbyCapacity, String theme, int participantsCount) {
        this.creator = creator;
        this.lobbyCapacity = lobbyCapacity;
        this.theme = theme;
        usersInLobby = FXCollections.observableArrayList();
        this.participantsCountWrapper[0] = participantsCount;
        usersInLobby.addListener(new ListChangeListener<ClientHandler>() {
            @Override
            public void onChanged(Change<? extends ClientHandler> change) {
                while(change.next()) {
                    if (change.wasAdded()) {
                        server.notifyChangeInLobby(true, creator.getUsername(), change.getAddedSubList().get(0).getUser());
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
}
