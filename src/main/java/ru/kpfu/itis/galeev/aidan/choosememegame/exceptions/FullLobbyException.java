package ru.kpfu.itis.galeev.aidan.choosememegame.exceptions;

public class FullLobbyException extends RuntimeException {
    public FullLobbyException() {
        super();
    }

    public FullLobbyException(String message) {
        super(message);
    }

    public FullLobbyException(String message, Throwable cause) {
        super(message, cause);
    }
}
