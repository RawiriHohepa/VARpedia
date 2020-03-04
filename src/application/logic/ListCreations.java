package application.logic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListCreations {
    private ObjectProperty<File> _selectedCreationProperty;
    private ObjectProperty<ObservableList<File>> _currentFilesProperty;

    public ListCreations() {
        _selectedCreationProperty = new SimpleObjectProperty<>();
        _currentFilesProperty = new SimpleObjectProperty<>();
    }

    public ObjectProperty<File> getSelectedCreationProperty() {
        return _selectedCreationProperty;
    }

    public void deleteSelectedFile() {
        _selectedCreationProperty.get().delete();
        updateCurrentFiles();
    }

    public ObjectProperty<ObservableList<File>> getCurrentFilesProperty() {
        return _currentFilesProperty;
    }

    // This will return a list of all current creations in the creations directory.
    public void updateCurrentFiles() {
        File[] files = Folders.CREATIONS.asFolder().listFiles();

        // Override each file's getName and toString methods to return its name without the file extension
        List<File> simpleNameFiles = new ArrayList<>();
        for (File file : files) {
            simpleNameFiles.add(new File(file.getAbsolutePath()) {
                @Override
                public String getName() {
                    return super.getName().replace(".mp4", "");
                }

                @Override
                public String toString() {
                    return super.getName().replace(".mp4", "");
                }
            });
        }

        // TODO research using the same observable list each time and just updating it
        _currentFilesProperty.setValue(FXCollections.observableArrayList(simpleNameFiles));
    }
}
