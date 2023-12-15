package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.GameSimple;

public class GameSceneController {
    @FXML
    private AnchorPane bigSituationPane;

    @FXML
    private HBox firstMemesBox;

    @FXML
    private Label labelBigSituation;

    @FXML
    private Label labelCardsCount;

    @FXML
    private Label labelHelpText;

    @FXML
    private Label labelScore;

    @FXML
    private Label labelSmallSituation;

    @FXML
    private Label labelUsername;

    @FXML
    private HBox participantsBox;

    @FXML
    private HBox secondMemesBox;

    @FXML
    private AnchorPane smallSituationPane;

    @FXML
    private HBox userMemesBox;

    @FXML
    public void initialize() {
        GameSimple gameSimple = MainApplication.getClient().getSimpleGame();
    }
}
