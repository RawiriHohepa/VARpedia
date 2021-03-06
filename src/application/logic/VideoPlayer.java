package application.logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class VideoPlayer {
    private MediaPlayer _mediaPlayer;
    private boolean _isPlaying;

    private static final String PLAY_BUTTON_TEXT = "\u25B6";
    private static final String PAUSE_BUTTON_TEXT = "| |";

    public VideoPlayer() {
        _isPlaying = false;
    }

    public MediaPlayer createMediaPlayer(File quizVideo) {
        String quizVideoURI = quizVideo.toURI().toString();

        Media video = new Media(quizVideoURI);
        _mediaPlayer = new MediaPlayer(video);

        _mediaPlayer.setAutoPlay(true);
        _isPlaying = true;

        return _mediaPlayer;
    }
    public void pausePlayMedia() {
        if (_isPlaying) {
            pauseMediaPlayer();
        } else {
            playMediaPlayer();
        }
        _isPlaying = !_isPlaying;
    }
    public void pauseMediaPlayer() {
        _mediaPlayer.pause();
    }
    public void playMediaPlayer() {
        _mediaPlayer.play();
    }
    public String getButtonText() {
        return mediaPlayerIsPlaying() ? PAUSE_BUTTON_TEXT : PLAY_BUTTON_TEXT;
    }
    public void stopMediaPlayer() {
        if (_mediaPlayer != null) {
            _mediaPlayer.stop();
        }
    }
    private boolean mediaPlayerIsPlaying() {
        return _isPlaying;
    }

    // PlayerController-exclusive methods
    public void toggleMute() {
        _mediaPlayer.setMute(!_mediaPlayer.isMute());
    }
    public void changeVideoTime(int secondsToAdd) {
        _mediaPlayer.seek(_mediaPlayer.getCurrentTime().add(Duration.seconds(secondsToAdd)));
    }
    public void disposeMediaPlayer() {
        if (_mediaPlayer != null) {
            _mediaPlayer.dispose();
        }
    }
}
