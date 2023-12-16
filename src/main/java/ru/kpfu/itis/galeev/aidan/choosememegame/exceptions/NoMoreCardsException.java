package ru.kpfu.itis.galeev.aidan.choosememegame.exceptions;

public class NoMoreCardsException extends RuntimeException {
    public NoMoreCardsException() {
        super();
    }

    public NoMoreCardsException(String message) {
        super(message);
    }

    public NoMoreCardsException(String message, Throwable cause) {
        super(message, cause);
    }
}
