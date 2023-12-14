package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import java.io.BufferedWriter;
import java.io.IOException;

public interface ServerMessages {
    String SUCCESS_AUTH = "SUCCESS";
    String COMMAND_USER = "USER";
    String OCCUPIED_USERNAME = "occupied";
    String COMMAND_AUTH = "AUTH";
    String COMMAND_LOBBY_USERS = "LOBBY_USER";
    String COMMAND_REQ_LOBBIES = "REQUEST_LOBBIES";
    String COMMAND_LOBBIES = "LOBBIES";
    String COMMANDS_SEPARATOR = "//";
    String ARGUMENTS_SEPARATOR = ";";
    String ITEMS_SEPARATOR = "\t";
    String COMMAND_CREATE_LOBBY = "CREATE_LOBBY";
    String SUCCESS_CREATE_LOBBY = "SUCCESS_LOBBY";
    String FAILURE_CREATE_LOBBY = "FAILURE_LOBBY";
    String COMMAND_REQ_LOBBY_INFO = "REQUEST_LOBBY_INFO";
    String COMMAND_LOBBY_DOESNT_EXIST = "LOBBY_DNT_EXIST";
    String COMMAND_LOBBY_FULL = "LOBBY_FULL";
    String COMMAND_LOBBY_INFO = "LOBBY_INFO";
    String COMMAND_LOBBY_CONNECT = "CONNECT_LOBBY";
    String SUCESS_CONNECT = "SUCESS_CONNECT";
    String FAILURE_CONNECT = "FAILURE_CONNECT";
    String COMMAND_USER_ENTERED = "USER_ENTERED";
    String COMMAND_USER_LEAVED = "USER_LEAVE";
    String COMMAND_EXIT_USER = "USER_EXIT_FROM_LOBBY";
    String COMMAND_LOBBY_TIMER = "UPDATE_LOBBY_TIMER";
    String COMMAND_START_GAME = "START_GAME";
    String COMMAND_NEED_PLAYERS = "NEED_PLAYERS";

    public static void sendMessage(BufferedWriter out, String message) throws IOException {
        out.write(message);
        out.flush();
    }
}
