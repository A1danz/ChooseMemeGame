package ru.kpfu.itis.galeev.aidan.choosememegame.client;

import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ServerMessages;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.StringConverter;

import java.io.*;
import java.net.SecureCacheResponse;
import java.net.Socket;
import java.util.ArrayList;
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
            String[] strLobbies = line.split(ServerMessages.COMMANDS_SEPARATOR)[1].split(";");
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

//            for (String strLobby : strLobbies) {
//                String[] lobbyFields = strLobby.split(",");
//                if (lobbyFields.length == 0) break;
//                // creator(username, path),lobby_capacity,theme, participantsCount, lobby_name
//                lobbies.add(new Lobby(
//                        new User(lobbyFields[0], lobbyFields[1]),
//                        null,
//                        Integer.parseInt(lobbyFields[2]),
//                        lobbyFields[3],
//                        Integer.parseInt(lobbyFields[4]),
//                        lobbyFields[5])
//                );
//            }
            return lobbies;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String createLobby(String name, int capacity, String theme) {
        // name, capacity, theme
//        sb.append(ServerMessages.COMMAND_CREATE_LOBBY).append(ServerMessages.COMMANDS_SEPARATOR);
//        sb.append(name).append(",").append(capacity).append(",").append(theme);
//        sb.append("\n");

        try {
//            out.write(sb.toString());
//            out.flush();
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
}
