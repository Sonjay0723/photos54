package controller;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.swing.ButtonGroup;

import javafx.fxml.*;
import model.*;
import view.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
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
	
	@FXML private ChoiceBox<String> choiceTag1;
	@FXML private ChoiceBox<String> choiceTag2;
	
	@FXML private DatePicker dateStart;
	@FXML private DatePicker dateEnd;	
	
	public Stage primaryStage;
	
	public void start(Stage primaryStage, User user, ArrayList<User> userList) {
		
		this.primaryStage = primaryStage;
		currUser = user;
		
		final ToggleGroup anotherTag = new ToggleGroup();
		anotherTag.getToggles().addAll(choiceAnd, choiceOr);
		
		for (int i = 0; i < user.getTagTypes().size(); i++) {
			choiceTag1.getItems().add(user.getTagTypes().get(i).getTagName());
			choiceTag2.getItems().add(user.getTagTypes().get(i).getTagName());
		}
		
		choiceTag2.setDisable(true);
		txtTag2.setDisable(true);
		
		btnSearch.setOnAction(event->{
			if ((choiceAnd.isSelected() || choiceOr.isSelected()) && (txtTag2.getText()=="")) {
				popUpMessage(primaryStage, "Please add a second tag");
			}
			else if ((dateStart.getValue()==null && dateEnd.getValue()==null) && txtTag1.getText()=="") {
				popUpMessage(primaryStage, "Please select a date range or a tag");
			}
			else {
				search();
				imageView.setItems(imageList);
				/*
				imageView.setCellFactory(param->new ListCell<Picture>() {
					
				});*/
			}
		});
		
		btnCreate.setOnAction(event->{
			if (txtAlbum.getText() == "")
				popUpMessage(primaryStage, "Please add an album name");
			else if (imageList.isEmpty())
				popUpMessage(primaryStage, "Please select images");
			else {
				if (agreeOrDisagree(primaryStage, "Would you like to make the selected songs an album?")) {
					createAlbum();
					saveData(userList);
				}
			}
		});
		
		btnClose.setOnAction(event->{
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
	}
	
	@FXML
	private void handleButtonAction(ActionEvent e) {
		if (choiceAnd.isSelected() || choiceOr.isSelected()) {
			choiceTag2.setDisable(false);
			txtTag2.setDisable(false);
		}
		else {
			choiceTag2.setDisable(true);
			txtTag2.setDisable(true);
		}
	}
	
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
				Tag tag1 = new Tag(choiceTag1.getValue(), txtTag1.getText(), false);
				Tag tag2 = txtTag2.getText().length() == 0 ? null : new Tag(choiceTag2.getValue(), txtTag2.getText(), false);
				
				//checks if a pic is within the date range if provided, no tags
				if (tag1.getValue().length() == 0) {
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
	
	private void createAlbum() {
		String albumName = txtAlbum.getText();
		
		List<Album> userAlbums = currUser.getAlbumList();
		for (int i = 0; i < userAlbums.size(); i++) {
			if (albumName.equals(userAlbums.get(i).getTitle())) {
				popUpMessage(primaryStage, "You already have an album with the same name");
				return;
			}
		}
		
		Album res = new Album(albumName);
		for (int i = 0; i < imageList.size(); i++)
			res.addPicture(imageList.get(i));
		
		currUser.addAlbum(res);
	}
	
	private void checkDate(Picture pic, LocalDate start, LocalDate end) {
		LocalDate day = LocalDateTime.ofInstant(pic.getDate().toInstant(), pic.getDate().getTimeZone().toZoneId()).toLocalDate();
		
		if ((start != null && !day.isBefore(start) && (end != null) && !day.isAfter(end)))
			imageList.add(pic);
		else if ((start == null && (end != null) && !day.isAfter(end)))
			imageList.add(pic);
		else if ((end == null && (start != null) && !day.isBefore(start)))
			imageList.add(pic);
		else if (start == null && end == null)
			imageList.add(pic);
	}
	
	private void popUpMessage(Stage primaryStage, String displayText) {
		Alert warning = new Alert(AlertType.WARNING);
		warning.initOwner(primaryStage);
		warning.setTitle("We ran into an issue...");
		warning.setHeaderText(displayText);
		warning.showAndWait();
	}
	
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
