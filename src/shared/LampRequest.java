package shared;


public class LampRequest extends Request{
	

	public enum LampStatus {ON, OFF}
	public enum LampAction {TURN_ON, TURN_OFF}
	
	public LampStatus CurrentStatus;
	public LampAction RequestAction;
	
	
	/**
	 * Elevator will create this request to inform Scheduler of current lamp status
	 * 
	 * @param name
	 * @param status
	 */
	public LampRequest(LampStatus status){
		this.CurrentStatus = status;
	}
	
	/**
	 * Scheduler calls this to give the lamp an action
	 * @param name
	 * @param status
	 */
	public LampRequest(LampAction action){
		this.RequestAction = action;
	}
}
