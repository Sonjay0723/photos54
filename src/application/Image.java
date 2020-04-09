package application;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Image implements Serializable {
	public SerializableImage image;
	public Calendar date;
	public String caption;
	public String imageName;
	public ObservableList<Tag> tagList = FXCollections.observableArrayList();
	
	public Image(SerializableImage image, Calendar date, String caption, String imageName) {
		this.image = image;
		this.date = date;
		this.caption = caption;
		this.imageName = imageName;
	}
	
	public SerializableImage getImage() {
		return image;
	}
	
	public Calendar getDate() {
		return date;
	}
	
	public void setDate(Calendar x) {
		this.date = x;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String x) {
		this.caption = x;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String x) {
		this.imageName = x;
	}
	
	public ObservableList<Tag> getTagList(){
		return tagList;
	}
	
	public void addTag(String name, String value) {
		Tag newTag = new Tag(name,value);
		tagList.add(newTag);
		Comparator<Tag> comparator = Comparator.comparing(Tag::toString);
		FXCollections.sort(tagList, comparator);
	}
	
	public void removeTag(Tag thisTag) {
		int position = 0;
		for(int i=0; i<tagList.size(); i++) {
			if(thisTag.equals(tagList.get(i))) {
				position = i;
				break;
			}
		}
		tagList.remove(position);
	}
	
	public boolean equals(Image x) {
		return this.imageName.toLowerCase().equals(x.imageName.toLowerCase());
	}
}
