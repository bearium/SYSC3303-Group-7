package GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JPanel;

public class ElevatorButtonPanel extends JPanel{
	private HashMap<Integer, Boolean> lamps;
	private final int PANEL_COLS = 6;
	public ElevatorButtonPanel(HashMap<Integer, Boolean> lamps) {
		this.lamps = lamps;
		initialize();
	}
	
	private void initialize() {
		//this.setSize(400, 400);
		this.setLayout(new GridLayout(PANEL_COLS, lamps.size()/PANEL_COLS));
		for(Integer i : lamps.keySet()) {
			RoundButton button = new RoundButton(i+"");
			if(lamps.get(i).booleanValue()) {
				button.setBackground(Color.RED);
			}
			//JLabel label = new JLabel("Floor: "+i);
			this.add(button);
		}
	}
}
