import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Code provided for PS-1
 * Testing code for region finding.
 *
 * @co-author Tim Pierson, Dartmouth CS10, Winter 2024 (based on code from prior terms)
 */
public class RegionsTest {
	/**
	 *
	 * @param title text to put at top of GUI window displaying recolored image
	 * @param finder class to find regions close to the targetColor
	 * @param targetColor find regions close to this color
	 */
	public RegionsTest(String title, RegionFinder finder, Color targetColor) {
		// Find and recolor regions
		finder.findRegions(targetColor);
		finder.recolorImage();
		BufferedImage image = finder.getRecoloredImage();

		//display recolored image
		ImageGUI gui = new ImageGUI(title, image);

	}


	public static void main(String[] args) {
		new RegionsTest("smiley", new RegionFinder(ImageIOLibrary.loadImage("pictures/smiley.png")), new Color(0, 0, 0));
		new RegionsTest("baker", new RegionFinder(ImageIOLibrary.loadImage("pictures/baker.png")), new Color(130, 100, 100));
	}
}
