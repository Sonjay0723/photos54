/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package controller;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
	final ToggleGroup howMany = new ToggleGroup();
	
	/**
	 * 
	 * All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, logging out, etc.) 
	 * The Original list of TagTypes for the specific User is initialized
	 * 
	 * @param primaryStage Current view
	 * @param currUser The Current User
	 * @param userList The List of all users
	 * @param currPicture The current picture for which to add tags for
	 * @param currAlbum The Current Album the picture is In
	 * @param index The index of the Current Picture in the Current Album
	 * @param AlbumIndex is the index of the current album for the user page
	 */
	public void start(Stage primaryStage, User currUser, ArrayList<User> userList, Picture currPicture, Album currAlbum, int index, int AlbumIndex) {
		
		this.primaryStage = primaryStage;
		
		tagTypeList = FXCollections.observableArrayList(currUser.getTagTypes());

		//set up multi/single radio buttons + when user is allowed to add new tag
		BooleanBinding isTagFieldEmpty = Bindings.isEmpty(newTag.textProperty());
		multiVal.setToggleGroup(howMany);
		singleVal.setToggleGroup(howMany);
		multiVal.setSelected(true);
		createNewType.disableProperty().bind(isTagFieldEmpty);
		multiVal.disableProperty().bind(isTagFieldEmpty);
		singleVal.disableProperty().bind(isTagFieldEmpty);
		BooleanBinding isValueFieldEmpty = Bindings.isEmpty(newValue.textProperty());
		addTag.disableProperty().bind(isValueFieldEmpty.or(typeView.getSelectionModel().selectedItemProperty().isNull()));
		
		typeView
        .getSelectionModel()
        .selectedIndexProperty();
		
		//selecting first value in list
		if(!tagTypeList.isEmpty()) {
			typeView.setItems(tagTypeList);
			typeView.getSelectionModel().select(0);
		}
		
		//creating a new tag type
		createNewType.setOnAction(event->{
			
			//make sure Tag Type does not already exist
			boolean isMulti = multiVal.isSelected();
			TagType newTagName = new TagType(newTag.getText(), isMulti);
			
			if(agreeOrDisagree(primaryStage, "Do you want to add "+newTagName.toString()+" to the list of Tag Types?")) {
			
				for(int i=0; i<tagTypeList.size(); i++) {
					if(newTag.getText().toLowerCase().equals(tagTypeList.get(i).getTagName().toLowerCase())) {
						popUpMessage(primaryStage, "This Tag Type Already Exists!");
						newTag.clear();
						return;
					}
				}
				
				//adding tag to User + Observable List
				tagTypeList.add(newTagName);
				Comparator<TagType> comparator = Comparator.comparing(TagType::getTagName);
				Collections.sort(tagTypeList, comparator);
				typeView.setItems(tagTypeList);
				
				for(int i=0; i< tagTypeList.size(); i++) {
					if(tagTypeList.get(i).getTagName().equals(newTagName.getTagName())) {
						typeView.getSelectionModel().select(i);
						break;
					}
				}
				
				currUser.addTagType(newTagName);
				saveData(userList);
			}
			
			newTag.clear();
			
		});
		
		//Adding a tag to the Image
		addTag.setOnAction(event->{
			
			//Make sure the tag is not a single value tag if it already exists in the current pictures tag list
			boolean canAdd = true;
			TagType currTag = typeView.getSelectionModel().getSelectedItem();
			for(int i=0; i<currPicture.getTagList().size(); i++) {
				if(currTag.getTagName().equals(currPicture.getTagList().get(i).getName()) && !currTag.getMulti())
					canAdd = false;
			}
			
			if(!canAdd)
				popUpMessage(primaryStage, currTag.getTagName()+" is only allowed to have one value, and "+currPicture.getPictureName()+" already has one");
			//add tag to the current users list of tags if User agrees
			else {
				Tag newTag = new Tag(currTag.getTagName(), newValue.getText(), currTag.getMulti());
				if(agreeOrDisagree(primaryStage, "Would you like to add the tag "+newTag.toString()+" to "+currPicture.getPictureName()+"?")) {
					currPicture.addTag(newTag);
					saveData(userList);
				}
			}
			
			newValue.clear();
		});
		
		//Going back to album page once user is done adding pictures
		cancelBtn.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Are you done adding tags?")){
				this.primaryStage.close();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/Album.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AlbumController albumView = loader.getController();
		            Stage stage = new Stage();
		            
		            albumView.start(stage, currUser, userList, currAlbum, index, AlbumIndex);
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
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
