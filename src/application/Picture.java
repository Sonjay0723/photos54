package application;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Picture implements Serializable {
	public SerializableImage picture;
	public Calendar date;
	public String caption;
	public String pictureName;
	public ObservableList<Tag> tagList = FXCollections.observableArrayList();
	
	public Picture(SerializableImage picture, Calendar date, String caption, String pictureName) {
		this.picture = picture;
		this.date = date;
		this.caption = caption;
		this.pictureName = pictureName;
		
		this.date.set(Calendar.MILLISECOND, 0);
	}
	
	public SerializableImage getPicture() {
		return picture;
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
	
	public String getPictureName() {
		return pictureName;
	}
	
	public void setPictureName(String x) {
		this.pictureName = x;
	}
	
	public ObservableList<Tag> getTagList(){
		return tagList;
	}
	
	public void addTag(Tag newTag) {
		tagList.add(newTag);
		Comparator<Tag> comparator = Comparator.comparing(Tag::toString);
		FXCollections.sort(tagList, comparator);
		//TODO add to file
	}
	
	public void removeTag(String name, String value) {
		Tag thisTag = new Tag(name,value);
		int position = 0;
		for(int i=0; i<tagList.size(); i++) {
			if(thisTag.equals(tagList.get(i))) {
				position = i;
				break;
			}
		}
		tagList.remove(position);
		//TODO remove from file
	}
	
	public boolean equals(Picture x) {
		return this.pictureName.toLowerCase().equals(x.pictureName.toLowerCase());
	}
}
