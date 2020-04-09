package controller;

import java.util.ArrayList;

import javafx.stage.Stage;
import model.User;

public class UserController {
	
	public Stage primaryStage;

	public void start(Stage primaryStage, User currUser, ArrayList<User> userList) {
		
		this.primaryStage = primaryStage;
		
	}
}
