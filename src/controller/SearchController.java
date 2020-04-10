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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SearchController {
	
	private ObservableList<Picture> imageList = FXCollections.observableArrayList();
	
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
	
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		
		final ToggleGroup anotherTag = new ToggleGroup();
		anotherTag.getToggles().addAll(choiceAnd, choiceOr);
		
		choiceTag2.setDisable(true);
		txtTag2.setDisable(true);
		
		btnSearch.setOnAction(event->{
			if (choiceAnd.isSelected() || choiceOr.isSelected()) {
				if (txtTag2.getText()=="")
					popUpMessage(primaryStage, "Please add a second tag");
				else if ((dateStart.getValue()==null && dateEnd.getValue()==null) && txtTag1.getText()=="")
					popUpMessage(primaryStage, "Please select a date range or a tag");
				else {
					search();
					imageView.setItems(imageList);
				}
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
		
		User currUser = new User("temp");
		//REPLACE WITH CODE TO GET PROPER USER!!!!!!!!!!!!!!!!!!!!
		
		List<Album> userAlbums = currUser.getAlbumList();
		int albums = userAlbums.size();
		
		for (int i = 0; i < albums; i++) {
			List<Picture> pics = userAlbums.get(i).getPictureList();
			
			for (int j = 0; j < pics.size(); j++) {
				Picture curr = pics.get(j);
				LocalDate start = dateStart.getValue();
				LocalDate end = dateEnd.getValue();
				Tag tag1 = new Tag(choiceTag1.getValue(), txtTag1.getText());
				Tag tag2 = txtTag2.getText().length() == 0 ? null : new Tag(choiceTag2.getValue(), txtTag2.getText());
				
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
}
