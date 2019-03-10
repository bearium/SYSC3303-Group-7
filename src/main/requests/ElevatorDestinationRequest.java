package main.requests;

import main.global.Fault;

public class ElevatorDestinationRequest extends Request {

	/**
	 * Name of the floor the elevator is moving to OR the floor elevator needs to visit
	 */
	private String DestinationFloor;
	
	/**
	 * Name of the elevator that is moving 
	 */
	private String ElevatorName;
	
	/**
	 * Name of the floor elevator is pickup up at
	 */
	private String PickupFloor;
	
	/**
	 * Fault included
	 */
	private Fault fault;
	
	/**
	 * Type of request for parsing purposes
	 */
	private static byte[] RequestType = new byte[] {1,8};


	/**
	 * 
	 * @param pickupFloor Floor to pickup at
	 * @param destName Destination floor
	 * @param elevatorName Name of elevator
	 */
	public ElevatorDestinationRequest(String pickupFloor, String destName, String elevatorName){
		this.setRequestType(RequestType);
		this.setPickupFloor(pickupFloor);
		this.setDestinationFloor(destName);
		this.setElevatorName(elevatorName);
	}
	
	public ElevatorDestinationRequest(String pickupFloor, String destName, String elevatorName, Fault fault){
		this(pickupFloor, destName, elevatorName);
		this.setFault(fault);
	}

	

	public static byte[] getRequestType() {
		return RequestType;
	}


	/**
	 * {@link ElevatorDestinationRequest#DestinationFloor}
	 * @return the floorName
	 */
	public String getDestinationFloor() {
		return DestinationFloor;
	}



	/**
	 * {@link ElevatorDestinationRequest#DestinationFloor}
	 * @param floorName the floorName to set
	 */
	public void setDestinationFloor(String floorName) {
		DestinationFloor = floorName;
	}



	/**
	 * @return the elevatorName
	 */
	public String getElevatorName() {
		return ElevatorName;
	}



	/**
	 * @param elevatorName the elevatorName to set
	 */
	public void setElevatorName(String elevatorName) {
		ElevatorName = elevatorName;
	}



	/**
	 * @return the pickupFloor
	 */
	public String getPickupFloor() {
		return PickupFloor;
	}



	/**
	 * @param pickupFloor the pickupFloor to set
	 */
	public void setPickupFloor(String pickupFloor) {
		PickupFloor = pickupFloor;
	}

	/**
	 * @return the fault
	 */
	public Fault getFault() {
		return fault;
	}

	/**
	 * @param fault the fault to set
	 */
	public void setFault(Fault fault) {
		this.fault = fault;
	}



}

