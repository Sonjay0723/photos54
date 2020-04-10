package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
	
	public static final long serialVersionUID = -2310738753538431907L;
	public String name;
	public String value;
	public boolean multi;
	
	public Tag(String name, String value, boolean multi) {
		this.name = name; 
		this.value = value;
		this.multi = multi;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean getMulti() {
		return multi;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other==null || !(other instanceof Tag))
			   return false;

		Tag curr =(Tag ) other;
		return curr.getName().equals(name) && curr.getValue().equals(value);
	}

	public int compareTo(Tag currTag) {
		return this.toString().toLowerCase().compareTo(currTag.toString().toLowerCase());
	}
	
	public String toString() {
		return name + " - " + value;
	}
}