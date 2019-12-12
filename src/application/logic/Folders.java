package application.logic;

import java.io.File;

public enum Folders {
    CREATIONS("creations"),
    QUIZ("quiz"),
    CHUNKS("chunks");

    private String _folderDirAsString;
    private File _folderDirAsFolder;

    Folders(String folder) {
        _folderDirAsString = System.getProperty("user.dir") + "/" + folder + "/";
        _folderDirAsFolder = new File(_folderDirAsString);
    }

    public String asString() {
        return _folderDirAsString;
    }

    public File asFolder() {
        return _folderDirAsFolder;
    }
}
