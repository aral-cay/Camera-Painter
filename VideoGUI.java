import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static org.bytedeco.opencv.global.opencv_core.cvFlip;
import static org.bytedeco.opencv.global.opencv_imgproc.cvResize;

/**
 * Class to handle webcam capture and processing, packaging up JavaCV.
 * When the camera takes a shot, it first fills the image instance variable with the camera's shot,
 * then calls the handleImage method.  handleImage by default sets image1 on Reunion.ImageGUI to the image instance variable.
 * Also sets call back methods for key press and mouse press.
 *
 * @author Tim Pierson, Dartmouth CS 10, Winter 2024, based on prior terms Webcam
 * @co-author Aral Cay
 *
 */
public class VideoGUI extends Reunion.ImageGUI {
	protected boolean mac = true;					// is this computer a mac?
	private static final double scale = 0.5;		// to downsize the image (for speed), set this to a fraction <= 1
	private static final boolean mirror = true;		// make true in order to mirror left<->right so your left hand is on the left side of the image
	protected BufferedImage image;					// image grabbed from webcam (if any)
	protected int width, height;					// image size

	private Grabby grabby;							// handles webcam grabbing
	private FrameGrabber grabber;					// JavaCV

	public VideoGUI() {
		this("Webcam");
	} //call constructor with default title
	public VideoGUI(String title) {
		super(title);

		//try to determine if this is a Mac OS (if doesn't work, manually set mac instance variable)
		String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			System.out.println("Looks like you're on a Mac");
			mac = true;
		}
		else {
			System.out.println("Looks like you're NOT on a Mac");
			mac = false;
		}
		System.out.println("Standby while the camera starts...");

		try {
			if (mac) grabber = new OpenCVFrameGrabber(0); // this seems to work for Macs
			else grabber = FrameGrabber.createDefault(0);  // this seems to work for Windows
			grabber.start();
			System.out.println("Started!");
		} catch (Exception e) {
			System.err.println("Failed to start frame grabber");
			System.err.println(e);
			System.exit(-1);
		}

		// Get size and figure out scaling
		int width = grabber.getImageWidth();
		int height = grabber.getImageHeight();
		System.out.println("Native camera size "+width+"*"+height);
		if (scale != 1) {
			width = (int)(width*scale);
			height = (int)(height*scale);
			System.out.println("Scaled to "+width+"*"+height);
		}
		initWindow(width,height);

		// Spawn a separate thread to handle grabbing.
		grabby = new Grabby();
		grabby.execute();
	}

	/**
	 * Set up graphic window of appropriate size (either one image, or two images side by side)
	 */
	private void initWindow(int width, int height) {
		this.width = width;
		this.height = height;

		// Listener to Reunion.ImageGUI's JPanel for events
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handleMousePress(event.getPoint().x, event.getPoint().y);
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent event) {
				handleKeyPress(event.getKeyChar());
			}
		});

		// Boilerplate to finish initializing the GUI to the specified size
		setSize(width, height);
		getContentPane().add(panel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Handles grabbing an image from the webcam (following JavaCV examples)
	 * storing it in image, and telling the canvas to repaint itself.
	 */
	private class Grabby extends SwingWorker<Void, Void> {
		protected Void doInBackground() throws Exception {
			OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
			Java2DFrameConverter paintConverter = new Java2DFrameConverter();
			while (!isCancelled()) {
				IplImage grabbed = null;
				while (grabbed == null) {
					try {
						grabbed = grabberConverter.convert(grabber.grab());
					}
					catch (Exception e) {
						Thread.sleep(100); // wait a bit
					}
				}
				if (mirror) {
					cvFlip(grabbed, grabbed, 1);
				}
				if (scale != 1) {
					IplImage resized = IplImage.create(width, height, grabbed.depth(), grabbed.nChannels());
					cvResize(grabbed, resized);
					grabbed = resized;
				}
				Frame frame = grabberConverter.convert(grabbed);
				image = paintConverter.getBufferedImage(frame);

				//let program process the new image
				handleImage();

				Thread.sleep(100); // slow it down
			}
			// All done; clean up
			grabber.stop();
			grabber.release();
			grabber = null;
			return null;
		}
	}

	/**
	 * Returns a reference to the panel's graphics window.  Useful for drawing on the window (WebcamTracking).
	 * @return reference to window as Graphics object.
	 */
	public Graphics getWindowReference() {
		return super.panel.getGraphics();
	}


	/**
	 * Draws image instance variable filled by camera as left image on Reunion.ImageGUI
	 * Override this method for alternative functionality.
	 */
	public void handleImage() {
		setImage1(image);
	}

	/**
	 * Called back when the mouse is pressed.
	 * Override for alternative functionality
	 * @param x mouse x location when pressed
	 * @param y mouse y location when pressed
	 */
	public void handleMousePress(int x, int y) {
		System.out.println("Got mouse " + x + ", " + y);
	}

	/**
	 * Called back when a key is pressed
	 * Override for alternative functionality
	 * @param key key that was pressed
	 */
	public void handleKeyPress(char key) {
		System.out.println("Key pressed: " + key);
	}
}