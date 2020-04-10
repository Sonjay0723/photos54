package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Album implements Serializable {
	
	private static final long serialVersionUID = 1891567810783724951L;
	public String title;
	public ArrayList<Picture> pictureList = new ArrayList<Picture>();
	
	public Album(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String x) {
		this.title = x;
	}
	
	public ArrayList<Picture> getPictureList(){
		return pictureList;
	}
	
	public void setList(ArrayList<Picture> newPictureList) {
		this.pictureList = newPictureList;
	}
	
	public void addPicture(Picture newPicture) {
		pictureList.add(newPicture);
		Comparator<Picture> comparator = Comparator.comparing(Picture::getPictureName);
		pictureList.sort(comparator);
	}
	
	public void removePicture(String thisPicture) {
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
		String ret = this.getTitle();
		ret += "\n"+Integer.toString(this.pictureList.size())+" Photos";
		ret += "\nDate Range:\n";

		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		Date maxDate;
		Date minDate;
		String maxDateS = "_";
		String minDateS = "_";
		if(this.pictureList.size()>0) {
			Date thisDate = this.pictureList.get(0).getDate().getTime();
			maxDate = thisDate;
			minDate = thisDate;
			minDateS = df.format(thisDate);
			maxDateS = minDateS;
			for(int i=0; i<this.pictureList.size(); i++) {
				if(this.pictureList.get(i).getDate().getTime().compareTo(maxDate)>0) {
					maxDate = this.pictureList.get(i).getDate().getTime();
					maxDateS = df.format(maxDate);
				}
				else if(this.pictureList.get(i).getDate().getTime().compareTo(minDate)<0){
					minDate = this.pictureList.get(i).getDate().getTime();
					minDateS = df.format(minDate);
				}
			}
		}
		ret+=minDateS+" - "+maxDateS;
		
		return ret;
	}
}
