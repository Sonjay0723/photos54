/**
 /**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Album;
import model.Picture;
import model.SerializableImage;
import model.Tag;
import model.TagType;
import model.User;

public class AlbumController {
	
	public Stage primaryStage;

	@FXML private Label albumTitle;
	
	@FXML private Button toSlideShow;
	@FXML private Button logout;
	@FXML private Button toUserPage;
	@FXML private Button addPicture;
	@FXML private Button deletePicture;
	@FXML private Button editCaption;
	@FXML private Button addTag;
	@FXML private Button deleteTag;
	@FXML private Button moveToAlbum;
	@FXML private Button copyToAlbum;
	
	@FXML private ListView<Picture> listViewImg;
	@FXML private ListView<Tag> listViewTag;
	
	@FXML private ImageView selectedImage;
	
	@FXML private TextField showCaption;
	@FXML private TextField showDate;
	
	@FXML private Button slideshowNext;
	@FXML private Button slideshowPrevious;
	@FXML private ImageView slideshowImage;
	
	@FXML private Button btnMove;
	@FXML private Button btnAdd;
	@FXML private ComboBox<String> albumChoice;
	
	ObservableList<Tag> tagList;
	public int currSlideShowImage = 0;
	
	/**
	 * All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, logging out, etc.) 
	 * The list of pictures for the current album is initialized
	 * 
	 * @param primaryStage The current stage
	 * @param currUser The current User
	 * @param userList The list of all user
	 * @param currAlbum The current Album for the Current User
	 * @param index Index of the Album's picture to be selected
	 */
	public void start(Stage primaryStage, User currUser, ArrayList<User> userList, Album currAlbum, int index) {
		
		this.primaryStage = primaryStage;
		
		albumTitle.setText(currAlbum.getTitle());
		
		ObservableList<Picture> pictureList = FXCollections.observableArrayList(currAlbum.getPictureList());
		
		listViewImg
        .getSelectionModel()
        .selectedIndexProperty()
        .addListener((obs, oldVal, newVal) -> whatInfo(pictureList));
		
		if(!pictureList.isEmpty()) {
			
			//TODO listView of thumbnails
			
			/*listViewImg.setCellFactory(new Callback<ListView<Picture>, ListCell<Picture>>() {
				@Override
				public ListCell<Picture> call(ListView<Picture> photoList) {
					return new PhotoCell();
				}
			});*/
			
			listViewImg.setItems(pictureList);
			listViewImg.setCellFactory(param -> new ListCell<Picture>() {
				ImageView imagePic = new ImageView();
				@Override
				public void updateItem(Picture pic, boolean empty) {
	                super.updateItem(pic, empty);
	                if (empty) {
	                    setText(null);
	                    setGraphic(null);
	                } else {
	                	imagePic.setImage(pic.getPicture().getImage());
	                	imagePic.setPreserveRatio(true);
	                	imagePic.setFitHeight(60);
	                    setText(pic.getCaption());
	                    setGraphic(imagePic);
	                }
				}
			});
			listViewImg.getSelectionModel().select(index);
			whatInfo(pictureList);
			
			refreshSlideShow(pictureList);
		}
		
		//add initial tag types
		currUser.addTagType(new TagType("location", false));
		currUser.addTagType(new TagType("person", true));
		currUser.addTagType(new TagType("event", false));
		
		for (int i = 0; i < currUser.getAlbumList().size(); i++) {
			if (!currAlbum.equals(currUser.getAlbumList().get(i)))
				albumChoice.getItems().add(currUser.getAlbumList().get(i).getTitle());
		}
		
		//set up when user is allowed to delete a picture/tag/move to an album/ add to an album
		deletePicture.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull());
		addTag.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull());
		albumChoice.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(Bindings.size(FXCollections.observableArrayList(currUser.getAlbumList())).isEqualTo(1)));
		btnMove.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(albumChoice.getSelectionModel().selectedItemProperty().isNull()));
		btnAdd.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(albumChoice.getSelectionModel().selectedItemProperty().isNull()));
		deleteTag.disableProperty().bind(listViewTag.getSelectionModel().selectedItemProperty().isNull());
		slideshowNext.disableProperty().bind(Bindings.size(pictureList).isEqualTo(0).or(Bindings.size(pictureList).isEqualTo(1)));
		slideshowPrevious.disableProperty().bind(Bindings.size(pictureList).isEqualTo(0).or(Bindings.size(pictureList).isEqualTo(1)));
		if(!pictureList.isEmpty()) {
			BooleanBinding hasChanged = Bindings.equal(showCaption.textProperty(), listViewImg.getSelectionModel().getSelectedItem().getCaption());
			editCaption.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(hasChanged));
		}
		else
			editCaption.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull());
		
		//add a picture to the album from the User's files
		addPicture.setOnAction(event->{
			
			//User chooses appropriate Image file
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(
					new ExtensionFilter("Image Files", "*.bmp", "*.BMP", "*.gif", "*.GIF", "*.jpg", "*.JPG", "*.png",
							"*.PNG"),
					new ExtensionFilter("Bitmap Files", "*.bmp", "*.BMP"),
					new ExtensionFilter("GIF Files", "*.gif", "*.GIF"), new ExtensionFilter("JPEG Files", "*.jpg", "*.JPG"),
					new ExtensionFilter("PNG Files", "*.png", "*.PNG"));
			File tmp = fc.showOpenDialog(null);
			
			//If user chose a picture, add it to the User's current Album's list of pictures
			if(tmp!=null) {
				Image img = new Image(tmp.toURI().toString());
				SerializableImage thisPic = new SerializableImage(img);
				String name = tmp.getName();
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(tmp.lastModified());
				
				Picture newPic = new Picture(thisPic, date, "", name);
				addPic(newPic, primaryStage, pictureList, currAlbum, true);
				saveData(userList);
				refreshSlideShow(pictureList);
			}
			//otherwise, display values of currently selected picture
			else
				whatInfo(pictureList);
		});
		
		//Delete the selected picture from the picture list
		deletePicture.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to remove "+listViewImg.getSelectionModel().getSelectedItem().getPictureName()+" from the list?")){
				deletePic(pictureList, currAlbum);
				saveData(userList);
				refreshSlideShow(pictureList);
				//update binding based on what is selected
				if(!pictureList.isEmpty()) {
					BooleanBinding hasChanged = Bindings.equal(showCaption.textProperty(), listViewImg.getSelectionModel().getSelectedItem().getCaption());
					editCaption.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(hasChanged));
				}
			}
			else
				whatInfo(pictureList);
		});
		
		//delete the selected tag of the selected picture
		deleteTag.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to remove "+listViewTag.getSelectionModel().getSelectedItem().toString()+" from the list?")){
				deleteTag(listViewImg.getSelectionModel().getSelectedItem());
				saveData(userList);
			}
		});
		
		//edit the caption of the selected picture
		editCaption.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like recaption "+listViewImg.getSelectionModel().getSelectedItem().getPictureName())){
				editCap(pictureList);
				saveData(userList);
				//update binding based on whatever is selected
				BooleanBinding hasChanged = Bindings.equal(showCaption.textProperty(), listViewImg.getSelectionModel().getSelectedItem().getCaption());
				editCaption.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(hasChanged));
			}
			else
				whatInfo(pictureList);
		});
		
		//add a tag to the selected picture from another scene
		addTag.setOnAction(event->{
			if(agreeOrDisagree(primaryStage, "Would you like to add tag(s) for "+listViewImg.getSelectionModel().getSelectedItem().getPictureName()+"?")) {
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
		
		//Go to the next picture in the picture list
		slideshowNext.setOnAction(event->{
			if(currSlideShowImage+1 >= pictureList.size()) 
				currSlideShowImage = 0;
			else
				currSlideShowImage++;
			
			Picture currImage = pictureList.get(currSlideShowImage);
			
			Image toShow = currImage.getPicture().getImage();
			slideshowImage.setImage(toShow);
			
			listViewImg.getSelectionModel().select(currSlideShowImage);
			whatInfo(pictureList);
			
		});
		
		//go to the previous picture in the picture list
		slideshowPrevious.setOnAction(event->{
			if(currSlideShowImage-1 <0) 
				currSlideShowImage = pictureList.size()-1;
			else
				currSlideShowImage--;
			
			Picture currImage = pictureList.get(currSlideShowImage);
			
			Image toShow = currImage.getPicture().getImage();
			slideshowImage.setImage(toShow);
			
			listViewImg.getSelectionModel().select(currSlideShowImage);
			whatInfo(pictureList);
			
		});
		
		//go back to the user's page with the list of albums and other info
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
		
		//move picture to a new album
		btnMove.setOnAction(event->{
			if(pictureList.isEmpty()) 
				popUpMessage(primaryStage, "There is no picture to select!");
			else if (agreeOrDisagree(primaryStage, "Would you like to move " + listViewImg.getSelectionModel().getSelectedItem().getPictureName() 
					+ " to " + albumChoice.getValue() + "?")) {
				Album chosenAlbum = currUser.getAlbum(albumChoice.getValue());
				Picture chosenPic = listViewImg.getSelectionModel().getSelectedItem();
				
				deletePic(pictureList, currAlbum);
				addPic(chosenPic, primaryStage, FXCollections.observableArrayList(chosenAlbum.getPictureList()), chosenAlbum, false);
				saveData(userList);
				refreshSlideShow(pictureList);
				albumChoice.setValue(null);
			}
		});
		
		//copy picture to a new album
		btnAdd.setOnAction(event->{
			if (agreeOrDisagree(primaryStage, "Would you like to add " + listViewImg.getSelectionModel().getSelectedItem().getPictureName() 
					+ " to " + albumChoice.getValue() + "?")) {
				Album chosenAlbum = currUser.getAlbum(albumChoice.getValue());
				Picture chosenPic = listViewImg.getSelectionModel().getSelectedItem();
				
				addPic(chosenPic, primaryStage, FXCollections.observableArrayList(chosenAlbum.getPictureList()), chosenAlbum, false);
				saveData(userList);
			}
		});
		
		//logout to login page
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
	
	/**
	 * Add a picture to the Picture list for the current album for the current user
	 * 
	 * @param newPicture Picture to add to picture list
	 * @param primaryStage the current stage
	 * @param pictureList The picture list to add a picture to
	 * @param thisAlbum the current album
	 * @param forThisAlbum tells if the move to album/copy to album is being done
	 */
	public void addPic(Picture newPicture, Stage primaryStage, ObservableList<Picture> pictureList, Album thisAlbum, boolean forThisAlbum){
		
		if(pictureList.isEmpty()) {
			
			pictureList.add(newPicture);
		
			if(forThisAlbum) {
				listViewImg.setItems(pictureList);
				listViewImg.getSelectionModel().select(0);
				whatInfo(pictureList);
			}
			
			thisAlbum.addPicture(newPicture);
			
			return;
		}
		
		//adding in appropriate location
		else if (!inListPic(newPicture, primaryStage, pictureList)) {
			for(int i=0; i<pictureList.size(); i++) {
				if(pictureList.get(i).compareTo(newPicture) > 0) {
					if(i==0) {
						
						pictureList.add(0,newPicture);
						
						if(forThisAlbum) {
							listViewImg.setItems(pictureList);
							listViewImg.setCellFactory(param -> new ListCell<Picture>() {
								ImageView imagePic = new ImageView();
								@Override
								public void updateItem(Picture pic, boolean empty) {
					                super.updateItem(pic, empty);
					                if (empty) {
					                    setText(null);
					                    setGraphic(null);
					                } else {
					                	imagePic.setImage(pic.getPicture().getImage());
					                	imagePic.setPreserveRatio(true);
					                	imagePic.setFitHeight(60);
					                    setText(pic.getCaption());
					                    setGraphic(imagePic);
					                }
								}
							});
							listViewImg.getSelectionModel().select(0);
							whatInfo(pictureList);
						}
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
					else if(i>=pictureList.size()) {
						
						pictureList.add(newPicture);

						if(forThisAlbum) {
							listViewImg.setItems(pictureList);
							listViewImg.setCellFactory(param -> new ListCell<Picture>() {
								ImageView imagePic = new ImageView();
								@Override
								public void updateItem(Picture pic, boolean empty) {
					                super.updateItem(pic, empty);
					                if (empty) {
					                    setText(null);
					                    setGraphic(null);
					                } else {
					                	imagePic.setImage(pic.getPicture().getImage());
					                	imagePic.setPreserveRatio(true);
					                	imagePic.setFitHeight(60);
					                    setText(pic.getCaption());
					                    setGraphic(imagePic);
					                }
								}
							});
							listViewImg.getSelectionModel().select(pictureList.size()-1);
							whatInfo(pictureList);
						}
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
					else {
						
						pictureList.add(i, newPicture);
						
						if(forThisAlbum) {
							listViewImg.setItems(pictureList);
							listViewImg.setCellFactory(param -> new ListCell<Picture>() {
								ImageView imagePic = new ImageView();
								@Override
								public void updateItem(Picture pic, boolean empty) {
					                super.updateItem(pic, empty);
					                if (empty) {
					                    setText(null);
					                    setGraphic(null);
					                } else {
					                	imagePic.setImage(pic.getPicture().getImage());
					                	imagePic.setPreserveRatio(true);
					                	imagePic.setFitHeight(60);
					                    setText(pic.getCaption());
					                    setGraphic(imagePic);
					                }
								}
							});
							listViewImg.getSelectionModel().select(i);
							whatInfo(pictureList);
						}
						
						thisAlbum.addPicture(newPicture);
						
						return;
					}
				}
				else if(pictureList.get(i).compareTo(newPicture) < 0) {
					continue;
				}
			}
			
			pictureList.add(newPicture);
			
			if(forThisAlbum) {
				listViewImg.setItems(pictureList);
				listViewImg.setCellFactory(param -> new ListCell<Picture>() {
					ImageView imagePic = new ImageView();
					@Override
					public void updateItem(Picture pic, boolean empty) {
		                super.updateItem(pic, empty);
		                if (empty) {
		                    setText(null);
		                    setGraphic(null);
		                } else {
		                	imagePic.setImage(pic.getPicture().getImage());
		                	imagePic.setPreserveRatio(true);
		                	imagePic.setFitHeight(60);
		                    setText(pic.getCaption());
		                    setGraphic(imagePic);
		                }
					}
				});
				listViewImg.getSelectionModel().select(pictureList.size()-1);
				whatInfo(pictureList);
			}
			
			thisAlbum.addPicture(newPicture);
			
			return;
		}
		
		return;
	}
	
	/**
	 * Delete the current picture from the album for the current user
	 * 
	 * @param pictureList the picture list from where to delete the selected picture from
	 * @param thisAlbum the current album that the picture is in
	 */
	public void deletePic(ObservableList<Picture> pictureList, Album thisAlbum){
		
		//delete current Picture
		int currIndex = listViewImg.getSelectionModel().getSelectedIndex();
		thisAlbum.removePicture(listViewImg.getSelectionModel().getSelectedItem());
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
	
	/**
	 * Delete the selected tag of the current selected picture
	 * 
	 * @param thisPicture the current selected picture
	 */
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
	
	/**
	 * Method to Edit the caption of the current selected picture
	 * 
	 * @param pictureList the list of pictures which has the selected picture is from
	 */
	public void editCap(ObservableList<Picture> pictureList) {
		Picture currPic = pictureList.get((listViewImg.getSelectionModel().getSelectedIndex()));
		
		String cap = showCaption.getText();
		
		currPic.setCaption(cap);
	}
	
	/**
	 * Refresh the contents of the slideshow, done after add/delete/etc of any picture in the picture list
	 * 
	 * @param pictureList the updated picture list that contains the images to show in the slideshow
	 */
	public void refreshSlideShow(ObservableList<Picture> pictureList) {
		if(!pictureList.isEmpty()) {
			if(currSlideShowImage>=pictureList.size())
				currSlideShowImage = pictureList.size()-1;
			else if(currSlideShowImage<0)
				currSlideShowImage = 0;
			Picture currImage = pictureList.get(currSlideShowImage);
			Image toShow = currImage.getPicture().getImage();
			slideshowImage.setImage(toShow);
		}
		else
			slideshowImage.setImage(null);
	}
	
	/**
	 * Method to Check if the picture is already in the picture list
	 * 
	 * @param search Picture to search for
	 * @param primaryStage current stage
	 * @param pictureList the picture list to search in to see if the picture already exists
	 * 
	 * @return True if it exists in the list, false otherwise
	 */
	public boolean inListPic(Picture search, Stage primaryStage, ObservableList<Picture> pictureList){
		if(pictureList.isEmpty())
			return false;
		for(int i=0; i<pictureList.size(); i++) {
			if(pictureList.get(i).getPicture().equals(search.getPicture())) {
				popUpMessage(primaryStage, "This Entry Already Exists in the List!");
				return true;
			}
		}
		return false;
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
	 * method to display values depending on what is selected in appropriate location
	 * Image,Caption,Date,Tag List of current selected Picture
	 * 
	 * @param photoList the list from where items are selected
	 */
	public void whatInfo(ObservableList<Picture> photoList) {
		
		if(!photoList.isEmpty()) {
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
			
			BooleanBinding hasChanged = Bindings.equal(showCaption.textProperty(), listViewImg.getSelectionModel().getSelectedItem().getCaption());
			editCaption.disableProperty().bind(listViewImg.getSelectionModel().selectedItemProperty().isNull().or(hasChanged));
		}
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
