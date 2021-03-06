/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package controller;

import java.io.*;
import java.util.*;

import model.*;
import javafx.beans.binding.Bindings;
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
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * 
 * deleting Users
 * Adding Users
 *logging out
 *
 */
public class AdminController {
	
	@FXML ListView<User> listView;
	
	@FXML private Button deleteBtn;
	@FXML private Button addBtn;
	@FXML private Button logoutBtn;
	
	@FXML private TextField usernameTxt;

	public Stage primaryStage;
	
	/**
	 * All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, logging out, etc.) 
	 * The List of Users is initialized
	 * 
	 * @param primaryStage Current stage
	 * @param arrayUsersList The list of users
	 * @throws Exception during changing scene or saving data to file
	 */
	
	public void start(Stage primaryStage, ArrayList<User> arrayUsersList) throws Exception {  
		
		ObservableList<User> usersList = FXCollections.observableArrayList(arrayUsersList);
		
		this.primaryStage = primaryStage;
		
		listView
	        .getSelectionModel()
	        .selectedIndexProperty()
	        .addListener((obs, oldVal, newVal) -> whatInfo(usersList));

		if(!usersList.isEmpty()) {
			listView.setItems(usersList);
			listView.getSelectionModel().select(0);
			whatInfo(usersList);
		}
		
		//set button disable/enable value
		addBtn.disableProperty().bind(Bindings.isEmpty(usernameTxt.textProperty()));
		deleteBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());

		//add a user
		addBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to add "+usernameTxt.getText()+" to the list?")) {
				//Creating User object
				User newUser = new model.User(usernameTxt.getText());
				add(newUser, primaryStage, usersList);
				saveData();
			}
			else
				whatInfo(usersList);
		});
		
		//delete a user
		deleteBtn.setOnAction(event->{
			if(usersList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove "+listView.getSelectionModel().getSelectedItem().getUsername()+" from the list?")){
				if (!usersList.isEmpty()){
					User currUser = usersList.get((listView.getSelectionModel().getSelectedIndex()));
					delete(currUser, usersList);
				}
			}
			saveData();
		});
		
		//go back to login page
		logoutBtn.setOnAction(event->{
			this.primaryStage.close();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/view/Login.fxml"));
			try {
	            AnchorPane root = (AnchorPane)loader.load();
	            LoginController loginView = loader.getController();
	            Stage stage = new Stage();
	            
	            loginView.start(stage);
	            Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Method to add a new user to the list in the appropriate location
	 * 
	 * @param newUser the user to add to the list
	 * @param primaryStage the current stage
	 * @param usersList the list to add the new user to
	 */
	public void add(User newUser, Stage primaryStage, ObservableList<User> usersList){
			
		if(usersList.isEmpty()) {
			
			newUser.addAlbum(addStockAlbum());
			
			usersList.add(newUser);
		
			listView.setItems(usersList);
			listView.getSelectionModel().select(0);
			whatInfo(usersList);
			
			return;
		}
		
		//adding in appropriate location
		else if (!inList(newUser, primaryStage, usersList)) {
			for(int i=0; i<usersList.size(); i++) {
				if(usersList.get(i).compareTo(newUser) > 0) {
					if(i==0) {
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(0,newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(0);
						whatInfo(usersList);
						return;
					}
					else if(i>=usersList.size()) {
						
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(usersList.size()-1);
						whatInfo(usersList);
						return;
					}
					else {
						
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(i, newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(i);
						whatInfo(usersList);
						return;
					}
				}
				else if(usersList.get(i).compareTo(newUser) < 0) {
					continue;
				}
			}
			
			newUser.addAlbum(addStockAlbum());
			
			usersList.add(newUser);
			//select User
			listView.setItems(usersList);
			listView.getSelectionModel().select(usersList.size()-1);
			whatInfo(usersList);
			
			return;
		}
		
		return;
	}
	
	/**
	 * Method to delete user from the list
	 * 
	 * @param currentUser the user to delete
	 * @param usersList the list to delete the user from
	 */
	public void delete(User currentUser, ObservableList<User> usersList){
		
		//delete current User
		int currIndex = listView.getSelectionModel().getSelectedIndex();
		usersList.remove(currIndex);
		listView.setItems(usersList);
		
		//select next User in List or clear text fields if list is empty
		if(!usersList.isEmpty()) {
			if(usersList.size() <= currIndex) {
				listView.getSelectionModel().select(currIndex-1);
				whatInfo(usersList);
			}
			else {
				listView.getSelectionModel().select(currIndex);
				whatInfo(usersList);
			}
		}
		else 
			usernameTxt.clear();
		
		return;
		
	}
	
	/**
	 * Method to check if User is already in the list 
	 * 
	 * @param search the user that is attempting to be added
	 * @param primaryStage the current stage
	 * @param usersList the list to search through to check if user exists
	 * 
	 * @return true if it does already exist, false otherwise
	 */
	public boolean inList(User search, Stage primaryStage, ObservableList<User> usersList){
		if(usersList.isEmpty())
			return false;
		for(int i=0; i<usersList.size(); i++) {
			if(usersList.get(i).getUsername().toLowerCase().compareTo(search.getUsername().toLowerCase()) == 0) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * method to display values depending on what is selected in appropriate location
	 * 
	 * @param usersList the list from where items are selected
	 */
	public void whatInfo(ObservableList<User> usersList) {
		if(!usersList.isEmpty()) {
			int currentIndex = listView.getSelectionModel().getSelectedIndex();
			User currUser = usersList.get(currentIndex);
			usernameTxt.setText(currUser.getUsername());
		}
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
	
	/**
	 * method to create a StockAlbum
	 * 
	 * @return the stock album
	 */
	public Album addStockAlbum() {
		//Create stock Album
		Album stockAlbum = new Album("stock");
		String stockAlbumPath = "data";
		
		File photos;
		for (int currentPhoto = 1; currentPhoto <= 5; currentPhoto++) {
			photos = new File(stockAlbumPath + "/Img" + Integer.toString(currentPhoto) + ".jpg");
			
			Image image = new Image(photos.toURI().toString());
			SerializableImage thisImage = new model.SerializableImage(image);
			String name = photos.getName();
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(photos.lastModified());
			Picture newPhoto = new model.Picture(thisImage, date, "Stock Photo", name);
			stockAlbum.addPicture(newPhoto);
		}
		
		return stockAlbum;
	}
	
	/**
	 * method to save User data
	 * 
	 * @param userList the list of all users with certain information having been changed
	 */
	private void saveData() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("data/dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			
			objectOutputStream.writeObject(new ArrayList<>(Arrays.asList(listView.getItems().toArray())));
			
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}