package ru.kpfu.itis.galeev.aidan.choosememegame.exceptions;

public class LobbyDoesntExistException extends RuntimeException {
    public LobbyDoesntExistException() {
        super();
    }

    public LobbyDoesntExistException(String message) {
        super(message);
    }

    public LobbyDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
