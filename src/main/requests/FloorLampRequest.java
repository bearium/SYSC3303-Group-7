package main.requests;

import main.global.LampStatus;
import main.global.Direction;
public class FloorLampRequest extends LampRequest {
	private Direction Direction;
	private static byte[] RequestType = new byte[] {1,7};

	public FloorLampRequest(Direction Direction, LampStatus status) {

		super(status);
		super.RequestType = RequestType;
		this.Direction = Direction;
	}

	
	public Direction getDirection() {
		return Direction;
	}


	public void setDirection(Direction direction) {
		Direction = direction;
	}


	public static byte[] getRequestType() {
		return RequestType;
	}
	
}
