package application;

import java.io.Serializable;

public class Tag implements Serializable {
	
	public static final long serialVersionUID = -2310738753538431907L;
	public String name;
	public String value;
	
	public Tag(String name, String value) {
		this.name = name; 
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
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