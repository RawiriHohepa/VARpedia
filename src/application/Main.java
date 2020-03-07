package application;

import application.logic.Folders;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
	private static ExecutorService _executorService;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Scenes.setPrimaryStage(primaryStage);

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

	public static ExecutorService getExecutorService() {
		if (_executorService == null) {
			_executorService = Executors.newFixedThreadPool(5);
		}

		return _executorService;
	}
}
