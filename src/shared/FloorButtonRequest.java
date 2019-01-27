package shared;

import java.util.Date;

public class FloorButtonRequest {
	public Date Time;
	public String FloorName;
	public FloorButton ButtonPressed;
	public CarButton DestinationFloor;
	
	public FloorButtonRequest(Date time, String FloorName, FloorButton button, CarButton destinationFloor){
		this.Time = time;
		this.FloorName = FloorName;
		this.ButtonPressed = button;
		this.DestinationFloor = destinationFloor;
	}
}
