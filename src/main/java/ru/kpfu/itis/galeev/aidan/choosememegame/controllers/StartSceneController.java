package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.server.ServerMessages;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartSceneController {
    private static final String USERNAME_PATTERN = "^[a-zA-Zа-яА-Я0-9_]{2,12}$";
    private static final Pattern username_pattern = Pattern.compile(USERNAME_PATTERN);

    @FXML
    private Button enterBtn;

    @FXML
    private TextField inputUsername;

    @FXML
    private ImageView loadingStatus;
    @FXML
    private VBox loadingBox;

    @FXML
    public void initialize() {
        enterBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Информация");
                alert.setHeaderText(null);

                if (inputUsername.getText().isBlank()) {
                    alert.setContentText("Пожалуйсте, заполните поле \"Nickname\"");
                    alert.showAndWait();
                } else if (inputUsername.getText().equals("default")) {
                    alert.setContentText("Извините, но данный никнейм не подходит.");
                    alert.showAndWait();
                } else {
                    Matcher matcher = username_pattern.matcher(inputUsername.getText());
                    if (!matcher.matches()) {
                        alert.setContentText("Формат или длина неверны. Допустимые символы: буквы, цифры, _. Длина: от 2 до 12.");

                        alert.showAndWait();
                    } else {
                        setLoadingStatus(true);
                        new Thread(() -> {
                            MainApplication.getClient().registerUser(inputUsername.getText());
                            String authResult = MainApplication.getClient().getAuthResult();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setLoadingStatus(false);
                                    if (authResult.equals(ServerMessages.SUCCESS_AUTH)) {
                                        try {
                                            switchToMenuScene();
                                        } catch (IOException ex) {
                                            alert.setContentText("Файлы игры повреждены. Пожалуйста, попробуйте еще. Если ошибка повторится - обратитесь в поддержку");
                                        }
                                    } else {
                                        alert.setContentText(authResult);
                                        alert.showAndWait();
                                    }
                                }
                            });
                        }).start();
                    }
                }
            }
        });
    }

    private void switchToMenuScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(Config.MENU_SCENE));
        Scene scene = new Scene(fxmlLoader.load(), Config.SCENE_WIDTH, Config.SCENE_HEIGHT);

        Stage primaryStage = (Stage) enterBtn.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    private void setLoadingStatus(boolean status) {
        enterBtn.setDisable(status);
        inputUsername.setDisable(status);
        loadingStatus.setVisible(status);
        loadingBox.setVisible(status);
    }



}
