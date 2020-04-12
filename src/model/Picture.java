/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class Picture implements Serializable {
	/**
	 * SerialID for Picture Class
	 * SerializableImage
	 * Date of picture
	 * Caption of Picture
	 * Name of Picture
	 * ArrayList of tags for picture
	 */
	private static final long serialVersionUID = -6520437079111074791L;
	public SerializableImage picture;
	public Calendar date;
	public String caption;
	public String pictureName;
	public ArrayList<Tag> tagList = new ArrayList<Tag>();
	
	/**
	 * Initialize picture 
	 * 
	 * @param picture SerializableImage
	 * @param date Date of picture
	 * @param caption Caption of Picture
	 * @param pictureName Name of Picture
	 */
	public Picture(SerializableImage picture, Calendar date, String caption, String pictureName) {
		this.picture = picture;
		this.date = date;
		this.caption = caption;
		this.pictureName = pictureName;
	}
	
	/**
	 * Get the SerializableImage
	 * 
	 * @return the SerializableImage
	 */
	public SerializableImage getPicture() {
		return picture;
	}
	
	/**
	 * Get the date
	 * 
	 * @return the date of the picture
	 */
	public Calendar getDate() {
		return date;
	}
	
	/**
	 * set date of the picture
	 * 
	 * @param x date to set picture date to
	 */
	public void setDate(Calendar x) {
		this.date = x;
	}
	
	/**
	 * Get the caption of the picture
	 * 
	 * @return the caption of the picture
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Set the caption of the picture
	 * 
	 * @param x caption to set picture caption to
	 */
	public void setCaption(String x) {
		this.caption = x;
	}
	
	/**
	 * Get the name of the picture
	 * 
	 * @return the name of the picture
	 */
	public String getPictureName() {
		return pictureName;
	}
	
	/**
	 * Set the name of the picture
	 * 
	 * @param x the name to set the picture name to
	 */
	public void setPictureName(String x) {
		this.pictureName = x;
	}
	
	/**
	 * Get the tag list of the picture
	 * 
	 * @return the array list of tags for the picture
	 */
	public ArrayList<Tag> getTagList(){
		return tagList;
	}
	
	/**
	 * Method to Add a tag to the picture's tag list
	 * 
	 * @param newTag the tag to add to the picture's tag list
	 */
	public void addTag(Tag newTag) {
		for (int i = 0; i < tagList.size(); i++) {
			if (newTag.getName().equals(tagList.get(i).getName()) && !tagList.get(i).getMulti())
				return;
			
			if (newTag.equals(tagList.get(i)))
				return;
		}
		
		tagList.add(newTag);
		Comparator<Tag> comparator = Comparator.comparing(Tag::toString);
		tagList.sort(comparator);
	}
	
	/**
	 * Method to remove a tag from the picture's tag list
	 * 
	 * @param name name of the tag to remove
	 * @param value value of the tag to remove
	 */
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
	
	/**
	 * equals method for picture
	 * 
	 * @param x Picture to see if equal
	 * 
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Picture x) {
		return this.pictureName.toLowerCase().equals(x.pictureName.toLowerCase());
	}
	
	/**
	 * compareTo Method for picture
	 * 
	 * @param currPicture Picture to compare to 
	 * 
	 * @return 0 if equal, any number greater/less than 0 otherwise depending on order of picture name's
	 */
	public int compareTo(Picture currPicture) {
		return this.getPictureName().toLowerCase().compareTo(currPicture.getPictureName().toLowerCase());
	}
	
	/**
	 * toString method for picture so that picture display is legible
	 * Has picture name
	 */
	public String toString() {
		return this.pictureName;
	}
}
