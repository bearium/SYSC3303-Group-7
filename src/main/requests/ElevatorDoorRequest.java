package shared;
import main.global.ElevatorDoorStatus;
public class ElevatorDoorRequest extends Request{
	//public enum DoorStatus {CLOSING, CLOSED, OPEN, OPENING}
	//public enum DoorAction {CLOSE, OPEN}
	
	private  String ElevatorName;
	//public DoorStatus CurrentStatus;
	private  ElevatorDoorStatus RequestAction;

	
	private static byte[] RequestType = new byte[] {1,3};
	
	/**
	 * Elevator will create this request to inform Scheduler of current motor status
	 * 
	 * @param name
	 * @param status

	public ElevatorDoorRequest(String name, DoorStatus status){
		this.ElevatorName = name;
		this.CurrentStatus = status;
	}	 
	*/
	
	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name
	 * @param status
	 */
	public ElevatorDoorRequest(String name, ElevatorDoorStatus action){
		super.RequestType = RequestType;
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
