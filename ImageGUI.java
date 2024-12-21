import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A simple JFrame for displaying one image, or two images side-by-side
 *   pass one image to the constructor to see just that image (takes either file name or BufferedImage)
 *   pass two images to the constructor to see both images side-by-side (image1 on left, image2 on right)
 *   can change images using setImage1 or setImage2, these will call repaint to show the new images on the screen
 * 
 * @author Tim Pierson, Dartmouth CS10, Winter 2024, based on DrawingGUI from prior terms
 * @co-author Aral Cay
 */
public class ImageGUI extends JFrame {
	JPanel panel;									//container to hold images
	protected BufferedImage image1;					//first image to display (on left)
	protected BufferedImage image2;                 //second image to display (on right)
	protected int pixelsBetweenImages = 10;		    //pixels between images when two images are displayed
	protected int defaultWidth = 800, defaultHeight = 600; //made a window this big if no image provided

	/**
	 * Constructor that creates a window of default size
	 * @param title display in window title bar
	 */
	public ImageGUI(String title) {
		super(title);
		initWindow();
	}

	/**
	 * Constructor that creates a frame sized for one image located at fileName
	 *
	 * @param title		displayed in window title bar
	 * @param fileName  image file to load
	 */
	public ImageGUI(String title, String fileName) {
		super(title); //create window

		//load one image, null image2
		image1 = ImageIOLibrary.loadImage(fileName);
		image2 = null;

		//set up window
		initWindow();
	}

	/**
	 * Constructor that creates a frame sized for one image passed as parameter
	 *
	 * @param title		displayed in window title bar
	 * @param image  image file to display
	 */
	public ImageGUI(String title, BufferedImage image) {
		super(title); //create window

		//save image, null image2
		image1 = image;
		image2 = null;

		//set up window
		initWindow();
	}

	/**
	 * Constructor that creates a frame sized for two images located at fileName1 and fileName2
	 * Images are shown side by side with spacer pixels between them
	 *
	 * @param title		displayed in window title bar
	 * @param fileName1  file name of image to load in image
	 * @param fileName2  file name of image to load in image2
	 */
	public ImageGUI(String title, String fileName1, String fileName2) {
		super(title); //create window

		//load two images
		image1 = ImageIOLibrary.loadImage(fileName1);
		image2 = ImageIOLibrary.loadImage(fileName2);

		//set up window
		initWindow();
	}

	/**
	 * Constructor that creates a frame sized for two images passed as parameters
	 * Images are shown side by side with spacer pixels between them
	 *
	 * @param title		displayed in window title bar
	 * @param image1    image to display on the left
	 * @param image2    iamge to display on the right
	 */
	public ImageGUI(String title, BufferedImage image1, BufferedImage image2) {
		super(title); //create window

		//save two images
		this.image1 = image1;
		this.image2 = image2;

		//set up window
		initWindow();
	}

	/**
	 * Set up graphic window of appropriate size (either one image, or two images side by side)
	 */
	private void initWindow() {
		int width = 0, height = 0;

		if (image1 != null) {
			width = image1.getWidth();
			height = image1.getHeight();
		}
		else {
			//if image1 set to null, use default size
			width = defaultWidth;
			height = defaultHeight;
		}

		//show image2 side by side if image2 not null
		if (image2 != null) {
			width += image2.getWidth() + pixelsBetweenImages;
			height = Math.max(image1.getHeight(), image2.getHeight());
		}

		// Create a JPanel to hold the images
		panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (image1 != null) {
					g.drawImage(image1, 0, 0, null);
				}
				if (image2 != null) {
					g.drawImage(image2, image1.getWidth()+ pixelsBetweenImages,0,null);
				}
			}
		};

		// Boilerplate to finish initializing the GUI to the specified size
		//System.out.println("getting size " + width + " " + height);
		setSize(width, height);
		getContentPane().add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Set the first image and repaint to refresh window with new image on the screen
	 * @param fileName name of image on disk to set
	 */
	public void setImage1(String fileName) {
		image1 = ImageIOLibrary.loadImage(fileName);
		repaint();
	}

	/**
	 * Set the first image and repaint to refresh window with new image on the screen
	 * @param image image to set
	 */
	public void setImage1(BufferedImage image) {
		image1 = image;
		repaint();
	}

	/**
	 * Set the second image and repaint to refresh window with new image on the screen
	 * @param fileName file where image is stored on disk
	 */
	public void setImage2(String fileName) {
		image2 = ImageIOLibrary.loadImage(fileName);
		int h = Math.max(image1.getHeight(), image2.getHeight());
		setSize(image1.getWidth()+image2.getWidth()+ pixelsBetweenImages, h);
		repaint();
	}

	/**
	 * Set the second image and repaint to refresh window with new image on the screen
	 * @param image BufferedImage to set as right image
	 */
	public void setImage2(BufferedImage image) {
		image2 = image;
		int h = Math.max(image1.getHeight(), image2.getHeight());
		setSize(image1.getWidth()+image2.getWidth()+ pixelsBetweenImages, h);
		repaint();
	}
}
