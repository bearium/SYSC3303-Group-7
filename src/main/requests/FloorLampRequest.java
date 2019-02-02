package main.requests;

import main.global.LampStatus;
import main.global.Direction;
public class FloorLampRequest extends LampRequest {
	
	/**
	 * Direction of the floor's button to light up
	 */
	private Direction Direction;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,7};

	/**
	 * 
	 * @param Direction {@link FloorLampRequest#Direction}
	 * @param status {@link LampRequest#getCurrentStatus()}
	 */
	public FloorLampRequest(Direction Direction, LampStatus status) {

		super(status);
		this.setRequestType(RequestType);
		this.Direction = Direction;
	}

	/**
	 * {@link FloorLampRequest#Direction}
	 */
	public Direction getDirection() {
		return Direction;
	}

	/**
	 * {@link FloorLampRequest#Direction}
	 */
	public void setDirection(Direction direction) {
		Direction = direction;
	}


	/**
	 * {@link FloorLampRequest#RequestType}
	 */
	public static byte[] getRequestType() {
		return RequestType;
	}
	
}
