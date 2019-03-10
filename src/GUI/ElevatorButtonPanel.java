package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import main.global.ElevatorDoorStatus;

public class ElevatorButtonPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, Boolean> lamps;
	private final int PANEL_COLS = 6;
	private ArrayList<RoundButton> buttons;
	public ElevatorButtonPanel(HashMap<Integer, Boolean> lamps) {
		this.lamps = lamps;
		initialize();
	}
	
	private void initialize() {
		//this.setSize(400, 400);
		buttons = new ArrayList<>();
		this.setLayout(new GridLayout(PANEL_COLS, lamps.size()/PANEL_COLS));
		for(Integer i : lamps.keySet()) {
			RoundButton button = new RoundButton(i+"");
			buttons.add(button);
			if(lamps.get(i).booleanValue()) {
				button.setBackground(Color.RED);
			}
			//JLabel label = new JLabel("Floor: "+i);
			this.add(button);
		}
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(Integer i : lamps.keySet()) {
			if(lamps.get(i).booleanValue()) {
				buttons.get(i - 1).setBackground(Color.RED);
			}
			else {
				buttons.get(i - 1).setBackground(Color.gray);
			}
		}
    }
	
	public void refreshStatus(HashMap<Integer, Boolean> lamps2) {
		this.lamps = lamps2;
		repaint();
	}
}
