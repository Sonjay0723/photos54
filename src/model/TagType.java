package model;

import java.io.Serializable;

public class TagType implements Serializable{
	
	String tagName;
	boolean multi;
	
	public TagType(String tagName, boolean multi) {
		this.tagName = tagName;
		this.multi = multi;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public boolean getMulti() {
		return multi;
	}
	
	public void setTag(String name, boolean type) {
		this.tagName = name.toLowerCase();
		this.multi = type;
	}
	
	public String toString() {
		return this.getTagName();
	}

}
