package application.controller;

import application.BackgroundMusicPlayer;
import application.Main;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.FileManager;
import application.logic.Quiz;
import application.logic.VideoPlayer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

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
    private Label _selectPrompt;
    @FXML
    private Label _quizListLabel;
    @FXML
    private Label _currentScoreText;
    @FXML
    private ImageView _quizImage;

    private Quiz _quiz;
    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private VideoPlayer _videoPlayer;
    private FileManager _fileManager;

    @FXML
    public void initialize() {
        _quiz = new Quiz();
        _backgroundMusicPlayer = Main.getBackgroundMusicPlayer();
        _videoPlayer = new VideoPlayer();
        _fileManager = new FileManager(Quiz.QUIZ_FOLDER);

        _currentScoreText.setText("   Current Score: " + _quiz.getCurrentScore());
        _quizPlayer.setVisible(false);

        BooleanBinding noCreationSelected = _listOfQuiz.getSelectionModel().selectedItemProperty().isNull();
        _deleteButton.disableProperty().bind(noCreationSelected);

        updateButtonTexts();
        setButtonsSelected();

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

        MediaPlayer quizPlayer = _videoPlayer.createMediaPlayer(_quiz.selectRandomVideo());
        _mediaView.setMediaPlayer(quizPlayer);
        _pausePlayButton.setText(_videoPlayer.getButtonText());
    }

    @FXML
    private void handleCheckButton() {
        _videoPlayer.pauseMediaPlayer();

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

            _videoPlayer.playMediaPlayer();
        }
    }

    @FXML
    private void handlePausePlayButton() {
        _videoPlayer.pausePlayMedia();
        _pausePlayButton.setText(_videoPlayer.getButtonText());
    }

    // Return to main menu
    @FXML
    private void handleBackButton() throws IOException {
        _videoPlayer.stopMediaPlayer();
        Scenes.changeScene(Scenes.MAIN_SCREEN_SCENE);
    }

    @FXML
    private void handleSkipButton() {
        _startButton.fire();
    }

    @FXML
    public void handleManageQuizButton() {
        _quizImage.setVisible(false);

        _selectPrompt.setVisible(true);
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
        _fileManager.setSelectedFileName(selectedQuiz);
        if (selectedQuiz != null) {
            _selectPrompt.setText("");
        }
    }

    private void ListCurrentQuiz() {
        _listOfQuiz.setItems(_fileManager.getCurrentFilesList());
    }

    @FXML
    private void handleDeleteButton() {
        AlertBuilder alertBuilder = new AlertBuilder()
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitle("Confirm Deletion")
                .setHeaderText("Delete " + _fileManager.getSelectedFileName() + "?")
                .setContentText("Are you sure you want to delete this quiz?");
        Alert deleteConfirmation = alertBuilder.getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _fileManager.deleteSelectedFile();
            ListCurrentQuiz();

            _selectPrompt.setText("                               " +
                    "Please select a quiz video to continue.");
        }
    }

    // Return back to quiz start screen.
    @FXML
    private void handleReturnButton() throws IOException {
        Scenes.changeScene(Scenes.QUIZ_SCENE);
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