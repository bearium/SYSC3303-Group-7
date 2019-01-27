package shared;

public class ElevatorDoorRequest extends Request{
	public enum DoorStatus {CLOSING, CLOSED, OPEN, OPENING}
	public enum DoorAction {CLOSE, OPEN}
	
	public DoorStatus CurrentStatus;
	public DoorAction RequestAction;
	
	public String ElevatorName;
	
	/**
	 * Elevator will create this request to inform Scheduler of current motor status
	 * 
	 * @param name
	 * @param status
	 */
	public ElevatorDoorRequest(String name, DoorStatus status){
		this.ElevatorName = name;
		this.CurrentStatus = status;
	}
	
	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name
	 * @param status
	 */
	public ElevatorDoorRequest(String name, DoorAction action){
		this.ElevatorName = name;
		this.RequestAction = action;
	}
}
