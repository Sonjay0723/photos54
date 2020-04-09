package application;

import java.util.Calendar;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Album {
	
	public String title;
	public ObservableList<Image> imageList = FXCollections.observableArrayList();
	
	public Album(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String x) {
		this.title = x;
	}
	
	public ObservableList<Image> getImageList(){
		return imageList;
	}
	
	public void addImage(SerializableImage image, Calendar date, String caption, String imageName) {
		Image newImage = new Image(image, date, caption, imageName);
		imageList.add(newImage);
	}
	
	public void removeImage(Image thisImage) {
		int position = 0;
		for(int i=0; i<imageList.size(); i++) {
			if(thisImage.equals(imageList.get(i))) {
				position = i;
				break;
			}
		}
		imageList.remove(position);
	}
	
	public Image getImage(String name) {
		Image searchFor = null;
		for(int i=0; i<this.imageList.size(); i++) {
			if(this.imageList.get(i).getImageName().equals(name)) {
				searchFor = this.imageList.get(i);
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
