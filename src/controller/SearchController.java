/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package controller;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import model.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SearchController {
	
	private ObservableList<Picture> imageList = FXCollections.observableArrayList();
	private User currUser;
	
	@FXML ListView<Picture> imageView;
	
	@FXML private Button btnSearch;
	@FXML private Button btnCreate;
	@FXML private Button btnClose;
	
	@FXML private TextField txtTag1;
	@FXML private TextField txtTag2;
	@FXML private TextField txtAlbum;
	
	@FXML private RadioButton choiceAnd;
	@FXML private RadioButton choiceOr;
	
	@FXML private ComboBox<String> choiceTag1;
	@FXML private ComboBox<String> choiceTag2;
	
	@FXML private DatePicker dateStart;
	@FXML private DatePicker dateEnd;	
	
	public Stage primaryStage;
	
	final ToggleGroup andOr = new ToggleGroup();
	
	/**
	+* All actions are handled for Buttons + FXML fields(When to disable/enable them, what to do when pressed, logging out, etc.) 
	* 
	* @param primaryStage current stage
	* @param user the current user
	* @param userList the list of users
	*/
	public void start(Stage primaryStage, User user, ArrayList<User> userList) {
		
		this.primaryStage = primaryStage;
		currUser = user;
		
		for (int i = 0; i < user.getTagTypes().size(); i++) {
			choiceTag1.getItems().add(user.getTagTypes().get(i).getTagName());
			choiceTag2.getItems().add(user.getTagTypes().get(i).getTagName());
		}
		
		//disable/enable Buttons and text fields
		choiceAnd.setToggleGroup(andOr);
		choiceOr.setToggleGroup(andOr);
		txtAlbum.disableProperty().bind(Bindings.size(imageList).isEqualTo(0));
		btnCreate.disableProperty().bind(Bindings.size(imageList).isEqualTo(0).or(Bindings.isEmpty(txtAlbum.textProperty())));
		txtTag1.disableProperty().bind(choiceTag1.getSelectionModel().selectedItemProperty().isNull());
		choiceAnd.disableProperty().bind(choiceTag1.getSelectionModel().selectedItemProperty().isNull().or(Bindings.isEmpty(txtTag1.textProperty())));
		choiceOr.disableProperty().bind(choiceTag1.getSelectionModel().selectedItemProperty().isNull().or(Bindings.isEmpty(txtTag1.textProperty())));
		choiceTag2.disableProperty().bind(choiceAnd.selectedProperty().not().and(choiceOr.selectedProperty().not()));
		txtTag2.disableProperty().bind(choiceTag2.getSelectionModel().selectedItemProperty().isNull());
		btnSearch.disableProperty().bind(
				(choiceTag1.getSelectionModel().selectedItemProperty().isNotNull().and(Bindings.isEmpty(txtTag1.textProperty())))
				.or(choiceTag2.getSelectionModel().selectedItemProperty().isNotNull().and(Bindings.isEmpty(txtTag2.textProperty())))
				//.or(dateStart.accessibleTextProperty().isNotNull().and(dateEnd.accessibleTextProperty().isNull()))
				//.or(dateEnd.accessibleTextProperty().isNotNull().and(dateStart.accessibleTextProperty().isNull()))
		);
		
		//search for pictures given date range and/or tag-value pair(s)
		btnSearch.setOnAction(event->{
			if ((choiceAnd.isSelected() || choiceOr.isSelected()) && (txtTag2.getText().length()==0)) {
				popUpMessage(primaryStage, "Please add a second tag");
			}
			else if ((dateStart.getValue()==null && dateEnd.getValue()==null) && txtTag1.getText().length()==0) {
				popUpMessage(primaryStage, "Please select a date range or a tag");
			}
			else {
				imageList.clear();
				search();
				imageView.setItems(imageList);
				
				imageView.setCellFactory(param -> new ListCell<Picture>() {
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
		                	imagePic.setFitHeight(100);
		                	String pattern = "MM/dd/yyyy HH:mm:ss";
		            		DateFormat df = new SimpleDateFormat(pattern);
		            		Date thisDate = pic.getDate().getTime();
		            		String imageDate = df.format(thisDate);
		                    setText("Caption: "+pic.getCaption()+"\nDate: "+imageDate);
		                    setGraphic(imagePic);
		                }
					}
				});
				
				choiceTag1.getSelectionModel().clearSelection();
				choiceTag2.getSelectionModel().clearSelection();
				txtTag1.clear();
				txtTag2.clear();
				choiceAnd.setSelected(false);
				choiceOr.setSelected(false);
				dateStart.setValue(null);
				dateEnd.setValue(null);
			}
		});
		
		//creating new album based on search results
		btnCreate.setOnAction(event->{
			if (agreeOrDisagree(primaryStage, "Would you like to make the selected songs an album?")) {
				createAlbum();
				saveData(userList);
				txtAlbum.clear();
			}
		});
		
		//going back to User page
		btnClose.setOnAction(event->{
			this.primaryStage.close();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource("/view/UserName.fxml"));
			try {
	            AnchorPane root = (AnchorPane)loader.load();
	            UserController userView = loader.getController();
	            Stage stage = new Stage();
	            
	            userView.start(stage, currUser, userList, 0);
	            Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	* Method to search for pictures by dates and/or tag-value pairs
	*/
	private void search() {
		imageList.removeAll();
		
		List<Album> userAlbums = currUser.getAlbumList();
		int albums = userAlbums.size();
		
		for (int i = 0; i < albums; i++) {
			List<Picture> pics = userAlbums.get(i).getPictureList();
			
			for (int j = 0; j < pics.size(); j++) {
				Picture curr = pics.get(j);
				LocalDate start = dateStart.getValue();
				LocalDate end = dateEnd.getValue();
				Tag tag1 = txtTag1.getText().length() == 0 ? null : new Tag(choiceTag1.getValue(), txtTag1.getText(), false);
				Tag tag2 = txtTag2.getText().length() == 0 ? null : new Tag(choiceTag2.getValue(), txtTag2.getText(), false);
				
				//checks if a pic is within the date range if provided, no tags
				if (tag1 == null) {
					checkDate(curr, start, end);
				}
				else if (tag2 == null) {
					List<Tag> tags = curr.getTagList();
					
					for (int k = 0; k < tags.size(); k++) {
						if (tag1.equals(tags.get(k))) {
							checkDate(curr, start, end);
							break;
						}
					}
				}
				else if (choiceAnd.isSelected()) {
					boolean firstTag = false;
					boolean secondTag = false;
					List<Tag> tags = curr.getTagList();
					
					for (int k = 0; k < tags.size(); k++) {
						if (tag1.equals(tags.get(k)))
							firstTag = true;
						
						if (tag2.equals(tags.get(k)))
							secondTag = true;
						
						if (firstTag && secondTag) {
							checkDate(curr, start, end);
							break;
						}
					}
				}
				else if (choiceOr.isSelected()) {
					List<Tag> tags = curr.getTagList();
					
					for (int k = 0; k < tags.size(); k++) {
						if (tag1.equals(tags.get(k)) || tag2.equals(tags.get(k))) {
							checkDate(curr, start, end);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method to create an album based on search results
	 */
	private void createAlbum() {
		String albumName = txtAlbum.getText();
		
		List<Album> userAlbums = currUser.getAlbumList();
		for (int i = 0; i < userAlbums.size(); i++) {
			if (albumName.toLowerCase().equals(userAlbums.get(i).getTitle().toLowerCase())) {
				popUpMessage(primaryStage, "You already have an album with the same name");
				return;
			}
		}
		
		Album res = new Album(albumName);
		for (int i = 0; i < imageList.size(); i++)
			res.addPicture(imageList.get(i));
		
		currUser.addAlbum(res);
	}
	
	/**
	* Method to check whether or not to add the picture based on whether or not it is within the specified date range
	* 
	* @param pic The picture that is being checked to see if it has the appropriate dates
	* @param start start date
	* @param end end date
	*/
	private void checkDate(Picture pic, LocalDate start, LocalDate end) {
		LocalDate day = LocalDateTime.ofInstant(pic.getDate().toInstant(), pic.getDate().getTimeZone().toZoneId()).toLocalDate();
		
		if ((start != null && !day.isBefore(start) && (end != null) && !day.isAfter(end))) {
			boolean isRepeat = false;
			for(int i=0; i< imageList.size(); i++) {
				if(imageList.get(i).getPicture().equals(pic.getPicture()))
					isRepeat = true;
			}
			if(!isRepeat)
				imageList.add(pic);
		}
		else if ((start == null && (end != null) && !day.isAfter(end))) {
			boolean isRepeat = false;
			for(int i=0; i< imageList.size(); i++) {
				if(imageList.get(i).getPicture().equals(pic.getPicture()))
					isRepeat = true;
			}
			if(!isRepeat)
				imageList.add(pic);
		}
		else if ((end == null && (start != null) && !day.isBefore(start))) {
			boolean isRepeat = false;
			for(int i=0; i< imageList.size(); i++) {
				if(imageList.get(i).getPicture().equals(pic.getPicture()))
					isRepeat = true;
			}
			if(!isRepeat)
				imageList.add(pic);
		}
		else if (start == null && end == null) {
			boolean isRepeat = false;
			for(int i=0; i< imageList.size(); i++) {
				if(imageList.get(i).getPicture().equals(pic.getPicture()))
					isRepeat = true;
			}
			if(!isRepeat)
				imageList.add(pic);
		}
	}
	
	/**
	 * method for warning signature
	 * 
	 * @param primaryStage current stage
	 * @param displayText Text to show in warning
	 */
	private void popUpMessage(Stage primaryStage, String displayText) {
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