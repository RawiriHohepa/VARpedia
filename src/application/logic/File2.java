package application.logic;

import java.io.File;

// TODO rename to better name
public class File2 extends File {
    private String _name;

    public File2(File file) {
        super(file.getAbsolutePath());
        _name = file.getName().replace(".mp4", "");
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }
}
