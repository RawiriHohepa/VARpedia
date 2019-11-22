package application.logic;

import application.Main;

import java.io.File;

public class ListCreations {
    // The quiz directory where all quiz videos are stored.
    public static final File CREATIONS_FOLDER = new File(System.getProperty("user.dir") + "/creations/");

    public ListCreations() {
        Main.setCurrentScene("ListCreationScene");
    }


}
