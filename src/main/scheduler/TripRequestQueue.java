package main.scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import main.global.Direction;

public class TripRequestQueue {
	private LinkedHashSet<TripRequest> queue;
	private HashSet<Integer> destinationFloors;
	private HashSet<Integer> pickupFloors;
	private Integer currentElevatorFloorLocation;
	private Direction currentElevatorDirection;
	private Direction queueDirection;
	private ArrayList<TripRequest> successfullyCompletedTripRequests;
	
	public TripRequestQueue(Integer currentElevatorFloorLocation, Direction currentElevatorDirection) {
		this.queue = new LinkedHashSet<TripRequest>();
		this.destinationFloors = new HashSet<Integer>();
		this.pickupFloors = new HashSet<Integer>();
		this.successfullyCompletedTripRequests = new ArrayList<TripRequest>();
		this.queueDirection = Direction.IDLE;
		this.currentElevatorFloorLocation = currentElevatorFloorLocation;
		this.currentElevatorDirection = currentElevatorDirection;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}
	
	/**
	 * 
	 * @param floor
	 */
	public void updateCurrentElevatorFloorLocation(Integer floor) {
		this.currentElevatorFloorLocation = floor;
	}
	
	/**
	 * 
	 * @param floor
	 */
	public void updateCurrentElevatorDirection(Direction direction) {
		this.currentElevatorDirection = direction;
	}
	
	/**
	 * 
	 * @return
	 */
	public Direction getNextElevatorDirection() {
		Iterator<TripRequest> iterator = queue.iterator();
		TripRequest tripRequest = null; 
		Direction nextDirection = null;
		//Get the first tripRequest
		if (iterator.hasNext()) {
			tripRequest = iterator.next();
		}
		
		switch (this.queueDirection) {
			case UP:
					if (this.currentElevatorFloorLocation > this.getLowestScheduledFloor()){
						nextDirection = Direction.DOWN;
					} else {
						nextDirection = Direction.UP;
					}
				break;
			case DOWN:
					if (this.currentElevatorFloorLocation < this.getHighestScheduledFloor()){
						nextDirection = Direction.UP;
					} else {
						nextDirection = Direction.DOWN;
					}
				break;
		}
		
		this.currentElevatorDirection = nextDirection;
		return nextDirection;
	}
	
	/**
	 * 
	 * @return
	 */
	private Integer getHighestScheduledFloor() {
		HashSet<Integer> allStops = new HashSet<Integer>();
		allStops.addAll(pickupFloors);
		allStops.addAll(destinationFloors);

		Iterator<Integer> iterator = allStops.iterator();
		Integer currentHighestFloor = iterator.next();
		while (iterator.hasNext()) {
			Integer nextFloorToCompare = iterator.next();
			if (nextFloorToCompare > currentHighestFloor) {
				currentHighestFloor = nextFloorToCompare;
			}
		}
		
		return currentHighestFloor;
	}
	
	/**
	 * 
	 * @return
	 */
	private Integer getLowestScheduledFloor() {
		HashSet<Integer> allStops = new HashSet<Integer>();
		allStops.addAll(pickupFloors);
		allStops.addAll(destinationFloors);
		
		Iterator<Integer> iterator = allStops.iterator();
		Integer currentLowestFloor = iterator.next();
		while (iterator.hasNext()) {
			Integer nextFloorToCompare = iterator.next();
			if (nextFloorToCompare < currentLowestFloor) {
				currentLowestFloor = nextFloorToCompare;
			}
		}
		
		return currentLowestFloor;
	}
	
	/**
	 * 
	 * @param tripRequest
	 * @return
	 */
	public boolean addFirstTripRequest(TripRequest tripRequest) {
		if (this.isEmpty()) {
			queue.add(tripRequest);
			this.queueDirection = tripRequest.getDirection();
			this.destinationFloors.add(tripRequest.getDestinationFloor());
			this.pickupFloors.add(tripRequest.getPickupFloor());
			return true;
		}
		return false;
	}
	
