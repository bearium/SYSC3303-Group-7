package main.requests;

public class ElevatorWaitRequest extends Request {

	/**
	 * Name of elevator waiting?
	 */
	String ElevatorName;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,9};


	/**
	 * Scheduler calls this to give the elevator a wait command
	 */
	public ElevatorWaitRequest(String elevatorName){
		this.setRequestType(RequestType);
	}

	

	/**
	 * @return the elevatorName
	 */
	public String getElevatorName() {
		return ElevatorName;
	}



	/**
	 * @param elevatorName the elevatorName to set
	 */
	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}



	public static byte[] getRequestType() {
		return RequestType;
	}

}
