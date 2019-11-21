package application.controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.IOException;


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
    private MediaPlayer _mediaPlayer;

    @FXML
    public void initialize() {
        Main.setCurrentScene("PlayerScene");

        // Initialise the media player with a video as soon as scene is loaded.
        String buttonText = Main.getBackgroundMusicPlayer().getButtonText();
        _backgroundMusicButton.setText(buttonText);
        _backgroundMusicButtonInPlayer.setText(buttonText);
        boolean buttonIsSelected = Main.getBackgroundMusicPlayer().getButtonIsSelected();
        _backgroundMusicButton.setSelected(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);

        _videoPlayerPane.getChildren().removeAll();

        _videoTitle.setText("  Now Playing: " + ListCreationsController.getSelectedCreationName());
        Media video = new Media(ListCreationsController.getSelectedFile().toURI().toString());
        _mediaPlayer = new MediaPlayer(video);
        _mediaPlayer.setAutoPlay(true);

        _mediaView.setMediaPlayer(_mediaPlayer);

        //Once the video is finished the user will return to the main menu.
        _mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                _returnButton.fire();
            }
        });
    }

    @FXML
    private void handlePausePlayButton() {
        if (_mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            _mediaPlayer.pause();
            _pausePlayButton.setText("\u25B6");
        } else {
            _mediaPlayer.play();
            _pausePlayButton.setText("| |");
        }
    }

    // for muting the audio of the video
    // Video will continue to play
    @FXML
    private void handleMuteButton() {
        _mediaPlayer.setMute(!_mediaPlayer.isMute());
    }

    // Will skip forwards 3 seconds in the video time.
    @FXML
    private void handleForwardButton() {
        changeVideoTime(3);
    }

    // Will skip back 3 seconds in the video time.
    @FXML
    private void handleBackwardsButton() {
        changeVideoTime(-3);
    }

    private void changeVideoTime(int secondsToAdd) {
        _mediaPlayer.seek(_mediaPlayer.getCurrentTime().add(Duration.seconds(secondsToAdd)));
    }

    // Return to main menu
    @FXML
    private void handleReturnButton() throws IOException {
        _mediaPlayer.stop();
        _mediaPlayer.dispose();
        Main.changeScene("resources/ListCreationsScene.fxml");
    }

    @FXML
    private void handleBackgroundMusic() {
        boolean buttonIsSelected = _backgroundMusicButton.isSelected();
        Main.getBackgroundMusicPlayer().handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    @FXML
    private void handleBackgroundMusicInPlayer() {
        boolean buttonIsSelected = _backgroundMusicButtonInPlayer.isSelected();
        Main.getBackgroundMusicPlayer().handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButton.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    private void updateButtonTexts() {
        String buttonText = Main.getBackgroundMusicPlayer().getButtonText();
        _backgroundMusicButton.setText(buttonText);
        _backgroundMusicButtonInPlayer.setText(buttonText);
    }
}
