package shared;

public class FloorLampRequest extends LampRequest {
	String FloorName;
	
	public FloorLampRequest(String name, LampAction action) {

		super(action);
		this.FloorName = name;
	}
	
	public FloorLampRequest(String name, LampStatus status) {

		super(status);
		this.FloorName = name;
	}

}
