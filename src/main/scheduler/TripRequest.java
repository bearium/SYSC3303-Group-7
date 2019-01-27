package main.scheduler;

import main.global.Direction;

public class TripRequest {
	private int pickupFloor;
	private int destinationFloor;
	private Direction direction;
	
	public TripRequest(int pickupFloor, int destinationFloor) {
		this.pickupFloor = pickupFloor;
		this.destinationFloor = destinationFloor;
		
		if (destinationFloor > pickupFloor) {
			this.direction = Direction.UP;
		} else {
			this.direction = Direction.DOWN;
		}
	}
	
	public int getPickupFloor() {
		return this.pickupFloor;
	}
	
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public boolean equals(TripRequest tripRequest) {
		if ((this.pickupFloor == tripRequest.getPickupFloor()) && (this.destinationFloor == tripRequest.getDestinationFloor()) && (this.direction == tripRequest.getDirection()) ) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append(this.pickupFloor);
		sb.append(",");
		sb.append(this.destinationFloor);
		sb.append(")");
		
		return sb.toString();
	}
}
