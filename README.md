Authors of group 54:
Jayson Pitta
Dhrishti Hazari
dah253

This is the Readme for Photos

In our "model package", we have:
A Users class, that contains a list of Albums from the Album class and a list of Tag Types for the User to choose from the Tag Types Class
An Album class, that contains a list of Pictures from the Picture Class
A Picture Class, that contains a list of tag-value pairs for the specific picture from the tag class as well as a SerializablImage from the Serializable Image Class
A SerializableImage class, which allows an Image to be properly saved through FileOutputStream/FileInputStream
A tag class, which contains tag-value pair as well as whether or not the tag type is allowed to have multiple values.

In our "controller package" we have:
A login controller that displays the login page and which takes the user to the correct page
	-If not run before
		a "dat" file under data is created, which stores all future data for all future Users
		a stock user is created and loaded with a stock album of 5 stock photos
		a list of users is initialized and will be passed to all controllers so anyupdates can be saved to the dat file
	-if "admin" is entered, redirects to admin page (NOT case sensitive)
	-if an existing username is entered, redirects to the user's page (NOT case sensitive)
An admin controller that displays a list of all Users
	-Add a user
		if it already exists(NON case sensitive) it cannot be added
		If it is added, a Stock album with stock photos is loaded to the user's albums
	-Delete a user
	-Logout
A user controller that displays a list of albums for the specific user
	-Add an album
		if it already exists(NON case sensitive) it cannot be added
	-Delete an Album
	-rename an Album
	-Open an Album to see its contents
	-Search for Pictures within all albums for the user
	-Logout
A search controller that allows the user to search by Date range and/or tag-value pair(s)
	-Date range option
	-Tag-Value pair option
	-And/Or if wanting to search by another Tag-Value pair
	-ListView of Image earch results
		if the same picture is duplicated across different albums(based in pixels not name of the image), it is only displayed once
	-Button to create an Album Based on search results
	-Back button to go back to User's page
	-Logout Button
An Album controller that displays a listView of Images, a slideshow of all images in the Album, a display view for The Image, Caption, Date, and tag-value pairs of the selected image
	-Add Picture button to add a picture from user's files
	-Delete picture button to delete selected picture
	-Edit caption button(ONLY WORKS IF CAPTION HAS ACTUALLY BEEN CHANGED IN THE CAPTION DISPLAY) for selected caption
	-Add Tag button that redirects to an add tag location
	-Delete tag button that deletes the selected tag for the selected image
	-Choose album drop down(ONLY WORKS IF THERE IS ANOTHER ALBUM TO MOVE/COPY THE SELECTED PICTURE TO)
		Move to album button : moves selected picture to the selected album
		Copy to album button: copies selected picture to the selected album
		IF THE OTHER ALBUM ALREADY CONTAINS THE PICTURE(compared by pixels, not name) IT WILL NOT HAPPEN
	-slideshow(as items move along slideshow, the selected image in the box changes as well)
	-Logout Button
	-Backbutton to go back to user page
An AddTag controller that displays a list of the User's tag types
	-Type in a value to the selected Tag Type and add it to the Images' list of tag value pairs
		THE PICTURE ACROSS ALL ALBUMS WILL GET IT'S TAG_VALUE PAIRS
		IF the tag-value pair is duplicate, the image will only display 1 of them
	-Create a new tag
		IF the tag type already exists(NON case sensitive), it cannot be added
		Multi-Value/ Single Value tag type is defined
	-Close button to go back to album page
	-Logout button
	
We used fileInputStrem/FileOutputStream to save/update all the data as information was changed in all the controllers by have an array list of users that was passed to all controllers and that is what was saved.
	
