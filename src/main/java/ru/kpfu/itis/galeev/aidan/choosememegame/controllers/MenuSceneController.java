package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.client.Client;
import ru.kpfu.itis.galeev.aidan.choosememegame.config.Config;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.GameTheme;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.Lobby;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuSceneController {
    private static final Map<String, String> NAME_THEMES = initThemesName();
    private static Map<String, String> initThemesName() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Стандартная", "default");
        return map;
    }

    private static final String CAPACITY_REGEX = "^[2-9]|10$";
    private static final String ROOM_NAME_REGEX = "^.{1,20}$";

    @FXML
    private Button btnCreateRoom;

    @FXML
    private ImageView btnRefreshRooms;

    @FXML
    private Button btnRoomEnterById;

    @FXML
    private ComboBox<String> comboBoxTheme;

    @FXML
    private TextField inputRoomId;

    @FXML
    private TextField inputRoomName;

    @FXML
    private TextField inputRoomCapacity;

    @FXML
    private ImageView imageUserAvatar;

    @FXML
    private Label labelUsername;

    @FXML
    private ScrollPane roomsPane;

    @FXML
    private VBox roomsBox;

    @FXML
    private ImageView loadingImage;

    @FXML
    public void initialize() {
        try {
            inputRoomCapacity.textProperty().addListener((observable, oldString, newString) -> {
                if (!newString.matches(CAPACITY_REGEX) && !newString.isEmpty() && !newString.equals("1")) {
                    inputRoomCapacity.setText(oldString);
                }
            });

            inputRoomName.textProperty().addListener(((observableValue, oldString, newString) -> {
                if (!newString.matches(ROOM_NAME_REGEX) && !newString.isEmpty()) {
                    inputRoomName.setText(oldString);
                }
            }));
            Client client = MainApplication.getClient();
            initUserData(client.getUser());
            initGameThemes();
            showLoading(true);
            new Thread(() -> {
                uploadRooms();
            }).start();

            btnCreateRoom.setOnAction((actionEvent -> {
                createLobby();
            }));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void uploadRooms() {
        List<Lobby> lobbies = MainApplication.getClient().getLobbies();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < lobbies.size(); i++) {
                    Lobby lobby = lobbies.get(i);
                    User creator = lobby.getCreator();
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setPrefHeight(120.0);
                    hbox.setPrefWidth(200.0);
                    hbox.setSpacing(5.0);

                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(100.0);
                    imageView.setFitWidth(200.0);
                    imageView.setPickOnBounds(true);
                    imageView.setPreserveRatio(true);
                    Image image = new Image(String.valueOf(MainApplication.class.getResource("img/avatars/" + creator.getPathToAvatar())));
                    imageView.setImage(image);

                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER_LEFT);
                    vbox.setPrefHeight(200.0);
                    vbox.setPrefWidth(400.0);
                    vbox.setSpacing(15.0);

                    VBox vbox2 = new VBox();
                    vbox2.setAlignment(Pos.CENTER_LEFT);

                    Label label1 = new Label("Комната Айдан, @" + creator.getUsername());
                    label1.setAlignment(Pos.TOP_LEFT);
                    label1.getStyleClass().add("label-room-name");

                    Label label2 = new Label("Тема: " + lobby.getTheme());
                    label2.setAlignment(Pos.TOP_LEFT);
                    label2.getStyleClass().add("label-author-name");

                    Label label3 = new Label("Количество участников: "+ lobby.getParticipantsCount() + "/" + lobby.getLobbyCapacity());
                    label3.setAlignment(Pos.TOP_LEFT);
                    label3.getStyleClass().add("label-participants");

                    vbox2.getChildren().addAll(label1, label2, label3);

                    Button button = new Button("Подключиться");
                    button.getStyleClass().add("enter-btn");
                    button.setMnemonicParsing(false);

                    vbox.getChildren().addAll(vbox2, button);

                    hbox.getChildren().addAll(imageView, vbox);

                    Insets insets = new Insets(5.0, 5.0, 5.0, 5.0);
                    hbox.setPadding(insets);

                    roomsBox.getChildren().add(hbox);

                    if (i != lobbies.size() - 1) {
                        Line line = new Line();
                        line.setEndX(100.0);
                        line.setStartX(-100.0);
                        line.setStroke(Color.web("#473f91"));
                        line.setStrokeWidth(0.5);
                        roomsBox.getChildren().add(line);
                    }
                }
            }
        });
        showLoading(false);
    }

    private void initUserData(User user) {
        imageUserAvatar.setImage(new Image(String.valueOf(MainApplication.class.getResource("img/avatars/" + user.getPathToAvatar())), 512, 512, false, false));
        Circle clip = new Circle(imageUserAvatar.getX() + 75, imageUserAvatar.getY() + 75, 75);
        imageUserAvatar.setClip(clip);

        labelUsername.setText(user.getUsername());
    }

    private void initGameThemes() {
        for (Map.Entry<String, String> entry : Config.GAME_THEMES.entrySet()) {
            comboBoxTheme.getItems().add(entry.getKey());
        }
    }

    private void showLoading(boolean status) {
        loadingImage.setVisible(status);
    }

    private void createLobby() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);

        if (inputRoomName.getText().isBlank()) {
            alert.setContentText("Нельзя создать комнату с пустым названием.");
            alert.showAndWait();
        } else if (inputRoomName.getText().startsWith(" ") || inputRoomName.getText().endsWith(" ")) {
            alert.setContentText("Название не может начинаться с пустого символа");
            alert.showAndWait();
        } else if (!inputRoomName.getText().matches(ROOM_NAME_REGEX)) {
            alert.setContentText("Название не соответствует формату: от 1 до 20 символов.");
            alert.showAndWait();
        } else if (!inputRoomCapacity.getText().matches(CAPACITY_REGEX)) {
            alert.setContentText("Число участников должно быть от 2 до 10.");
            alert.showAndWait();
        } else if (comboBoxTheme.getValue() == null) {
            alert.setContentText("Выберите тему");
            alert.showAndWait();
        } else {
            String resultOfCreatingLobby = MainApplication.getClient().createLobby(
                    inputRoomName.getText(),
                    Integer.parseInt(inputRoomCapacity.getText()),
                    NAME_THEMES.get(comboBoxTheme.getValue()));
            if (resultOfCreatingLobby.equals("Лобби успешно создано")) {
                System.out.println("SUCCESS");
            } else {
                alert.setContentText(resultOfCreatingLobby);
                alert.showAndWait();
            }
        }
    }
}
