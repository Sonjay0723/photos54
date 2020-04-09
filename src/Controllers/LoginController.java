package Controllers;

import java.io.*;
import java.util.*;
import javafx.fxml.*;
import Display.*;

import application.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML
	private Button enter;
	@FXML
	private TextField username;
	@FXML
	private Button exit;
	
	ArrayList<User> users;
	//this is the path to the file where user info should be stored
	private final String path = "data/data.dat";
	Boolean validUser = false;
	
	public void start(Stage primaryStage) {
		enter.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to go to this user's page?")) {
				loginToPage(primaryStage);
			}
		});
	}
	
	@FXML
	public void loginToPage(Stage primaryStage) {
		
		String usernameTxt = username.getText();
		
		//if the text field is empty
		if(usernameTxt.isEmpty()) {
			popUpMessage(primaryStage, "The Username Text Field is empty!");
			return;
		}
		
		File userInfo = new File(path);
		
		//Create new stock and admin users if the user file path does not exist
		if (!userInfo.exists()) {
			
			userInfo.createNewFile();
			
			Album stockAlbum = new Album("stock");
			String stockPhotos = "data/stock";
			
			File photos;
			for (int currentPhoto = 1; currentPhoto <= 5; currentPhoto++) {
				photos = new File(stockPhotos + "/img" + Integer.toString(currentPhoto) + ".jpg");

				if (photos != null) {
					//TODO insert Stock image into stockAlbum
				}
			}
			
			//Create a stock user
			User stock = new User("stock");
			//TODO add stockAlbum to stock's Album Object
			users = new ArrayList<User>();
			users.add(stock);
			
			//TODO write the User into the File path
			
			//create an Admin user
			User admin = new User("Admin");
			users.add(admin);
			//TODO display users ArrayList
			
			//TODO write the User into the File path
			
		}
		
		//TODO Read through files
		
		User currUser = null;

		//Check if User exists
		for (User currentUser : users) {
			if (currentUser.getUsername().equals(usernameTxt)) {
				currUser = currentUser;
				if(usernameTxt.equals("Admin")){
					//TODO load Admin dashboard 
				}
				else {
					//TODO load specific user
				}
				break;
			}
		}
		
		//If user does not exist, there is an error
		if(currUser == null)
			popUpMessage(primaryStage, "This User Does Not Exist");
	}
	
	//method for warning signature
		public void popUpMessage(Stage primaryStage, String displayText) {
			Alert warning = new Alert(AlertType.WARNING);
			warning.initOwner(primaryStage);
			warning.setTitle("We ran into an issue...");
			warning.setHeaderText(displayText);
			warning.showAndWait();
		}
		
		public boolean agreeOrDisagree(Stage primaryStage, String displayText) {
			Alert sayYes = new Alert(AlertType.CONFIRMATION);
			sayYes.initOwner(primaryStage);
			sayYes.setContentText(displayText);
			
			ButtonType yesButton = new ButtonType("Continue");
			ButtonType noButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			
			sayYes.getButtonTypes().setAll(yesButton, noButton);
			
			Optional<ButtonType> result = sayYes.showAndWait();
			if (result.get() == yesButton)  {
				return true;
			}
			return false;
		}

}
