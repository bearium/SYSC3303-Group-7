package GUI;
import javax.swing.*;

import main.elevatorSubsystem.ElevatorState;
import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;
public class GUItest {

	public static void main(String[] args) {
		JFrame frame = new JFrame("lol");
		ElevatorState state = new ElevatorState(0, 0, Direction.IDLE, ElevatorStatus.STOPPED, ElevatorDoorStatus.OPENED,35);
		frame.add(new ElevatorMainPanel(state));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	

}
