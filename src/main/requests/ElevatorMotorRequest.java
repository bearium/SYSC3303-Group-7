package main.requests;
import main.global.Direction;

public class ElevatorMotorRequest extends Request{
	//public enum MotorStatus {ON, OFF}
	//public enum MotorAction {TURN_ON, TURN_OFF}
	
	//public MotorStatus CurrentStatus;
	private  Direction RequestAction;
	
	private  String ElevatorName;
	
	private static byte[] RequestType = new byte[] {1,5};
	/**
	 * Elevator will create this request to inform Scheduler of current motor status
	 * 
	 * @param name
	 * @param status
	 *
	public ElevatorMotorRequest(String name, MotorStatus status){
		this.ElevatorName = name;
		this.CurrentStatus = status;
	}/*
	
	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name
	 * @param status
	 */
	public ElevatorMotorRequest(String name, Direction action){
		super.RequestType = RequestType;
		this.ElevatorName = name;
		this.RequestAction = action;
	}
	
	
	public Direction getRequestAction() {
		return RequestAction;
	}
	public void setRequestAction(Direction requestAction) {
		RequestAction = requestAction;
	}
	public String getElevatorName() {
		return ElevatorName;
	}
	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}
	public static byte[] getRequestType() {
		return RequestType;
	}

	
}
