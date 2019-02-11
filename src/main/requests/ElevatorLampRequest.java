package main.requests;

import main.global.LampStatus;

public class ElevatorLampRequest extends LampRequest {
	
	/**
	 * The name of the floor button in the elevator
	 */
	private String ElevatorButton;
	
	/**
	 * The type of the request
	 */
	private static byte[] RequestType = new byte[] {1,4};

	/**
	 * Create a request to change the status of a button lamp of a given elevator
	 * @param button {@link ElevatorLampRequest#ElevatorButton}
	 * @param status {@link LampRequest#getCurrentStatus()}
	 */
	public ElevatorLampRequest(String button, LampStatus status) {
		super(status);
		this.setRequestType(RequestType);
		this.ElevatorButton = button;
		// TODO Auto-generated constructor stub
	}


	/**
	 * {@link ElevatorLampRequest#ElevatorButton}
	 * @return
	 */
	public String getElevatorButton() {
		return ElevatorButton;
	}

	/**
	 * {@link ElevatorLampRequest#ElevatorButton}
	 */
	public void setElevatorButton(String elevatorButton) {
		ElevatorButton = elevatorButton;
	}

	/**
	 * {@link ElevatorLampRequest#RequestType}
	 */
	public static byte[] getRequestType() {
		return RequestType;
	}

	
	
}

