package main.requests;
import main.global.LampStatus;

public class LampRequest extends Request{
	

	//public enum LampStatus {ON, OFF}
	//public enum LampAction {TURN_ON, TURN_OFF}
	
	private LampStatus CurrentStatus;
	//public LampAction RequestAction;
	
	
	/**
	 * Elevator will create this request to inform Scheduler of current lamp status
	 * 
	 * @param name
	 * @param status
	 */
	public LampRequest(LampStatus status){
		this.CurrentStatus = status;
	}


	public LampStatus getCurrentStatus() {
		return CurrentStatus;
	}


	public void setCurrentStatus(LampStatus currentStatus) {
		CurrentStatus = currentStatus;
	}
	
	/**
	 * Scheduler calls this to give the lamp an action
	 * @param name
	 * @param status
	 */
//	public LampRequest(LampAction action){
//		this.RequestAction = action;
//	}
}
