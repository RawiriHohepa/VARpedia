package application;

import application.controller.Controller;
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
    private static Scenes _currentScene;

    private final String _fxmlFileName;
    private Controller _controller;

    Scenes(String sceneName) {
        _fxmlFileName = SCENES_FOLDER + sceneName + ".fxml";
    }

    // This method is used throughout this application to change between scenes.
    // Upon the correct button actions by the user, the scene will switch to the next scene indicated by
    // the parameter String fxmlFileName.
    static public void changeScene(Scenes scene) {
        FXMLLoader fMXLLoader = new FXMLLoader();
        fMXLLoader.setLocation(Main.class.getResource(scene._fxmlFileName));

        Parent newLayout = null;
        try {
            newLayout = fMXLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene newScene = new Scene(newLayout);
        _primaryStage.setScene(newScene);
        _primaryStage.show();

        scene._controller = fMXLLoader.getController();
        _currentScene = scene;
    }

    static public void setPrimaryStage(Stage primaryStage) {
        _primaryStage = primaryStage;
    }

    public Controller getController() {
        return _controller;
    }

    public static Scenes getCurrentScene() {
        return _currentScene;
    }
}
