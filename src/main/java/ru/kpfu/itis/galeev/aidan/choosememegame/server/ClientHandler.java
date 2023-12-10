package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;

import java.io.*;
import java.net.SecureCacheResponse;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
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
                    String command = lineValues[0];
                    switch (command) {
                        case "USER":
                            String username = lineValues[1];
                            out.write(ServerMessages.COMMAND_AUTH);
                            out.write(ServerMessages.COMMANDS_SEPARATOR);
                            if (server.usernameIsFree(username)) {
                                user = new User(username);
                                out.write(ServerMessages.SUCCESS_AUTH);
                            } else {
                                out.write(ServerMessages.OCCUPIED_USERNAME);
                            }
                            out.write("\n");
                            out.flush();
                            break;
                        case ServerMessages.COMMAND_REQ_LOBBIES:
                            System.out.println("LOBBIES REQUESTD");
                            List<Lobby> lobbies = server.getLobbies();
                            stringBuilder.append(ServerMessages.COMMAND_LOBBIES);
                            stringBuilder.append(ServerMessages.COMMANDS_SEPARATOR);
                            for (Lobby lobby : lobbies) {
                                if (lobby.getParticipantsCount() != lobby.getLobbyCapacity()) {
                                    User creator = lobby.getCreator();
                                    // creator,lobby_capacity,theme, participantsCount
                                    stringBuilder.append(creator.getUsername())
                                            .append(",")
                                            .append(creator.getPathToAvatar())
                                            .append(",")
                                            .append(lobby.getLobbyCapacity())
                                            .append(",")
                                            .append(lobby.getTheme())
                                            .append(",")
                                            .append(lobby.getParticipantsCount())
                                            .append(";");
                                }
                            }
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                            stringBuilder.append("\n");
                            out.write(stringBuilder.toString());
                            out.flush();
                            System.out.println("LOBBIES SENDED-" + stringBuilder.toString());
                            stringBuilder.delete(0, stringBuilder.length());
                            break;
                        case ServerMessages.COMMAND_CREATE_LOBBY:
                            // name, capacity, theme
                            String[] lobbyOptions = lineValues[1].split(",");
                            if (server.lobbies.containsKey(user.getUsername())) {
                                out.write("У вас уже есть созданное лобби. Попробуйте позже" + "\n");
                                out.flush();
                            } else {
                                try {
                                    server.lobbies.put(user.getUsername(), new Lobby(
                                            user,
                                            server,
                                            Integer.parseInt(lobbyOptions[1]),
                                            lobbyOptions[2],
                                            1
                                    ));
                                    out.write(ServerMessages.SUCCESS_CREATE_LOBBY + "\n");
                                    out.flush();
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                    out.write("Не удалось создать лобби. Попробуйте позже" + "\n");
                                    out.flush();
                                }
                            }
                            break;
                        default:
                            System.out.println(command.equals(ServerMessages.COMMAND_CREATE_LOBBY));
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
