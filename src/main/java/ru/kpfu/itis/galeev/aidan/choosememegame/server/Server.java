package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    ServerSocket serverSocket;
    ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(Config.PORT);
            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("CLIENT IS REGISTERED IN SERVER");
                ClientHandler clientHandler = new ClientHandler(client);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
