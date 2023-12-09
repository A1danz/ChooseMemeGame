package ru.kpfu.itis.galeev.aidan.choosememegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kpfu.itis.galeev.aidan.choosememegame.client.Client;

import java.io.IOException;

public class MainApplication extends Application implements CustomApplication {
    Client client;
    private static CustomApplication instance;

    public MainApplication() {
        instance = this;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("start-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        client = new Client();

        stage.setTitle("ChooseMeme");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void registerClient(String username) {
        client.registerUser(username);
    }

    public static CustomApplication getInstance() {
        return instance;
    }
}