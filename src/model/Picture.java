package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Picture implements Serializable {
	
	private static final long serialVersionUID = 6955723612371190680L;
	
	public SerializableImage picture;
	public Calendar date;
	public String caption;
	public String pictureName;
	public ArrayList<Tag> tagList = new ArrayList<Tag>();
	
	public Picture(SerializableImage picture, Calendar date, String caption, String pictureName) {
		this.picture = picture;
		this.date = date;
		this.caption = caption;
		this.pictureName = pictureName;
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
	
	public ArrayList<Tag> getTagList(){
		return tagList;
	}
	
	public void addTag(Tag newTag) {
		tagList.add(newTag);
		Comparator<Tag> comparator = Comparator.comparing(Tag::toString);
		tagList.sort(comparator);
	}
	
	public void removeTag(String name, String value) {
		Tag thisTag = new Tag(name,value,false);
		int position = 0;
		for(int i=0; i<tagList.size(); i++) {
			if(thisTag.equals(tagList.get(i))) {
				position = i;
				break;
			}
		}
		tagList.remove(position);
	}
	
	public boolean equals(Picture x) {
		return this.pictureName.toLowerCase().equals(x.pictureName.toLowerCase());
	}
	
	public int compareTo(Picture currPicture) {
		return this.getPictureName().toLowerCase().compareTo(currPicture.getPictureName().toLowerCase());
	}
}
