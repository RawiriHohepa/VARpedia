package application.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private String _selectedFileName;

    // The quiz directory where all quiz videos are stored.
    private static File TARGET_FOLDER = null;

    public FileManager(File targetFolder) {
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
        System.out.println(getSelectedFile().toString());
        _selectedFileName = null;
    }
    private File getSelectedFile() {
        // Removal of the index on the quiz video name
        // and creating it as a file to be played or deleted.
        return new File(TARGET_FOLDER + "/" + getSelectedFileName() + ".mp4");
    }
    public ObservableList<String> getCurrentFilesList() {
        List<String> fileNamesList = new ArrayList<String>();

        // Will get every file in the quiz directory and create an indexed
        // list of file names.
        int indexCounter = 1;
        for (final File file : TARGET_FOLDER.listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".mp4")) {
                fileNamesList.add("" + indexCounter + ". " + fileName.replace(".mp4", ""));
                indexCounter++;
            }
        }
        
        // Turning the list of file names into an listView<String> for the GUI.
        return FXCollections.observableArrayList(fileNamesList);
    }
}
