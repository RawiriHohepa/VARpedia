package application.controller;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;

public class MainScreenController {

    @FXML
    private void handleListButton() throws IOException {
        Main.setScene("resources/listCreationsScene.fxml");

    }
    @FXML
    private void handleQuizButton() throws IOException {
        File folder = new File(System.getProperty("user.dir") + "/quiz/");
        if (!(folder.listFiles().length== 0)){
            Main.setScene("resources/QuizScene.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No quiz videos");
            alert.setContentText("Please create a creation first and then start the quiz.");
            alert.showAndWait();
            return;
        }


    }

}