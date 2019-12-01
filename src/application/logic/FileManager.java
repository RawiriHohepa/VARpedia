package application.logic;

import application.Scenes;
import application.controller.NewCreationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private String _selectedFileName;

    // The quiz directory where all quiz videos are stored.
    private Folders TARGET_FOLDER;

    public FileManager(Folders targetFolder) {
        TARGET_FOLDER = targetFolder;
    }
    //===================FILES=======================
    public void setSelectedFileName(String selectedFileName) {
        _selectedFileName = selectedFileName;
    }
    public String getSelectedFileName() {
        // Removal of the index on the quiz video name
        return ("" + _selectedFileName.substring(_selectedFileName.indexOf(".") + 2));
    }
    public void deleteSelectedFile() {
        getSelectedFile().delete();
        _selectedFileName = null;
    }
    public File getSelectedFile() {
        // Removal of the index on the quiz video name
        // and creating it as a file to be played or deleted.
        return new File(TARGET_FOLDER.asString() + "/" + getSelectedFileName() + ".mp4");
    }
    public ObservableList<String> getCurrentFilesList() {
        List<String> fileNamesList = new ArrayList<String>();

        // Will get every file in the quiz directory and create an indexed
        // list of file names.
        int indexCounter = 1;
        for (File file : TARGET_FOLDER.asFolder().listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".mp4")) {
                fileNamesList.add("" + indexCounter + ". " + fileName.replace(".mp4", ""));
                indexCounter++;
            }
        }
        
        // Turning the list of file names into an listView<String> for the GUI.
        return FXCollections.observableArrayList(fileNamesList);
    }

    // This method will clean the temporary fold that stored the audio chunks, the flikr images
    // the no audio .mp4 file the .wav file as well as the folders themselves.
    public static void cleanFolders() {
        NewCreationController newCreationController = (NewCreationController) Scenes.NEW_CREATION_SCENE.getController();
        // The creations directory where all creations are stored.
        File creationFolder = new File(Folders.CREATIONS.asString() + newCreationController.getSearchTerm() + "/" );
        for (final File creationFileName : creationFolder.listFiles()) {
            creationFileName.delete();
        }
        creationFolder.delete();

        // The chunks directory where all audio chunks are stored.
        File chunksFolder = Folders.CHUNKS.asFolder();
        for (final File chunkFileName : chunksFolder.listFiles()) {
            chunkFileName.delete();
        }
        chunksFolder.delete();
    }
}
