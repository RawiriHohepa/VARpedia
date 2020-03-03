package application.controller;

import application.BackgroundMusicPlayer;
import application.Scenes;
import application.logic.AlertBuilder;
import application.logic.FileManager;
import application.logic.ImagesSelection;
import application.tasks.FlickrImagesTask;
import application.Main;
import application.tasks.ImageVideoTask;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class ImagesSelectionController extends Controller {
    @FXML
    private ImageView _flickrImage0;
    @FXML
    private ImageView _flickrImage1;
    @FXML
    private ImageView _flickrImage2;
    @FXML
    private ImageView _flickrImage3;
    @FXML
    private ImageView _flickrImage4;
    @FXML
    private ImageView _flickrImage5;
    @FXML
    private ImageView _flickrImage6;
    @FXML
    private ImageView _flickrImage7;
    @FXML
    private ImageView _flickrImage8;
    @FXML
    private ImageView _flickrImage9;
    @FXML
    private CheckBox _includeImage0;
    @FXML
    private CheckBox _includeImage1;
    @FXML
    private CheckBox _includeImage2;
    @FXML
    private CheckBox _includeImage3;
    @FXML
    private CheckBox _includeImage4;
    @FXML
    private CheckBox _includeImage5;
    @FXML
    private CheckBox _includeImage6;
    @FXML
    private CheckBox _includeImage7;
    @FXML
    private CheckBox _includeImage8;
    @FXML
    private CheckBox _includeImage9;

    @FXML
    private ToggleButton _backgroundMusicButton;

    @FXML
    private ProgressBar _imagesProgressBar;
    @FXML
    private Text _imageDownloadInProgress;
    @FXML
    private TextField _creationNameTextField;
    @FXML
    private Button _submitButton;
    @FXML
    private Pane _progressPane;
    @FXML
    private ImageView _clockImage;
    @FXML
    private Pane _imagePane;

    private List<ImageView> _flickrImageViewList;
    private List<CheckBox> _checkBoxIncludeImageList;

    private ImagesSelection _imagesSelection;
    private BackgroundMusicPlayer _backgroundMusicPlayer;
    private ExecutorService _team;

    @FXML
    private void initialize() {
        _imagesSelection = new ImagesSelection();
        _backgroundMusicPlayer = Main.getBackgroundMusicPlayer();
        _team = Main.getTeam();

        _clockImage.setVisible(true);
        _progressPane.setVisible(true);

        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
        _backgroundMusicButton.setSelected(_backgroundMusicPlayer.getButtonIsSelected());

        _flickrImageViewList = new ArrayList<ImageView>(Arrays.asList(_flickrImage0, _flickrImage1, _flickrImage2, _flickrImage3, _flickrImage4, _flickrImage5
                , _flickrImage6, _flickrImage7, _flickrImage8, _flickrImage9));
        _checkBoxIncludeImageList = new ArrayList<CheckBox>(Arrays.asList(_includeImage0, _includeImage1, _includeImage2, _includeImage3, _includeImage4, _includeImage5
                , _includeImage6, _includeImage7, _includeImage8, _includeImage9));

        /**
         * Credit to user DVarga
         * Full credit in NewCreationController in method setUpBooleanBindings()
         */
        // Don't let the user create a creation if they remove the default creation name
        BooleanBinding textIsEmpty = Bindings.createBooleanBinding(() ->
                        _creationNameTextField.getText().trim().isEmpty(),
                _creationNameTextField.textProperty());
        _submitButton.disableProperty().bind(textIsEmpty);

        downloadFlickrImages();
    }

    @FXML
    private void handleSubmitButton() {
        int numImagesDeleted = 0;
        for (int i = 0; i < _checkBoxIncludeImageList.size(); i++) {
            if (!_checkBoxIncludeImageList.get(i).isSelected()) {
                new File(_imagesSelection.getImagesFolder() + "/" + i + ".jpg").delete();
                numImagesDeleted++;
            }
        }

        int numberOfImages = 10 - numImagesDeleted;

        String creationName = _creationNameTextField.getText().trim();
        if (numberOfImages == 0) {
            Alert noImagesSelectedError = new AlertBuilder()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("No images selected")
                    .setContentText("Please select at least one image.")
                    .getResult();
            noImagesSelectedError.showAndWait();
            return;
        } else if (creationName.isEmpty()) {
            // this should not evaluate to true due to bindings
            return;
        } else if (!creationName.matches("[a-zA-Z0-9_-]*")) {
            // checking that the creation name is valid set of characters
            Alert invalidCreationNameError = new AlertBuilder()
                    .setAlertType(Alert.AlertType.WARNING)
                    .setTitle("Invalid Creation name")
                    .setContentText("Please enter a valid creation name consisting of alphabet letters, digits, underscores, and hyphens only.")
                    .getResult();
            invalidCreationNameError.showAndWait();
        } else if (!_imagesSelection.isUniqueCreationName(creationName)) {
            Alert overrideExistingCreationPopup = new AlertBuilder()
                    .setAlertType(Alert.AlertType.CONFIRMATION)
                    .setTitle("Override")
                    .setHeaderText("Creation name already exists")
                    .setContentText("Would you like to override the existing creation?")
                    .getResult();
            Optional<ButtonType> buttonClicked = overrideExistingCreationPopup.showAndWait();

            // Override existing file name i.e. delete current file and create new file
            if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
                _imagesSelection.deleteExistingFile(creationName);

                createVideo(creationName, numberOfImages);
            }
        } else {
            // If there are no problems with any inputs will create the creation normally.
            createVideo(creationName, numberOfImages);
        }
    }

    /*The method to create the creation
      This method will pull images from flickr based on user input.
      Using these images a video will be created with the search term added as text.*/
    private void createVideo(String creationName, int numberOfImages) {
        Alert creationInProgressPopup = new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setTitle("Creation in progress")
                .setHeaderText("Creation is being made, please wait...")
                .setContentText("You will be informed when the creation is complete.")
                .getResult();
        creationInProgressPopup.show();

        // Thread to ensure that GUI remains concurrent while the video is being created
        ImageVideoTask flickrImagesTask = new ImageVideoTask(_imagesSelection.getSearchTerm(), creationName, numberOfImages);
        _team.submit(flickrImagesTask);

        // return to main menu
        Scenes.MAIN_SCREEN.changeTo();

        flickrImagesTask.setOnSucceeded(workerStateEvent -> {
            // close the 'in progress' popup
            Button cancelButton = (Button) creationInProgressPopup.getDialogPane().lookupButton(ButtonType.OK);
            cancelButton.fire();

            // This will refresh the List of creations only if the user is currently on the List of creations scene
            // Otherwise when the user enters, the initialize() method of ListCreationsController will
            // refresh the list of creations.
            if (Scenes.getCurrentScene().equals(Scenes.LIST_CREATIONS)) {
                Scenes.LIST_CREATIONS.changeTo();
            }

            Alert creationFinishedPopup = new AlertBuilder()
                    .setAlertType(Alert.AlertType.INFORMATION)
                    .setTitle("Creation completed")
                    .setHeaderText("Creation completed: " + creationName + " is finished")
                    .setContentText("You can find it in the Creations or Quiz sections")
                    .getResult();
            creationFinishedPopup.showAndWait();
        });
    }

    // Method for retrieving Flickr images. The retrieval is done through a bash process
    // in the task class FlickrImagesTask.
    private void downloadFlickrImages() {
        FlickrImagesTask imagesTask = new FlickrImagesTask(_imagesSelection.getSearchTerm());

        _imagesProgressBar.progressProperty().bind(imagesTask.progressProperty());

        _team.submit(imagesTask);
        imagesTask.setOnSucceeded(workerStateEvent -> {
            _progressPane.setVisible(false);
            _imageDownloadInProgress.setVisible(false);
            _imagesProgressBar.setVisible(false);
            _clockImage.setVisible(false);

            populateFlickrImageViews();
        });
    }

    private void populateFlickrImageViews() {
        List<Image> imageList = _imagesSelection.getImageList();

        for (int i = 0; i < _flickrImageViewList.size(); i++) {
            _flickrImageViewList.get(i).setImage(imageList.get(i));
        }

        for (ImageView flickrImageView : _flickrImageViewList) {
            flickrImageView.setVisible(true);
        }
        for (CheckBox checkBoxIncludeImage : _checkBoxIncludeImageList) {
            checkBoxIncludeImage.setVisible(true);
        }

        // By default first checkbox is ticked.
        _includeImage0.setSelected(true);

        // By default creation name is search term. If the search
        // term is already associated with another creation it is serialized.
        _creationNameTextField.setText(_imagesSelection.getDefaultCreationName());

        _creationNameTextField.setVisible(true);
        _submitButton.setVisible(true);
        _imagePane.setVisible(true);
    }

    @FXML
    private void handleCreationCancelButton() {
        // Return to main menu
        Scenes.MAIN_SCREEN.changeTo();
        FileManager.cleanFolders();
    }

    @FXML
    private void handleBackgroundMusic() {
        _backgroundMusicPlayer.handleBackgroundMusic(_backgroundMusicButton.isSelected());
        _backgroundMusicButton.setText(_backgroundMusicPlayer.getButtonText());
    }
}