package model;

import java.io.File;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class User implements Serializable{
	
	private static final long serialVersionUID = 8177923271139908648L;
	public String username;
	public ObservableList<Album> albumList = FXCollections.observableArrayList();
	
	public User(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String x) {
		this.username = x;
	}
	
	public ObservableList<Album> getAlbumList(){
		return this.albumList;
	}
	
	public void addAlbum(Album newAlbum) {
		albumList.add(newAlbum);
		Comparator<Album> comparator = Comparator.comparing(Album::getTitle);
		FXCollections.sort(albumList, comparator);
		this.createFiles();
	}
	
	public void removeAlbum(String name) {
		int position = 0;
		for(int i=0; i<albumList.size(); i++) {
			if(name.toLowerCase().equals(albumList.get(i).getTitle().toLowerCase())) {
				position = i;
				break;
			}
		}
		albumList.remove(position);
		//TODO remove from files
	}
	
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
	
	@Override
	public boolean equals(Object other) {
		if(other==null || !(other instanceof User))
			   return false;

		User curr =(User) other;
		return curr.getUsername().toLowerCase().equals(username.toLowerCase());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	public int compareTo(User currUser) {
		return this.getUsername().toLowerCase().compareTo(currUser.getUsername().toLowerCase());
	}
	
	public String toString() {
		return this.getUsername();
	}
	
	//method to create appropriate Files
	private void createFiles() {
		
		String path = "data/Users/" + this.getUsername();
		File userFile = new File(path);
		userFile.mkdir();
		
		for(int j = 0; j<this.getAlbumList().size(); j++) {
			
			path += "/" + this.getAlbumList().get(j);
			System.out.println(path+"\n");
			File albumFile = new File(path);
			albumFile.mkdir();
			
			for(int k=0; k<this.getAlbumList().get(j).getPictureList().size(); k++) {
				
				//create path that will also create parent directories
				path += "/" + this.getAlbumList().get(j).getPictureList().get(k).getPictureName();
		        
				//Use FileOutputStream to add picture to file
				try {
					FileOutputStream out = new FileOutputStream(path);
					ObjectOutputStream oout = new ObjectOutputStream(out);
					
					oout.writeObject(this.getAlbumList().get(j).getPictureList().get(k).getPicture());
					
					oout.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//enter in info
				String endingPath = this.getAlbumList().get(j).getPictureList().get(k).getPictureName().replaceAll(".jpg", ".txt");
				path = path.replace(this.getAlbumList().get(j).getPictureList().get(k).getPictureName(), endingPath);
				
				File imageInfo = new File(path);
				try {
					writeImage(this.getAlbumList().get(j).getPictureList().get(k), imageInfo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//revert path
				path = path.replace("/"+endingPath, "");
			}
			//revert path
			path = path.replace("/" + this.getAlbumList().get(j),"");
		}
		path = "";
	}
	
	//method to write to an Image file
	private void writeImage(Picture Photo, File path) throws IOException {
	
		//use file OutputStream to write to info file
		try {
			FileOutputStream out2 = new FileOutputStream(path);
			ObjectOutputStream oout2 = new ObjectOutputStream(out2);
			
			String cap = Photo.getCaption();
			oout2.writeObject(cap+"\n");
			oout2.writeObject(Photo.getDate());
			ObservableList<Tag> listTags = Photo.getTagList();
			for(int i=0; i<listTags.size(); i++) {
				String currTag = listTags.get(i).toString();
				oout2.writeObject(currTag+"\n");
			}
			
			oout2.close();
			out2.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*Date date = Photo.getDate().getTime();
		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		String dateTime = df.format(date);
		w1.write(dateTime+"\n");*/
		
		/* Converting string to Calendar
		 * String sDate1="12/14/2006 01:07:66";  
    	 * Date date1=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(sDate1);  
    	 * Calendar thisDate = Calendar.getInstance();
    	 * thisDate.setTime(date1);
		*/
		
		return;
	}
}
