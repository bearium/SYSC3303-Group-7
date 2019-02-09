package main.requests;

public class ElevatorArrivalRequest extends Request {

	/**
	 * Name of the elevator arriving
	 */
	private String ElevatorName;
	
	/**
	 * Name of the floor the elevator is arriving at
	 */
	private String FloorName;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,2};
	
	/**
	 * Create a request for an elevator's arrival at a floor
	 * @param Elevator {@link ElevatorArrivalRequest#ElevatorName}
	 * @param Floor {@link ElevatorArrivalRequest#FloorName}
	 */
	public ElevatorArrivalRequest(String Elevator, String Floor){
		this.setRequestType(RequestType);
		this.ElevatorName = Elevator;
		this.FloorName = Floor;
	}
	
	/**
	 * {@link ElevatorArrivalRequest#ElevatorName}
	 */
	public String getElevatorName() {
		return ElevatorName;
	}

	/**
	 * {@link ElevatorArrivalRequest#ElevatorName}
	 */
	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}

	/**
	 * {@link ElevatorArrivalRequest#FloorName}
	 */
	public String getFloorName() {
		return FloorName;
	}
	
	/**
	 * {@link ElevatorArrivalRequest#FloorName}
	 */
	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

	/**
	 * {@link ElevatorArrivalRequest#RequestType}
	 */
	public static byte[] getRequestType() {
		return RequestType;
	}

}
