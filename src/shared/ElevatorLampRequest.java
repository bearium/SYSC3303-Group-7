package shared;

public class ElevatorLampRequest extends LampRequest {
	String ElevatorName;
	CarButton ElevatorButton;
	
	public ElevatorLampRequest(String name, CarButton button, LampAction action) {
		super(action);
		this.ElevatorButton = button;
		this.ElevatorName = name;
		// TODO Auto-generated constructor stub
	}
	public ElevatorLampRequest(String name, CarButton button, LampAction action) {
		super(action);
		this.ElevatorButton = button;
		this.ElevatorName = name;
		// TODO Auto-generated constructor stub
	}
}
