/**
 * @author Dhrishti hazari
 * @author Jayson Pitta
 */

package model;

import java.io.Serializable;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SerializableImage implements Serializable {
		
	/**
	 * SerialID for SerializableImage Class
	 * Width of SerializableImage
	 * Height of SerializableImage
	 * 2D Array of pictures
	 */
	private static final long serialVersionUID = 6554568434893643354L;
	private int width, height;
	private int[][] pixels;

	/**
	 * Initialize SerializableImage
	 * 
	 * @param image to turn into a SerializableImage
	 */
	public SerializableImage(Image image) {
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		pixels = new int[width][height];
		
		PixelReader reader = image.getPixelReader();
		for (int currentWidth = 0; currentWidth < width; currentWidth++)
			for (int currentHeight = 0; currentHeight < height; currentHeight++)
				pixels[currentWidth][currentHeight] = reader.getArgb(currentWidth, currentHeight);
	}

	/**
	 * Get the Image from the SerializableImage
	 * 
	 * @return the Image
	 */
	public Image getImage() {
		WritableImage image = new WritableImage(width, height);
		
		PixelWriter w = image.getPixelWriter();
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				w.setArgb(i, j, pixels[i][j]);
		
		return image;
	}

	/**
	 * Get the width of the image
	 * 
	 * @return the width of the image
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the image
	 * 
	 * @return the height of the image
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Get the pixels of the image 
	 * 
	 * @return a 2D array of the pixels of the image 
	 */
	public int[][] getPixels() {
		return pixels;
	}
	
	/**
	 * equals method for SerializableImage by comparing pixels
	 * 
	 * @param image the SerializableImage to see if it is equal to
	 * 
	 * @return true if equal, false otherwise
	 */
	public boolean equals(SerializableImage image) {
		if (width != image.getWidth() || height != image.getHeight())
			return false;
		
		for (int currentRow = 0; currentRow < width; currentRow++)
			for (int currentColumn = 0; currentColumn < height; currentColumn++)
				if (pixels[currentRow][currentColumn] != image.getPixels()[currentRow][currentColumn])
					return false;
		
		return true;
	}
}
