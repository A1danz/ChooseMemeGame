package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ClientHandler;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;

import java.util.List;

public class LobbySimple {
    private String name;
    private User creator;
    private List<User> usersInLobby;
    private final int lobbyCapacity;
    private String theme;
    private int participantsCount;


    public LobbySimple(User creator, int lobbyCapacity, String theme, int participantsCount, String name, List<User> participants) {
        this.creator = creator;
        this.lobbyCapacity = lobbyCapacity;
        this.theme = theme;
        usersInLobby = FXCollections.observableArrayList();
        this.participantsCount = participantsCount;
        this.name = name;
        this.usersInLobby = participants;
    }


    public List<User> getUsersInLobby() {
        return usersInLobby;
    }

    public void setUsersInLobby(List<User> usersInLobby) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public boolean isFull() {
        return lobbyCapacity == usersInLobby.size();
    }

    @Override
    public String toString() {
        return "LobbySimple{" +
                "name='" + name + '\'' +
                ", creator=" + creator +
                ", usersInLobby=" + usersInLobby +
                ", lobbyCapacity=" + lobbyCapacity +
                ", theme='" + theme + '\'' +
                ", participantsCount=" + participantsCount +
                '}';
    }
}
