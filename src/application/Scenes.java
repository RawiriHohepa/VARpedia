package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public enum Scenes {
    IMAGES_SELECTION_SCENE ("ImagesSelectionScene"),
    LIST_CREATIONS_SCENE ("ListCreationsScene"),
    MAIN_SCREEN_SCENE ("MainScreenScene"),
    NEW_CREATION_SCENE ("NewCreationScene"),
    PLAYER_SCENE ("PlayerScene"),
    QUIZ_SCENE ("QuizScene");

    private final static String SCENES_FOLDER = "resources/";

    private final String dir;
    Scenes(String fxmlName) {
        dir = SCENES_FOLDER + fxmlName + ".fxml";
    }

    public String getSceneDir() {
        return dir;
    }

    // This method is used throughout this application to change between scenes.
    // Upon the correct button actions by the user, the scene will switch to the next scene indicated by
    // the parameter String fxmlFileName.
    static public void changeScene(String fxmlFileName) throws IOException {
        Stage _primaryStage = Main._primaryStage;

        FXMLLoader fMXLLoader = new FXMLLoader();
        fMXLLoader.setLocation(Main.class.getResource(fxmlFileName));
        Parent newLayout = fMXLLoader.load();
        Scene newScene = new Scene(newLayout);
        _primaryStage.setScene(newScene);
        _primaryStage.show();
    }
}
