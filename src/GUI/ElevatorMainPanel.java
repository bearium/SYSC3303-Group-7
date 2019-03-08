package GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import main.elevatorSubsystem.ElevatorState;
import main.global.ElevatorDoorStatus;

public class ElevatorMainPanel extends JPanel implements Observer {
	private final int PANEL_COLS = 6;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8457181598233373200L;
	
	
	ElevatorState elevator;
	ElevatorDoorsPanel DP;
	ElevatorButtonPanel BP;
	ArrayList<JLabel> buttons;
	
	public ElevatorMainPanel(ElevatorState elevator) {
		this.setElevator(elevator);
		elevator.toggleLamp(3, true);
		this.initialize();
	}
	
	private void initialize() {
		//this.setSize(400, 400);
		
		HashMap<Integer, Boolean> lamps = elevator.getLamps();
		this.setLayout(new GridLayout(1,3));
		DP = new ElevatorDoorsPanel(elevator.getDoorStatus());
		this.add(DP);
		BP = new ElevatorButtonPanel(lamps);
		this.add(BP);
		elevator.addObserver(this);
		JButton toggle_close = new JButton("Close/Open");
		JPanel last_panel = new JPanel();
		boolean p = true;
		toggle_close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				elevator.setDoorStatus(elevator.getDoorStatus() == ElevatorDoorStatus.OPENED ? ElevatorDoorStatus.CLOSED : ElevatorDoorStatus.OPENED );
				elevator.toggleLamp(5, Math.random() * 10 > 5 ? true : false);
			}
			
		});
		last_panel.add(toggle_close);
		this.add(last_panel);
	}
	
	
	
	/**
	 * @return the elevator
	 */
	public ElevatorState getElevator() {
		return elevator;
	}
	/**
	 * @param elevator the elevator to set
	 */
	public void setElevator(ElevatorState elevator) {
		this.elevator = elevator;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ElevatorState) {
		//	ElevatorState newState = (ElevatorState) arg0;
			this.refresh();
		}
	}

	private void refresh() {
		DP.refreshStatus(elevator.getDoorStatus());
		BP.refreshStatus(elevator.getLamps());
		this.repaint();
	}
	
}
