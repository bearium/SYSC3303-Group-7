package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;

public class ElevatorDirectionPanel extends JPanel implements Observer{
	BufferedImage up_on; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage up_off;// = new ImageIcon("resources/images/elevator/elevator_opened");
	BufferedImage down_on; //= new ImageIcon("resources/images/elevator/elevator_closed");
	BufferedImage down_off;
	ImageIcon outOfService;
	ImageIcon idle;
	ImageIcon moving;
	
	int currentFloor;
	JLabel up;
	JLabel down;
	JLabel currFloor;
	JLabel status;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1270317896891165638L;
	
	private Direction direction;
	

	public ElevatorDirectionPanel(Direction initial_direction) {
		super();
		this.setSize(126,32);
		this.setDirection(initial_direction);
		
		try 
		{
			URL url = ElevatorDirectionPanel.class.getResource("../resources/images/elevator/OutOfService.jpg");
			up_on = ImageIO.read(new File("src\\resources\\images\\elevator\\up_on.png"));
			up_off = ImageIO.read(new File("src\\resources\\images\\elevator\\up_off.png"));
			down_on = ImageIO.read(new File("src\\resources\\images\\elevator\\down_on.png"));
			down_off = ImageIO.read(new File("src\\resources\\images\\elevator\\down_off.png"));
			outOfService = new ImageIcon(url);
			 url = ElevatorDirectionPanel.class.getResource("../resources/images/elevator/InMotion.gif");
			moving = new ImageIcon(url);
			 url = ElevatorDirectionPanel.class.getResource("../resources/images/elevator/idle.gif");
			idle = new ImageIcon(url);
		}
		catch (Exception E) {
			System.out.println("UH OH " + System.getProperty("user.dir"));
			E.printStackTrace();
		}
		up_on =  toBufferedImage(up_on.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		up_off =  toBufferedImage(up_off.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		down_on =  toBufferedImage(down_on.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		down_off =  toBufferedImage(down_off.getScaledInstance(32,32, Image.SCALE_DEFAULT));
		outOfService =  getScaledImage(outOfService.getImage(), 32,32);
		moving = getScaledImage(moving.getImage(), 32, 32);
		idle = getScaledImage(idle.getImage(), 32 ,32);

		up = new JLabel();
		down = new JLabel();
		status = new JLabel();
		
		currFloor = new JLabel();
		String filename="src\\resources\\images\\elevator\\digital-7.ttf";
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File(filename));
			font = font.deriveFont(Font.BOLD,20);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			currFloor.setFont(font);
			currFloor.setForeground(Color.red);
			
			
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currFloor.setBorder(new EmptyBorder(0, 10, 10, 10));
		

		

		
		this.add(down);
		this.add(currFloor);
		this.add(up);
		this.add(status);
		this.setBackground(Color.BLACK);
		
		
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
	
	public int getCurrFloor() {
		return this.currentFloor;
	}
	
	public void setCurrFloor(int newCurrFloor) {
		this.currentFloor = newCurrFloor;
	}
	
	public void checkOutOfService(ElevatorStatus status) {
		if (status == ElevatorStatus.OUT_OF_SERVICE) {
			this.status.setIcon(outOfService);
		}
		else if(status == ElevatorStatus.MOVING) {
			this.status.setIcon(moving);
		}
		else if(status == ElevatorStatus.STOPPED) {
			this.status.setIcon(idle);
		}
		
	}
	
	public void refreshStatus(Direction new_status, int newCurrFloor, ElevatorStatus elevatorStatus) {
		setDirection(new_status);
		setCurrFloor(newCurrFloor);
		checkOutOfService(elevatorStatus);
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
        up.setIcon(new ImageIcon(image_up));
        down.setIcon(new ImageIcon(image_down));
        currFloor.setText(Integer.toString(currentFloor));
        //g.drawImage(image_down, 0, 0, this); // see javadoc for more info on the parameters            
        //g.drawImage(image_up, 64, 0, this);
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
