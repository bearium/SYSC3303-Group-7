package main.requests;

import main.global.LampStatus;

public class ElevatorLampRequest extends LampRequest {
	private String ElevatorButton;
	private static byte[] RequestType = new byte[] {1,4};
	
//	public ElevatorLampRequest(String name, String button, LampAction action) {
	
//		super(action);
//		this.ElevatorButton = button;
//		this.ElevatorName = name;
//		// TODO Auto-generated constructor stub
//	}
	
	public ElevatorLampRequest(String button, LampStatus status) {
		super(status);
		super.RequestType = RequestType;
		this.ElevatorButton = button;
		// TODO Auto-generated constructor stub
	}

	public String getElevatorButton() {
		return ElevatorButton;
	}

	public void setElevatorButton(String elevatorButton) {
		ElevatorButton = elevatorButton;
	}

	public static byte[] getRequestType() {
		return RequestType;
	}

	
	
}

