package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;

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
                        MainApplication.getInstance().registerClient(inputUsername.getText());
                    }
                }
            }
        });
    }

}
