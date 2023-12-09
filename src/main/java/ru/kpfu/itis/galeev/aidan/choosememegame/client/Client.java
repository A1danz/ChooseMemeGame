package ru.kpfu.itis.galeev.aidan.choosememegame.client;

import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;

import java.io.*;
import java.net.Socket;

public class Client {
    private BufferedReader in;
    private BufferedWriter out;
    private User user;

    public Client() {
        try {
            Socket socket = new Socket(Config.HOST, Config.PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendInfoAboutUser() {
        try {
            out.write("USER/" + user.getUsername());
            out.flush();
            System.out.println("INFO SENDED");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerUser(String username) {
        user = new User(username);

        sendInfoAboutUser();
    }
}
