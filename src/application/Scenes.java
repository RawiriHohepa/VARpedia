package application;

import application.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public enum Scenes {
    IMAGES_SELECTION("ImagesSelectionScene"),
    LIST_CREATIONS("ListCreationsScene"),
    MAIN_SCREEN("MainScreenScene"),
    NEW_CREATION("NewCreationScene"),
    PLAYER("PlayerScene"),
    QUIZ("QuizScene");

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
    public void changeTo() {
        FXMLLoader fMXLLoader = new FXMLLoader();
        fMXLLoader.setLocation(Main.class.getResource(_fxmlFileName));

        Parent newLayout = null;
        try {
            newLayout = fMXLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene newScene = new Scene(newLayout);
        _primaryStage.setScene(newScene);
        _primaryStage.show();

        _controller = fMXLLoader.getController();
        _currentScene = this;
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
