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
    private static Stage _primaryStage;

    private final String _fxmlFileName;
    Scenes(String sceneName) {
        _fxmlFileName = SCENES_FOLDER + sceneName + ".fxml";
    }

    // This method is used throughout this application to change between scenes.
    // Upon the correct button actions by the user, the scene will switch to the next scene indicated by
    // the parameter String fxmlFileName.
    static public void changeScene(Scenes scene) throws IOException {
        FXMLLoader fMXLLoader = new FXMLLoader();
        fMXLLoader.setLocation(Main.class.getResource(scene._fxmlFileName));
        Parent newLayout = fMXLLoader.load();
        Scene newScene = new Scene(newLayout);
        _primaryStage.setScene(newScene);
        _primaryStage.show();
    }

    static public void setPrimaryStage(Stage primaryStage) {
        _primaryStage = primaryStage;
    }
}
