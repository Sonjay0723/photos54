/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package model;

import java.io.Serializable;

public class TagType implements Serializable{
	
	/**
	 * SerialID for Tag Class
	 * name of tag(tag type)
	 * if it can have multiple values or not
	 */
	private static final long serialVersionUID = -4258621636890253439L;
	String tagName;
	boolean multi;
	
	/**
	 * Initialize TagType
	 * 
	 * @param tagName name of tag(tag type)
	 * @param multi if it can have multiple values or not
	 */
	public TagType(String tagName, boolean multi) {
		this.tagName = tagName.toLowerCase();
		this.multi = multi;
	}
	
	/**
	 * Get the tag type of the tag
	 * 
	 * @return the tag type of the tag
	 */
	public String getTagName() {
		return tagName;
	}
	
	/**
	 * Get whether or not the tag can have multiple values or not
	 * 
	 * @return true of it can have multiple values, false otherwise
	 */
	public boolean getMulti() {
		return multi;
	}
	
	/**
	 * Set tag type values
	 * 
	 * @param name is the tag type 
	 * @param type is whether or not the tag can have multiple values or not
	 */
	public void setTag(String name, boolean type) {
		this.tagName = name.toLowerCase();
		this.multi = type;
	}
	
	/**
	 * toString method for tagtype so that tagtype display is legible
	 * Has tag type
	 */
	public String toString() {
		return this.getTagName();
	}

}
