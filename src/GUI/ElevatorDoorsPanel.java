package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.global.ElevatorDoorStatus;
import main.global.Fault;

public class ElevatorDoorsPanel extends JPanel implements Observer{
	BufferedImage icon_closed; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage icon_opened;// = new ImageIcon("resources/images/elevator/elevator_opened");
	ImageIcon icon_opening;
	ImageIcon icon_closing;
	JLabel doors;
	Fault elevator_fault;
	private final int IMAGE_SCALE = 3;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1270317896891165638L;

	private ElevatorDoorStatus status;

	public ElevatorDoorsPanel(ElevatorDoorStatus initial_status) {
		super();
		this.setStatus(initial_status);

		try 
		{
			icon_closed = ImageIO.read(new File("src\\resources\\images\\elevator\\elevator_closed.png"));
			icon_opened = ImageIO.read(new File("src\\resources\\images\\elevator\\elevator_opened.png"));
			URL url = ElevatorDoorsPanel.class.getResource("../resources/images/elevator/Door_Closing.gif");
			icon_closing = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
			url = ElevatorDoorsPanel.class.getResource("../resources/images/elevator/Door_Opening.gif");
			icon_opening = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));

		}
		catch (Exception E) {
			System.out.println("UH OH " + System.getProperty("user.dir"));
			E.printStackTrace();
		}
		icon_closed = toBufferedImage(icon_closed.getScaledInstance(65 * IMAGE_SCALE,80 * IMAGE_SCALE, Image.SCALE_DEFAULT));
		icon_opened = toBufferedImage(icon_opened.getScaledInstance(65 * IMAGE_SCALE,80 * IMAGE_SCALE, Image.SCALE_DEFAULT));
		icon_opening = getScaledImage(icon_opening.getImage(), 65 * IMAGE_SCALE, 80 * IMAGE_SCALE);
		icon_closing = getScaledImage(icon_closing.getImage(), 65 * IMAGE_SCALE ,80 * IMAGE_SCALE);
		doors = new JLabel();
		this.add(doors);
		this.setBackground(Color.black);
	}

	/**
	 * @return the status
	 */
	public ElevatorDoorStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ElevatorDoorStatus status) {
		this.status = status;
	}

	public void refreshStatus(ElevatorDoorStatus new_status) {
		setStatus(new_status);
		this.repaint();
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon image;
		//doors.setIcon(null);
			if(status == ElevatorDoorStatus.CLOSED)
			{
				//icon_closing.getImage().flush();
				image =  icon_closing;
				icon_opening.getImage().flush();
			}
			else 
			{
				//icon_opening.getImage().flush();
				image = icon_opening;
				icon_closing.getImage().flush();
			}
		
		//  image.getImage().flush();
		doors.setIcon(image);

		//g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
	}

	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	@Override
	public void update(Observable arg0, Object arg1) {

	}
	private ImageIcon getScaledImage(Image srcImg, int w, int h)

	{

		Image newimg = srcImg.getScaledInstance(w, h, Image.SCALE_DEFAULT); // scale it the smooth way

		return new ImageIcon(newimg); // transform it back

	}

}
