package application.controller;

import application.BackgroundMusicPlayer;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.ListCreations;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.Optional;

public class ListCreationsController extends Controller {
    @FXML
    private Button _backgroundMusicButton;

    @FXML
    private ListView<File> _listViewCreations;

    @FXML
    private Button _playButton;
    @FXML
    private Button _deleteButton;
    @FXML
    private Label _selectPrompt;

    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private ListCreations _listCreations;

    @FXML
    public void initialize() {
        _backgroundMusicPlayer = BackgroundMusicPlayer.getInstance();
        _listCreations = new ListCreations();

        _backgroundMusicButton.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());

        // Update the model whenever a creation is selected or deselected
        _listCreations.getSelectedCreationProperty().bind(_listViewCreations.getSelectionModel().selectedItemProperty());

        // Disable the buttons and display a prompt whenever there is no creation selected
        BooleanBinding noCreationSelected = _listCreations.getSelectedCreationProperty().isNull();
        _playButton.disableProperty().bind(noCreationSelected);
        _deleteButton.disableProperty().bind(noCreationSelected);
        noCreationSelected.addListener((obs, oldValue, newValue) -> {
            // TODO edit position of _selectPrompt so the empty space is not needed
            _selectPrompt.setText(newValue ? "                  Please select a creation to continue." : "");
        });

        _listViewCreations.itemsProperty().bind(_listCreations.getCurrentFilesProperty());

        _listCreations.updateCurrentFiles();
    }

    @FXML
    private void handleNewCreationButton() {
        Scenes.NEW_CREATION.changeTo();
    }

    @FXML
    private void handlePlayButton() {
        Scenes.PLAYER.changeTo();
    }

    @FXML
    private void handleReturnButton() {
        Scenes.MAIN_SCREEN.changeTo();
    }

    @FXML
    private void handleDeleteButton() {
        Alert deleteConfirmation = new AlertBuilder()
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitle("Confirm Deletion")
                .setHeaderText("Delete " + _listCreations.getSelectedCreationProperty().get().getName() + "?")
                .setContentText("Are you sure you want to delete this creation?")
                .getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _listCreations.deleteSelectedFile();
        }
    }

    // Used by PlayerController
    public String getSelectedCreationName() {
        return _listCreations.getSelectedCreationProperty().get().getName();
    }

    // Used by PlayerController
    public File getSelectedFile() {
        return _listCreations.getSelectedCreationProperty().get();
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.toggleBackgroundMusic();
    }
}
