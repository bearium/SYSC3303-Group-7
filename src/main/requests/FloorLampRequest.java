package main.requests;

import main.global.LampStatus;
import main.global.Direction;
public class FloorLampRequest extends LampRequest {
<<<<<<< HEAD
	private String FloorName;
	private static byte[] RequestType = new byte[] {1,7};
	
//	public FloorLampRequest(String name, LampAction action) {
//
//		super(action);
//		this.FloorName = name;
//	}
	
	public FloorLampRequest(String name, LampStatus status) {
=======
	private Direction Direction;
	private static byte[] RequestType = new byte[] {1,7};

	public FloorLampRequest(Direction Direction, LampStatus status) {
>>>>>>> refs/remotes/origin/master

		super(status);
		super.RequestType = RequestType;
		this.Direction = Direction;
	}

	
	public Direction getDirection() {
		return Direction;
	}

<<<<<<< HEAD
	public String getFloorName() {
		return FloorName;
	}

	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

=======

	public void setDirection(Direction direction) {
		Direction = direction;
	}


>>>>>>> refs/remotes/origin/master
	public static byte[] getRequestType() {
		return RequestType;
	}
	
}
