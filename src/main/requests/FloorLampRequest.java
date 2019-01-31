package shared;

import main.global.LampStatus;

public class FloorLampRequest extends LampRequest {
	String FloorName;
	static byte[] RequestType = new byte[] {1,7};
	
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

}
