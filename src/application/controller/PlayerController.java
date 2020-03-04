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
public class PlayerController extends Controller {
    @FXML
    private Button _backgroundMusicButton;
    @FXML
    private Button _backgroundMusicButtonInPlayer;
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

        _backgroundMusicButton.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());
        _backgroundMusicButtonInPlayer.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());

        _videoPlayerPane.getChildren().removeAll();

        ListCreationsController listCreationsController = (ListCreationsController) Scenes.LIST_CREATIONS.getController();
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
        Scenes.LIST_CREATIONS.changeTo();
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.toggleBackgroundMusic();
    }
}
