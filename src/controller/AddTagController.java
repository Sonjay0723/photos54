package controller;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Album;
import model.Picture;
import model.Tag;
import model.TagType;
import model.User;

public class AddTagController {
	
	public Stage primaryStage;
	
	@FXML private ListView<TagType> typeView;
	@FXML private Button addTag;
	@FXML private Button createNewType;
	@FXML private Button cancelBtn;
	@FXML private TextField newValue;
	@FXML private TextField newTag;
	@FXML private RadioButton multiVal;
	@FXML private RadioButton singleVal;
	
	ObservableList<TagType> tagTypeList;
	
	public void start(Stage primaryStage, User currUser, ArrayList<User> userList, Picture currPicture, Album currAlbum, int index) {
		
		this.primaryStage = primaryStage;
		
		tagTypeList = FXCollections.observableArrayList(currUser.getTagTypes());
		
		typeView
        .getSelectionModel()
        .selectedIndexProperty();
		
		if(!tagTypeList.isEmpty()) {
			typeView.setItems(tagTypeList);
			typeView.getSelectionModel().select(0);
		}
		
		addTag.setOnAction(event->{
			
			boolean canAdd = true;
			TagType currTag = typeView.getSelectionModel().getSelectedItem();
			for(int i=0; i<currPicture.getTagList().size(); i++) {
				if(currTag.getTagName().equals(currPicture.getTagList().get(i).getName()) && !currTag.getMulti())
					canAdd = false;
			}
			
			if(tagTypeList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to add a value for!");
			else if(!canAdd)
				popUpMessage(primaryStage, currTag.getTagName()+" is only allowed to have one value, and "+currPicture.getPictureName()+" already has one");
			else if(newValue.getText().isBlank())
				popUpMessage(primaryStage, "There is no tag value entered!");
			else {
				Tag newTag = new Tag(currTag.getTagName(), newValue.getText(), currTag.getMulti());
				if(agreeOrDisagree(primaryStage, "Would you like to add the tag "+newTag.toString()+" to "+currPicture.getPictureName()+"?")) {
					currPicture.addTag(newTag);
					saveData(userList);
				}
			}
		});
		
		cancelBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Are you done adding tags?")){
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/Album.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AlbumController albumView = loader.getController();
		            Stage stage = new Stage();
		            
		            albumView.start(stage, currUser, userList, currAlbum, index);
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
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
