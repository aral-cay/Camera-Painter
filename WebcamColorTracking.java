import java.awt.*;

/**
 * Fun with the webcam, built on JavaCV
 * Tracks a color, as specified by mouse press
 * 
 * @author Tim Pierson, Dartmouth CS10, Winter 2024, based on prior terms WebcamColorTracking
 */

public class WebcamColorTracking extends VideoGUI {
	private Color trackColor=null;		 	// point-tracking target color

	/**
	 * Determines which point is closest to the trackColor, returns it
	 */
	private Point track() {
		int cx = 0, cy = 0; // coordinates with best matching color
		int closest = 10000; // start with a too-high number so that everything will be smaller
		// Nested loop over every pixel
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// Euclidean distance squared between colors
				Color c = new Color(image.getRGB(x,y));
				int d = (c.getRed() - trackColor.getRed()) * (c.getRed() - trackColor.getRed())
						+ (c.getGreen() - trackColor.getGreen()) * (c.getGreen() - trackColor.getGreen())
						+ (c.getBlue() - trackColor.getBlue()) * (c.getBlue() - trackColor.getBlue());
				
				//track point with closest color to trackColor (so far)
				if (d < closest) {
					closest = d;
					cx = x; cy = y;
				}
			}
		}
		//return point that had the closest color
		return new Point(cx,cy);
	}

	/**
	 * DrawingGUI method, here setting trackColor from where mouse was pressed
	 */
	@Override
	public void handleMousePress(int x, int y) {
		System.out.println("Got mouse press");
		if (image != null) {
			trackColor = new Color(image.getRGB(x, y));
			System.out.println("tracking " + trackColor);
		}
	}

	/**
	 * Webcam method, here also showing tracked color point (called when WebCam repaints in doInBackground)
	 */
	@Override
	public void handleImage() {
		super.handleImage();
		if (trackColor != null) {
			// Draw circle at point with color closest to trackColor, then draw circle border in the inverse color
			Point p = track();

			//draw circle around point to highlight
			Graphics g = super.getWindowReference(); //VideoGUI's panel graphics window
			g.setColor(trackColor);
			g.fillOval(p.x, p.y, 15, 15);
			((Graphics2D)g).setStroke(new BasicStroke(4)); // thick border
			g.setColor(new Color(255-trackColor.getRed(), 255-trackColor.getGreen(), 255-trackColor.getBlue()));
			g.drawOval(p.x, p.y, 15, 15);
		}
	}

	public static void main(String[] args) {
		new WebcamColorTracking();
	}
}
