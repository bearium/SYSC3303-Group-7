package main.requests;

public class ElevatorDestinationRequest extends Request {

	/**
	 * Name of the floor the elevator is moving to OR the floor elevator needs to visit
	 */
	private String FloorName;

	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,3};


	/**
	 * Scheduler calls this to give the elevator an action
	 * @param name 
	 * @param status
	 */
	public ElevatorDestinationRequest(String name){
		this.setRequestType(RequestType);
		this.setFloorName(name);
	}

	

	public static byte[] getRequestType() {
		return RequestType;
	}


	/**
	 * {@link ElevatorDestinationRequest#FloorName}
	 * @return the floorName
	 */
	public String getFloorName() {
		return FloorName;
	}



	/**
	 * {@link ElevatorDestinationRequest#FloorName}
	 * @param floorName the floorName to set
	 */
	public void setFloorName(String floorName) {
		FloorName = floorName;
	}



}

