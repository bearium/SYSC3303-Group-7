package main.requests;

import main.global.Direction;
import main.global.LampStatus;

public class DirectionLampRequest extends LampRequest{
	
	private Direction LampDirection;
	private static byte[] RequestType = new byte[] {1,1};
	
//	public DirectionLampRequest(Direction direction, LampAction action) {
//		super(action);
//		this.LampDirection = direction;
//		// TODO Auto-generated constructor stub
//	}
	
	public DirectionLampRequest(Direction direction, LampStatus status) {
		super(status);
		super.RequestType = RequestType;
		this.LampDirection = direction;
		// TODO Auto-generated constructor stub
	}

	public Direction getLampDirection() {
		return LampDirection;
	}

	public void setLampDirection(Direction lampDirection) {
		LampDirection = lampDirection;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}

}
