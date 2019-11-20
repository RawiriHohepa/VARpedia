package application.logic;

import application.BackgroundMusicPlayer;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class Quiz {
    private int _currentScore;
    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private MediaPlayer _mediaPlayer;
    private File _quizVideo;
    private String _quizTerm;
    private String _selectedQuiz;

    // The quiz directory where all quiz videos are stored.
    private static final File QUIZ_FOLDER = new File(System.getProperty("user.dir") + "/quiz/");

    public Quiz() {
        Main.setCurrentScene("QuizScene");

        _currentScore = 0;
        _backgroundMusicPlayer = Main.backgroundMusicPlayer();
    }
    //==================QUIZ VIDEO AND TERM===============================
    // This method will retrieve a random quiz video from the current repository of quiz videos.
    public void selectRandomVideo() {
        File[] quizVideosArray = QUIZ_FOLDER.listFiles();

        List<File> quizVideosList = new ArrayList<File>(Arrays.asList(quizVideosArray));
        Collections.shuffle(quizVideosList);

        Random rand = new Random();
        _quizVideo = quizVideosList.get(rand.nextInt(quizVideosList.size()));

        _quizTerm = _quizVideo.getName().replace(".mp4", "");
    }
    public boolean answerIsCorrect(String answer) {
        return answer.equalsIgnoreCase(_quizTerm);
    }
    public String getQuizTerm() {
        return _quizTerm;
    }
    public ObservableList<String> getCurrentListOfQuiz() {
        List<String> creationNamesList = new ArrayList<String>();

        // Will get every file in the quiz directory and create an indexed
        // list of file names.
        int indexCounter = 1;
        for (final File quiz : QUIZ_FOLDER.listFiles()) {
            String fileName = quiz.getName();
            if (fileName.endsWith(".mp4")) {
                creationNamesList.add("" + indexCounter + ". " + fileName.replace(".mp4", ""));
                indexCounter++;
            }
        }

        // Turning the list of quiz names into an listView<String> for the GUI.
        return FXCollections.observableArrayList(creationNamesList);
    }
    //==================SCORE===============================
    public void incrementScore() {
        _currentScore++;
    }
    public int getCurrentScore() {
        return _currentScore;
    }
    //==================BACKGROUND MUSIC===============================
    public String getBackgroundMusicButtonText() {
        return _backgroundMusicPlayer.getButtonText();
    }
    public boolean backgroundMusicButtonIsSelected() {
        return _backgroundMusicPlayer.getButtonIsSelected();
    }

    //==================MEDIA PLAYER===============================
    public MediaPlayer createMediaPlayer() {
        String quizVideoURI = _quizVideo.toURI().toString();

        Media video = new Media(quizVideoURI);
        _mediaPlayer = new MediaPlayer(video);

        _mediaPlayer.setAutoPlay(true);
        //Once the video is finished the video will replay from the start
        _mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                _mediaPlayer.seek(Duration.ZERO);
                _mediaPlayer.play();
            }
        });

        return _mediaPlayer;
    }
    public String pausePlayMedia() {
        if (mediaPlayerIsPlaying()) {
            pauseMediaPlayer();
            return "\u25B6";
        } else {
            playMediaPlayer();
            return "| |";
        }
    }
    public void pauseMediaPlayer() {
        _mediaPlayer.pause();
    }
    public void playMediaPlayer() {
        _mediaPlayer.play();
    }
    public void stopMediaPlayer() {
        _mediaPlayer.stop();
    }
    private boolean mediaPlayerIsPlaying() {
        return _mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    public boolean mediaPlayerIsCreated() {
        return _mediaPlayer != null;
    }
    //===================FILES=======================
    public void deleteSelectedFile() {
        getSelectedFile().delete();
        _selectedQuiz = null;
    }
    public void setSelectedQuiz(String selectedQuiz) {
        _selectedQuiz = selectedQuiz;
    }
    public File getSelectedFile() {
        // Removal of the index on the quiz video name
        // and creating it as a file to be played or deleted.
        String fileName = getSelectedQuizName();
        return new File(QUIZ_FOLDER + fileName + ".mp4");
    }
    public String getSelectedQuizName() {
        // Removal of the index on the quiz video name
        return ("" + _selectedQuiz.substring(_selectedQuiz.indexOf(".") + 2));
    }
}
