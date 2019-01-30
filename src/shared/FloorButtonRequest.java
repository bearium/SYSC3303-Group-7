package shared;
import main.global.Direction;
import java.util.Date;

public class FloorButtonRequest extends Request{
	public Date Time;
	public String FloorName;
	public Direction Direction;
	public String DestinationFloor;
	
	public FloorButtonRequest(Date time, String FloorName, Direction Direction, String destinationFloor){
		this.Time = time;
		this.FloorName = FloorName;
		this.Direction = Direction;
		this.DestinationFloor = destinationFloor;
	}
	
}
