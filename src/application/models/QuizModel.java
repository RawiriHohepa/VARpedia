package application.models;

import application.logic.Folders;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.*;

public class QuizModel {
    private int _currentScore;
    private String _quizTerm;
    private IntegerProperty _currentScoreIntegerProperty;
    private StringProperty _currentScoreTextProperty;

    public QuizModel() {
        _currentScore = 0;

        _currentScoreIntegerProperty = new SimpleIntegerProperty(0);
        _currentScoreTextProperty = new SimpleStringProperty("   Current Score: " + _currentScore);
        _currentScoreIntegerProperty.addListener((obs, oldValue, newValue) -> {
            _currentScoreTextProperty.setValue("   Current Score: " + newValue);
        });
    }

    // This method will retrieve a random quiz video from the current repository of quiz videos.
    public File selectRandomVideo() {
        File[] quizVideosArray = Folders.QUIZ.asFolder().listFiles();

        List<File> quizVideosList = new ArrayList<File>(Arrays.asList(quizVideosArray));
        Collections.shuffle(quizVideosList);

        Random rand = new Random();
        File quizVideo = quizVideosList.get(rand.nextInt(quizVideosList.size()));

        _quizTerm = quizVideo.getName().replace(".mp4", "");

        return quizVideo;
    }
    public boolean answerIsCorrect(String answer) {
        return answer.equalsIgnoreCase(_quizTerm);
    }

    public void incrementScore() {
        _currentScore++;
        _currentScoreIntegerProperty.setValue(_currentScoreIntegerProperty.get() + 1);
    }
    public int getCurrentScore() {
        return _currentScore;
    }

    public IntegerProperty getCurrentScoreIntegerProperty() {
        return _currentScoreIntegerProperty;
    }

    public StringProperty getCurrentScoreTextProperty() {
        return _currentScoreTextProperty;
    }
}
