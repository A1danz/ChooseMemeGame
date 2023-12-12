package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
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
                    switch (command) {
                        case ServerMessages.COMMAND_USER:
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
                            break;
                        case ServerMessages.COMMAND_REQ_LOBBIES:
                            System.out.println("LOBBIES REQUESTD");
                            List<Lobby> lobbies = server.getLobbies();
                            ArrayList<String[]> lobbiesForMessage = new ArrayList<>();

                            for (Lobby lobby : lobbies) {
                                if (lobby.getParticipantsCount() != lobby.getLobbyCapacity()) {
                                    User creator = lobby.getCreator();
                                    // creator,lobby_capacity,theme, participantsCount, lobby_name
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
                            break;
                        case ServerMessages.COMMAND_CREATE_LOBBY:
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
                            break;
                        case ServerMessages.COMMAND_REQ_LOBBY_INFO:
                            String creatorUsername = messageByClient.getValue()[0][0];
                            Lobby lobby = server.getLobby(creatorUsername);
                            if (lobby == null) {
                                ServerMessages.sendMessage(out, StringConverter.createCommand(
                                        ServerMessages.COMMAND_LOBBY_DOESNT_EXIST,
                                        new String[][]{new String[]{"Лобби не существует"}}
                                        ));
                            } else {
                                if (lobby.isFull()) {
                                    ServerMessages.sendMessage(out, StringConverter.createCommand(
                                            ServerMessages.COMMAND_LOBBY_FULL,
                                            new String[][]{new String[]{"К сожалению, лобби заполнено"}}
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
                            break;
                        default:
                            throw new UnsupportedOperationException("Unsupported command: " + command);
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
            out.write(ServerMessages.COMMAND_LOBBY_USERS + ServerMessages.COMMANDS_SEPARATOR);
            if (entered) {
                out.write(ServerMessages.ARG_ENTERED);
                out.write("=true;");
                out.write(ServerMessages.ARG_USERNAME);
                out.write("=" + user.getUsername() + ";");
                out.write(ServerMessages.ARG_AVATAR_PATH);
                out.write("=" + user.getPathToAvatar());
            } else {
                out.write("=false;");
                out.write(ServerMessages.ARG_USERNAME);
                out.write("=" + user.getUsername() + ";");

            }
            out.write("\n");
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
}
