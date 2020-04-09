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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML private Button enter;
	@FXML private TextField username;
	@FXML private Button exit;
	
	//this is the path to the file where user info should be stored
	private final String path = "data";
	
	public Stage primaryStage;
	
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		
		//create base directory if it does not exist
		File baseDir = new File(path);
		if(!baseDir.exists()) {
			baseDir.mkdirs();
		}
		
		//create User's directory if it does not exist
		String userDirPath = path+"/Users";
		File userDir = new File(userDirPath);
		if(!userDir.exists()) {
			userDir.mkdir();
		}
		
		//Create stock Album if it does not exist
		User stockUser = new User("stock");
		String stockUserPath = "data/Users/stock";
		File stockUserFile = new File(stockUserPath);
		
		if(!stockUserFile.exists()) {
			Album stockAlbum = new Album("stock");
			String stockAlbumPath = "src/application";
			
			for (int currentPhoto = 1; currentPhoto <= 5; currentPhoto++) {
				File photos = new File(stockAlbumPath + "/Img" + Integer.toString(currentPhoto) + ".jpg");
				
				Image image = new Image(photos.toURI().toString());
				SerializableImage thisImage = new application.SerializableImage(image);
				String name = photos.getName();
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(photos.lastModified());
				Picture newPhoto = new application.Picture(thisImage, date, "Stock Photo", name);
				stockAlbum.addPicture(newPhoto);
			}
			stockUser.addAlbum(stockAlbum);
			
			//TODO Load album into files
		}
		
		enter.setOnAction(event->{
			if(username.getText().isEmpty())
				popUpMessage(primaryStage, "The Username Text Field is empty!");
			else if(agreeOrDisagree(primaryStage, "Would you like to go to this user's page?")) {
				loginToPage(primaryStage);
			}
		});
		
		exit.setOnAction(event->{
			//TODO MAKE SURE THAT ALL INFORMATION IS SAVED BY UPDATING FILES!
			primaryStage.close();
			return;
		});
	}
	
	public void loginToPage(Stage primaryStage) {
		
		String usernameTxt = username.getText();
		
		//if user is admin, redirect to admin
		if(usernameTxt.toLowerCase().equals("admin")) {
			primaryStage.close();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/Display/Admin.fxml"));
			try {
	            AnchorPane root = (AnchorPane)loader.load();
	            AdminController adminView = loader.getController();
	            Stage stage = new Stage();
	            
	            adminView.start(stage);
	            Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		String userPath = path + "/Users/"+usernameTxt;
		File userInfo = new File(userPath);
		
		//if the user file path does not exist then it is an error
		if (!userInfo.exists()) {
			popUpMessage(primaryStage, "This User Does Not Exist");
			return;
		}
		
		//if the user does exist, redirect to specific user page
		else {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/Display/UserName.fxml"));
				AnchorPane root = (AnchorPane) loader.load();
				
				UserController libraryController = loader.getController();
				libraryController.start(primaryStage, usernameTxt);
				
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);  
				primaryStage.show();
	
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
	}
	
	//method for warning signature
	public void popUpMessage(Stage primaryStage, String displayText) {
		Alert warning = new Alert(AlertType.WARNING);
		warning.initOwner(primaryStage);
		warning.setTitle("We ran into an issue...");
		warning.setHeaderText(displayText);
		warning.showAndWait();
	}
	
	//method for confirmations
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
