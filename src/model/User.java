package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class User implements Serializable{
	
	private static final long serialVersionUID = 8177923271139908648L;
	public String username;
	public ArrayList<Album> albumList = new ArrayList<Album>();
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String x) {
		this.username = x;
	}
	
	public ArrayList<Album> getAlbumList(){
		return this.albumList;
	}
	
	public void addAlbum(Album newAlbum) {
		albumList.add(newAlbum);
		Comparator<Album> comparator = Comparator.comparing(Album::getTitle);
		albumList.sort(comparator);
	}
	
	public void removeAlbum(String name) {
		int position = 0;
		for(int i=0; i<albumList.size(); i++) {
			if(name.toLowerCase().equals(albumList.get(i).getTitle().toLowerCase())) {
				position = i;
				break;
			}
		}
		albumList.remove(position);
		//TODO remove from files
	}
	
	public Album getAlbum(String title) {
		Album searchFor = null;
		for(int i=0; i<this.albumList.size(); i++) {
			if(this.albumList.get(i).getTitle().equals(title)) {
				searchFor = this.albumList.get(i);
				break;
			}
		}
		return searchFor;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other==null || !(other instanceof User))
			   return false;

		User curr =(User) other;
		return curr.getUsername().toLowerCase().equals(username.toLowerCase());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	public int compareTo(User currUser) {
		return this.getUsername().toLowerCase().compareTo(currUser.getUsername().toLowerCase());
	}
	
	public String toString() {
		return this.getUsername();
	}
}
