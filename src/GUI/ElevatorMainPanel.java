package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.elevatorSubsystem.ElevatorState;
import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;

public class ElevatorMainPanel extends JPanel implements Observer {
	private final int PANEL_COLS = 6;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8457181598233373200L;


	ElevatorState elevator;
	ElevatorDirectionPanel DP2;
	ElevatorDoorsPanel DP;
	ElevatorButtonPanel BP;
	ArrayList<JLabel> buttons;

	public ElevatorMainPanel(ElevatorState elevator) {
		this.setElevator(elevator);
		this.initialize();
	}

	private void initialize() {
		this.setSize(60, 500);
		this.setBorder(new EmptyBorder(0, 10, 10, 10));
		HashMap<Integer, Boolean> lamps = elevator.getLamps();
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		//JPanel holdPanel = new JPanel();
		//holdPanel.setLayout(new BoxLayout(holdPanel,BoxLayout.PAGE_AXIS));
		DP2 = new ElevatorDirectionPanel(elevator.getDirection());
		//DP2.setSize(32, 32);
		this.add(DP2);
		DP = new ElevatorDoorsPanel(elevator.getDoorStatus());
		DP.setSize(200,200);
		this.add(DP);


		//holdPanel.add(DP2);
		//holdPanel.add(DP);
		//this.add(holdPanel);
		BP = new ElevatorButtonPanel(lamps);
		this.add(BP);
		elevator.addObserver(this);
		this.setBackground(Color.black);
		//this.add(last_panel);
		this.updateUI();
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
			if(arg1 != null) {
				if(arg1 instanceof ElevatorDoorStatus) {

					DP.refreshStatus((ElevatorDoorStatus) arg1);
				}
			}
			else 
				this.refresh();
		}
	}

	private void refresh() {
		BP.refreshStatus(elevator.getLamps());
		DP2.refreshStatus(elevator.getDirection(), elevator.getCurrentFloor(), elevator.getCurrentStatus());
		this.repaint();
	}

}
