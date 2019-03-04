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
		ElevatorDoorsPanel DP = new ElevatorDoorsPanel(ElevatorDoorStatus.CLOSED);
		this.add(DP);
		this.add(new ElevatorButtonPanel(lamps));
		
		JButton toggle_close = new JButton("Close/Open");
		JPanel last_panel = new JPanel();
		toggle_close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DP.refreshStatus(DP.getStatus() == ElevatorDoorStatus.OPENED ? ElevatorDoorStatus.CLOSED :ElevatorDoorStatus.OPENED );
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
		
	}
	
}
