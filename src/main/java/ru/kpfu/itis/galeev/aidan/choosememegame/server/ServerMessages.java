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
    String COMMAND_LOBBY_INFO_RECEIVED = "LOBBY_RECEIVED";
    String COMMAND_LOBBY_CONNECT = "CONNECT_LOBBY";
    String SUCESS_CONNECT = "SUCESS_CONNECT";
    String FAILURE_CONNECT = "FAILURE_CONNECT";
    String COMMAND_USER_ENTERED = "USER_ENTERED";
    String COMMAND_USER_LEAVED = "USER_LEAVE"; // means that participant leave from lobby
    String COMMAND_EXIT_USER = "USER_EXIT_FROM_LOBBY"; // means that user(client) exit from lobby
    String COMMAND_LOBBY_TIMER = "UPDATE_LOBBY_TIMER";
    String COMMAND_START_GAME = "START_GAME";
    String COMMAND_NEED_PLAYERS = "NEED_PLAYERS";
    String COMMAND_GET_GAME = "GET_GAME";
    String COMMAND_GAME_INFO = "GAME_INFO";
    String COMMAND_GET_CARDS = "GET_CARDS";
    String COMMAND_ALL_READY = "ALL_READY";
    String COMMAND_READY_FOR_GAME = "READY_FOR_GAME";
    String COMMAND_GAME_START_TIMER = "GAME_START_TIMER";
    String COMMAND_USER_THROW_CARD = "USER_THROW_CARD";
    String COMMAND_NEW_SITUATION_CARD = "NEW_SITUATION_CARD";
    String COMMAND_DROP_BIG_SITUATION = "DROP_BIG_SITUATION";


    public static void sendMessage(BufferedWriter out, String message) throws IOException {
        out.write(message);
        out.flush();
    }
}
