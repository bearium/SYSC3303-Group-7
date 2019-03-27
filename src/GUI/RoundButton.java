package GUI;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;

public class RoundButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3338870056346866782L;

	public RoundButton(String label) {
		super(label);
		this.setFont(new Font("Arial", Font.PLAIN, 10));
		setBackground(Color.lightGray);
		setForeground(Color.blue);
		//setFocusable(false);
		//this.setEnabled(false);
		/*
     These statements enlarge the button so that it 
     becomes a circle rather than an oval.
		 */
		Dimension size = getPreferredSize();
		size.width = 40;
		size.height = 40;
		size.width = size.height = Math.max(size.width, size.height);
		setPreferredSize(size);
		

		/*
     This call causes the JButton not to paint the background.
     This allows us to paint a round background.
		 */
		setContentAreaFilled(false);
		this.actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
			
		};
	}

	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			g.setColor(Color.gray);
		} else {
			g.setColor(getBackground());
		}
		g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

		super.paintComponent(g);
	}

	protected void paintBorder(Graphics g) {
		g.setColor(Color.darkGray);
		int BORDER_SIZE = 1;

		g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		g.setColor(Color.white);
		g.drawOval(BORDER_SIZE, BORDER_SIZE  , getSize().width - BORDER_SIZE * 2 - 1, getSize().height - BORDER_SIZE * 2 - 1);
		BORDER_SIZE ++;
		//g.drawOval(BORDER_SIZE, BORDER_SIZE  , getSize().width - BORDER_SIZE * 2 - 2, getSize().height - BORDER_SIZE * 2 - 2);

	}

	// Hit detection.
	Shape shape;

	public boolean contains(int x, int y) {
		// If the button has changed size,  make a new shape object.
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
		}
		return shape.contains(x, y);
	}
	// retrieved from: https://www.javacodex.com/More-Examples/2/14
}