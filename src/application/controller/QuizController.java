package application.controller;

import application.Main;
import application.logic.AlertBuilder;
import application.logic.Quiz;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class QuizController {
    @FXML
    MediaView _mediaView;
    @FXML
    private ToggleButton _backgroundMusicButton;
    @FXML
    private ToggleButton _backgroundMusicButtonInPlayer;
    @FXML
    private Pane _quizPlayer;
    @FXML
    private TextField _playerAnswerTextField;
    @FXML
    private Button _startButton;
    @FXML
    private Button _skipButton;
    @FXML
    private Button _checkButton;
    @FXML
    private Button _pausePlayButton;
    @FXML
    private Button _manageQuizButton;
    @FXML
    private ListView<String> _listOfQuiz;
    @FXML
    private Button _deleteButton;
    @FXML
    private Button _returnButton;
    @FXML
    private Button _backButton;
    @FXML
    private Label selectPrompt;
    @FXML
    private Label _quizListLabel;
    @FXML
    private Label _currentScoreText;
    @FXML
    private ImageView _quizImage;

    private Quiz _quiz;

    @FXML
    public void initialize() {
        _quiz = new Quiz();

        _currentScoreText.setText("   Current Score: " + _quiz.getCurrentScore());
        _quizPlayer.setVisible(false);

        BooleanBinding noCreationSelected = _listOfQuiz.getSelectionModel().selectedItemProperty().isNull();
        _deleteButton.disableProperty().bind(noCreationSelected);

        updateButtonTexts();

        boolean buttonIsSelected = _quiz.backgroundMusicButtonIsSelected();
        _backgroundMusicButton.setSelected(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);

        _startButton.setVisible(true);
        _manageQuizButton.setVisible(true);


        _listOfQuiz.setVisible(false);
        _returnButton.setVisible(false);
        _pausePlayButton.setVisible(false);
        _checkButton.setVisible(false);
        _skipButton.setVisible(false);
        _deleteButton.setVisible(false);
        _playerAnswerTextField.setVisible(false);
        _backgroundMusicButtonInPlayer.setVisible(false);

        /*
         * Credit to user DVarga
         * Full credit in NewCreationController in method setUpBooleanBindings()
         */
        // Don't let the user check their answer until they enter an answer
        BooleanBinding textIsEmpty = Bindings.createBooleanBinding(() ->
                        _playerAnswerTextField.getText().trim().isEmpty(),
                _playerAnswerTextField.textProperty());
        _checkButton.disableProperty().bind(textIsEmpty);
    }


    @FXML
    private void handleStartButton() {
        _quizPlayer.setVisible(true);
        _quizImage.setVisible(false);

        _currentScoreText.setVisible(true);
        _startButton.setVisible(false);
        _manageQuizButton.setVisible(false);
        _pausePlayButton.setVisible(true);
        _checkButton.setVisible(true);
        _skipButton.setVisible(true);
        _playerAnswerTextField.setVisible(true);
        _backgroundMusicButtonInPlayer.setVisible(true);

        _quiz.selectRandomVideo();

        _mediaView.setMediaPlayer(_quiz.createMediaPlayer());
        _pausePlayButton.setText("| |");
    }

    @FXML
    private void handleCheckButton() {
        _quiz.pauseMediaPlayer();

        AlertBuilder alertBuilder = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setHeaderText(null);

        if (_quiz.answerIsCorrect(_playerAnswerTextField.getText())) {
            //updating the score when user gets answer correct
            _quiz.incrementScore();
            _currentScoreText.setText("   Current Score: " + _quiz.getCurrentScore());
            _playerAnswerTextField.setText("");

            alertBuilder.setTitle("Well done that was right.")
                    .setContentText("Good luck in the next one.");
            alertBuilder.getResult().showAndWait();

            _startButton.fire();
        } else {
            alertBuilder.setTitle("Sorry that was wrong.")
                    .setContentText("Please try again.");
            alertBuilder.getResult().showAndWait();

            _quiz.playMediaPlayer();
        }
    }

    @FXML
    private void handlePausePlayButton() {
        String newButtonText = _quiz.pausePlayMedia();
        _pausePlayButton.setText(newButtonText);
    }

    // Return to main menu
    @FXML
    private void handleBackButton() throws IOException {
        if (_quiz.mediaPlayerIsCreated()) {
            _quiz.stopMediaPlayer();
        }
        Main.changeScene("resources/MainScreenScene.fxml");
    }

    @FXML
    private void handleSkipButton() {
        _startButton.fire();
    }

    @FXML
    public void handleManageQuizButton() {
        _quizImage.setVisible(false);

        selectPrompt.setVisible(true);
        _quizListLabel.setVisible(true);

        ListCurrentQuiz();
        _backButton.setVisible(false);
        _returnButton.setVisible(true);
        _listOfQuiz.setVisible(true);
        _manageQuizButton.setVisible(false);
        _deleteButton.setVisible(true);
        _startButton.setVisible(false);
    }

    @FXML
    public void handleSelectedQuiz() {
        String selectedQuiz = _listOfQuiz.getSelectionModel().getSelectedItem();
        _quiz.setSelectedQuiz(selectedQuiz);
        if (selectedQuiz != null) {
            selectPrompt.setText("");
        }
    }

    private void ListCurrentQuiz() {
        _listOfQuiz.setItems(_quiz.getCurrentListOfQuiz());
    }

    public File getSelectedFile() {
        return _quiz.getSelectedFile();
    }

    private String getSelectedQuizName() {
        return _quiz.getSelectedQuizName();
    }

    @FXML
    private void handleDeleteButton() {
        AlertBuilder alertBuilder = new AlertBuilder()
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitle("Confirm Deletion")
                .setHeaderText("Delete " + getSelectedQuizName() + "?")
                .setContentText("Are you sure you want to delete this quiz?");
        Alert deleteConfirmation = alertBuilder.getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _quiz.deleteSelectedFile();
            ListCurrentQuiz();

            selectPrompt.setText("                               " +
                    "Please select a quiz video to continue.");
        }
    }

    // Return back to quiz start screen.
    @FXML
    private void handleReturnButton() throws IOException {
        Main.changeScene("resources/QuizScene.fxml");
    }

    @FXML
    private void handleBackgroundMusic() {
        boolean buttonIsSelected = _backgroundMusicButton.isSelected();
        Main.backgroundMusicPlayer().handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButtonInPlayer.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    @FXML
    private void handleBackgroundMusicInPlayer() {
        boolean buttonIsSelected = _backgroundMusicButtonInPlayer.isSelected();
        Main.backgroundMusicPlayer().handleBackgroundMusic(buttonIsSelected);
        _backgroundMusicButton.setSelected(buttonIsSelected);
        updateButtonTexts();
    }

    private void updateButtonTexts() {
        String buttonText = _quiz.getBackgroundMusicButtonText();
        _backgroundMusicButton.setText(buttonText);
        _backgroundMusicButtonInPlayer.setText(buttonText);
    }
}