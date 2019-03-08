package GUI;

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
		for(ElevatorMonitor monitor : elevatorMonitors.values()) {
			ElevatorState state = monitor.getElevatorState();
			ElevatorMainPanel EMP =new ElevatorMainPanel(state);
			this.add(EMP);
			
		}
		initializeJFrame();
	}
	
	private void initializeJFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
}
