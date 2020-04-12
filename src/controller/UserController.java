/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package controller;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Album;
import model.User;

public class UserController {
	
	@FXML ListView<Album> listView;
	
	@FXML private Button deleteBtn;
	@FXML private Button addBtn;
	@FXML private Button editBtn;
	@FXML private Button logoutBtn;
	@FXML private Button searchBtn;
	@FXML private Button redirectAlbumBtn;
	
	@FXML private Text userNameTxt;
	@FXML private TextField albumTxt;
	
	public Stage primaryStage;
	/**
	 * All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, logging out, etc.) 
	 * The list of albums for the current user is initialized
	 * 
	 * @param primaryStage current stage
	 * @param currUser the current user
	 * @param userList the list of all users
	 * @param index is the index of the current album for the user page
	 */
	public void start(Stage primaryStage, User currUser, ArrayList<User> userList, int index) {
		
		this.primaryStage = primaryStage;
		
		ObservableList<Album> albumList = FXCollections.observableArrayList(currUser.getAlbumList());
		
		userNameTxt.setText(currUser.getUsername());
		
		listView
        .getSelectionModel()
        .selectedIndexProperty()
        .addListener((obs, oldVal, newVal) -> whatInfo(albumList));
		
		if(!albumList.isEmpty()) {
			listView.setItems(albumList);
			listView.getSelectionModel().select(index);
			whatInfo(albumList);
		}
		
		//disable/enable buttons appropriately
		addBtn.disableProperty().bind(Bindings.isEmpty(albumTxt.textProperty()));
		deleteBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
		redirectAlbumBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
		if(!albumList.isEmpty()) {
			BooleanBinding hasChanged = Bindings.equal(albumTxt.textProperty(), listView.getSelectionModel().getSelectedItem().getTitle());
			editBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull().or(hasChanged).or(Bindings.isEmpty(albumTxt.textProperty())));
		}
		else
			editBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull().or(Bindings.isEmpty(albumTxt.textProperty())));
		
		//Add album to the list
		addBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to add the album  "+albumTxt.getText()+" to the list?")) {
				//Creating Album object
				Album newAlbum = new model.Album(albumTxt.getText());
				add(newAlbum, primaryStage, albumList, currUser);
				saveData(userList);
			}
			else
				whatInfo(albumList);
		});
		
		//Delete selected album from the list
		deleteBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to remove the album "+ listView.getSelectionModel().getSelectedItem().getTitle()+" from the list?")){
				delete(albumList, currUser);
				saveData(userList);
			}
			else
				whatInfo(albumList);
		});
		
		//Edit name of the album that is currently selected
		editBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like rename the album "+listView.getSelectionModel().getSelectedItem().getTitle()+"?")){
				edit(albumList,primaryStage, currUser);
				saveData(userList);
			}
			else
				whatInfo(albumList);
		});
		
		//Open selected album
		redirectAlbumBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like open the album "+listView.getSelectionModel().getSelectedItem().getTitle()+"?")){
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/Album.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AlbumController albumView = loader.getController();
		            Stage stage = new Stage();
		            
		            albumView.start(stage, currUser, userList, listView.getSelectionModel().getSelectedItem(), 0, listView.getSelectionModel().getSelectedIndex());
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		//logout of user and go to login page
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
		
		//search for pictures in a different scene
		searchBtn.setOnAction(event->{
			this.primaryStage.close();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/view/Search.fxml"));
			try {
	            AnchorPane root = (AnchorPane)loader.load();
	            SearchController searchView = loader.getController();
	            Stage stage = new Stage();
	            
	            searchView.start(stage, currUser, userList);
	            Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * method to Add a new Album to the User, initialized to be empty
	 * 
	 * @param newAlbum the new album to add for the current user
	 * @param primaryStage the current stage
	 * @param albumList the ObservableList to add the new album to
	 * @param thisUser the user to add the new album to
	 */
	public void add(Album newAlbum, Stage primaryStage, ObservableList<Album> albumList, User thisUser){
		
		if(albumList.isEmpty()) {
			
			albumList.add(newAlbum);
		
			listView.setItems(albumList);
			listView.getSelectionModel().select(0);
			whatInfo(albumList);
			
			thisUser.addAlbum(newAlbum);
			
			return;
		}
		
		//adding in appropriate location
		else if (!inList(newAlbum, primaryStage, albumList)) {
			for(int i=0; i<albumList.size(); i++) {
				if(albumList.get(i).compareTo(newAlbum) > 0) {
					if(i==0) {
						
						albumList.add(0,newAlbum);
						//select User
						listView.setItems(albumList);
						listView.getSelectionModel().select(0);
						whatInfo(albumList);
						
						thisUser.addAlbum(newAlbum);
						
						return;
					}
					else if(i>=albumList.size()) {
						
						albumList.add(newAlbum);
						//select Album
						listView.setItems(albumList);
						listView.getSelectionModel().select(albumList.size()-1);
						whatInfo(albumList);
						
						thisUser.addAlbum(newAlbum);
						
						return;
					}
					else {
						
						albumList.add(i, newAlbum);
						//select Album
						listView.setItems(albumList);
						listView.getSelectionModel().select(i);
						whatInfo(albumList);
						
						thisUser.addAlbum(newAlbum);
						
						return;
					}
				}
				else if(albumList.get(i).compareTo(newAlbum) < 0) {
					continue;
				}
			}
			
			albumList.add(newAlbum);
			//select Album
			listView.setItems(albumList);
			listView.getSelectionModel().select(albumList.size()-1);
			whatInfo(albumList);
			
			thisUser.addAlbum(newAlbum);
			
			return;
		}
		
		return;
	}
	
	/**
	 * Method to delete selected album from user's albums
	 * 
	 * @param albumList observable list of User's albums from where to delete an album
	 * @param thisUser the current user
	 */
	public void delete(ObservableList<Album> albumList, User thisUser){
		
		//delete current Album
		int currIndex = listView.getSelectionModel().getSelectedIndex();
		thisUser.removeAlbum(listView.getSelectionModel().getSelectedItem().getTitle());
		albumList.remove(currIndex);
		listView.setItems(albumList);
		
		//select next Album in List or clear text fields if list is empty
		if(!albumList.isEmpty()) {
			if(albumList.size() <= currIndex) {
				listView.getSelectionModel().select(currIndex-1);
				whatInfo(albumList);
			}
			else {
				listView.getSelectionModel().select(currIndex);
				whatInfo(albumList);
			}
		}
		else 
			albumTxt.clear();
		
		return;
	}
	
	/**
	 * Method to edit name of current selected album
	 * 
	 * @param albumList ObservableList of albums from where to select album to be edited
	 * @param primaryStage the current stage
	 * @param thisUser the current user for which to edit the album information
	 */
	public void edit(ObservableList<Album> albumList, Stage primaryStage, User thisUser) {
		
		//find out which entries to edit
		String title;
		title = albumTxt.getText();
		Album currAlbum = albumList.get((listView.getSelectionModel().getSelectedIndex()));
		
		//create updated song and delete old one
		Album newAlbum = new Album(title);
		newAlbum.setList(currAlbum.getPictureList());
		delete(albumList, thisUser);
		
		//if the newAlbum does not exist in the albumlist, add it to the albumlist, otherwise add the original album back in
		if(!inList(newAlbum, primaryStage, albumList))
			add(newAlbum,primaryStage, albumList, thisUser);
		else
			add(currAlbum,primaryStage, albumList, thisUser);
		
		return;
	}
	
	/**
	 * Method to Check if the picture is already in the picture list
	 * 
	 * @param search Picture to search for
	 * @param primaryStage current stage
	 * @param albumList the picture list to search in to see if the picture already exists
	 * 
	 * @return True if it exists in the list, false otherwise
	 */
	public boolean inList(Album search, Stage primaryStage, ObservableList<Album> albumList){
		if(albumList.isEmpty())
			return false;
		for(int i=0; i<albumList.size(); i++) {
			if(albumList.get(i).getTitle().toLowerCase().compareTo(search.getTitle().toLowerCase()) == 0) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * method to display values depending on what is selected in appropriate location
	 * current album of current selected Picture
	 * 
	 * @param albumList the list from where items are selected
	 */
	public void whatInfo(ObservableList<Album> albumList) {
		if(!albumList.isEmpty()) {
			int currentIndex = listView.getSelectionModel().getSelectedIndex();
			Album currAlbum = albumList.get(currentIndex);
			albumTxt.setText(currAlbum.getTitle());
			BooleanBinding hasChanged = Bindings.equal(albumTxt.textProperty(), listView.getSelectionModel().getSelectedItem().getTitle());
			editBtn.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull().or(hasChanged).or(Bindings.isEmpty(albumTxt.textProperty())));
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
	 * method to save User data
	 * 
	 * @param userList the list of all users with certain information having been changed
	 */
	private void saveData(ArrayList<User> userList) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("data/dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			
			objectOutputStream.writeObject(userList);
			
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
