package ru.kpfu.itis.galeev.aidan.choosememegame.exceptions;

public class LobbyWrongInfo extends RuntimeException {
    public LobbyWrongInfo() {
        super();
    }

    public LobbyWrongInfo(String message) {
        super(message);
    }

    public LobbyWrongInfo(String message, Throwable cause) {
        super(message, cause);
    }
}
