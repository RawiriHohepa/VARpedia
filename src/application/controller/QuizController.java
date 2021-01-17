package application.controller;

import application.BackgroundMusicPlayer;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.FileManager;
import application.logic.Folders;
import application.logic.VideoPlayer;
import application.models.ListViewModel;
import application.models.QuizModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class QuizController extends Controller {
    @FXML
    MediaView _mediaView;
    @FXML
    private Button _backgroundMusicButton;
    @FXML
    private Button _backgroundMusicButtonInPlayer;
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
    private ListView<File> _listOfQuiz;
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

    private QuizModel _model;
    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private VideoPlayer _videoPlayer;
    private FileManager _fileManager;
    private ListViewModel _listViewModel;

    @FXML
    public void initialize() {
        _model = new QuizModel();
        _backgroundMusicPlayer = BackgroundMusicPlayer.getInstance();
        _listViewModel = new ListViewModel(Folders.QUIZ, _listOfQuiz.getSelectionModel().selectedItemProperty());

        _backgroundMusicButton.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());
        _backgroundMusicButtonInPlayer.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());

        // Disable the buttons and display a prompt whenever there is no creation selected
        BooleanBinding noQuizSelectedBinding = _listViewModel.getNoFileSelectedBinding();
        _deleteButton.disableProperty().bind(noQuizSelectedBinding);

        _listOfQuiz.itemsProperty().bind(_listViewModel.getCurrentFilesProperty());
        _selectPrompt.textProperty().bind(_listViewModel.getSelectPromptTextProperty());

        _listViewModel.updateCurrentFiles();

        _currentScoreText.textProperty().bind(_model.getCurrentScoreTextProperty());


        _videoPlayer = new VideoPlayer();
        _fileManager = new FileManager(Folders.QUIZ);

        _quizPlayer.setVisible(false);


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

        MediaPlayer quizPlayer = _videoPlayer.createMediaPlayer(_model.selectRandomVideo());
        //Once the video is finished the video will replay from the start
        quizPlayer.setOnEndOfMedia(() -> {
            quizPlayer.seek(Duration.ZERO);
            quizPlayer.play();
        });

        _mediaView.setMediaPlayer(quizPlayer);
        _pausePlayButton.setText(_videoPlayer.getButtonText());
    }

    @FXML
    private void handleCheckButton() {
        _videoPlayer.pauseMediaPlayer();

        AlertBuilder answerResultPopupBuilder = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setHeaderText(null);

        if (_model.answerIsCorrect(_playerAnswerTextField.getText())) {
            //updating the score when user gets answer correct
            _model.incrementScore();
            _playerAnswerTextField.setText("");

            answerResultPopupBuilder.setTitle("Well done that was right.")
                    .setContentText("Good luck in the next one.");
            answerResultPopupBuilder.getResult().showAndWait();

            _startButton.fire();
        } else {
            answerResultPopupBuilder.setTitle("Sorry that was wrong.")
                    .setContentText("Please try again.");
            answerResultPopupBuilder.getResult().showAndWait();

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
    private void handleBackButton() {
        _videoPlayer.stopMediaPlayer();
        Scenes.MAIN_SCREEN.changeTo();
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

        _listViewModel.updateCurrentFiles();
        _backButton.setVisible(false);
        _returnButton.setVisible(true);
        _listOfQuiz.setVisible(true);
        _manageQuizButton.setVisible(false);
        _deleteButton.setVisible(true);
        _startButton.setVisible(false);
    }

    @FXML
    private void handleDeleteButton() {
        Alert deleteConfirmation = new AlertBuilder()
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitle("Confirm Deletion")
                .setHeaderText("Delete " + _listViewModel.getSelectedFileName() + "?")
                .setContentText("Are you sure you want to delete this quiz?")
                .getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _listViewModel.deleteSelectedFile();
            _listViewModel.updateCurrentFiles();
        }
    }

    // Return back to quiz start screen.
    @FXML
    private void handleReturnButton() {
        Scenes.QUIZ.changeTo();
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.toggleBackgroundMusic();
    }
}