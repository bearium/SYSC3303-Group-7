package shared;

import main.global.Direction;
import main.global.LampStatus;

public class DirectionLampRequest extends LampRequest{
	
	Direction LampDirection;
	static byte[] RequestType = new byte[] {1,1};
	
//	public DirectionLampRequest(Direction direction, LampAction action) {
//		super(action);
//		this.LampDirection = direction;
//		// TODO Auto-generated constructor stub
//	}
	
	public DirectionLampRequest(Direction direction, LampStatus status) {
		super(status);
		this.LampDirection = direction;
		// TODO Auto-generated constructor stub
	}
}
