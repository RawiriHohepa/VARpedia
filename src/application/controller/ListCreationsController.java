package application.controller;

import application.BackgroundMusicPlayer;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.Folders;
import application.models.ListViewModel;
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
    private ListViewModel _listViewModel;

    @FXML
    public void initialize() {
        _backgroundMusicPlayer = BackgroundMusicPlayer.getInstance();
        _listViewModel = new ListViewModel(Folders.CREATIONS, _listViewCreations.getSelectionModel().selectedItemProperty());

        _backgroundMusicButton.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());

        // Disable the buttons and display a prompt whenever there is no creation selected
        BooleanBinding noCreationSelectedBinding = _listViewModel.getNoFileSelectedBinding();
        _playButton.disableProperty().bind(noCreationSelectedBinding);
        _deleteButton.disableProperty().bind(noCreationSelectedBinding);
        noCreationSelectedBinding.addListener((obs, oldValue, newValue) -> {
            // TODO edit position of _selectPrompt so the empty space is not needed
            _selectPrompt.setText(newValue ? "                  Please select a creation to continue." : "");
        });

        _listViewCreations.itemsProperty().bind(_listViewModel.getCurrentFilesProperty());

        _listViewModel.updateCurrentFiles();
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
                .setHeaderText("Delete " + _listViewModel.getSelectedFileProperty().get().getName() + "?")
                .setContentText("Are you sure you want to delete this creation?")
                .getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _listViewModel.deleteSelectedFile();
        }
    }

    // Used by PlayerController
    public String getSelectedCreationName() {
        return _listViewModel.getSelectedFileProperty().get().getName();
    }

    // Used by PlayerController
    public File getSelectedFile() {
        return _listViewModel.getSelectedFileProperty().get();
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.toggleBackgroundMusic();
    }
}
