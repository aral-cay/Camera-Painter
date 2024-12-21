import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Webcam-based drawing
 * Dartmouth CS 10, Winter 2024
 * @co-author Mehmet Can Yilmaz
 * @author Aral Cay
 */
public class CamPaint extends VideoGUI {
	private char displayMode = 'w';  // display mode = character 'w'
	private RegionFinder finder;  // regionfinder class object
	private Color targetColor;  // chooses the target color
	private Color paintColor = Color.blue;  // chooses the paint color
	private BufferedImage painting;  //canvas painting

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		super("CamPaint");
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * VideoGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void handleImage() {
		if (targetColor != null) {  // if there's a target color
			finder.setImage(image);  // finder sets the image
			finder.findRegions(targetColor);
			finder.recolorImage();

//			ArrayList<Point> largestRegion =
			if (finder.largestRegion() != null) {  //if there's a largest region
				for (Point p : finder.largestRegion()) {  // for each point p in the region
					painting.setRGB(p.x, p.y, paintColor.getRGB());  // sets the RGB to paint color
				}
			}
		}

			if (displayMode == 'w') {
				setImage1(image);  // 'w' character sets image to image
			} else if (displayMode == 'r') {
				setImage1(finder.getRecoloredImage());  // 'r' gets the recolored image
			} else if (displayMode == 'p') {
				setImage1(painting);  // 'p' gets the painting
			}

	}

	/**
	 * Overrides the Webcam method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) {  // if there's an image
			targetColor = new Color(image.getRGB(x, y));  // a new target color is set
			//finder.findRegions(targetColor);
		}
	}

	/**
	 * Webcam method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') {  // if key is set to either one of these,
			displayMode = k;  // display mode opens
		} else if (k == 'c') {
			clearPainting();  // c clears the painting
		} else if (k == 'o') {
			ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
			// 'o' saves the recolored image
		} else if (k == 's') {
			ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
			// 's' saves the painting
		} else {
			System.out.println("unexpected key " + k);
			// error message in case of another key pressed
		}
	}



	public static void main(String[] args) {
		new CamPaint();  // new campaint is run under main
	}
}
