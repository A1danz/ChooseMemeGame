package ru.kpfu.itis.galeev.aidan.choosememegame.client;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.FullLobbyException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyDoesntExistException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyWrongInfo;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.LobbySimple;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
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
            out.write(StringConverter.createCommand(ServerMessages.COMMAND_USER, new String[][]{new String[]{user.getUsername()}}));
            //out.write("USER" + ServerMessages.COMMANDS_SEPARATOR + user.getUsername() + "\n");
            out.flush();
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
//            String[] line = in.readLine().split(ServerMessages.COMMANDS_SEPARATOR);
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
            throw new RuntimeException(e);
        }
    }

    public void followToLobbyUpdates(ObservableList<User> usersInLobby) {
        try {
            while (true) {
                Map.Entry<String, String[][]> messageByServer = StringConverter.getCommand(in.readLine());
                String command = messageByServer.getKey();
                String[][] arguments = messageByServer.getValue();
                switch (command) {
                    case ServerMessages.COMMAND_USER_ENTERED -> {
                        User enteredUser = new User(arguments[0][0], arguments[0][1]);
                        usersInLobby.add(enteredUser);
                    } case ServerMessages.COMMAND_USER_LEAVED -> {

                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void leaveLobby(String lobbyCreator) {
        try {
            ServerMessages.sendMessage(out, StringConverter.createCommand(
                    ServerMessages.COMMAND_EXIT_USER,
                    new String[][]{new String[]{lobbyCreator, user.getUsername()}}
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
