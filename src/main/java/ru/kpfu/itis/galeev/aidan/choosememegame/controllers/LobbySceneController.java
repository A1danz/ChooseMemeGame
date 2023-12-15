package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;


import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.Parent;
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

    @FXML
    private Label labelMinutes;

    @FXML
    private Label labelSeconds;

    private ObservableList<User> usersInLobby = FXCollections.observableArrayList();
    private SimpleIntegerProperty timerInSeconds = new SimpleIntegerProperty(0);
    private SimpleBooleanProperty gameStarted = new SimpleBooleanProperty(false);
    private SimpleStringProperty alertString = new SimpleStringProperty("");
    private User lobbyCreator;
    @FXML
    public void initialize() {
        try {
            LobbySimple lobbySimple = MainApplication.getClient().getLobby(DataHolder.connectingLobbyCreator);
            System.out.println(lobbySimple);
            initLobbyInformation(lobbySimple);
            initExitBtn();
            initTimer();
            initStartGame();
            initAlert();
            followToUpdates();

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
        System.out.println(lobby.getTheme());
        labelTheme.setText(Config.GAME_THEMES.get(lobby.getTheme()).getName());
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
                    } else if (change.wasRemoved()) {
                        Platform.runLater(() -> {
                            for (int i = 0; i < usersInLobby.size(); i++) {
                                User user = usersInLobby.get(i);
                                putUserIntoBox(
                                        (VBox) participantsPane.getChildren().get(i),
                                        user
                                );
                            }

                            ((VBox) participantsPane.getChildren().get(usersInLobby.size())).getChildren().clear();
                        });
                    }
                }
            }
        });
    }

    public void followToUpdates() {
        MainApplication.getClient().followToLobbyUpdates(usersInLobby, timerInSeconds, gameStarted, alertString);
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

    private void initTimer() {
        timerInSeconds.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int currentTime = t1.intValue();
                int minutes = currentTime / 60;
                int seconds = currentTime % 60;
                Platform.runLater(() -> {
                    labelMinutes.setText("0" + minutes);
                    labelSeconds.setText((seconds / 10 == 0) ? "0" + seconds : String.valueOf(seconds));
                });
            }
        });
    }

    private void initStartGame() {
        gameStarted.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    DataHolder.gameOwner = DataHolder.connectingLobbyCreator;
                    Platform.runLater(() -> {
                        try {
                            swapToGameScene();
                        } catch (IOException ex) {
                            System.out.println(ex);
                            gameStarted.set(false);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Информация");
                            alert.setHeaderText(null);
                            alert.setContentText("Ой-ой... Не получилось выполнить переход в игру.");
                            alert.showAndWait();
                        }
                    });
                }
            }
        });
    }

    private void initAlert() {
        alertString.addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                Platform.runLater(() -> {
                    if (t1.isEmpty()) return;
                    alertString.setValue("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация");
                    alert.setHeaderText(null);
                    alert.setContentText(t1);
                    alert.showAndWait();
                });
            }
        });
    }

    private void swapToGameScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(Config.GAME_SCENE));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) labelRoomName.getScene().getWindow();
        stage.setScene(scene);
    }




}
