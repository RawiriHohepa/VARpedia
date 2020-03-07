package application.models;

import application.Scenes;
import application.controller.NewCreationController;
import application.logic.Folders;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagesSelectionModel {
    private String _searchTerm;
    private File _imagesFolder;

    public ImagesSelectionModel() {
        NewCreationController newCreationController = (NewCreationController) Scenes.NEW_CREATION.getController();
        _searchTerm = newCreationController.getSearchTerm();
        _imagesFolder = new File(Folders.CREATIONS.asString() + _searchTerm);
    }

    // This method will check if the given name is already associated with
    // an existing creation. Returns false if the creation name is already used.
    // Returns true otherwise.
    public boolean isUniqueCreationName(String creationName) {
        for (File creationFile : Folders.CREATIONS.asFolder().listFiles()) {
            if (creationFile.getName().equals("" + creationName + ".mp4")) {
                // An already existing creation name is invalid.
                return false;
            }
        }
        return true;
    }

    public void deleteExistingFile(String creationName) {
        File _existingFile = new File(Folders.CREATIONS.asString() + creationName + ".mp4");
        _existingFile.delete();
    }

    // This method is used to find a valid creation name to be used as the
    // predefined creation nae in the application.
    public String getDefaultCreationName() {
        File quizFileName;

        // check non-serialized name first
        quizFileName = new File(Folders.CREATIONS.asString() + _searchTerm + ".mp4");
        if (!quizFileName.exists()) {
            return _searchTerm;
        } else {
            String serializedCreationName;
            int creationNumber = 1;

            do {
                serializedCreationName = _searchTerm + "-" + creationNumber;
                quizFileName = new File(Folders.CREATIONS.asString() + serializedCreationName + ".mp4");
                creationNumber++;
            } while (quizFileName.exists());

            return serializedCreationName;
        }
    }

    public List<Image> getImageList() {
        File[] imageFileArray = getImagesFolder().listFiles();
        Arrays.sort(imageFileArray);
        List<Image> imageList = new ArrayList<Image>();
        for (File imageFile : imageFileArray) {
            if (imageFile.getName().endsWith(".jpg")) {
                imageList.add(new Image(imageFile.toURI().toString()));
            }
        }
        return imageList;
    }

    public String getSearchTerm() {
        return _searchTerm;
    }

    public File getImagesFolder() {
        return _imagesFolder;
    }
}
