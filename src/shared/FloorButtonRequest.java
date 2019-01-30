package shared;

import java.util.Date;

public class FloorButtonRequest extends Request{
	public Date Time;
	public String FloorName;
	public int ButtonPressed;
	public int DestinationFloor;
	
	public FloorButtonRequest(Date time, String FloorName, int floorButton, int destinationFloor){
		this.Time = time;
		this.FloorName = FloorName;
		this.ButtonPressed = floorButton;
		this.DestinationFloor = destinationFloor;
	}
}
