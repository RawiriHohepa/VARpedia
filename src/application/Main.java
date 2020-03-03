package application;

import application.logic.Folders;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
	private static ExecutorService _team = Executors.newFixedThreadPool(5);
	private static BackgroundMusicPlayer _backgroundMusicPlayer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		_backgroundMusicPlayer = new BackgroundMusicPlayer();
		Scenes.setPrimaryStage(primaryStage);

		String userDir = System.getProperty("user.dir");
		
		File creationsFolder = Folders.CREATIONS.asFolder();
		if (!creationsFolder.exists()) {
			creationsFolder.mkdirs();
		}

		File quizFolder = Folders.QUIZ.asFolder();
		if (!quizFolder.exists()) {
			quizFolder.mkdirs();
		}

		Scenes.MAIN_SCREEN.changeTo();
		
		/**
		 * Credit to anonymous classmate from peer review
		 * "Should use Platform.exit() method to exit the program when the window is closed.
		 * (e.g. stageName.setOnCloseRequest(......))"
		 */
		// Cleanly exit application by closing all processes when the user exits
		primaryStage.setOnCloseRequest(windowEvent -> System.exit(0));
	}
	
	public static BackgroundMusicPlayer getBackgroundMusicPlayer() {
		return _backgroundMusicPlayer;
	}

	public static ExecutorService getTeam() {
		return _team;
	}
}
