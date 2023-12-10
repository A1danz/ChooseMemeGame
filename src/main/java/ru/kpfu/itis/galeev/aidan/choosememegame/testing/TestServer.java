package ru.kpfu.itis.galeev.aidan.choosememegame.testing;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.Server;

public class TestServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.getLobbies().add(new Lobby(
                new User("andrey"),
                null,
                10,
                "standart",
                2
        ));

        System.out.println(server.getLobbies().size());
    }
}
