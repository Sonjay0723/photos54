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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AdminController {
	
	public ObservableList<User> usersList = FXCollections.observableArrayList();
	
	@FXML ListView<User> listView;
	
	@FXML private Button deleteBtn;
	@FXML private Button addBtn;
	@FXML private Button logoutBtn;
	
	@FXML private TextField usernameTxt;
	
	public void start(Stage primaryStage) throws Exception {   
		
		primaryStage.setTitle("Admin Page");
		
		File fp = new File("usersList.txt");
		fp.createNewFile();
		
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
			if(usersList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove this user from the list?")){
				if (!usersList.isEmpty()){
					User currUser = usersList.get((listView.getSelectionModel().getSelectedIndex()));
					delete(currUser);
					try {
						write(usersList, fp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		logoutBtn.setOnAction(event->{
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/Display/login.fxml"));
				AnchorPane root = (AnchorPane) loader.load();
				
				LoginController libraryController = loader.getController();
				libraryController.start(primaryStage);
				
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);  
				primaryStage.show();
	
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
						usersList.add(0,newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(0);
						whatInfo();
						return;
					}
					else if(i>=usersList.size()) {
						usersList.add(newUser);
						//select User
						listView.setItems(usersList);
						listView.getSelectionModel().select(usersList.size()-1);
						whatInfo();
						return;
					}
					else {
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
			User newUser = new application.User(username);
			
			res.add(newUser);
		}
		r1.close();
		return res;
	}
	
}