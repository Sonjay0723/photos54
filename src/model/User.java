/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class User implements Serializable{
	
	/**
	 * SerialID for Tag Class
	 * username
	 * Arraylist of Albums for the user
	 * ArrayList of tag Types for the User
	 */
	private static final long serialVersionUID = 2271877744395820871L;
	public String username;
	public ArrayList<Album> albumList = new ArrayList<Album>();
	public ArrayList<TagType> tagTypes = new ArrayList<TagType>();
	
	/**
	 * Initialize User
	 * 
	 * @param username Username of the user
	 */
	public User(String username) {
		this.username = username;
	}
	
	/**
	 * Get the username of the user
	 * @return the username of the user
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set the username of the user
	 * @param x String to set username to
	 */
	public void setUsername(String x) {
		this.username = x;
	}
	
	/**
	 * Get the album list of the user
	 * 
	 * @return and ArrayList of albums that the user has
	 */
	public ArrayList<Album> getAlbumList(){
		return this.albumList;
	}
	
	/**
	 * Get the tag types of the user
	 * 
	 * @return and ArrayList of tag types for the user
	 */
	public ArrayList<TagType> getTagTypes(){
		return this.tagTypes;
	}
	
	/**
	 * Add a tag type to the list of tag types for the user
	 * 
	 * @param tagName the tag type to add to the list
	 */
	public void addTagType(TagType tagName) {
		for(int i=0; i<tagTypes.size(); i++) {
			if(tagTypes.get(i).getTagName().equals(tagName.getTagName()))
				return;
		}
		tagTypes.add(tagName);
		Comparator<TagType> comparator = Comparator.comparing(TagType::getTagName);
		tagTypes.sort(comparator);
	}
	
	/**
	 * Add a new album to the album list for the user
	 * 
	 * @param newAlbum the new album to add
	 */
	public void addAlbum(Album newAlbum) {
		albumList.add(newAlbum);
		Comparator<Album> comparator = Comparator.comparing(Album::getTitle);
		albumList.sort(comparator);
	}
	
	/**
	 * Remove an album from the album list
	 * 
	 * @param name Title of the album to remove from the album list
	 */
	public void removeAlbum(String name) {
		int position = 0;
		for(int i=0; i<albumList.size(); i++) {
			if(name.toLowerCase().equals(albumList.get(i).getTitle().toLowerCase())) {
				position = i;
				break;
			}
		}
		albumList.remove(position);
	}
	
	/**
	 * Get the specific album
	 * 
	 * @param title title of the album to get
	 * 
	 * @return the specified album from the album list
	 */
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
	
	/**
	 * Override equals for user
	 * does it based on username
	 */
	@Override
	public boolean equals(Object other) {
		if(other==null || !(other instanceof User))
			   return false;

		User curr =(User) other;
		return curr.getUsername().toLowerCase().equals(username.toLowerCase());
	}
	
	/**
	 * Override hashcode of User
	 */
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	/**
	 * CompareTo for User 
	 * 
	 * @param currUser User to compare to
	 * 
	 * @return true if usernames are equal, false otherwise
	 */
	public int compareTo(User currUser) {
		return this.getUsername().toLowerCase().compareTo(currUser.getUsername().toLowerCase());
	}
	
	/**
	 * toString method for user so that user display is legible
	 * Has username
	 */
	public String toString() {
		return this.getUsername();
	}
}
