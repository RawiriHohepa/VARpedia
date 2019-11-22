package application.controller;

import application.BackgroundMusicPlayer;
import application.Main;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.FileManager;
import application.logic.Folders;
import application.logic.ListCreations;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ListCreationsController {
    @FXML
    private ToggleButton _backgroundMusicButton;

    @FXML
    private ListView<String> _listViewCreations;

    @FXML
    private Button _playButton;
    @FXML
    private Button _deleteButton;
    @FXML
    private Label _selectPrompt;

    private ListCreations _listCreations;
    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private FileManager _fileManager;

    private static String _selectedCreation;

    @FXML
    public void initialize() {
        _listCreations = new ListCreations();
        _backgroundMusicPlayer = Main.getBackgroundMusicPlayer();
        _fileManager = new FileManager(Folders.CREATIONS);

        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
        _backgroundMusicButton.setSelected(_backgroundMusicPlayer.getButtonIsSelected());

        // Disable the buttons whenever there is no creation selected
        BooleanBinding noCreationSelected = _listViewCreations.getSelectionModel().selectedItemProperty().isNull();
        _playButton.disableProperty().bind(noCreationSelected);
        _deleteButton.disableProperty().bind(noCreationSelected);

        ListCurrentFiles();
    }

    @FXML
    private void handleNewCreationButton() throws IOException {
        Scenes.changeScene(Scenes.NEW_CREATION_SCENE);
    }

    @FXML
    private void handlePlayButton() throws IOException {
        Scenes.changeScene(Scenes.PLAYER_SCENE);
    }

    @FXML
    private void handleReturnButton() throws IOException {
        Scenes.changeScene(Scenes.MAIN_SCREEN_SCENE);
    }

    @FXML
    public void handleSelectedCreation() {
        String selectedCreation = _listViewCreations.getSelectionModel().getSelectedItem();
        _fileManager.setSelectedFileName(selectedCreation);
        if (selectedCreation != null) {
            _selectPrompt.setText("");
        }

        //TODO remove, somehow transfer info to PlayerController
        _selectedCreation = selectedCreation;
    }

    @FXML
    private void handleDeleteButton() {
        Alert deleteConfirmation = new AlertBuilder()
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitle("Confirm Deletion")
                .setHeaderText("Delete " + _fileManager.getSelectedFileName() + "?")
                .setContentText("Are you sure you want to delete this creation?")
                .getResult();
        Optional<ButtonType> buttonClicked = deleteConfirmation.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            _fileManager.deleteSelectedFile();
            ListCurrentFiles();

            _selectPrompt.setText("                  Please select a creation to continue.");
        }
    }

    // This will return a list of all current creations in the creations directory.
    // This list will be displayed to the user in the view interface.
    public void ListCurrentFiles() {
        _listViewCreations.setItems(_fileManager.getCurrentFilesList());
    }

    public static File getSelectedFile() {
        return new File(Folders.CREATIONS.asString() + "/" + getSelectedCreationName() + ".mp4");
    }

    public static String getSelectedCreationName() {
        // Removal of the index on the creation name
        return ("" + _selectedCreation.substring(_selectedCreation.indexOf(".") + 2));
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.handleBackgroundMusic(_backgroundMusicButton.isSelected());
        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
    }
}
