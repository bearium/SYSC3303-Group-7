package main.requests;
import main.global.Direction;

public class ElevatorMotorRequest extends Request{

	/**
	 * Status or Action to give to scheduler or motor, respectively
	 */
	private  Direction RequestAction;
	
	/**
	 * Name of the motor's elevator
	 */
	private  String ElevatorName;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,5};

	/**
	 * Scheduler calls this to give the elevator a motor action, and the elevator sends it back as confirmation (?)
	 * @param name {@link ElevatorMotorRequest#RequestAction}
	 * @param action {@link LampRequest#getCurrentStatus()}
	 */
	public ElevatorMotorRequest(String name, Direction action){
		this.setRequestType(RequestType);
		this.ElevatorName = name;
		this.RequestAction = action;
	}
	
	
	/**
	 * {@link ElevatorMotorRequest#RequestAction}
	 */
	public Direction getRequestAction() {
		return RequestAction;
	}
	
	/**
	 * {@link ElevatorMotorRequest#RequestAction}
	 */
	public void setRequestAction(Direction requestAction) {
		RequestAction = requestAction;
	}
	
	/**
	 * {@link ElevatorMotorRequest#ElevatorName}
	 */
	public String getElevatorName() {
		return ElevatorName;
	}
	
	/**
	 * {@link ElevatorMotorRequest#ElevatorName}
	 */
	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}
	
	/**
	 * {@link ElevatorMotorRequest#RequestType}
	 */
	public static byte[] getRequestType() {
		return RequestType;
	}

	
}
