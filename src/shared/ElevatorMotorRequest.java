package shared;

public class ElevatorMotorRequest extends Request{
	public enum MotorStatus {ON, OFF}
	public enum MotorAction {TURN_ON, TURN_OFF}
	
	public MotorStatus CurrentStatus;
	public MotorAction RequestAction;
	
	public String ElevatorName;
	
	/**
	 * Elevator will create this request to inform Scheduler of current motor status
	 * 
	 * @param name
	 * @param status
	 */
	public ElevatorMotorRequest(String name, MotorStatus status){
		this.ElevatorName = name;
		this.CurrentStatus = status;
	}
	
	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name
	 * @param status
	 */
	public ElevatorMotorRequest(String name, MotorAction action){
		this.ElevatorName = name;
		this.RequestAction = action;
	}
}
