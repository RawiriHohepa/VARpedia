package application.controller;

import application.BashCommand;
import application.Main;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreationController {
	private ExecutorService team = Executors.newFixedThreadPool(3);
	
	private String _searchTerm;
	
	@FXML
    private Text enterSearchTerm;
	@FXML
    private TextField enterSearchTermTextInput;
	@FXML
    private Button searchWikipediaButton;
	@FXML
    private Text searchInProgress;
	@FXML
    private Text termNotFound;
	
	
	
	@FXML
    private TextArea searchResultTextArea;
	@FXML
    private Button previewChunk;
	@FXML
    private Button saveChunk;
	@FXML
    private Text voiceLabel;
	@FXML
    private ChoiceBox<String> voiceDropDownMenu;
	@FXML
    private ListView<CheckBox> chunkList;
	@FXML
    private Slider numImagesSlider;
	@FXML
    private TextField creationNameTextField;
	@FXML
    private Button finalCreate;
	
	@FXML
	private void initialize() {
		voiceDropDownMenu.getItems().addAll("Default", "NZ-Man", "NZ-Woman");
		voiceDropDownMenu.setValue("Default");
	}
	
    @FXML
    private void handleCreationCancelButton(ActionEvent event) throws IOException {

        Parent creationViewParent = FXMLLoader.load(Main.class.getResource("resources/home.fxml"));
        Scene creationViewScene = new Scene(creationViewParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(creationViewScene);
        window.show();
    }

	@FXML
    private void handleSearchWikipedia(ActionEvent event) throws IOException {
		getSearchResult();
	}

	@FXML
    private void handlePreviewChunk(ActionEvent event) throws IOException {
		String chunk = searchResultTextArea.getSelectedText();
		
		boolean isValidChunk = checkForValidChunk(chunk);
		if (isValidChunk) {
			// Run bash script
    		String[] command = new String[]{"/bin/bash", "-c", "./script.sh preview " + chunk};
			BashCommand bashCommand = new BashCommand(command);
			team.submit(bashCommand);
		}
	}
	
	@FXML
    private void handleSaveChunk(ActionEvent event) throws IOException {
		String chunk = searchResultTextArea.getSelectedText();
		
		boolean isValidChunk = checkForValidChunk(chunk);
		if (isValidChunk) {
			String voice = voiceDropDownMenu.getValue();
			
			// Run bash script
    		String[] command = new String[]{"/bin/bash", "-c", "./script.sh save " + voice + " " + chunk};
			BashCommand bashCommand = new BashCommand(command);
			team.submit(bashCommand);
			
			bashCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					updateChunkList();
				}
			});
		}
	}
	
	@FXML
    private void handleCreateCreation(ActionEvent event) throws IOException {

	}
	
	@FXML
    private void handleSliderDragged(ActionEvent event) throws IOException {

	}
	
	
    private void displayChunkSelection() {
    	// Hide search elements
    	enterSearchTerm.setVisible(false);
    	enterSearchTermTextInput.setVisible(false);
    	searchWikipediaButton.setVisible(false);
    	searchInProgress.setVisible(false);
        termNotFound.setVisible(false);
        
        // Show chunk elements
        searchResultTextArea.setVisible(true); 
        previewChunk.setVisible(true);
    	saveChunk.setVisible(true);
    	voiceLabel.setVisible(true);
    	voiceDropDownMenu.setVisible(true);
    	chunkList.setVisible(true);
    	numImagesSlider.setVisible(true);
        creationNameTextField.setVisible(true);
        finalCreate.setVisible(true);
    }

    private void getSearchResult() {
    	_searchTerm = enterSearchTermTextInput.getText();
    	if (_searchTerm.equals("") || _searchTerm.equals(null)) {
    		termNotFound.setVisible(true);
    	} else {
    		termNotFound.setVisible(false);
    		searchInProgress.setVisible(true);
    		
    		// Run bash script that uses wikit and returns the the result of the search
    		String[] command = new String[]{"/bin/bash", "-c", "./script.sh search " + _searchTerm};
			BashCommand bashCommand = new BashCommand(command);
			team.submit(bashCommand);
			
			// Using concurrency allows the user to cancel the creation if the search takes too long
			bashCommand.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					try {
        				/**
        				 * Returns a list with only one element
        				 * If the term was not found, the element is "(Term not found)"
        				 * Otherwise the element is the search result
        				 */
        				List<String> searchResult = bashCommand.get();
        				
        				if (searchResult.get(0).equals("(Term not found)")) {
        					searchInProgress.setVisible(false);
        					termNotFound.setVisible(true);
        				} else {
        					displayChunkSelection();
        			        searchResultTextArea.setText(searchResult.get(0));
        				}
    
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			} catch (ExecutionException e) {
        				e.printStackTrace();
        			}
				}
			});
    	}
    }
    
    /**
     * @return whether or not the chunk is valid
     * valid if 0 < number of words <= 30, or the chunk is too long but the user confirms anyway
     */
    private boolean checkForValidChunk(String chunk) {
    	int numberOfWords = countWords(chunk);
		if (numberOfWords == 0) {
			Alert alert = new Alert(AlertType.ERROR, "Please select a chunk by highlighting text.");
			alert.showAndWait();
			return false;
			
		} else if (numberOfWords > 30) {
			String warningMessage = "Chunks longer than 30 words can sound worse. Are you sure you want to create this chunk?";
			Alert alert = new Alert(AlertType.WARNING, warningMessage, ButtonType.CANCEL, ButtonType.YES);
			
			// Display the confirmation alert and store the button pressed
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
    }
    
    private int countWords(String input) {
    	if (input == null || input.isEmpty()) {
    		return 0;
    	}

    	// Splits the input at any instance of one or more whitespace character, then counts the number of splits
    	String[] words = input.split("\\s+");
    	return words.length;
    }
    
    private void updateChunkList() {
    	System.out.println("done");
    }
}
