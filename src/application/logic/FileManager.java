package application.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private String _selectedQuiz;

    // The quiz directory where all quiz videos are stored.
    private static final File QUIZ_FOLDER = new File(System.getProperty("user.dir") + "/quiz/");
    //===================FILES=======================
    public void setSelectedQuiz(String selectedQuiz) {
        _selectedQuiz = selectedQuiz;
    }
    public String getSelectedQuizName() {
        // Removal of the index on the quiz video name
        return ("" + _selectedQuiz.substring(_selectedQuiz.indexOf(".") + 2));
    }
    public void deleteSelectedFile() {
        getSelectedFile().delete();
        System.out.println(getSelectedFile().toString());
        _selectedQuiz = null;
    }
    private File getSelectedFile() {
        // Removal of the index on the quiz video name
        // and creating it as a file to be played or deleted.
        String fileName = getSelectedQuizName();
        return new File(QUIZ_FOLDER + "/" + fileName + ".mp4");
    }
    public ObservableList<String> getCurrentListOfQuiz() {
        List<String> creationNamesList = new ArrayList<String>();

        // Will get every file in the quiz directory and create an indexed
        // list of file names.
        int indexCounter = 1;
        for (final File quiz : QUIZ_FOLDER.listFiles()) {
            String fileName = quiz.getName();
            if (fileName.endsWith(".mp4")) {
                creationNamesList.add("" + indexCounter + ". " + fileName.replace(".mp4", ""));
                indexCounter++;
            }
        }

        // Turning the list of quiz names into an listView<String> for the GUI.
        return FXCollections.observableArrayList(creationNamesList);
    }
}
