package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class BackgroundMusicPlayer {
    private static BackgroundMusicPlayer _instance;

    private final File BACKGROUND_MUSIC_DIR;
    private MediaPlayer _mediaPlayer;

    private BooleanProperty _isPlayingProperty;
    private StringProperty _buttonTextProperty;

    private BackgroundMusicPlayer() {
        BACKGROUND_MUSIC_DIR = new File(System.getProperty("user.dir") + "/music/" + "khalafnasirs_Magicy_World.mp3");

        _isPlayingProperty = new SimpleBooleanProperty(false);
        _buttonTextProperty = new SimpleStringProperty("Music: Off");

        _isPlayingProperty.addListener((obs, oldValue, newValue) -> {
            _buttonTextProperty.setValue(newValue ? "Music: On" : "Music: Off");
        });

        createMediaPlayer();
    }

    private void createMediaPlayer() {
        Media video = new Media(BACKGROUND_MUSIC_DIR.toURI().toString());
        _mediaPlayer = new MediaPlayer(video);

        //Once the video is finished the video will replay from the start
        _mediaPlayer.setOnEndOfMedia(() -> _mediaPlayer.seek(Duration.ZERO));
    }

    public void toggleBackgroundMusic() {
        if (!_isPlayingProperty.get()) {
            _mediaPlayer.play();
            _isPlayingProperty.setValue(true);
        } else {
            _mediaPlayer.pause();
            _isPlayingProperty.setValue(false);
        }
    }

    public static BackgroundMusicPlayer getInstance() {
        if (_instance == null) {
            _instance = new BackgroundMusicPlayer();
        }

        return _instance;
    }

    public StringProperty getButtonTextProperty() {
        return _buttonTextProperty;
    }
}
