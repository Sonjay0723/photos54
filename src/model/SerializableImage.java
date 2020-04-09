package model;

import java.io.Serializable;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class SerializableImage implements Serializable {
		
	private static final long serialVersionUID = -1266447099158556616L;
	private int width, height;
	private int[][] pixels;

	public SerializableImage(Image image) {
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		pixels = new int[width][height];
		
		PixelReader reader = image.getPixelReader();
		for (int currentWidth = 0; currentWidth < width; currentWidth++)
			for (int currentHeight = 0; currentHeight < height; currentHeight++)
				pixels[currentWidth][currentHeight] = reader.getArgb(currentWidth, currentHeight);
	}

	public Image getImage() {
		WritableImage image = new WritableImage(width, height);
		
		PixelWriter w = image.getPixelWriter();
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				w.setArgb(i, j, pixels[i][j]);
		
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int[][] getPixels() {
		return pixels;
	}
	
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
