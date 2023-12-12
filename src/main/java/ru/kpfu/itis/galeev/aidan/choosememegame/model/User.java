package ru.kpfu.itis.galeev.aidan.choosememegame.model;

import java.util.Objects;

public class User {
    private String username;
    private String pathToAvatar;

    public User(String username) {
        this.username = username;
        pathToAvatar = "default/1.png";
    }

    public User(String username, String pathToAvatar) {
        this.username = username;
        this.pathToAvatar = pathToAvatar;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
}
