package ru.kpfu.itis.galeev.aidan.choosememegame.server;

import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private BufferedReader in;
    private BufferedWriter out;
    private User user;

    public ClientHandler(Socket client) {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        try {
            String line = null;
            System.out.println("START HANDLING MESSAGE");
            line = in.readLine();
            System.out.println("STOP HANDLING MESSAGE");
            System.out.println(line);
            if (line != null) {
                String[] valuesOfLine = line.substring(5).split(";");
                String username = valuesOfLine[0];
                user = new User(username);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createUser() {
        System.out.println("ENTER TO FUNC");
        boolean userIsCreated = false;
        try {
            String line = null;
            line = in.readLine();
            System.out.println(line);
            if (line != null) {
                String[] valuesOfLine = line.substring(5).split(";");
                String username = valuesOfLine[0];
                user = new User(username);
                userIsCreated = true;
            }
            System.out.println("EXIT INTO FUNC");
        } catch (IOException ex) {
            System.out.println("CATCH EXE");
            throw new RuntimeException(ex);
        }
    }
}
