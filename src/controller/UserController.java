package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

	public void start(Stage primaryStage, User currUser, ArrayList<User> userList) {
		
		this.primaryStage = primaryStage;
		
		ObservableList<Album> albumList = FXCollections.observableArrayList(currUser.getAlbumList());
		
		userNameTxt.setText(currUser.getUsername());
		
		listView
        .getSelectionModel()
        .selectedIndexProperty()
        .addListener((obs, oldVal, newVal) -> whatInfo(albumList));
		
		if(!albumList.isEmpty()) {
			listView.setItems(albumList);
			listView.getSelectionModel().select(0);
			whatInfo(albumList);
		}
		
		addBtn.setOnAction(event->{
			if(albumTxt.getText().isEmpty())
				popUpMessage(primaryStage, "The Username field is Empty!");
			if(agreeOrDisagree(primaryStage, "Would you like to add this album to the list?")) {
				//Creating User object
				Album newAlbum = new model.Album(albumTxt.getText());
				add(newAlbum, primaryStage, albumList, currUser);
				saveData(userList);
			}
			else
				whatInfo(albumList);
		});
		
		deleteBtn.setOnAction(event->{
			if(albumList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove this album from the list?")){
				if (!albumList.isEmpty()){
					delete(albumList, currUser);
					saveData(userList);
				}
			}
		});
		
		editBtn.setOnAction(event->{
			if(albumList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to rename!");
			else if(agreeOrDisagree(primaryStage, "Would you like rename this album?")){
				if (!albumList.isEmpty()){
					edit(albumList,primaryStage, currUser);
					saveData(userList);
				}
			}
			else
				whatInfo(albumList);
		});
		
		redirectAlbumBtn.setOnAction(event->{
			if(albumList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to Open!");
			else if(agreeOrDisagree(primaryStage, "Would you like open this album?")){
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/Album.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AlbumController albumView = loader.getController();
		            Stage stage = new Stage();
		            
		            albumView.start(stage, currUser, userList, listView.getSelectionModel().getSelectedItem());
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
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
						//select User
						listView.setItems(albumList);
						listView.getSelectionModel().select(albumList.size()-1);
						whatInfo(albumList);
						
						thisUser.addAlbum(newAlbum);
						
						return;
					}
					else {
						
						albumList.add(i, newAlbum);
						//select User
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
			//select User
			listView.setItems(albumList);
			listView.getSelectionModel().select(albumList.size()-1);
			whatInfo(albumList);
			
			thisUser.addAlbum(newAlbum);
			
			return;
		}
		
		return;
	}
	
	public void delete(ObservableList<Album> albumList, User thisUser){
		
		//delete current User
		int currIndex = listView.getSelectionModel().getSelectedIndex();
		thisUser.removeAlbum(listView.getSelectionModel().getSelectedItem().getTitle());
		albumList.remove(currIndex);
		listView.setItems(albumList);
		
		//select next User in List or clear text fields if list is empty
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
	
	public void edit(ObservableList<Album> albumList, Stage primaryStage, User thisUser) {
		
		//find out which entries to edit
		String title;
		String currTitle;
		title = albumTxt.getText();
		Album currAlbum = albumList.get((listView.getSelectionModel().getSelectedIndex()));
		currTitle = currAlbum.getTitle();
		

		//check if important fields are now empty
		if(title.isBlank()) {
			popUpMessage(primaryStage, "You cannot change the title of an Album to a Blank Title!");
		}
		
		//create updated song and delete old one
		Album newAlbum = new Album(title);
		newAlbum.setList(currAlbum.getPictureList());
		delete(albumList, thisUser);
		
		//if the newSong does not exist in the albumlist, add it to the albumlist, otherwise add the original song back in
		if(currTitle.compareTo(title)==0) {
			add(currAlbum,primaryStage, albumList, thisUser);
			popUpMessage(primaryStage,"The information for this title has not been edited!");
			return;
		}
		
		else if(!inList(newAlbum, primaryStage, albumList))
			add(newAlbum,primaryStage, albumList, thisUser);
		else
			add(currAlbum,primaryStage, albumList, thisUser);
		return;
	}
	
	//method to check if same title is already in albumList
	public boolean inList(Album search, Stage primaryStage, ObservableList<Album> albumList){
		if(albumList.isEmpty())
			return false;
		for(int i=0; i<albumList.size(); i++) {
			if(albumList.get(i).compareTo(search) == 0) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
	}
	
	//method to display values
	public void whatInfo(ObservableList<Album> albumList) {
		int currentIndex = listView.getSelectionModel().getSelectedIndex();
		Album currAlbum = albumList.get(currentIndex);
		albumTxt.setText(currAlbum.getTitle());
	}
	
	//method for warning signature
	public void popUpMessage(Stage primaryStage, String displayText) {
		Alert warning = new Alert(AlertType.WARNING);
		warning.initOwner(primaryStage);
		warning.setTitle("We ran into an issue...");
		warning.setHeaderText(displayText);
		warning.showAndWait();
	}
	
	//method to allow user to back out of decision
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
	
	//method to save User data
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
