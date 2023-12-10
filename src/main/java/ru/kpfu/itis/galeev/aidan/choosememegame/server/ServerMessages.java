package ru.kpfu.itis.galeev.aidan.choosememegame.server;

public interface ServerMessages {
    String SUCCESS_AUTH = "SUCCESS";
    String OCCUPIED_USERNAME = "occupied";
    String COMMAND_AUTH = "AUTH";
    String COMMAND_LOBBY_USERS = "LOBBY_USER";
    String ARG_ENTERED = "is_entered";
    String ARG_USERNAME = "username";
    String ARG_AVATAR_PATH = "avatar_path";
    String COMMAND_REQ_LOBBIES = "REQUEST_LOBBIES";
    String COMMAND_LOBBIES = "LOBBIES";
    String COMMANDS_SEPARATOR = "//";
    String COMMAND_CREATE_LOBBY = "CREATE_LOBBY";
    String SUCCESS_CREATE_LOBBY = "SUCCESS_LOBBY";
}