	/**
	 * Attempt to add an en route trip request to the trip request queue. 
	 * Queue must be empty for an en route trip to be added. Check to ensure that the new tripRequest is in the same direction as the tripRequestQueue.
	 * Also needs to know the current elevatorDirection, to know whether this trip can be accommodate en route requests (only if the elevatorDirection matches the trip request queue's direction)
	 * Also checks the currentElevatorFloorLocation to deteremine whether or not the trip Request can be accommodated. (if the pickup has been passed, depending on the direction)
	 * Duplicate requests are ignored (as sets are used).
	 * 
	 * @param tripRequest
	 * @return
	 */
	public boolean addEnRouteTripRequest(TripRequest tripRequest) {
		if (!this.isEmpty()) {
			// Otherwise, if the tripRequest is in the same direction as the tripRequestQueue, it may be eligible to be added to the queue, depending on the location and direction of the elevator
			//The elevator must be moving in the same direction as the tripRequestQueue to be considered in service (as opposed to travelling in the opposite direction of the tripRequestQueue direction to service the first tripRequest)
			if ((this.queueDirection == tripRequest.getDirection()) && (this.currentElevatorDirection == this.queueDirection) ) {
				//Depending on the direction of the elevator/queue/trip, determine whether the elevator has already passed the pickup floor of the tripRequest
				switch(currentElevatorDirection) {
				//TODO Probably need to have a specific condition if the floor's are equal, likely would need to check the status of the elevator's door...
					case UP:
						//If this elevator is already passed the pickup floor then the elevator would have to backtrack to take this tripRequest, skip to check next elevator
						if (currentElevatorFloorLocation > tripRequest.getPickupFloor()) {
							return false;
						}
						break;
					case DOWN:
						//If this elevator is already passed the pickup floor then the elevator would have to backtrack to take this tripRequest, skip to check next elevator
						if (currentElevatorFloorLocation < tripRequest.getPickupFloor()) {
							return false;
						}
						break;
				}
				
				queue.add(tripRequest);
				this.destinationFloors.add(tripRequest.getDestinationFloor());
				this.pickupFloors.add(tripRequest.getPickupFloor());
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public boolean isStopRequired(int floor) {
		if (this.containsFloor(floor) && (this.currentElevatorDirection == this.queueDirection)) {
			return true;
		}
		return false;
	}
	
	/**
	 * If the floor is a destination floor, remove it from destinationFloors and remove the tripRequest from the tripRequestQueue.
	 * If there are no more tripRequests, then set the queueDirection to IDLE.
	 * @param floor
	 * @return
	 */
	public boolean removeDestinationFloor(int floor) {
		if (this.destinationFloors.contains(floor)) {
			this.destinationFloors.remove(floor);
			this.removeTripsWithDestinationFloor(floor);
			if (this.isEmpty()) {
				this.queueDirection = Direction.IDLE;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param floor
	 * @return
	 */
	public boolean removePickupFloor(int floor) {
		if (this.pickupFloors.contains(floor)) {
			this.pickupFloors.remove(floor);
			return true;
		}
		return false;
	}
	
	/**
	 * This method determines whether a floor is contained in the queue. This method does not distinguish between pickup or destination floors.
	 * 
	 * @param floor
	 * @return
	 */
	public boolean containsFloor(int floor) {
		if (this.pickupFloors.contains(floor) || this.destinationFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method will remove any trips from the queue if the 'floor' value is the destination. This method is meant to be called when the
	 * elevator arrives at a floor. This is used to determine if 
	 * 
	 * @param floor
	 * @return
	 */
	public boolean containsDestinationFloor(int floor) {
		if (this.destinationFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method will remove any trips from the queue if the 'floor' value is the destination. This method is meant to be called when the
	 * elevator arrives at a floor. This is used to determine if 
	 * 
	 * @param floor
	 * @return
	 */
	public boolean containsPickupFloor(int floor) {
		if (this.pickupFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Remove any trips from the queue that have a specific destination.
	 * 
	 * @param destination
	 */
	private void removeTripsWithDestinationFloor(int destination) {
		//An iterator is used instead of a simple foreach over the set because in a foreach elements cannot be removed from a hashset properly.
		Iterator<TripRequest> iterator = queue.iterator();
		while (iterator.hasNext()) {
			TripRequest tripRequest = iterator.next();
			if (destination == tripRequest.getDestinationFloor()) {
				this.successfullyCompletedTripRequests.add(tripRequest);
				iterator.remove();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Direction getQueueDirection() {
		return this.queueDirection;
	}
	
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		Iterator<TripRequest> iterator = queue.iterator();
		while (iterator.hasNext()) {
			TripRequest tripRequest = iterator.next();
			sb.append(tripRequest.toString());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
