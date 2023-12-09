package ru.kpfu.itis.galeev.aidan.choosememegame.model;

public class User {
    private String username;
    private String pathToAvatar;

    public User(String username) {
        this.username = username;
        pathToAvatar = "default.png";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPathToAvatar() {
        return pathToAvatar;
    }

    public void setPathToAvatar(String pathToAvatar) {
        this.pathToAvatar = pathToAvatar;
    }
}
