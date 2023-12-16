package ru.kpfu.itis.galeev.aidan.choosememegame.client;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.FullLobbyException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyDoesntExistException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyWrongInfo;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ServerMessages;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.StringConverter;

import java.io.*;
import java.net.SecureCacheResponse;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Client {
    private BufferedReader in;
    private BufferedWriter out;
    private User user;
    public User getUser() {
        return user;
    }
    private Thread updatesInLobbyThread;
    private boolean isFollowed = false;

    public Client() {
        try {
            Socket socket = new Socket(Config.HOST, Config.PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendInfoAboutUser() {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_USER,
                    new String[][]{new String[]{user.getUsername(), user.getPathToAvatar()}}));
            System.out.println("INFO SENDED");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerUser(String username) {
        user = new User(username);

        sendInfoAboutUser();
    }

    public String getAuthResult() {
        try {
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
            if (messageByServer.getKey().equals(ServerMessages.COMMAND_AUTH)) {
                String result = messageByServer.getValue()[0][0];
                if (result.equals(ServerMessages.SUCCESS_AUTH)) {
                    return ServerMessages.SUCCESS_AUTH;
                } else if (result.equals(ServerMessages.OCCUPIED_USERNAME)) {
                    return "Извините, но данный никнейм занят. Попробуйте другой";
                } else {
                    throw new UnsupportedOperationException("unsupported result");
                }
            } else {
                throw new UnsupportedOperationException("Unsupported command");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Lobby> getLobbies() {
        try {
            ArrayList<Lobby> lobbies = new ArrayList<>();
//            out.write(ServerMessages.COMMAND_REQ_LOBBIES + ServerMessages.COMMANDS_SEPARATOR +"\n");
            ServerMessages.sendMessage(out, StringConverter.createCommand(ServerMessages.COMMAND_REQ_LOBBIES, new String[][]{}));
            String line = in.readLine();
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(line);
            System.out.println("LOBBIES GETTED");
            System.out.println(line);


            // user_username, user_avatar, null, lobby_capacity, theme participants_count, room_name
            for (String[] lobby : messageByServer.getValue()) {
                lobbies.add(new Lobby(
                        new User(lobby[0], lobby[1]),
                        null,
                        Integer.parseInt(lobby[2]),
                        lobby[3],
                        Integer.parseInt(lobby[4]),
                        lobby[5]
                ));
            }

            return lobbies;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createLobby(String name, int capacity, String theme) {
        // name, capacity, theme

        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_CREATE_LOBBY,
                    new String[][]{new String[]{name, String.valueOf(capacity), theme}}
                    ));
            String result = in.readLine();
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(result);
            if (messageByServer.getKey().equals(ServerMessages.FAILURE_CREATE_LOBBY)) {
                return messageByServer.getValue()[0][0];
            }
            return ServerMessages.SUCCESS_CREATE_LOBBY;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LobbySimple getLobby(String creatorUsername) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_REQ_LOBBY_INFO,
                    new String[][]{new String[]{creatorUsername}}
            ));
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
            // lobby(name(0), participantsCount(1), capacity(2), theme(3), creator(username(4), avatar(5)));participants(username, avatar)
            if (messageByServer.getKey().equals(ServerMessages.COMMAND_LOBBY_INFO)) {
                List<User> participants = new ArrayList<>();
                String[][] arguments = messageByServer.getValue();
                for (int i = 1; i < arguments.length; i++) {
                    participants.add(new User(arguments[i][0], arguments[i][1]));
                }
                String[] lobbySimpleInfo = arguments[0];

                ServerMessages.sendMessage(out, StringConverter.createCommand(
                        ServerMessages.COMMAND_LOBBY_INFO_RECEIVED,
                        new String[][]{new String[]{"received"}}
                ));

                return new LobbySimple(
                        new User(lobbySimpleInfo[4], lobbySimpleInfo[5]),
                        Integer.parseInt(lobbySimpleInfo[2]),
                        lobbySimpleInfo[3],
                        participants.size(),
                        lobbySimpleInfo[0],
                        participants
                );
            } else if (messageByServer.getKey().equals(ServerMessages.COMMAND_LOBBY_DOESNT_EXIST)) {
                throw new LobbyDoesntExistException("Лобби \"" + creatorUsername + "\" не существует.");
            } else if (messageByServer.getKey().equals(ServerMessages.COMMAND_LOBBY_FULL)) {
                throw new FullLobbyException("Лобби \"" + creatorUsername + "\" заполнено");
            } else {
                System.out.println("Unsupported - " + messageByServer.getKey());
                throw new LobbyWrongInfo("Не удалось получить информацию о лобби.");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String connectToLobby(String creatorUsername) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_LOBBY_CONNECT,
                    new String[][]{new String[]{creatorUsername}}));

            Map.Entry<String, String[][]> answerByServer = StringConverter.getCommand(in.readLine());

            if (answerByServer.getKey().equals(ServerMessages.SUCESS_CONNECT)) {
                return answerByServer.getKey();
            }
            return answerByServer.getValue()[0][0];
        } catch (IOException e) {
            System.out.println("throw exc");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void followToLobbyUpdates(ObservableList<User> usersInLobby, SimpleIntegerProperty timerInSeconds,
                                     SimpleBooleanProperty gameStarted, SimpleStringProperty alert) {
        isFollowed = true;
        updatesInLobbyThread = new Thread(() -> {
            try {
                while (isFollowed) {
                    Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
                    String command = messageByServer.getKey();
                    String[][] arguments = messageByServer.getValue();
                    switch (command) {
                        case ServerMessages.COMMAND_USER_ENTERED -> {
                            User enteredUser = new User(arguments[0][0], arguments[0][1]);
                            usersInLobby.add(enteredUser);
                        }
                        case ServerMessages.COMMAND_USER_LEAVED -> {
                            User leavedUser = new User(arguments[0][0], arguments[0][1]);
                            usersInLobby.remove(leavedUser);
                        }
                        case ServerMessages.COMMAND_LOBBY_TIMER -> {
                            timerInSeconds.set(Integer.parseInt(arguments[0][0]));
                        }
                        case ServerMessages.COMMAND_START_GAME -> {
                            gameStarted.set(true);
                            isFollowed = false;
                        }
                        case ServerMessages.COMMAND_NEED_PLAYERS -> {
                            alert.set(arguments[0][0]);
                        }
                        case ServerMessages.COMMAND_EXIT_USER -> {
                            isFollowed = false;
                        }
                        default -> {
                            if(isFollowed) throw new UnsupportedOperationException("Unsupported command: " + command);
                            else break;
                        }
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        updatesInLobbyThread.start();
    }

    public void leaveLobby(String lobbyCreator) {
        unfollowLobbyUpdates();
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_EXIT_USER,
                    new String[][]{new String[]{lobbyCreator, user.getUsername()}}
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GameSimple getSimpleGame(String gameCreator) {
        unfollowLobbyUpdates();
        System.out.println(updatesInLobbyThread.isAlive());
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_GET_GAME,
                    new String[][]{new String[]{gameCreator}}));
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
            if (messageByServer.getKey().equals(ServerMessages.COMMAND_GAME_INFO)) {
                // creator, memeCardsCount, situationCardsCount, usersInGameList
                String[][] arguments = messageByServer.getValue();
                User creator = new User(arguments[0][0], arguments[0][1]);
                int memeCardsCount = Integer.parseInt(arguments[1][0]);
                int situationCardsCount = Integer.parseInt(arguments[2][0]);
                ArrayList<GameUserSimple> gameUserSimplesList = new ArrayList<>();
                for (int i = 3; i < arguments.length; i++) {
                    gameUserSimplesList.add(new GameUserSimple(new User(arguments[i][0], arguments[i][1])));
                }
                GameSimple gameSimple = new GameSimple(
                        creator,
                        gameUserSimplesList,
                        memeCardsCount,
                        situationCardsCount,
                        getUser()
                );
                System.out.println(gameSimple);
                return gameSimple;
            } else {
                throw new UnsupportedOperationException("[getSimpleGame()] Не ожидаемая команда: " + messageByServer.getKey());
            }
        } catch (IOException ex) {
            System.out.println(ex);
            throw new RuntimeException(ex);
        }
    }

    public void unfollowLobbyUpdates() {
        isFollowed = false;
        System.out.println(updatesInLobbyThread.isAlive());
    }

    public List<MemeCard> getCards(String gameOwner) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                            ServerMessages.COMMAND_GET_CARDS,
                            new String[][]{new String[]{gameOwner, user.getUsername()}}
                    )
            );
            Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
            ArrayList<MemeCard> cards = new ArrayList<>();
            for (String s : messageByServer.getValue()[0]) {
                cards.add(new MemeCard(s));
            }

            return cards;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void followToGameUpdates(SimpleBooleanProperty gameStarted, SimpleIntegerProperty gameStartTimer,
                                    ObservableMap<String, ThrownCard> usersThrownCards, GameSimple game
                                    ) {
        Thread gameUpdatesThread = new Thread(() -> {
            try {
                boolean run = true;
                while (run) {
                    Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
                    String command = messageByServer.getKey();
                    String[][] arguments = messageByServer.getValue();
                    switch (command) {
                        case ServerMessages.COMMAND_ALL_READY -> {
                            gameStarted.setValue(true);
                        }
                        case ServerMessages.COMMAND_GAME_START_TIMER -> {
                            gameStartTimer.set(Integer.parseInt(arguments[0][0]));
                        }
                        case ServerMessages.COMMAND_USER_THROW_CARD -> {
                            // cardOwner, img
                            String cardOwner = arguments[0][0];
                            String pathToCard = arguments[0][1];
                            usersThrownCards.put(cardOwner, new ThrownCard(new MemeCard(pathToCard)));
                        }
                        case ServerMessages.COMMAND_NEW_SITUATION_CARD -> {
                            String situationText = arguments[0][0];
                            game.setSituationText(situationText);
                        }
                        case ServerMessages.COMMAND_DROP_BIG_SITUATION -> {
                            game.setSituationText("");
                        }
                        case ServerMessages.COMMAND_START_VOTING -> {
                            game.setVotingStarted(true);
                        }
                        case ServerMessages.COMMAND_USER_VOTED -> {
                            String votedFor = arguments[0][0];
                            int votesCount = Integer.parseInt(arguments[0][1]);
                            usersThrownCards.get(votedFor).setVotes(votesCount);
                        }
                        case ServerMessages.COMMAND_POINTS_UPDATED -> {
                            String pointsOwner = arguments[0][0];
                            int points = Integer.parseInt(arguments[0][1]);
                            if (pointsOwner.equals(user.getUsername())) {
                                game.getUser().pointsProperty().set(points);
                            }
                            game.getUsersInGame().forEach((participant) -> {
                                if (participant.getUser().getUsername().equals(pointsOwner)) {
                                    participant.pointsProperty().set(points);
                                }
                            });
                        }
                        case ServerMessages.COMMAND_ADD_MEME_CARD -> {
                            String pathToCard = arguments[0][0];
                            game.setAddedCard(pathToCard);
                        }
                        case ServerMessages.COMMAND_UPDATE_MEME_CARDS_COUNT -> {
                            int cardsCount = Integer.parseInt(arguments[0][0]);
                            game.setMemeCardsCount(cardsCount);
                        }
                        case ServerMessages.COMMAND_NEW_ROUND_BEGIN -> {
                            game.setNewRoundBegin(true);
                        }
                        case ServerMessages.COMMAND_GAME_TIMER -> {
                            int seconds = Integer.parseInt(arguments[0][0]);
                            game.timerUpdatesProperty().set(seconds);
                        }
                        case ServerMessages.COMMAND_GAME_ENDED -> {
                            String username = arguments[0][0];
                            String avatarPath = arguments[0][1];
                            int points = Integer.parseInt(arguments[0][2]);

                            run = false;
                            game.addWinner(new User(username, avatarPath), points);
                        }
                        default -> {
                            throw new UnsupportedOperationException("Unsupported command: " + command);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println(ex);
                throw new RuntimeException(ex);
            }
        });
        gameUpdatesThread.start();
    }

    public void readyForGame(String gameOwner) {
        try {
            ServerMessages.sendMessage(
                    out,
                    StringConverter.createCommand(
                            ServerMessages.COMMAND_READY_FOR_GAME,
                            new String[][]{new String[]{gameOwner}}
                    )
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void throwCard(String gameOwner, String pathToCard) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_CLIENT_THROW_CARD,
                    new String[][]{new String[]{gameOwner, pathToCard}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void voteInGame(String gameOwner, String votedFor) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_CLIENT_VOTED,
                    new String[][]{new String[]{gameOwner, votedFor}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
