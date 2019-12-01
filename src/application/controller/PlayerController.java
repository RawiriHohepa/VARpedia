package application.controller;

import application.BackgroundMusicPlayer;
import application.Main;
import application.Scenes;
import application.logic.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Media video creation will play as soon as the scene is loaded included
 * is a pause/play button, skip forwards, skip backwards and mute button
 */
public class PlayerController {
    @FXML
    private ToggleButton _backgroundMusicButton;
    @FXML
    private ToggleButton _backgroundMusicButtonInPlayer;
    @FXML
    private Pane _videoPlayerPane;
    @FXML
    private Button _returnButton;
    @FXML
    private Button _pausePlayButton;
    @FXML
    private Label _videoTitle;

    @FXML
    private MediaView _mediaView;

    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private VideoPlayer _videoPlayer;

    @FXML
    public void initialize() {
        _backgroundMusicPlayer = Main.getBackgroundMusicPlayer();
        _videoPlayer = new VideoPlayer();

        // Initialise the media player with a video as soon as scene is loaded.
        updateButtonTexts();
        setButtonsSelected();

        _videoPlayerPane.getChildren().removeAll();

        ListCreationsController listCreationsController = (ListCreationsController) Scenes.LIST_CREATIONS_SCENE.getController();
        _videoTitle.setText("  Now Playing: " + listCreationsController.getSelectedCreationName());

        MediaPlayer mediaPlayer = _videoPlayer.createMediaPlayer(listCreationsController.getSelectedFile());
        _mediaView.setMediaPlayer(mediaPlayer);

        //Once the video is finished the user will return to the main menu.
        mediaPlayer.setOnEndOfMedia(() -> _returnButton.fire());
    }

    @FXML
    private void handlePausePlayButton() {
        _videoPlayer.pausePlayMedia();
        _pausePlayButton.setText(_videoPlayer.getButtonText());
    }

    // for muting the audio of the video
    // Video will continue to play
    @FXML
    private void handleMuteButton() {
        _videoPlayer.toggleMute();
    }

    // Will skip forwards 3 seconds in the video time.
    @FXML
    private void handleForwardButton() {
        _videoPlayer.changeVideoTime(3);
    }

    // Will skip back 3 seconds in the video time.
    @FXML
    private void handleBackwardsButton() {
        _videoPlayer.changeVideoTime(-3);
    }

    // Return to main menu
    @FXML
    private void handleReturnButton() {
        _videoPlayer.stopMediaPlayer();
        _videoPlayer.disposeMediaPlayer();
        Scenes.changeScene(Scenes.LIST_CREATIONS_SCENE);
    }

    @FXML
    private void handleBackgroundMusic() {
        boolean buttonIsSelected = _backgroundMusicButton.isSelected();
        _backgroundMusicPlayer.handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    @FXML
    private void handleBackgroundMusicInPlayer() {
        boolean buttonIsSelected = _backgroundMusicButtonInPlayer.isSelected();
        _backgroundMusicPlayer.handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButton.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    private void updateButtonTexts() {
        String buttonText = _backgroundMusicPlayer.getButtonText();
        _backgroundMusicButton.setText(buttonText);
        _backgroundMusicButtonInPlayer.setText(buttonText);
    }

    private void setButtonsSelected() {
        boolean buttonIsSelected = _backgroundMusicPlayer.getButtonIsSelected();
        _backgroundMusicButton.setSelected(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);
    }
}
