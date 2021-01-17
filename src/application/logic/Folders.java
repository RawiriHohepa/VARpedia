package application.logic;

import java.io.File;

public enum Folders {
    CREATIONS("creations"),
    QUIZ("quiz"),
    CHUNKS("chunks");

    private String _name;
    private String _relativePath;
    private String _absolutePath;
    private String _folderDirAsString;
    private File _folderDirAsFolder;

    Folders(String name) {
        _name = name;
        _relativePath = "/" + _name + "/";
        _absolutePath = System.getProperty("user.dir") + _relativePath;
        _folderDirAsString = System.getProperty("user.dir") + "/" + _name + "/";
        _folderDirAsFolder = new File(_folderDirAsString);
    }

    public String asString() {
        return _folderDirAsString;
    }

    public File asFolder() {
        return _folderDirAsFolder;
    }
}
