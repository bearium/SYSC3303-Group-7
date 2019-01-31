package main.requests;

public class ElevatorArrivalRequest extends Request {

	

	private String ElevatorName;
	private String FloorName;
	private static byte[] RequestType = new byte[] {1,2};
	
	public ElevatorArrivalRequest(String Elevator, String Floor){
		super.RequestType = RequestType;
		this.ElevatorName = Elevator;
		this.FloorName = Floor;
	}
	
	public String getElevatorName() {
		return ElevatorName;
	}

	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}

	public String getFloorName() {
		return FloorName;
	}

	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}

}
