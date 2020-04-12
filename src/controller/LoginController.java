/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package controller;

import java.io.*;
import java.util.*;

import model.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML private Button enter;
	@FXML private TextField username;
	
	public Stage primaryStage;
	
	ArrayList<User> users;
	
	/**
	 * All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, etc.) 
	 * The Original list of TagTypes for the specific User is initialized
	 * If this is the first time running the application, a dat file where all information is to be saved + a stock user is created
	 * 
	 * @param primaryStage Current stage
	 */
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		
		//create base directory if it does not exist
		File baseFile = new File("data/dat");
		
		//If first time running, create stock user
		if(!baseFile.exists()) {
			
			try {
				baseFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Album stockAlbum = new Album("stock");
			String stockAlbumPath = "data";
			
			for (int currentPhoto = 1; currentPhoto <= 5; currentPhoto++) {
				File photos = new File(stockAlbumPath + "/Img" + Integer.toString(currentPhoto) + ".jpg");
				
				Image image = new Image(photos.toURI().toString());
				SerializableImage thisImage = new model.SerializableImage(image);
				String name = photos.getName();
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(photos.lastModified());
				Picture newPhoto = new model.Picture(thisImage, date, "Stock Photo", name);
				stockAlbum.addPicture(newPhoto);
			}
			
			User stockUser = new User("stock");
			stockUser.addAlbum(stockAlbum);
			users = new ArrayList<User>();
			users.add(stockUser);
		
			try {
				FileOutputStream out = new FileOutputStream("data/dat");
				ObjectOutputStream oout = new ObjectOutputStream(out);
	
				oout.writeObject(users);
	
				oout.close();
				out.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		//Set button disable/enable properties
		enter.disableProperty().bind(Bindings.isEmpty(username.textProperty()));
		
		//login to specific user
		enter.setOnAction(event->{
			if(username.getText().isEmpty())
				popUpMessage(primaryStage, "The Username Text Field is empty!");
			else if(agreeOrDisagree(primaryStage, "Would you like to go to "+username.getText()+"'s page?")) {
				loginToPage(primaryStage);
			}
		});
		
	}
	
	/**
	 * Login to the specific user entered
	 * 
	 * @param primaryStage current stage
	 */
	@SuppressWarnings("unchecked")
	public void loginToPage(Stage primaryStage) {
		
		try {
			FileInputStream fileInputStream = new FileInputStream("data/dat");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			
			users = (ArrayList<User>) objectInputStream.readObject();
			
			objectInputStream.close();
			fileInputStream.close();

			User user = null;

			for (User currentUser : users) {
				if (currentUser.getUsername().toLowerCase().equals(username.getText().toLowerCase())) {
					user = currentUser;
				}
			}
		
			String usernameTxt = username.getText();
			
			//if user is admin, redirect to admin
			if(usernameTxt.toLowerCase().equals("admin")) {
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/Admin.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AdminController loginView = loader.getController();
		            Stage stage = new Stage();
		            
		            loginView.start(stage, users);
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
			
			//if the user does not exist then it is an error
			if (user==null) {
				popUpMessage(primaryStage, "This User Does Not Exist");
				return;
			}
			
			//if the user does exist, redirect to specific user page
			else {
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/UserName.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            UserController userView = loader.getController();
		            Stage stage = new Stage();
		            
		            userView.start(stage, user, users, 0);
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}catch(Exception e) {}
		return;
	}
	
	/**
	 * method for warning signature
	 * 
	 * @param primaryStage current stage
	 * @param displayText Text to show in warning
	 */
	public void popUpMessage(Stage primaryStage, String displayText) {
		Alert warning = new Alert(AlertType.WARNING);
		warning.initOwner(primaryStage);
		warning.setTitle("We ran into an issue...");
		warning.setHeaderText(displayText);
		warning.showAndWait();
	}
	
	/**
	 * method to allow user to back out of decision
	 * 
	 * @param primaryStage current stage
	 * @param displayText text to show what to agree for
	 * 
	 * @return true if agreed, false otherwise
	 */
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
