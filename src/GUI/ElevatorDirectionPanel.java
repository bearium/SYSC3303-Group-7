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
import javax.swing.JPanel;

import main.global.Direction;
import main.global.ElevatorDoorStatus;

public class ElevatorDirectionPanel extends JPanel implements Observer{
	BufferedImage up_on; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage up_off;// = new ImageIcon("resources/images/elevator/elevator_opened");
	BufferedImage down_on; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage down_off;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1270317896891165638L;
	
	private Direction direction;

	public ElevatorDirectionPanel(Direction initial_direction) {
		super();
		this.setDirection(initial_direction);
		
		try 
		{
			up_on = ImageIO.read(new File("src\\resources\\images\\elevator\\up_on.png"));
			up_off = ImageIO.read(new File("src\\resources\\images\\elevator\\up_off.png"));
			down_on = ImageIO.read(new File("src\\resources\\images\\elevator\\down_on.png"));
			down_off = ImageIO.read(new File("src\\resources\\images\\elevator\\down_off.png"));
		}
		catch (Exception E) {
			System.out.println("UH OH " + System.getProperty("user.dir"));
			E.printStackTrace();
		}
		up_on =  toBufferedImage(up_on.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		up_off =  toBufferedImage(up_off.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		down_on =  toBufferedImage(down_on.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		down_off =  toBufferedImage(down_off.getScaledInstance(32,32, Image.SCALE_DEFAULT));
	}

	/**
	 * @return the status
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @param status the status to set
	 */
	public void setDirection(Direction dir) {
		this.direction = dir;
	}
	
	public void refreshStatus(Direction new_status) {
		setDirection(new_status);
		this.repaint();
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage image_up;
        BufferedImage image_down;
        
        if(direction == Direction.DOWN)
        {
        	image_down =  down_on;
        	image_up = up_off;
        }
        else if(direction == Direction.UP)
        {
        	image_down = down_off;
        	image_up = up_on;
        }
        else {
        	image_down = down_off;
        	image_up = up_off;
        }
        g.drawImage(image_down, 0, 0, this); // see javadoc for more info on the parameters            
        g.drawImage(image_up, 64, 0, this);
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
