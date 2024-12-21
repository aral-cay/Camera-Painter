import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/*
* @co-author: Aral Cay
* @co-author: Mehmet Can Yilmaz
* @date: Jan 21, 2024
* @purpose: Region finder algorithm for webcam and image color detection
* */

public class RegionFinder {
	private static final int maxColorDiff = 20; // maximum color difference to be recognized
	private static final int minRegion = 50;  // minimum number of pixels for a region

	private BufferedImage image;  // original image
	private BufferedImage recoloredImage;  // final image
	private ArrayList<ArrayList<Point>> regions;  // region array

	public RegionFinder() {
		this.image = null;
	}  //constructs image to null

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}  // constructs image to image

	public void setImage(BufferedImage image) {
		this.image = image;
	}  // sets image to image

	public BufferedImage getImage() {
		return image;
	}  // returns image

	public BufferedImage getRecoloredImage() {
		return recoloredImage;  // returns final image
	}

	public void findRegions(Color targetColor) {
		regions = new ArrayList<>();  // regions is the region arraylist
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < image.getWidth(); x++) {  // nested loop for looping over pixels
			for (int y = 0; y < image.getHeight(); y++) {
				if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x, y)), targetColor)) {
					ArrayList<Point> toVisit = new ArrayList<>();  // toVisit determines unvisited points into an array
					ArrayList<Point> currentRegion = new ArrayList<>();  //current region is the arraylist for the tracked region

					toVisit.add(new Point(x, y));  // if unvisited, point is added

					while (!toVisit.isEmpty()) {  // runs while there are unvisited points
						Point current = toVisit.remove(0);

						if (visited.getRGB(current.x, current.y) == 0) {  //if point belongs to the current region,
							currentRegion.add(current);  // added to the array
							visited.setRGB(current.x, current.y, 1);

							for (int i = -1; i <= 1; i++) {  // nested for loop for discovering neighbours
								for (int j = -1; j <= 1; j++) {
									int neighborX = current.x + i;  // neighbour coordinates are determined by current + i
									int neighborY = current.y + j;

									if (neighborX >= 0 && neighborX < image.getWidth() && neighborY >= 0 && neighborY < image.getHeight()) {
										if (colorMatch(new Color(image.getRGB(neighborX, neighborY)), targetColor)) {  // java's inbuilt colormatch function is used for tracking the color
											toVisit.add(new Point(neighborX, neighborY));
										}
									}
								}
							}
						}
					}

					if (currentRegion.size() >= minRegion) {  // if there are enough pixels, new region is added
						regions.add(currentRegion);
					}
				}
			}
		}
	}

	protected static boolean colorMatch(Color c1, Color c2) {  // checks colors to see if they are the same
		int diffRed = Math.abs(c1.getRed() - c2.getRed());
		int diffGreen = Math.abs(c1.getGreen() - c2.getGreen());
		int diffBlue = Math.abs(c1.getBlue() - c2.getBlue());



		return diffRed <= maxColorDiff && diffGreen <= maxColorDiff && diffBlue <= maxColorDiff;  // returns this boolean value, respective of the maxColorDiff
	}


	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {  // determining the largest region
		if (regions == null || regions.isEmpty()) {
			return null; // no regions detected
		}

		// Find the region with the maximum size
		ArrayList<Point> largestRegion = regions.get(0); // assuming that the first region is the largest
		int maxSize = largestRegion.size();

		for (int i = 1; i < regions.size(); i++) {
			ArrayList<Point> currentRegion = regions.get(i);
			if (currentRegion.size() > maxSize) {  // updates the largest region inside this if condition
				maxSize = currentRegion.size();
				largestRegion = currentRegion;
			}
		}

		return largestRegion;  // returns the actual largest region
	}

	public void recolorImage() {  // recolors the image appropriately
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);

		for (ArrayList<Point> region : regions) {  // using for each loop for the region arraylist
			Color randomColor = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
			// using math.random to generate a 0-255 value for every color of RGB
			for (Point point : region) { // using for each loop for points
				recoloredImage.setRGB(point.x, point.y, randomColor.getRGB());  // recolors each point one by one
			}
		}
	}
}
