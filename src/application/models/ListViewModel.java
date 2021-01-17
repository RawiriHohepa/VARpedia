package application.models;

import application.logic.Folders;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListViewModel {
    private ObjectProperty<File> _selectedFileProperty;
    private ObjectProperty<ObservableList<File>> _currentFilesProperty;
    private BooleanBinding _noFileSelectedBinding;
    private StringProperty _selectPromptTextProperty;
    private Folders _targetFolder;

    public ListViewModel(Folders targetFolder, ReadOnlyObjectProperty<File> selectedItemProperty) {
        _selectedFileProperty = new SimpleObjectProperty<>();
        _currentFilesProperty = new SimpleObjectProperty<>();
        _selectPromptTextProperty = new SimpleStringProperty();

        _noFileSelectedBinding = _selectedFileProperty.isNull();
        _noFileSelectedBinding.addListener((obs, oldValue, newValue) -> {
            // TODO edit position of _selectPrompt so the empty space is not needed
            _selectPromptTextProperty.setValue(newValue ? "                  Please select a video to continue." : "");
        });

        _targetFolder = targetFolder;
        _selectedFileProperty.bind(selectedItemProperty);
    }

    public void deleteSelectedFile() {
        _selectedFileProperty.get().delete();
        updateCurrentFiles();
    }

    // This will return a list of all current files in the target directory.
    public void updateCurrentFiles() {
        File[] files = _targetFolder.asFolder().listFiles();

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

    public String getSelectedFileName() {
        return _selectedFileProperty.get().getName();
    }

    public ObjectProperty<File> getSelectedFileProperty() {
        return _selectedFileProperty;
    }

    public ObjectProperty<ObservableList<File>> getCurrentFilesProperty() {
        return _currentFilesProperty;
    }

    public BooleanBinding getNoFileSelectedBinding() {
        return _noFileSelectedBinding;
    }

    public StringProperty getSelectPromptTextProperty() {
        return _selectPromptTextProperty;
    }
}
