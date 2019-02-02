package main.scheduler;

import main.global.Direction;

/**
 * The TripRequest will model a trip request. It includes a pickup floor, destination floor and a direction.
 *
 */
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
	
	/**
	 * Get the pickup floor.
	 * @return
	 */
	public int getPickupFloor() {
		return this.pickupFloor;
	}
	
	/**
	 * Get the destination floor.
	 * @return
	 */
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	/**
	 * Get the direction.
	 * @return
	 */
	public Direction getDirection() {
		return this.direction;
	}
	
	/**
	 * A way to compare trip request objects. This is used to prevent duplicate trip requests in any set collection.
	 * @param tripRequest
	 * @return
	 */
	public boolean equals(TripRequest tripRequest) {
		if ((this.pickupFloor == tripRequest.getPickupFloor()) && (this.destinationFloor == tripRequest.getDestinationFloor()) && (this.direction == tripRequest.getDirection()) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Coordinate notation to depict a trip request ex -> (pickup, destination)
	 */
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
