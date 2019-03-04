package main.requests;
import main.global.Direction;
import main.global.Fault;
public class FloorButtonRequest extends Request{
	
	/**
	 * The date at which the elevator was requested (or button was pressed)
	 */
	private String Time;
	
	/**
	 * The name of the floor where the button was pressed
	 */
	private  String FloorName;
	
	/**
	 * The direction of the button pressed
	 */
	private  Direction Direction;
	
	/**
	 * The destination of the passenger
	 */
	private String DestinationFloor;
	
	/**
	 * Fault in elevator, Optional
	 */
	private Fault Fault;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,6};
	
	/**
	 * 
	 * @param time {@link FloorButtonRequest#Time}
	 * @param FloorName {@link FloorButtonRequest#FloorName}
	 * @param Direction {@link FloorButtonRequest#Direction}
	 * @param destinationFloor {@link FloorButtonRequest#DestinationFloor}
	 */
	public FloorButtonRequest(String time, String FloorName, Direction Direction, String destination){
		this.setRequestType(RequestType);
		this.Time = time;
		this.FloorName = FloorName;
		this.Direction = Direction;
		this.setDestinationFloor(destination);
		
	}
	
	public FloorButtonRequest(String time, String FloorName, Direction Direction, String destination, Fault fault){
		this(time, FloorName, Direction, destination);
		this.setFault(fault);
	}

	/**
	 * {@link FloorButtonRequest#Time}
	 */
	public String getTime() {
		return Time;
	}

	/**
	 * {@link FloorButtonRequest#Time}
	 */
	public void setTime(String time) {
		Time = time;
	}

	/**
	 * {@link FloorButtonRequest#FloorName}
	 */
	public String getFloorName() {
		return FloorName;
	}

	/**
	 * {@link FloorButtonRequest#FloorName}
	 */
	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

	/**
	 * {@link FloorButtonRequest#Direction}
	 */
	public Direction getDirection() {
		return Direction;
	}

	/**
	 * {@link FloorButtonRequest#Direction}
	 */
	public void setDirection(Direction direction) {
		Direction = direction;
	}

	/**
	 * @return the destinationFloor
	 */
	public String getDestinationFloor() {
		return DestinationFloor;
	}

	/**
	 * @param destinationFloor the destinationFloor to set
	 */
	public void setDestinationFloor(String destinationFloor) {
		DestinationFloor = destinationFloor;
	}

	/**
	 * {@link FloorButtonRequest#RequestType}
	 */
	public static byte[] getRequestType() {
		return RequestType;
	}

	/**
	 * {@link FloorButtonRequest#Fault}
	 */
	public Fault getFault() {
		return Fault;
	}
	
	/**
	 * {@link FloorButtonRequest#Fault}
	 */
	public void setFault(Fault fault) {
		Fault = fault;
	}
	
	
}
