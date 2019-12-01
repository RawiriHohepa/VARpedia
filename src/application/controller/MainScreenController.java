package application.controller;

import application.BackgroundMusicPlayer;
import application.Main;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.Folders;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.io.IOException;

public class MainScreenController {
    @FXML
    private ToggleButton _backgroundMusicButton;

    private BackgroundMusicPlayer _backgroundMusicPlayer;

    @FXML
    public void initialize() {
        Main.setCurrentScene("MainScreenScene");
        _backgroundMusicPlayer = Main.getBackgroundMusicPlayer();

        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
        _backgroundMusicButton.setSelected(_backgroundMusicPlayer.getButtonIsSelected());
    }

    @FXML
    private void handleListButton() throws IOException {
        Scenes.changeScene(Scenes.LIST_CREATIONS_SCENE);
    }

    @FXML
    private void handleNewCreationButton() throws IOException {
        Scenes.changeScene(Scenes.NEW_CREATION_SCENE);
    }

    @FXML
    private void handleQuizButton() throws IOException {
        if (Folders.QUIZ.asFolder().listFiles().length == 0) {
            Alert noQuizVideoAlert = new AlertBuilder()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("No quiz videos")
                    .setHeaderText("There are currently no quiz videos to learn from.")
                    .setContentText("Please create a creation first and then start the quiz.")
                    .getResult();
            noQuizVideoAlert.showAndWait();
        } else {
            Scenes.changeScene(Scenes.QUIZ_SCENE);
        }
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.handleBackgroundMusic(_backgroundMusicButton.isSelected());
        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
    }

    @FXML
    private void handleCreateInformation() {
        Alert createInfo = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setTitle("Make a new creation")
                .setHeaderText("Make a brand cew creation to watch or learn from.")
                .setContentText("You will be guided through the creation process step by step. " +
                        "You can make a creation for anything that you want to know about.")
                .getResult();
        createInfo.show();
    }

    @FXML
    private void handlePlayInformation() {
        Alert playInfo = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setTitle("Watch a creation")
                .setHeaderText("Watch any creation that you have made.")
                .setContentText("You will see a list of creations that you have made in the past. " +
                    "You can play any creation that you want to.")
                .getResult();
        playInfo.show();
    }

    @FXML
    private void handleLearnInformation() {
        Alert learnInfo = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setTitle("Play a game")
                .setHeaderText("A fun quiz game begins when you press the button.")
                .setContentText("You will be quizzed based on all past creations you have made. " +
                    "Good luck going for the highest score.")
                .getResult();
        learnInfo.show();
    }

    @FXML
    private void handleVarpediaInformation() {
        Alert varpediaInfo = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setTitle("Welcome to VARpedia")
                .setHeaderText("A Visual, Aural and Reading encyclopedia.")
                .setContentText("For the feature overview of this application please " +
                    "refer to the User Manual where each feature is explained fully.")
                .getResult();
        varpediaInfo.show();
    }
}
