package ru.kpfu.itis.galeev.aidan.choosememegame.testing;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;

public class TestServer {
    public static void main(String[] args) {
        Server server = new Server();

        System.out.println(server.getLobbies().size());
    }
}
