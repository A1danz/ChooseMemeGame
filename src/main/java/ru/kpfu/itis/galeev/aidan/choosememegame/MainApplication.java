package ru.kpfu.itis.galeev.aidan.choosememegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.kpfu.itis.galeev.aidan.choosememegame.client.Client;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;

import java.io.IOException;
import java.lang.ref.Cleaner;

public class MainApplication extends Application {
    private static Client client;

    public MainApplication() {
        client = new Client();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(Config.START_SCENE));
        Scene scene = new Scene(fxmlLoader.load(), Config.SCENE_WIDTH, Config.SCENE_HEIGHT);


        stage.setTitle("ChooseMeme");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Client getClient() {
        return client;
    }
}