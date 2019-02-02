package main.requests;
import main.global.Direction;

public class FloorButtonRequest extends Request{
	private String Time;
	private  String FloorName;
	private  Direction Direction;
	private  String DestinationFloor;
	private static byte[] RequestType = new byte[] {1,6};
	
	public FloorButtonRequest(String time, String FloorName, Direction Direction, String destinationFloor){
		super.RequestType = RequestType;
		this.Time = time;
		this.FloorName = FloorName;
		this.Direction = Direction;
		this.DestinationFloor = destinationFloor;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getFloorName() {
		return FloorName;
	}

	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

	public Direction getDirection() {
		return Direction;
	}

	public void setDirection(Direction direction) {
		Direction = direction;
	}

	public String getDestinationFloor() {
		return DestinationFloor;
	}

	public void setDestinationFloor(String destinationFloor) {
		DestinationFloor = destinationFloor;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}

	
	
}
