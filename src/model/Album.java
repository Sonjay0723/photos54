package model;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.io.Serializable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Album implements Serializable{
	
	private static final long serialVersionUID = 1891567810783724951L;
	public String title;
	public ObservableList<Picture> pictureList = FXCollections.observableArrayList();
	
	public Album(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String x) {
		this.title = x;
	}
	
	public ObservableList<Picture> getPictureList(){
		return pictureList;
	}
	
	public void addPicture(Picture newPicture) {
		//TODO add to files
		pictureList.add(newPicture);
		Comparator<Picture> comparator = Comparator.comparing(Picture::getPictureName);
		FXCollections.sort(pictureList, comparator);
	}
	
	public void removePicture(String thisPicture) {
		//TODO remove to files
		int position = 0;
		for(int i=0; i<pictureList.size(); i++) {
			if(thisPicture.equals(pictureList.get(i).getPictureName())) {
				position = i;
				break;
			}
		}
		pictureList.remove(position);
	}
	
	public Picture getPicture(String name) {
		Picture searchFor = null;
		for(int i=0; i<this.pictureList.size(); i++) {
			if(this.pictureList.get(i).getPictureName().equals(name)) {
				searchFor = this.pictureList.get(i);
				break;
			}
		}
		return searchFor;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other==null || !(other instanceof Album))
			   return false;

		Album curr =(Album) other;
		return curr.getTitle().toLowerCase().equals(title.toLowerCase());
	}
	
	@Override
	 public int hashCode() {
		 return Objects.hash(title);
	 }
	
	public int compareTo(Album currAlbum) {
		return this.getTitle().toLowerCase().compareTo(currAlbum.getTitle().toLowerCase());
	}
	
	public String toString() {
		return this.getTitle();
	}
}
