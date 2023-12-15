package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.StringConverter;

import java.io.*;
import java.net.SecureCacheResponse;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private BufferedWriter out;
    private User user;
    private Server server;
    public boolean lobbyIsLoaded = false; // not to receive notifications about timer updates

    public ClientHandler(Socket client, Server server) {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            this.server = server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String line = null;
                if ((line = in.readLine()) != null) {
                    System.out.println(line);
                    String[] lineValues = line.split(ServerMessages.COMMANDS_SEPARATOR);
                    Map.Entry<String, String[][]> messageByClient = StringConverter.getCommand(line);
                    String command = messageByClient.getKey();
                    String[][] arguments = messageByClient.getValue();
                    switch (command) {
                        case ServerMessages.COMMAND_USER -> {
                            String username = messageByClient.getValue()[0][0];
                            String result;

                            if (server.usernameIsFree(username)) {
                                user = new User(username);
                                //out.write(ServerMessages.SUCCESS_AUTH);
                                result = ServerMessages.SUCCESS_AUTH;
                            } else {
                                result = ServerMessages.OCCUPIED_USERNAME;
                            }

                            out.write(StringConverter.createCommand(ServerMessages.COMMAND_AUTH, new String[][]{new String[]{result}}));
                            out.flush();
                        }
                        case ServerMessages.COMMAND_REQ_LOBBIES -> {
                            List<Lobby> lobbies = server.getLobbies();
                            ArrayList<String[]> lobbiesForMessage = new ArrayList<>();

                            for (Lobby lobby : lobbies) {
                                if (lobby.getParticipantsCount() != lobby.getLobbyCapacity()) {
                                    User creator = lobby.getCreator();
                                    // creator,lobby_capacity,theme, participantsCount, lobby_name
                                    // user_username, user_avatar, null, lobby_capacity, theme participants_count, room_name
                                    lobbiesForMessage.add(new String[]{
                                            creator.getUsername(),
                                            creator.getPathToAvatar(),
                                            String.valueOf(lobby.getLobbyCapacity()),
                                            lobby.getTheme(),
                                            String.valueOf(lobby.getParticipantsCount()),
                                            lobby.getName()
                                    });
                                }
                            }

                            ServerMessages.sendMessage(out, StringConverter.createCommand(
                                    ServerMessages.COMMAND_LOBBIES,
                                    lobbiesForMessage.toArray(new String[lobbiesForMessage.size()][])
                                    ));
                        }
                        case ServerMessages.COMMAND_LOBBY_INFO_RECEIVED -> {
                            lobbyIsLoaded = true;
                        }
                        case ServerMessages.COMMAND_CREATE_LOBBY -> {
                            // name, capacity, theme
                            String[] lobbyOptions = messageByClient.getValue()[0];
                            if (server.lobbies.containsKey(user.getUsername())) {
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.FAILURE_CREATE_LOBBY,
                                        new String[][]{new String[]{"У вас уже есть созданное лобби. Попробуйте позже"}}
                                ));
                            } else {
                                try {
                                    server.lobbies.put(user.getUsername(), new Lobby(
                                            user,
                                            server,
                                            Integer.parseInt(lobbyOptions[1]),
                                            lobbyOptions[2],
                                            1,
                                            lobbyOptions[0]
                                    ));
                                    server.getLobby(user.getUsername()).addUser(this);
                                    ServerMessages.sendMessage(out, StringConverter.createCommand(
                                            ServerMessages.SUCCESS_CREATE_LOBBY,
                                            new String[][]{new String[]{" "}}
                                    ));
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                    ServerMessages.sendMessage(out, StringConverter.createCommand(
                                            ServerMessages.FAILURE_CREATE_LOBBY,
                                            new String[][]{new String[]{"Не удалось создать лобби. Попробуйте позже"}}
                                    ));
                                }
                            }
                        }
                        case ServerMessages.COMMAND_REQ_LOBBY_INFO -> {
                            String creatorUsername = messageByClient.getValue()[0][0];
                            Lobby lobby = server.getLobby(creatorUsername);
                            if (lobby == null) {
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.COMMAND_LOBBY_DOESNT_EXIST,
                                        new String[][]{new String[]{"Лобби не существует"}}
                                        ));
                            } else {
                                // lobby(name(0), participantsCount(1), capacity(2), theme(3), creator(username(4), avatar(5)));participants(username, avatar)
                                String[] lobbySimpleInfo = new String[]{
                                        lobby.getName(),
                                        String.valueOf(lobby.getParticipantsCount()),
                                        String.valueOf(lobby.getLobbyCapacity()),
                                        lobby.getTheme(),
                                        lobby.getCreator().getUsername(),
                                        lobby.getCreator().getPathToAvatar()
                                };
                                String[][] participants = new String[lobby.getParticipantsCount()][2];
                                List<ClientHandler> lobbyParticipants = lobby.getParticipants();
                                for (int i = 0; i < lobby.getParticipantsCount(); i++) {
                                    User user = lobbyParticipants.get(i).getUser();
                                    participants[i][0] = user.getUsername();
                                    participants[i][1] = user.getPathToAvatar();
                                }

                                String[][] lobbyInfo = new String[1 + participants.length][];
                                lobbyInfo[0] = lobbySimpleInfo;
                                for (int i = 0; i < participants.length; i++) {
                                    lobbyInfo[1 + i] = participants[i];
                                }
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.COMMAND_LOBBY_INFO,
                                        lobbyInfo
                                ));
                            }
                        }
                        case ServerMessages.COMMAND_LOBBY_CONNECT -> {
                            String creatorUsername = messageByClient.getValue()[0][0];
                            Lobby lobby = server.getLobby(creatorUsername);
                            if (lobby == null) {
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.FAILURE_CONNECT,
                                        new String[][]{new String[]{"Лобби не существует"}}
                                ));
                                break;
                            } else if (lobby.isFull()) {
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.FAILURE_CONNECT,
                                        new String[][]{new String[]{"К сожалению, лобби заполнено"}}
                                ));
                                break;
                            }
                            ServerMessages.sendMessage(out, StringConverter.createCommand(
                                    ServerMessages.SUCESS_CONNECT,
                                    new String[][]{new String[]{"Success"}}
                                    ));
                            lobby.addUser(this);
                        }
                        case ServerMessages.COMMAND_EXIT_USER -> {
                            String creatorUsername = arguments[0][0];
                            lobbyIsLoaded = false;

                            Lobby lobby = server.lobbies.get(creatorUsername);
                            if (lobby != null) {
                                lobby.removeUser(this);
                            }
                            ServerMessages.sendMessage(out, StringConverter.createCommand(
                                    ServerMessages.COMMAND_EXIT_USER,
                                    new String[][]{new String[]{"exit"}}
                                    ));
                        }
                        case ServerMessages.COMMAND_GET_GAME -> {
                            // creator, memeCardsCount, situationCardsCount, usersInGameList
                            String gameOwner = arguments[0][0];
                            Game game = server.games.get(gameOwner);
                            ArrayList<GameUserSimple> gameUserSimpleList = new ArrayList<>();
                            game.getUsersInGame().forEach((participant) -> {
                                gameUserSimpleList.add(new GameUserSimple(participant.getUser()));
                            });
                            GameSimple gameSimple = new GameSimple(
                                    game.getCreator(),
                                    gameUserSimpleList,
                                    game.getMemeCards().size(),
                                    game.getSituations().size(),
                                    null
                            );
                            String[][] result = new String[3 + gameUserSimpleList.size()][];
                            result[0] = new String[]{gameSimple.getCreator().getUsername(), gameSimple.getCreator().getPathToAvatar()}; // creator
                            result[1] = new String[]{String.valueOf(gameSimple.getMemeCardsCount())}; // memecards
                            result[2] = new String[]{String.valueOf(gameSimple.getSituationsCount())}; // situations
                            for (int i = 0; i < gameUserSimpleList.size(); i++) {
                                User user = gameUserSimpleList.get(i).getUser();
                                result[i + 3] = new String[]{user.getUsername(), user.getPathToAvatar()};
                            } // usersInGameList
                            ServerMessages.sendMessage(out, StringConverter.createCommand(
                                    ServerMessages.COMMAND_GAME_INFO,
                                    result
                                    ));
                        }
                        case ServerMessages.COMMAND_GET_CARDS -> {
                            String gameOwner = arguments[0][0];
                            String cardsOwner = arguments[0][1];
                            Game game = server.games.get(gameOwner);
                            List<MemeCard> cards = new ArrayList<>();
                            for (GameUser participant : game.getUsersInGame()) {
                                if (participant.getUser().getUsername().equals(cardsOwner)) {
                                    cards.addAll(participant.getCards());
                                    break;
                                }
                            }

                            String[][] result = new String[1][cards.size()];
                            for (int i = 0; i < cards.size(); i++) {
                                result[0][i] = cards.get(i).getPathToCard();
                            }
                            ServerMessages.sendMessage(out, StringConverter.createCommand(
                                    ServerMessages.COMMAND_GET_CARDS,
                                    result
                            ));
                        }
                        case ServerMessages.COMMAND_READY_FOR_GAME -> {
                            String gameOwner = arguments[0][0];
                            Game game = server.games.get(gameOwner);
                            game.increaseReadyPlayersCount();
                        }
                        default -> {
                            throw new UnsupportedOperationException("Unsupported command: " + command);
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            if (ex.getMessage().contains("Connection reset")) {
                server.disconnect(this);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User getUser() {
        return user;
    }

    public void sendInfoAboutUsersInLobby(User user, boolean entered) {
        try {
            String[][] userInfo = new String[][]{new String[]{user.getUsername(), user.getPathToAvatar()}};
            if (entered) {
                ServerMessages.sendMessage(out, StringConverter.createCommand(
                        ServerMessages.COMMAND_USER_ENTERED,
                        userInfo
                ));
            } else {
                ServerMessages.sendMessage(out, StringConverter.createCommand(
                        ServerMessages.COMMAND_USER_LEAVED,
                        userInfo
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLobbyTimer(int time) {
        try {
            if (!lobbyIsLoaded) return;
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_LOBBY_TIMER,
                    new String[][]{new String[]{String.valueOf(time)}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyStartGame() {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_START_GAME,
                    new String[][]{new String[]{"start"}}
            ));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void notifyNeedMorePlayers(int participantsCount, int playersForStartCount) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_NEED_PLAYERS,
                    new String[][]{new String[]{"Для старта необходимо: " + playersForStartCount}}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyAllReady() {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_ALL_READY,
                    new String[][]{new String[]{"all_ready"}}
            ));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void notifyStartGameTimer(int seconds) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_GAME_START_TIMER,
                    new String[][]{new String[]{String.valueOf(seconds)}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyUserThrowCard(String cardOwner, ThrownCard thrownCard) {
        // cardOwner, img
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_USER_THROW_CARD,
                    new String[][]{new String[]{cardOwner, thrownCard.getMemeCard().getPathToCard()}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyNewSituationCard(Situation situation) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_NEW_SITUATION_CARD,
                    new String[][]{new String[]{situation.getSituationText()}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyDropBigSituation() {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_DROP_BIG_SITUATION,
                    new String[][]{new String[]{"drop_big_situation"}}
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return user.toString();
    }
}
