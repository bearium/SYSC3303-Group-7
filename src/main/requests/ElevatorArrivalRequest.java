package main.requests;

public class ElevatorArrivalRequest extends Request {

	String ElevatorName;
	String FloorName;
	static byte[] RequestType = new byte[] {1,2};
	
	public ElevatorArrivalRequest(String Elevator, String Floor){
		super.RequestType = RequestType;
		this.ElevatorName = Elevator;
		this.FloorName = Floor;
	}
}
