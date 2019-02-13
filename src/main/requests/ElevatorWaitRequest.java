package main.requests;

public class ElevatorWaitRequest extends Request {

	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,9};


	/**
	 * Scheduler calls this to give the elevator a wait command
	 */
	public ElevatorWaitRequest(){
		this.setRequestType(RequestType);
	}

	

	public static byte[] getRequestType() {
		return RequestType;
	}

}
