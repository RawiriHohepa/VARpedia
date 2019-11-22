package application.logic;

import application.Main;

import java.io.File;
import java.util.*;

public class Quiz {
    private int _currentScore;
    private String _quizTerm;

    public Quiz() {
        Main.setCurrentScene("QuizScene");

        _currentScore = 0;
    }
    //==================QUIZ VIDEO AND TERM===============================
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
    //==================SCORE===============================
    public void incrementScore() {
        _currentScore++;
    }
    public int getCurrentScore() {
        return _currentScore;
    }
}
