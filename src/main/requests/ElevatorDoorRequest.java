package main.requests;
import main.global.ElevatorDoorStatus;

public class ElevatorDoorRequest extends Request{

	/**
	 * Name of the elevator's doors to open
	 */
	private  String ElevatorName;
	private  ElevatorDoorStatus RequestAction;

	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,3};
	
	
	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name 
	 * @param status
	 */
	public ElevatorDoorRequest(String name, ElevatorDoorStatus action){
		this.setRequestType(RequestType);
		this.ElevatorName = name;
		this.RequestAction = action;
	}

	public String getElevatorName() {
		return ElevatorName;
	}

	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}

	public ElevatorDoorStatus getRequestAction() {
		return RequestAction;
	}

	public void setRequestAction(ElevatorDoorStatus requestAction) {
		RequestAction = requestAction;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}

	
	
}
