package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.Picture;
import model.SerializableImage;
import model.Tag;
import model.TagType;
import model.User;

public class AlbumController {
public Stage primaryStage;

	//Done
	@FXML private Label albumTitle;
	
	@FXML private Button toSlideShow;
	//Done
	@FXML private Button logout;
	//Done
	@FXML private Button toUserPage;
	//Done
	@FXML private Button addPicture;
	//Done
	@FXML private Button deletePicture;
	//Done
	@FXML private Button editCaption;
	//Done
	@FXML private Button addTag;
	//Done
	@FXML private Button deleteTag;
	@FXML private Button moveToAlbum;
	@FXML private Button copyToAlbum;
	
	//Done
	@FXML private ListView<Picture> listViewImg;
	//Done
	@FXML private ListView<Tag> listViewTag;
	
	//Done
	@FXML private ImageView selectedImage;
	
	//Done
	@FXML private TextField showCaption;
	//Done
	@FXML private TextField showDate;
	
	ObservableList<Tag> tagList;
	
	public void start(Stage primaryStage, User currUser, ArrayList<User> userList, Album currAlbum, int index) {
		
		this.primaryStage = primaryStage;
		
		albumTitle.setText(currAlbum.getTitle());
		
		ObservableList<Picture> pictureList = FXCollections.observableArrayList(currAlbum.getPictureList());
		
		listViewImg
        .getSelectionModel()
        .selectedIndexProperty()
        .addListener((obs, oldVal, newVal) -> whatInfo(pictureList));
		
		if(!pictureList.isEmpty()) {
			listViewImg.setItems(pictureList);
			listViewImg.getSelectionModel().select(index);
			whatInfo(pictureList);
		}
		
		currUser.addTagType(new TagType("location", false));
		currUser.addTagType(new TagType("person", true));
		currUser.addTagType(new TagType("event", false));
		
		addPicture.setOnAction(event->{
			FileChooser fc = new FileChooser();
			File tmp = fc.showOpenDialog(null);
			
			if(tmp!=null) {
				Image img = new Image(tmp.toURI().toString());
				SerializableImage thisPic = new SerializableImage(img);
				String name = tmp.getName();
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(tmp.lastModified());
				
				Picture newPic = new Picture(thisPic, date, "", name);
				addPic(newPic, primaryStage, pictureList, currAlbum);
				saveData(userList);
			}
			else {
				whatInfo(pictureList);
			}
		});
		
		deletePicture.setOnAction(event->{
			if(pictureList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove "+listViewImg.getSelectionModel().getSelectedItem().getPictureName()+" from the list?")){
				if (!pictureList.isEmpty()){
					deletePic(pictureList, currAlbum);
					saveData(userList);
				}
			}
		});
		
		deleteTag.setOnAction(event->{
			if(pictureList.isEmpty() || tagList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to delete!");
			else if(agreeOrDisagree(primaryStage, "Would you like to remove "+listViewTag.getSelectionModel().getSelectedItem().toString()+" from the list?")){
				if (!tagList.isEmpty()){
					deleteTag(listViewImg.getSelectionModel().getSelectedItem());
					saveData(userList);
				}
			}
		});
		
		editCaption.setOnAction(event->{
			if(pictureList.isEmpty()) 
				popUpMessage(primaryStage, "There is nothing selected to recaption!");
			else if(agreeOrDisagree(primaryStage, "Would you like recaption "+listViewImg.getSelectionModel().getSelectedItem().getPictureName())){
				if (!pictureList.isEmpty()){
					editCap(pictureList);
					saveData(userList);
				}
			}
			else
				whatInfo(pictureList);
		});
		
		addTag.setOnAction(event->{
			if(pictureList.isEmpty())
				popUpMessage(primaryStage, "There is no Image selected to add a tag for!");
			else if(agreeOrDisagree(primaryStage, "Would you like to add tag(s) for "+listViewImg.getSelectionModel().getSelectedItem().getPictureName()+"?")) {
				this.primaryStage.close();
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("/view/AddTag.fxml"));
				try {
		            AnchorPane root = (AnchorPane)loader.load();
		            AddTagController tagView = loader.getController();
		            Stage stage = new Stage();
		            
		            tagView.start(stage, currUser, userList, listViewImg.getSelectionModel().getSelectedItem(), currAlbum, listViewImg.getSelectionModel().getSelectedIndex());
		            Scene scene = new Scene(root);
		            stage.setScene(scene);
		            stage.show();
		
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		toUserPage.setOnAction(event->{
			this.primaryStage.close();
			FXMLLoader loader = new FXMLLoader();
		    loader.setLocation(getClass().getResource("/view/UserName.fxml"));
			try {
		        AnchorPane root = (AnchorPane)loader.load();
		        UserController userView = loader.getController();
		        Stage stage = new Stage();
		        
		        userView.start(stage, currUser, userList);
		        Scene scene = new Scene(root);
		        stage.setScene(scene);
		        stage.show();
		
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		
		logout.setOnAction(event->{
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
	
	public void addPic(Picture newPicture, Stage primaryStage, ObservableList<Picture> pictureList, Album thisAlbum){
		
		if(pictureList.isEmpty()) {
			
			pictureList.add(newPicture);
		
			listViewImg.setItems(pictureList);
			listViewImg.getSelectionModel().select(0);
			whatInfo(pictureList);
			
			thisAlbum.addPicture(newPicture);
			
			return;
		}
		
		//adding in appropriate location
		else if (!inListPic(newPicture, primaryStage, pictureList)) {
			for(int i=0; i<pictureList.size(); i++) {
				if(pictureList.get(i).compareTo(newPicture) > 0) {
					if(i==0) {
						
						pictureList.add(0,newPicture);
						//select User
						listViewImg.setItems(pictureList);
						listViewImg.getSelectionModel().select(0);
						whatInfo(pictureList);
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
					else if(i>=pictureList.size()) {
						
						pictureList.add(newPicture);
						//select User
						listViewImg.setItems(pictureList);
						listViewImg.getSelectionModel().select(pictureList.size()-1);
						whatInfo(pictureList);
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
					else {
						
						pictureList.add(i, newPicture);
						//select User
						listViewImg.setItems(pictureList);
						listViewImg.getSelectionModel().select(i);
						whatInfo(pictureList);
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
				}
				else if(pictureList.get(i).compareTo(newPicture) < 0) {
					continue;
				}
			}
			
			pictureList.add(newPicture);
			//select User
			listViewImg.setItems(pictureList);
			listViewImg.getSelectionModel().select(pictureList.size()-1);
			whatInfo(pictureList);
			
			thisAlbum.addPicture(newPicture);
			
			return;
		}
		
		return;
	}
	
	public void deletePic(ObservableList<Picture> pictureList, Album thisAlbum){
		
		//delete current Picture
		int currIndex = listViewImg.getSelectionModel().getSelectedIndex();
		thisAlbum.removePicture(listViewImg.getSelectionModel().getSelectedItem().getPictureName());
		pictureList.remove(currIndex);
		listViewImg.setItems(pictureList);
		
		//select next User in List or clear text fields if list is empty
		if(!pictureList.isEmpty()) {
			if(pictureList.size() <= currIndex) {
				listViewImg.getSelectionModel().select(currIndex-1);
				whatInfo(pictureList);
			}
			else {
				listViewImg.getSelectionModel().select(currIndex);
				whatInfo(pictureList);
			}
		}
		else {
			//Clear all info
			showCaption.clear();
			showDate.clear();
			selectedImage.setImage(null);
			tagList.clear();
		}
		
		return;
	}
	
	public void deleteTag(Picture thisPicture){
		
		//delete current Picture
		int currIndex = listViewTag.getSelectionModel().getSelectedIndex();
		thisPicture.removeTag(listViewTag.getSelectionModel().getSelectedItem().getName(), listViewTag.getSelectionModel().getSelectedItem().getValue());
		tagList.remove(currIndex);
		listViewTag.setItems(tagList);
		
		//select next User in List or clear text fields if list is empty
		if(!tagList.isEmpty()) {
			if(tagList.size() <= currIndex)
				listViewTag.getSelectionModel().select(currIndex-1);
			else
				listViewTag.getSelectionModel().select(currIndex);
		}
		else
			tagList.clear();
		
		return;
	}
	
	public void editCap(ObservableList<Picture> pictureList) {
		Picture currPic = pictureList.get((listViewImg.getSelectionModel().getSelectedIndex()));
		
		String cap = showCaption.getText();
		String currCap = currPic.getCaption();
		
		if(cap.equals(currCap)) {
			popUpMessage(primaryStage,"The information for "+currPic.getPictureName()+"'s caption has not been edited!");
			return;
		}
		
		currPic.setCaption(cap);
	}
	
	public boolean inListPic(Picture search, Stage primaryStage, ObservableList<Picture> pictureList){
		if(pictureList.isEmpty())
			return false;
		for(int i=0; i<pictureList.size(); i++) {
			if(pictureList.get(i).compareTo(search) == 0) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
	}
	
	//method for warning signature
	public void popUpMessage(Stage primaryStage, String displayText) {
		Alert warning = new Alert(AlertType.WARNING);
		warning.initOwner(primaryStage);
		warning.setTitle("We ran into an issue...");
		warning.setHeaderText(displayText);
		warning.showAndWait();
	}

	//method to display Image values
	public void whatInfo(ObservableList<Picture> photoList) {
		
		int currentIndex = listViewImg.getSelectionModel().getSelectedIndex();
		Picture currImage = photoList.get(currentIndex);
		
		Image toShow = currImage.getPicture().getImage();
		selectedImage.setImage(toShow);
		
		showCaption.setText(currImage.getCaption());
		
		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		Date thisDate = currImage.getDate().getTime();
		String displayDate = df.format(thisDate);
		showDate.setText(displayDate);
		
		tagList = FXCollections.observableArrayList(currImage.getTagList());
		
		if(!tagList.isEmpty()) {
			listViewTag.setItems(tagList);
			listViewTag.getSelectionModel().select(0);
		}
		else
			listViewTag.setItems(tagList);
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
