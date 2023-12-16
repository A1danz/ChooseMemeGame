package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;

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
    HashMap<String, Game> games = new HashMap<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(Config.PORT);
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
            if (!client.getUser().equals(user)) {
                client.sendInfoAboutUsersInLobby(user, entered);
            }
        }
    }

    public List<Lobby> getLobbies() {
        return lobbies.values().stream().toList();
    }

    public Lobby getLobby(String creatorUsername) {
        return lobbies.get(creatorUsername);
    }

    public void disconnect(ClientHandler disconnectedClient) {
        System.out.println("USER DISCONNECTED: " + disconnectedClient.getUser().getUsername());
        clients.remove(disconnectedClient);
    }

    public void notifyTimerUpdate(int time, User creator) {
        Lobby lobby = lobbies.get(creator.getUsername());
        if (lobby != null) {
            for (ClientHandler participant : lobby.getUsersInLobby()) {
                participant.updateLobbyTimer(time);
            }
        }
    }

    public void notifyStartGame(User creator) {
        Lobby lobby = lobbies.get(creator.getUsername());
        games.put(lobby.getCreator().getUsername(), new Game(lobby));
        if (lobby != null) {
            for (ClientHandler participant : lobby.getUsersInLobby()) {
                participant.notifyStartGame();
            }
        }
    }

    public void notifyNeedMorePlayersForStart(User creator, int participantsCount, int playerForStartCount) {
        Lobby lobby = lobbies.get(creator.getUsername());
        if (lobby != null) {
            for (ClientHandler participant : lobby.getUsersInLobby()) {
                participant.notifyNeedMorePlayers(participantsCount, playerForStartCount);
            }
        }
    }

    public void notifyAllReady(String gameOwner) {
        Game game = games.get(gameOwner);
        game.getUsersInGame().forEach((participant) -> {
            participant.getClientHandler().notifyAllReady();
        });
    }

    public void notifyStartGameTimer(String gameOwner, int seconds) {
        Game game = games.get(gameOwner);
        game.getUsersInGame().forEach((participant) -> {
            participant.getClientHandler().notifyStartGameTimer(seconds);
        });
    }

    public void notifyUserThrowCard(String gameOwner, String cardOwner, ThrownCard thrownCard) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyUserThrowCard(cardOwner, thrownCard);
            });
        }
    }

    public void notifyNewSituationCard(String gameOwner, Situation situation) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyNewSituationCard(situation);
            });
        }
    }

    public void notifyDropBigSituation(String gameOwner) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyDropBigSituation();
            });
        }
    }

    public void notifyStartVotingProcess(String gameOwner) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyStartVotingProcess();
            });
        }
    }

    public void notifyUserVoted(String gameOwner, String votedFor, int votesCount) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyUserVoted(votedFor, votesCount);
            });
        }
    }

    public void notifyPointsUpdated(String gameOwner, String pointsOwner, int points) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyPointsUpdated(pointsOwner, points);
            });
        }
    }

    public void notifyNewRoundBegin(String gameOwner) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyNewRoundBegin();
            });
        }
    }

    public void notifyGameTimer(String gameOwner, int seconds) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyGameTimer(seconds);
            });
        }
    }

    public void notifyGameEnded(String gameOwner, User user, int points) {
        Game game = games.get(gameOwner);
        if (game != null) {
            game.getUsersInGame().forEach((participant) -> {
                participant.getClientHandler().notifyGameEnded(user, points);
            });
        }
    }

    public void removeGame(String gameOwner) {
        games.remove(gameOwner);
    }

}
