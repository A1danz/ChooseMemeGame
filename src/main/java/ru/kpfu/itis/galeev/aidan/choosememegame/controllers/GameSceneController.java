package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.client.Client;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.GameSimple;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.GameUserSimple;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.MemeCard;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.DataHolder;

import java.util.List;

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
    private ImageView userAvatar;

    @FXML
    private ImageView imageInstruction;

    @FXML
    private Button btnInstruction;

    @FXML
    private ImageView imageWaitingPlayers;
    private SimpleBooleanProperty gameStarted = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        try {
            String gameOwner = DataHolder.gameOwner;
            Client client = MainApplication.getClient();
            GameSimple gameSimple = client.getSimpleGame(gameOwner);
            GameUserSimple gameUser = gameSimple.getUser();

            initUserCard(gameUser);
            initCardsCount(gameSimple.getMemeCardsCount());
            initGameParticipants(gameSimple.getUsersInGame());

            initStartGameActions();
            client.followToGameUpdates(gameStarted);

            List<MemeCard> cards = client.getCards(gameOwner);
            initUserCards(cards);

            initInstruction();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }

    private void initStartGameActions() {
        gameStarted.addListener((observableValue, previous, now) -> {
            if (now) {
                imageWaitingPlayers.setVisible(false);
            }
        });
    }

    public void initUserCard(GameUserSimple gameUser ) {
        labelUsername.setText(gameUser .getUser().getUsername());
        labelScore.setText(String.valueOf(gameUser .getPoints()));
        userAvatar.setImage(new Image(String.valueOf(MainApplication.class.getResource("img/avatars/" + gameUser.getUser().getPathToAvatar()))));
    }

    public void initCardsCount(int cardsCount) {
        labelCardsCount.setText(String.valueOf(cardsCount));
    }

    public void initGameParticipants(List<GameUserSimple> participants) {
        participantsBox.getChildren().clear();
        participants.forEach((participant) -> {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.TOP_CENTER);
            vbox.setPrefHeight(88.0);
            vbox.setPrefWidth(86.0);

            VBox innerVBox = new VBox();
            innerVBox.setAlignment(Pos.TOP_CENTER);
            innerVBox.setPrefHeight(49.0);
            innerVBox.setPrefWidth(122.0);
            VBox.setMargin(innerVBox, new Insets(0, 0, 0, 0));

            Circle circle = new Circle();
            circle.setFill(Color.WHITE);
            circle.setRadius(26.0);
            circle.setStroke(Color.BLACK);
            circle.setStrokeType(StrokeType.INSIDE);
            circle.setStrokeWidth(0.0);

            ImageView imageView = new ImageView();
            imageView.setFitHeight(45.0);
            imageView.setFitWidth(45.0);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            Image image = new Image(String.valueOf(MainApplication.class.getResource(
                    "img/avatars/" + participant.getUser().getPathToAvatar()
            )));
            imageView.setImage(image);
            VBox.setMargin(imageView, new Insets(-49, 0, 0, -0.25));

            Label label1 = new Label();
            label1.setAlignment(Pos.CENTER);
            label1.setPrefHeight(26.0);
            label1.setPrefWidth(134.0);
            label1.getStyleClass().add("label-game-participant");
            label1.setText(participant.getUser().getUsername());
            label1.setTextAlignment(TextAlignment.CENTER);
            VBox.setMargin(label1, new Insets(-10, 0, 0, 0));

            Label label2 = new Label();
            label2.getStyleClass().add("label-game-participant-score");
            label2.setText(String.valueOf(participant.getPoints()));
            VBox.setMargin(label2, new Insets(-5, 0, 0, 0));

            Circle innerCircle = new Circle();
            innerCircle.setFill(Color.web("#f6b801"));
            innerCircle.setRadius(5.0);
            innerCircle.setStroke(Color.BLACK);
            innerCircle.setStrokeType(StrokeType.INSIDE);
            innerCircle.setStrokeWidth(0.0);
            VBox.setMargin(innerCircle, new Insets(-3, 0, 0, 0));

            vbox.getChildren().addAll(innerVBox, label1, label2, innerCircle);
            innerVBox.getChildren().addAll(circle, imageView);

            participantsBox.getChildren().add(vbox);
        });
    }

    public void initUserCards(List<MemeCard> cards) {
        userMemesBox.getChildren().clear();
        cards.forEach((card) -> {
            ImageView imageView = new ImageView(new Image(String.valueOf(
                    MainApplication.class.getResource(card.getPathToCard())
            )));

            imageView.setFitHeight(70);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setPickOnBounds(true);

            userMemesBox.getChildren().add(imageView);
        });
    }

    private void initInstruction() {
        imageInstruction.setVisible(true);
        btnInstruction.setVisible(true);
        btnInstruction.setOnAction(actionEvent -> {
            imageInstruction.setVisible(false);
            btnInstruction.setVisible(false);
            imageWaitingPlayers.setVisible(true);
            MainApplication.getClient().readyForGame(DataHolder.gameOwner);
        });
    }
}
