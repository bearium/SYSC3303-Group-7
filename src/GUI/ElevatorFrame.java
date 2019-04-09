package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

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
			EMP.add(new JScrollPane(tp));
			System.out.println("Adding elevator");
		}
		initializeJFrame();
	}
	
	private void initializeJFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		this.setMinimumSize(new Dimension(950, 600));
		this.setSize(600,800);
		this.setResizable(true);
		this.getContentPane().setBackground(SharedSettings.background_color);
		this.setVisible(true);
		
	}
	
}