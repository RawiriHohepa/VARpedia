package application.controller;

import application.BackgroundMusicPlayer;
import application.Main;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.Folders;
import application.logic.NewCreation;
import application.tasks.CombineChunksTask;
import application.tasks.PreviewTextTask;
import application.tasks.SaveTextTask;
import application.tasks.WikiSearchTask;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class NewCreationController extends Controller {
    @FXML
    private Button _backgroundMusicButton;
    @FXML
    private Label _enterSearchTerm;
    @FXML
    private TextField _enterSearchTermTextInput;
    @FXML
    private Button _searchWikipediaButton;
    @FXML
    private Text _searchInProgress;
    @FXML
    private TextArea _searchResultTextArea;
    @FXML
    private Button _previewChunk;
    @FXML
    private Button _saveChunk;
    @FXML
    private Label _voiceSelectDescription;
    @FXML
    private Label _textSelectDescription;
    @FXML
    private Label _chunkSelectDescription;
    @FXML
    private ChoiceBox<String> _voiceDropDownMenu;
    @FXML
    private ListView<String> _chunkList;

    @FXML
    private Button _selectButton;
    @FXML
    private Button _moveUpButton;
    @FXML
    private Button _moveDownButton;
    @FXML
    private Button _deleteButton;
    @FXML
    private Pane _progressPane;

    @FXML
    private ImageView _searchImage;

    @FXML
    private ProgressBar _wikiProgressBar;

    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private NewCreation _newCreation;
    private ExecutorService _executorService;

    private static final String NO_CHUNKS_FOUND_TEXT = "No Chunks Found.";

    @FXML
    private void initialize() {
        _backgroundMusicPlayer = BackgroundMusicPlayer.getInstance();
        _newCreation = new NewCreation();
        _executorService = Main.getExecutorService();

        _newCreation.cleanChunks();

        _searchImage.setVisible(true);

        _backgroundMusicButton.textProperty().bind(_backgroundMusicPlayer.getButtonTextProperty());

        setUpBooleanBindings();

        // Add the options for chunk voices, and set the default choice
        _voiceDropDownMenu.getItems().addAll("Default", "NZ-Man", "NZ-Woman");
        _voiceDropDownMenu.setValue("Default");
    }

    @FXML
    private void handleCreationCancelButton() {
        _newCreation.cleanChunks();
        // Return to main menu
        Scenes.MAIN_SCREEN.changeTo();
    }

    @FXML
    private void handleSearchWikipedia() {
        _newCreation.setSearchTerm(_enterSearchTermTextInput.getText().trim());

        _searchImage.setVisible(false);
        _progressPane.setVisible(true);
        _wikiProgressBar.setVisible(true);

        _searchInProgress.setVisible(true);

        // Run bash script that uses wikit and returns the result of the search
        WikiSearchTask wikiSearchTask = new WikiSearchTask(_newCreation.getSearchTerm());
        _wikiProgressBar.progressProperty().bind(wikiSearchTask.progressProperty());

        _executorService.submit(wikiSearchTask);

        // Using concurrency allows the user to cancel the creation if the search takes too long
        wikiSearchTask.setOnSucceeded(workerStateEvent -> {
            try {
                _searchImage.setVisible(false);
                _progressPane.setVisible(false);
                _wikiProgressBar.setVisible(false);

                // Returns a list with only one element.
                // If the term was not found, the element is "(Term not found)"
                // Otherwise the element is the search result
                String searchResult = wikiSearchTask.get();

                if (searchResult.contains("not found :^")) {
                    _searchInProgress.setVisible(false);

                    Alert invalidSearchAlert = new AlertBuilder()
                            .setAlertType(AlertType.ERROR)
                            .setTitle("That term cannot be searched")
                            .setHeaderText(null)
                            .setContentText("Please enter a different search term")
                            .getResult();
                    invalidSearchAlert.showAndWait();
                } else {
                    _searchResultTextArea.setText(searchResult);
                    displayChunkSelection();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handlePreviewChunk() {
        String chunk = _searchResultTextArea.getSelectedText().trim();
        chunk = chunk.replaceAll("[^a-zA-Z0-9-_ ]*", "");

        if (isValidChunk(chunk)) {
            // Run bash script using festival tts to speak the selected text to the user
            PreviewTextTask previewTextTask = new PreviewTextTask(chunk);
            _executorService.submit(previewTextTask);
        }
    }

    @FXML
    private void handleSaveChunk() {
        String chunk = _searchResultTextArea.getSelectedText().trim();
        chunk = chunk.replaceAll("[^a-zA-Z0-9-_ ]*", "");

        if (isValidChunk(chunk)) {
            String voiceChoice = _voiceDropDownMenu.getValue();

            // Run bash script using festival to save a .wav file containing the spoken selected text

            SaveTextTask saveTextTask = new SaveTextTask(voiceChoice, chunk);
            _executorService.submit(saveTextTask);

            saveTextTask.setOnSucceeded(workerStateEvent -> {
                // Make the new chunk visible to the user
                _chunkList.getItems().add(saveTextTask.getValue().replace(".wav", ""));
                _chunkList.setDisable(false);
                _selectButton.setDisable(false);

                if (_chunkList.getItems().get(0).equals(NO_CHUNKS_FOUND_TEXT)) {
                    _chunkList.getItems().remove(0);
                }

                if (_chunkList.getItems().size() > 1 && _newCreation.getSelectedChunk() != null) {
                    _moveUpButton.setDisable(false);
                    _moveDownButton.setDisable(false);
                }
            });
        }
    }

    // This method will give a confirmation of deletion. If the user confirms,
    // the selected chunk will be deleted.
    @FXML
    private void handleDeleteChunkButton() {
        int chunkIndex = _chunkList.getSelectionModel().getSelectedIndex();
        _chunkList.getItems().remove(chunkIndex);
        File selectedfile = new File(Folders.CHUNKS.asString() + _newCreation.getSelectedChunk() + ".wav");
        selectedfile.delete();

        _chunkList.getSelectionModel().clearSelection();
        _newCreation.setSelectedChunk(null);

        _moveUpButton.setDisable(true);
        _moveDownButton.setDisable(true);

        if (_chunkList.getItems().size() == 0) {
            _chunkList.getItems().add(NO_CHUNKS_FOUND_TEXT);
            _chunkList.setDisable(true);
            _selectButton.setDisable(true);
        }
    }

    @FXML
    private void handleMoveUpButton() {
        if (_newCreation.getSelectedChunk() != null) {
            int selectedChunkIndex = _chunkList.getSelectionModel().getSelectedIndex();

            // only move up if they do not select the top-most chunk
            if (selectedChunkIndex > 0) {
                moveSelectedChunk(selectedChunkIndex, selectedChunkIndex - 1);
            }
        }
    }

    @FXML
    private void handleMoveDownButton() {
        if (_newCreation.getSelectedChunk() != null) {
            int selectedChunkIndex = _chunkList.getSelectionModel().getSelectedIndex();

            // only move down if they do not select the bottom-most chunk
            if (selectedChunkIndex < _chunkList.getItems().size() - 1) {
                moveSelectedChunk(selectedChunkIndex, selectedChunkIndex + 1);
            }
        }
    }

    private void moveSelectedChunk(int oldIndex, int newIndex) {
        _chunkList.getItems().remove(oldIndex);
        _chunkList.getItems().add(newIndex, _newCreation.getSelectedChunk());
        _chunkList.getSelectionModel().select(newIndex);
    }

    @FXML
    private void handleSelectButton() {
        File outputFolder = new File(Folders.CREATIONS.asString() + _newCreation.getSearchTerm());
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        combineAudioChunks();
    }

    private void combineAudioChunks() {
        ObservableList<String> chunksList = _chunkList.getItems();

        // Run bash script to create a combined audio of each selected chunk
        CombineChunksTask combineChunksTask = new CombineChunksTask(chunksList, _newCreation.getSearchTerm());
        _executorService.submit(combineChunksTask);

        combineChunksTask.setOnSucceeded(workerStateEvent -> {
            Scenes.IMAGES_SELECTION.changeTo();
        });
    }

    @FXML
    public void handleSelectedChunk() {
        _newCreation.setSelectedChunk(_chunkList.getSelectionModel().getSelectedItem());

        if (_newCreation.getSelectedChunk() != null) {
            _selectButton.setDisable(false);

            if (_chunkList.getItems().size() > 1) {
                _moveUpButton.setDisable(false);
                _moveDownButton.setDisable(false);
            }
        }
    }

    private void displayChunkSelection() {
        // Hide search elements
        _enterSearchTerm.setVisible(false);
        _enterSearchTermTextInput.setVisible(false);
        _searchWikipediaButton.setVisible(false);
        _searchInProgress.setVisible(false);

        // Show chunk creation elements
        _searchResultTextArea.setVisible(true);
        _previewChunk.setVisible(true);
        _saveChunk.setVisible(true);
        _voiceSelectDescription.setVisible(true);
        _textSelectDescription.setVisible(true);
        _voiceDropDownMenu.setVisible(true);

        // Show chunk selection elements
        _chunkSelectDescription.setVisible(true);
        _chunkList.setVisible(true);
        _selectButton.setVisible(true);
        _deleteButton.setVisible(true);
        _moveUpButton.setVisible(true);
        _moveDownButton.setVisible(true);

        // Show the currently stored chunks
        updateChunkList();
    }

    private void updateChunkList() {
        List<String> chunkNamesList = _newCreation.getChunkNamesList();

        if (chunkNamesList.size() == 0) {
            chunkNamesList.add(NO_CHUNKS_FOUND_TEXT);

            // Prevent the user from selecting "No Chunks Found" as a chunk
            _chunkList.setDisable(true);
            _selectButton.setDisable(true);
        } else {
            _chunkList.setDisable(false);
            _selectButton.setDisable(false);
        }

        // Turns the list of chunk names into an ObservableList<String> and displays to the GUI.
        ObservableList<String> observableChunkNamesList = FXCollections.observableArrayList(chunkNamesList);
        _chunkList.setItems(observableChunkNamesList);
    }

    private boolean isValidChunk(String chunk) {
        if (_newCreation.numberOfWords(chunk) < 30) {
            return true;
        }

        // If the user selects 30 or more words, they must confirm that they want to continue
        String warningMessage = "Chunks longer than 30 words can result in a lower sound quality. Are you sure you want to create this chunk?";
        Alert longChunkConfirmationPopup = new Alert(AlertType.WARNING, warningMessage, ButtonType.CANCEL, ButtonType.YES);
        longChunkConfirmationPopup.getDialogPane().getStylesheets().add(("Alert.css"));
        // Display the confirmation alert and store the button pressed
        Optional<ButtonType> buttonClicked = longChunkConfirmationPopup.showAndWait();

        return (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.YES);
    }

    private void setUpBooleanBindings() {
        /**
         * Credit to user DVarga
         * https://stackoverflow.com/questions/37853634/javafx-textfield-onkeytyped-not-working-properly
         * "BooleanBinding textIsEmpty = Bindings.createBooleanBinding(() -> textField.getText().trim().isEmpty(), textField.textProperty());
         * button.disableProperty().bind(textIsEmpty);"
         */
        // Don't let the user search until they enter a search term
        BooleanBinding textIsEmpty = Bindings.createBooleanBinding(() ->
                        _enterSearchTermTextInput.getText().trim().isEmpty(),
                _enterSearchTermTextInput.textProperty());
        _searchWikipediaButton.disableProperty().bind(textIsEmpty);

        // Don't let the user create a chunk until they select some text
        BooleanBinding noTextSelected = Bindings.createBooleanBinding(() ->
                        !(_newCreation.numberOfWords(_searchResultTextArea.getSelectedText().trim()) > 0),
                _searchResultTextArea.selectedTextProperty());
        _previewChunk.disableProperty().bind(noTextSelected);
        _saveChunk.disableProperty().bind(noTextSelected);

        // Don't let the user delete the selected chunk until they select at least one
        BooleanBinding noChunkSelected = _chunkList.getSelectionModel().selectedItemProperty().isNull();
        _deleteButton.disableProperty().bind(noChunkSelected);
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.toggleBackgroundMusic();
    }

    public String getSearchTerm() {
        return _newCreation.getSearchTerm();
    }
}