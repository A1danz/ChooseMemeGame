module ru.kpfu.itis.galeev.aidan.choosememegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.kpfu.itis.galeev.aidan.choosememegame to javafx.fxml;
    exports ru.kpfu.itis.galeev.aidan.choosememegame;

    opens ru.kpfu.itis.galeev.aidan.choosememegame.controllers to javafx.fxml;
    exports ru.kpfu.itis.galeev.aidan.choosememegame.controllers;
}