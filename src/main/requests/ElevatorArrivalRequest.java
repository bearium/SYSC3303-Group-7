package main.requests;
import main.global.Direction;

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
	 * Direction the elevator is currently moving in
	 */
	private Direction Direction;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,2};
	
	/**
	 * Create a request for an elevator's arrival at a floor
	 * @param elevator {@link ElevatorArrivalRequest#ElevatorName}
	 * @param floor {@link ElevatorArrivalRequest#FloorName}
	 */
	public ElevatorArrivalRequest(String elevator, String floor, Direction direction){
		this.setRequestType(RequestType);
		this.setElevatorName(elevator);
		this.setFloorName(floor); 
		this.setDirection(direction);
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

	/**
	 * {@link ElevatorArrivalRequest#Direction}
	 */
	public Direction getDirection() {
		return Direction;
	}

	/**
	 * {@link ElevatorArrivalRequest#Direction}
	 */
	public void setDirection(Direction direction) {
		Direction = direction;
	}

}
