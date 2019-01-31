package shared;
import main.global.Direction;
import java.util.Date;

public class FloorButtonRequest extends Request{
	public Date Time;
	public String FloorName;
	public Direction Direction;
	public String DestinationFloor;
	static byte[] RequestType = new byte[] {1,6};
	
	public FloorButtonRequest(Date time, String FloorName, Direction Direction, String destinationFloor){
		super.RequestType = RequestType;
		this.Time = time;
		this.FloorName = FloorName;
		this.Direction = Direction;
		this.DestinationFloor = destinationFloor;
	}
	
}
