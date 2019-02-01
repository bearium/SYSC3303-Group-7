package main.requests;

import main.global.LampStatus;

public class FloorLampRequest extends LampRequest {
	private String FloorName;
	private static byte[] RequestType = new byte[] {1,7};
	
//	public FloorLampRequest(String name, LampAction action) {
//
//		super(action);
//		this.FloorName = name;
//	}
	
	public FloorLampRequest(String name, LampStatus status) {

		super(status);
		super.RequestType = RequestType;
		this.FloorName = name;
	}

	public String getFloorName() {
		return FloorName;
	}

	public void setFloorName(String floorName) {
		FloorName = floorName;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}
	
}
