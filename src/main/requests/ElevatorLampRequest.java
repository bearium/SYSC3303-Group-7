package main.requests;

import main.global.LampStatus;

public class ElevatorLampRequest extends LampRequest {
	String ElevatorName;
	String ElevatorButton;
	static byte[] RequestType = new byte[] {1,4};
	
//	public ElevatorLampRequest(String name, String button, LampAction action) {
	
//		super(action);
//		this.ElevatorButton = button;
//		this.ElevatorName = name;
//		// TODO Auto-generated constructor stub
//	}
	
	public ElevatorLampRequest(String name, String button, LampStatus status) {
		super(status);
		super.RequestType = RequestType;
		this.ElevatorButton = button;
		this.ElevatorName = name;
		// TODO Auto-generated constructor stub
	}
}

