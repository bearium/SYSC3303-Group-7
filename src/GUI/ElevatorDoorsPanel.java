package GUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.global.ElevatorDoorStatus;

public class ElevatorDoorsPanel extends JPanel implements Observer{
	BufferedImage icon_closed; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage icon_opened;// = new ImageIcon("resources/images/elevator/elevator_opened");
	JLabel doors;
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
			icon_closed = ImageIO.read(new File("src\\resources\\images\\elevator\\Door_Closing.gif"));
			icon_opened = ImageIO.read(new File("src\\resources\\images\\elevator\\Door_Opening.gif"));
		}
		catch (Exception E) {
			System.out.println("UH OH " + System.getProperty("user.dir"));
			E.printStackTrace();
		}
		icon_closed = toBufferedImage(icon_closed.getScaledInstance(65 * 3,80 * 3, Image.SCALE_DEFAULT));
		icon_opened = toBufferedImage(icon_opened.getScaledInstance(65 * 3,80 * 3, Image.SCALE_DEFAULT));
		doors = new JLabel();
		this.add(doors);
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
        BufferedImage image;
        if(status == ElevatorDoorStatus.CLOSED)
        {
        	image =  icon_closed;
        }
        else 
        {
        	image = icon_opened;
        }
        doors.setIcon(new ImageIcon(image));
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
	
}
