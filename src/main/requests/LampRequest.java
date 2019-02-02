package main.requests;
import main.global.LampStatus;

public class LampRequest extends Request{
	
	/**
	 * The action or status of the lamp
	 */
	private LampStatus CurrentStatus;
	
	/**
	 * This type of request will send be created to send actions to the lamps across the system
	 * @param status action or status of the lamp
	 */
	public LampRequest(LampStatus status){
		this.CurrentStatus = status;
	}

	/**
	 * {@link LampRequest#CurrentStatus}
	 */
	public LampStatus getCurrentStatus() {
		return CurrentStatus;
	}

	/**
	 * {@link LampRequest#CurrentStatus}
	 */
	public void setCurrentStatus(LampStatus currentStatus) {
		CurrentStatus = currentStatus;
	}
}
