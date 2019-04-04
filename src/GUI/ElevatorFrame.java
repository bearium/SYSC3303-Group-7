package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.JFrame;

import main.elevatorSubsystem.ElevatorState;
import main.scheduler.ElevatorMonitor;

public class ElevatorFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ElevatorFrame(HashMap<String, ElevatorMonitor> elevatorMonitors) {
		super("Elevator GUI Monitor");
		this.setLayout(new GridBagLayout());
		for(ElevatorMonitor monitor : elevatorMonitors.values()) {
			ElevatorState state = monitor.getElevatorState();
			ElevatorMainPanel EMP =new ElevatorMainPanel(state);
			this.add(EMP);
			ElevatorTripPanel tp = new ElevatorTripPanel(monitor);
			EMP.add(tp);
			System.out.println("Adding elevator");
		}
		initializeJFrame();
	}
	
	private void initializeJFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		this.setMinimumSize(new Dimension(950, 900));
		this.setSize(1000,1000);
		this.setResizable(true);
		this.getContentPane().setBackground(Color.black);
		this.setVisible(true);
		
	}
	
}
