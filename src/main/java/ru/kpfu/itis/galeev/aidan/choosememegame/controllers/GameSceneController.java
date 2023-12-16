package ru.kpfu.itis.galeev.aidan.choosememegame.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import ru.kpfu.itis.galeev.aidan.choosememegame.MainApplication;
import ru.kpfu.itis.galeev.aidan.choosememegame.client.Client;
import ru.kpfu.itis.galeev.aidan.choosememegame.model.*;
import ru.kpfu.itis.galeev.aidan.choosememegame.utils.DataHolder;

import java.util.HashMap;
import java.util.List;

public class GameSceneController {
    @FXML
    private AnchorPane bigSituationPane;

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
    private GridPane thrownCardsPane;

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

    @FXML
    private Label labelStartTimer;

    private GameSimple game;
    private SimpleBooleanProperty gameStarted = new SimpleBooleanProperty(false);
    private SimpleIntegerProperty startTimer = new SimpleIntegerProperty(10);
    private HashMap<String, ThrownCard> innerMap = new HashMap<>();
    private ObservableMap<String, ThrownCard> usersThrownCards = FXCollections.observableMap(innerMap);

    @FXML
    public void initialize() {
        try {
            String gameOwner = DataHolder.gameOwner;
            Client client = MainApplication.getClient();
            game = client.getSimpleGame(gameOwner);
            GameUserSimple gameUser = game.getUser();

            initUserCard(gameUser);
            initCardsCount(game.getMemeCardsCount());
            initGameParticipants(game.getUsersInGame());
            List<MemeCard> cards = client.getCards(gameOwner);
            initUserCards(cards);
            initThrownCardsPane();

            // game updates
            initUserThrownCard();
            initUpdatingSituation();

            client.followToGameUpdates(gameStarted, startTimer, usersThrownCards, game);
            initStartGameActions();
            initInstruction();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }

    private void initStartGameActions() {
        gameStarted.addListener((observableValue, previous, now) -> {
            if (now) {
                Platform.runLater(() -> {
                    imageWaitingPlayers.setVisible(false);
                    labelStartTimer.setVisible(true);
                    labelStartTimer.setText(startTimer.getValue().toString());
                });
                startTimer.addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        Platform.runLater(() -> {
                            if (t1.intValue() > 0) {
                                labelStartTimer.setText(String.valueOf(t1.intValue()));
                            } else {
                                labelStartTimer.setVisible(false);
                            }
                        });
                    }
                });
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

            Circle circleBehindAvatar = new Circle();
            circleBehindAvatar.setFill(Color.WHITE);
            circleBehindAvatar.setRadius(26.0);
            circleBehindAvatar.setStroke(Color.BLACK);
            circleBehindAvatar.setStrokeType(StrokeType.INSIDE);
            circleBehindAvatar.setStrokeWidth(0.0);

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

            Circle mustThrowCircle = new Circle();
            mustThrowCircle.setFill(Color.web("#f6b801"));
            mustThrowCircle.setRadius(5.0);
            mustThrowCircle.setStroke(Color.BLACK);
            mustThrowCircle.setStrokeType(StrokeType.INSIDE);
            mustThrowCircle.setStrokeWidth(0.0);
            mustThrowCircle.setVisible(false);
            VBox.setMargin(mustThrowCircle, new Insets(-3, 0, 0, 0));

            vbox.getChildren().addAll(innerVBox, label1, label2, mustThrowCircle);
            innerVBox.getChildren().addAll(circleBehindAvatar, imageView);

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

            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    MainApplication.getClient().throwCard(game.getCreator().getUsername(), card.getPathToCard());
                    userMemesBox.getChildren().remove(imageView);
                }
            });

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

    private void initUserThrownCard() {
        usersThrownCards.addListener(new MapChangeListener<String, ThrownCard>() {
            @Override
            public void onChanged(Change<? extends String, ? extends ThrownCard> change) {
                if (change.wasAdded()) {
                    String cardOwner = change.getKey();
                    ThrownCard thrownCard = change.getValueAdded();
                    System.out.println("card owner - " + cardOwner + " thrown-card - " + thrownCard.getMemeCard().getPathToCard());

                    // generating box
                    ImageView imageView = new ImageView(new Image(String.valueOf(
                            MainApplication.class.getResource(thrownCard.getMemeCard().getPathToCard())
                    )));
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(122);
                    imageView.setPickOnBounds(true);
                    imageView.setPreserveRatio(true);

                    Label label = new Label(cardOwner);
                    label.getStyleClass().add("label-card-owner");
                    VBox.setMargin(label, new Insets(-5, 0, 0, 0));
                    System.out.println(thrownCardsPane.getChildren());
                    List<GameUserSimple> users = game.getUsersInGame();
                    System.out.println(users.size());
                    if (cardOwner.equals(MainApplication.getClient().getUser().getUsername())) {
                        Platform.runLater(() -> {
                            System.out.println((VBox) thrownCardsPane.getChildren().get(users.size()) + " myself card");
                            ((VBox) thrownCardsPane.getChildren().get(users.size())).getChildren().addAll(imageView, label);
                        });
                    } else {
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).getUser().getUsername().equals(cardOwner)) {
                                int memePlaceIndex = i;
                                Platform.runLater(() -> {
                                    System.out.println((VBox) thrownCardsPane.getChildren().get(memePlaceIndex) + " other card");
                                    ((VBox) thrownCardsPane.getChildren().get(memePlaceIndex)).getChildren().addAll(imageView, label);
                                });
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void initThrownCardsPane() {
        System.out.println(thrownCardsPane.getColumnConstraints() + " old column");
        System.out.println(thrownCardsPane.getRowConstraints() + " old row");
        thrownCardsPane.getChildren().clear();
        thrownCardsPane.getColumnConstraints().clear();
        thrownCardsPane.getRowConstraints().clear();
        int usersCount = game.getUsersInGame().size() + 1;
        int rowCount = usersCount > 5 ? 2 : 1;
        int columnCount = Math.min(usersCount, 5);
        for (int i = 0; i < columnCount; i++) {
            // <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0" />
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(100.0);
            colConst.setMinWidth(10.0);
            colConst.setHgrow(Priority.SOMETIMES);
            thrownCardsPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < rowCount; i++) {
            // <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(10.0);
            rowConstraints.setPrefHeight(30.0);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            thrownCardsPane.getRowConstraints().add(rowConstraints);
        }
        System.out.println(rowCount * columnCount + "rowCount * columnCount");
        for (int i = 0; i < rowCount * columnCount; i++) {
            int rowIndex = i % 5;
            int columnIndex = i / 5;
            VBox vBox = new VBox();
            if (rowCount * columnCount > 5) {
                vBox.setAlignment(Pos.TOP_CENTER);
            } else {
                vBox.setAlignment(Pos.CENTER);
            }
            thrownCardsPane.add(vBox, rowIndex, columnIndex);
        }

        System.out.println(thrownCardsPane.getColumnConstraints() + " columnConst");
        System.out.println(thrownCardsPane.getRowConstraints() + " rownConst");
        System.out.println(thrownCardsPane.getColumnCount() + " colCount");
        System.out.println(thrownCardsPane.getRowCount() + "rowConst");
    }

    public void initUpdatingSituation() {
        game.getSituation().addListener((observableValue, old, newValue) -> Platform.runLater(() -> {
            System.out.println(old + " - old; " + newValue + " - new");
            if (!newValue.isEmpty()) {
                setBigSituation(true, newValue);
            } else {
                setBigSituation(false, newValue);
                setSmallSituation(true, old);
                startThrowCardProcess();
            }
        }));
    }

    private void setBigSituation(boolean visible, String situationText) {
        Platform.runLater(() -> {
            bigSituationPane.setVisible(visible);
            labelBigSituation.setText(situationText);
        });
    }

    private void setSmallSituation(boolean visible, String situationText) {
        Platform.runLater(() -> {
            smallSituationPane.setVisible(visible);
            labelSmallSituation.setText(situationText);
        });
    }

    private void startThrowCardProcess() {
        Platform.runLater(() -> {
            labelHelpText.setText("Настала очередь бросать карту!\nПросто нажмите на подходящую картинку");
        });
    }
}
