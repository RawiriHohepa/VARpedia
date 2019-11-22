package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

import application.controller.NewCreationController;

public class Main extends Application {

	static private BackgroundMusicPlayer _backgroundMusicPlayer;
	private static String _currentScene;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		_backgroundMusicPlayer = new BackgroundMusicPlayer();
		Scenes.setPrimaryStage(primaryStage);

		String userDir = System.getProperty("user.dir");
		
		File creationsFolder = new File(userDir + "/creations");
		if (!creationsFolder.exists()) {
			creationsFolder.mkdirs();
		}

		File quizFolder = new File(userDir + "/quiz");
		if (!quizFolder.exists()) {
			quizFolder.mkdirs();
		}

		Scenes.changeScene(Scenes.MAIN_SCREEN_SCENE);
		
		/**
		 * Credit to anonymous classmate from peer review
		 * "Should use Platform.exit() method to exit the program when the window is closed.
		 * (e.g. stageName.setOnCloseRequest(......))"
		 */
		// Cleanly exit application by closing all processes when the user exits
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
	}
	
	// This method will clean the temporary fold that stored the audio chunks, the flikr images
    // the no audio .mp4 file the .wav file as well as the folders themselves.
	static public void cleanFolders() {
		String userDir = System.getProperty("user.dir");
		// The creations directory where all creations are stored.
		File creationFolder = new File(userDir + "/creations/" + NewCreationController.getSearchTerm() + "/" );
		for (final File creationFileName : creationFolder.listFiles()) {
			creationFileName.delete();
		}
		creationFolder.delete();

		// The chunks directory where all audio chunks are stored.
		File chunksFolder = new File(userDir + "/chunks/" );
		for (final File chunkFileName : chunksFolder.listFiles()) {
			chunkFileName.delete();
		}
		chunksFolder.delete();
	}


	static public void setCurrentScene(String currentScene){
		_currentScene=currentScene;
	}

	static public String getCurrentScene(){
		return _currentScene;
	}
	
	static public BackgroundMusicPlayer getBackgroundMusicPlayer() {
		return _backgroundMusicPlayer;
	}
}
