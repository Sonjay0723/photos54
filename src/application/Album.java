package application;

import java.util.Calendar;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Album {
	
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
	
	public int compareTo(Album currAlbum) {
		return this.getTitle().toLowerCase().compareTo(currAlbum.getTitle().toLowerCase());
	}
}
