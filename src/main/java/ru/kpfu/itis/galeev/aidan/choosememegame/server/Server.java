package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Server {
    ServerSocket serverSocket;
    ArrayList<ClientHandler> clients = new ArrayList<>();
    HashMap<String, Lobby> lobbies = new HashMap<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(Config.PORT);
            lobbies.put("andrey", new Lobby(
                    new User("andrey"),
                    this,
                    10,
                    "standart",
                    2
            ));

            lobbies.put("Aidanich", new Lobby(
                    new User("Aidanich"),
                    this,
                    10,
                    "hasbik",
                    8
            ));
            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("CLIENT IS REGISTERED IN SERVER");
                ClientHandler clientHandler = new ClientHandler(client, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean usernameIsFree(String username) {
        boolean isFree = true;
        for (ClientHandler client : clients) {
            User user = client.getUser();
            if (user != null) {
                if (user.getUsername().equals(username)) {
                    isFree = false;
                    break;
                }
            }
        }

        return isFree;
    }

    public void notifyChangeInLobby(boolean entered, String lobbyCreator, User user) {
        Lobby lobby = lobbies.get(lobbyCreator);

        for (ClientHandler client : lobby.getUsersInLobby()) {
            client.sendInfoAboutUsersInLobby(user, entered);
        }
    }

    public List<Lobby> getLobbies() {
        return lobbies.values().stream().toList();
    }

    public void disconnect(ClientHandler disconnectedClient) {
        System.out.println("USER DISCONNECTED: " + disconnectedClient.getUser().getUsername());
        clients.remove(disconnectedClient);
    }
}
