package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.DataHolder;

public class LobbySceneController {
    @FXML
    private Button btnExit;

    @FXML
    private Label cardsCount;

    @FXML
    private ImageView imageUserAvatar;

    @FXML
    private Label labelCreatorName;

    @FXML
    private Label labelParticipantsCount;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelTheme;

    @FXML
    private Label questionsCount;
    @FXML
    public void initialize() {
        labelCreatorName.setText(DataHolder.connectingLobby);
    }

}
