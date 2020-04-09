package application;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class User {
	public String username;
	public ObservableList<Album> albumList = FXCollections.observableArrayList();
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String x) {
		this.username = x;
	}
	
	public ObservableList<Album> getAlbumList(){
		return albumList;
	}
	
	public void addAlbum(String name) {
		Album newAlbum = new Album(name);
		albumList.add(newAlbum);
		Comparator<Album> comparator = Comparator.comparing(Album::getTitle);
		FXCollections.sort(albumList, comparator);
	}
	
	public void removeTag(Album thisAlbum) {
		int position = 0;
		for(int i=0; i<albumList.size(); i++) {
			if(thisAlbum.equals(albumList.get(i))) {
				position = i;
				break;
			}
		}
		albumList.remove(position);
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
	
	public int compareTo(User currUser) {
		return this.getUsername().toLowerCase().compareTo(currUser.getUsername().toLowerCase());
	}
}
