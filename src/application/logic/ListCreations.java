package application.logic;

import application.Main;

import java.io.File;

public class ListCreations {
    private static String _selectedCreation;

    public ListCreations() {
        Main.setCurrentScene("ListCreationScene");
    }

    public static void setSelectedCreation(String selectedCreation) {
        _selectedCreation = selectedCreation;
    }

    public static String getSelectedCreationName() {
        // Removal of the index on the creation name
        return ("" + _selectedCreation.substring(_selectedCreation.indexOf(".") + 2));
    }

    public static File getSelectedFile() {
        //TODO replace with _fileManager.getSelectedFile()
        // need to make non-static
        return new File(Folders.CREATIONS.asString() + "/" + getSelectedCreationName() + ".mp4");
    }
}
