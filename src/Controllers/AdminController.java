//Dhrishti Hazari and Jayson Pitta
package Controllers;

import java.awt.Label;
import java.io.*;
import java.util.*;
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
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AdminController {
	
	public ObservableList<User> usersList = FXCollections.observableArrayList();
	
	@FXML ListView<User> listView;
	
	@FXML private Button deleteBtn;
	@FXML private Button addBtn;
	@FXML private Button logoutBtn;
	
	@FXML private TextField usernameTxt;

	public Stage primaryStage;
	
	public void start(Stage primaryStage) throws Exception {  
		
		this.primaryStage = primaryStage;
		
		File fp = new File("data/usersList.txt");
		fp.createNewFile();
		
		/*
		File allUsersDir = new File("data/Users");
		if(!allUsersDir.exists()) {
			allUsersDir.mkdir();
		}
		*/
		
		usersList.addAll(read(fp));
		
		listView
	        .getSelectionModel()
	        .selectedIndexProperty()
	        .addListener((obs, oldVal, newVal) -> whatInfo());

		if(!usersList.isEmpty()) {
			listView.setItems(usersList);
			listView.getSelectionModel().select(0);
			whatInfo();
		}

		addBtn.setOnAction(event->{
			//TODO add to files
			if(agreeOrDisagree(primaryStage, "Would you like to add this user to the list?")) {
				add(usernameTxt.getText(), primaryStage);
				try {
					write(usersList, fp);
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
			else
				whatInfo();
		});
		
		deleteBtn.setOnAction(event->{
			//TODO remove from files
			if(usersList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove this user from the list?")){
				if (!usersList.isEmpty()){
					User currUser = usersList.get((listView.getSelectionModel().getSelectedIndex()));
					delete(currUser);
					try {
						write(usersList, fp);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		logoutBtn.setOnAction(event->{
			primaryStage.close();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/Display/Login.fxml"));
			try {
	            AnchorPane root = (AnchorPane)loader.load();
	            AdminController loginView = loader.getController();
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
	
	
	public void add(String username, Stage primaryStage){
		
		//Creating User object
		User newUser = new application.User(username);
		
		//checking if fields are entered incorrectly
		if(username.isEmpty()) {
			popUpMessage(primaryStage, "The Username field is Empty!");
			return;
		}
			
		if(usersList.isEmpty()) {
			
			newUser.addAlbum(addStockAlbum());
			
			usersList.add(newUser);
			//select User
			listView.setItems(usersList);
			listView.getSelectionModel().select(0);
			whatInfo();
			
			return;
		}
		
		//adding in appropriate location
		else if (!inList(newUser, primaryStage)) {
			for(int i=0; i<usersList.size(); i++) {
				if(usersList.get(i).compareTo(newUser) > 0) {
					if(i==0) {
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(0,newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(0);
						whatInfo();
						return;
					}
					else if(i>=usersList.size()) {
						
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(usersList.size()-1);
						whatInfo();
						return;
					}
					else {
						
						newUser.addAlbum(addStockAlbum());
						
						usersList.add(i, newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(i);
						whatInfo();
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
			whatInfo();
			
			return;
		}
		
		return;
	}
	
	public void delete(User currentUser){
		
		//delete current User
		int currIndex = listView.getSelectionModel().getSelectedIndex();
		usersList.remove(currIndex);
		listView.setItems(usersList);
		
		//select next User in List or clear text fields if list is empty
		if(!usersList.isEmpty()) {
			if(usersList.size() <= currIndex) {
				listView.getSelectionModel().select(currIndex-1);
				whatInfo();
			}
			else {
				listView.getSelectionModel().select(currIndex);
				whatInfo();
			}
		}
		else 
			usernameTxt.clear();
		
		return;
		
	}
	
	//method to check if same name+artist is already in usersList
	public boolean inList(User search, Stage primaryStage){
		if(usersList.isEmpty())
			return false;
		for(int i=0; i<usersList.size(); i++) {
			if(usersList.get(i).compareTo(search) == 0) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
	}
	
	//method to display values
	public void whatInfo() {
		int currentIndex = listView.getSelectionModel().getSelectedIndex();
		User currUser = usersList.get(currentIndex);
		usernameTxt.setText(currUser.getUsername());
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
	
	//method to create a StockAlbum
	public Album addStockAlbum() {
		//Create stock Album
		Album stockAlbum = new Album("stock");
		String stockAlbumPath = "src/application";
		
		File photos;
		for (int currentPhoto = 1; currentPhoto <= 5; currentPhoto++) {
			photos = new File(stockAlbumPath + "/Img" + Integer.toString(currentPhoto) + ".jpg");
			
			Image image = new Image(photos.toURI().toString());
			SerializableImage thisImage = new application.SerializableImage(image);
			String name = photos.getName();
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(photos.lastModified());
			Picture newPhoto = new application.Picture(thisImage, date, "Stock Photo", name);
			stockAlbum.addPicture(newPhoto);
		}
		
		return stockAlbum;
	}
	
	//method to write to file(FROM SCRATCH)
	private void write(ObservableList<User> Users, File fp) throws IOException {
		if (fp.delete()) {
			fp.createNewFile();
		}
		
		if (Users.size() == 0)
			return;
		
		PrintWriter  w1 = new PrintWriter(fp);
		for (int i = 0; i < Users.size(); i++) {
			String str = Users.get(i).getUsername();
			w1.write(str);
		}
		w1.close();
		return;
	}
	
	//Method to read from file in order to load in previous information
	private ObservableList<User> read(File fp) throws FileNotFoundException, IOException, ClassNotFoundException {
		Scanner r1 = new Scanner(fp);
		ObservableList<User> res = FXCollections.observableArrayList();
		
		while (r1.hasNextLine()) {
			
			String username = r1.nextLine();
			
			res.add(new application.User(username));
		}
		r1.close();
		return res;
	}
	
}