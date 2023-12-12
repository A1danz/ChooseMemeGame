package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.FullLobbyException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyDoesntExistException;
import ru.kpfu.itis.galeev.aidan.choosememegame.exceptions.LobbyWrongInfo;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.LobbySimple;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.DataHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private GridPane participantsPane;

    @FXML
    private Label labelRoomName;
    private ObservableList<User> usersInLobby = FXCollections.observableArrayList();
    private User lobbyCreator;
    @FXML
    public void initialize() {
        try {
            LobbySimple lobbySimple = MainApplication.getClient().getLobby(DataHolder.connectingLobbyCreator);

            initLobbyInformation(lobbySimple);
            initExitBtn();

            Thread updatesThread = new Thread(() -> {
                followToUpdates();
            });
            updatesThread.start();
        } catch (LobbyDoesntExistException | FullLobbyException | LobbyWrongInfo exception) {
            swapToMenuScene(exception.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }

    private void swapToMenuScene(String reason) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(reason);
        alert.showAndWait();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(Config.MENU_SCENE));
            Scene scene = new Scene(fxmlLoader.load(), Config.SCENE_WIDTH, Config.SCENE_HEIGHT);

            Stage primaryStage = (Stage) btnExit.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initLobbyInformation(LobbySimple lobby) {
        User creator = lobby.getCreator();
        lobbyCreator = creator;
        labelRoomName.setText(lobby.getName());
        labelCreatorName.setText(creator.getUsername());
        imageUserAvatar.setImage(new Image(String.valueOf(MainApplication.class.getResource("img/avatars/" + creator.getPathToAvatar()))));
        labelStatus.setText("Ожидание");
        labelTheme.setText(lobby.getTheme());
        labelParticipantsCount.setText(lobby.getParticipantsCount() + "/" + lobby.getLobbyCapacity());

        List<User> participants = lobby.getUsersInLobby();
        for (int i = 0; i < participants.size(); i++) {
            User participant = participants.get(i);
            VBox playerVBox = (VBox) participantsPane.getChildren().get(i);
            usersInLobby.add(participant);

            putUserIntoBox(playerVBox, participant);
        }


        for (int i = lobby.getLobbyCapacity(); i < participantsPane.getChildren().size(); i++) {
            ImageView imageView = new ImageView();
            imageView.setFitHeight(116.0);
            imageView.setFitWidth(120.0);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setImage(new Image(String.valueOf(MainApplication.class.getResource("img/forbidden.png"))));

            ((VBox) participantsPane.getChildren().get(i)).getChildren().clear();
            ((VBox) participantsPane.getChildren().get(i)).getChildren().add(imageView);
        }

        usersInLobby.addListener(new ListChangeListener<User>() {
            @Override
            public void onChanged(Change<? extends User> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        Platform.runLater(() -> putUserIntoBox(
                                (VBox) participantsPane.getChildren().get(usersInLobby.size() - 1),
                                usersInLobby.get(usersInLobby.size() - 1)
                        ));
                    }
                }
            }
        });
    }

    public void followToUpdates() {
        MainApplication.getClient().followToLobbyUpdates(usersInLobby);
    }

    private void putUserIntoBox(VBox playerVBox, User participant) {
        playerVBox.getChildren().clear();
        if (participant.equals(MainApplication.getClient().getUser())) {
            playerVBox.getStyleClass().clear();
            playerVBox.getStyleClass().add("user-card");
        }

        ImageView imageView = new ImageView();
        imageView.setFitHeight(80.0);
        imageView.setFitWidth(80.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        imageView.setImage(new Image(String.valueOf(MainApplication.class.getResource("img/avatars/" + participant.getPathToAvatar()))));

        Label label = new Label(participant.getUsername());
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(130.0);
        label.setPrefWidth(459.0);
        label.getStyleClass().add(participant.equals(lobbyCreator) ? "creator-name" : "player-name");


        label.setTextAlignment(TextAlignment.CENTER);

        VBox.setMargin(label, new Insets(10.0, 0, 0, 0));
        playerVBox.getChildren().addAll(imageView, label);
    }

    public void initExitBtn() {
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MainApplication.getClient().leaveLobby(lobbyCreator.getUsername());
                swapToMenuScene("Вы успешно покинули лобби!");
            }
        });
    }

}
