package main.scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import main.elevatorSubsystem.ElevatorState;
import main.global.*;

/**
 * The purpose of the ElevatorMonitor is to manage an elevator's state and trip queue. The ElevatorMonitor needs to be updated every time the elevators state changes.
 * The ElevatorMonitor is responsible for determining whether the elevator can accommodate a trip request or not depending on the elevator's state. Each ElevatorMonitor 
 * is responsible for a single Elevator.
 *
 */
public class ElevatorMonitor {
	private String elevatorName;
	private LinkedHashSet<TripRequest> queue;
	private HashSet<Integer> destinationFloors;
	private HashSet<Integer> pickupFloors;
	private Direction queueDirection;
	private ArrayList<TripRequest> successfullyCompletedTripRequests;
	private ElevatorState elevatorState;
	
	public ElevatorMonitor(String elevatorName, Integer elevatorStartFloorLocation, Integer currentElevatorFloorLocation, Direction currentElevatorDirection, ElevatorStatus currentElevatorStatus, ElevatorDoorStatus currentElevatorDoorStatus, Integer totalNumberOfFloors) {
		this.elevatorName = elevatorName;
		this.queue = new LinkedHashSet<TripRequest>();
		this.destinationFloors = new HashSet<Integer>();
		this.pickupFloors = new HashSet<Integer>();
		this.successfullyCompletedTripRequests = new ArrayList<TripRequest>();
		this.queueDirection = Direction.IDLE;
		this.elevatorState = new ElevatorState(
				elevatorStartFloorLocation,
				currentElevatorFloorLocation,
				currentElevatorDirection,
				currentElevatorStatus,
				currentElevatorDoorStatus,
				totalNumberOfFloors);
	}
	
	/**
	 * Returns whether the queue is empty or not.
	 * @return
	 */
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}
	
	/**
	 * Update the elevator's floor location
	 * @param floor
	 */
	public void updateElevatorFloorLocation(Integer floor) {
		this.elevatorState.setCurrentFloor(floor);
	}
	
	/**
	 * Get the elevator's current floor location.
	 * @return
	 */
	public Integer getElevatorFloorLocation() {
		return this.elevatorState.getCurrentFloor();
	}
	
	/**
	 * Get the elevator's starting floor location.
	 * @return
	 */
	public Integer getElevatorStartingFloorLocation() {
		return this.elevatorState.getStartFloor();
	}
	
	/**
	 * Update the elevator's direction.
	 * @param floor
	 */
	public void updateElevatorDirection(Direction direction) {
		this.elevatorState.setDirection(direction);
	}
	
	/**
	 * Update the elevator's status.
	 * @param status
	 */
	public void updateElevatorStatus(ElevatorStatus status) {
		this.elevatorState.setStatus(status);
	}

	/**
	 * Get the elevator's status.
	 * @return
	 */
	public ElevatorStatus getElevatorStatus() {
		return this.elevatorState.getCurrentStatus();
	}
	
	/**
	 * Update the elevator's door status.
	 * @param status
	 */
	public void updateElevatorDoorStatus(ElevatorDoorStatus doorStatus) {
		this.elevatorState.setDoorStatus(doorStatus);
	}
	
	/**
	 * Get the next direction for this elevator based on the current state of the elevator and the contents of it's trip queues.
	 * @return
	 */
	public Direction getNextElevatorDirection() {
		Direction nextDirection = null;
		
		//If there are no more trips left, the elevator's next direction is IDLE (as far as the tripRequestQueue is concerned)
		//If there are no more trip requests in the queue, then determine whether the elevator needs to move to get back to its starting floor.
		if (this.isEmpty()) {
			if (this.elevatorState.getCurrentFloor() > this.elevatorState.getStartFloor()) {
				nextDirection = Direction.DOWN;
			} else if (this.elevatorState.getCurrentFloor() < this.elevatorState.getStartFloor()){
				nextDirection = Direction.UP;
			} else {
				nextDirection = Direction.IDLE;
			}
		} else {
			switch (this.queueDirection) {
				case UP:
						if (this.elevatorState.getCurrentFloor() > this.getLowestScheduledFloor()){
							nextDirection = Direction.DOWN;
						} else {
							nextDirection = Direction.UP;
						}
					break;
				case DOWN:
						if (this.elevatorState.getCurrentFloor() < this.getHighestScheduledFloor()){
							nextDirection = Direction.UP;
						} else {
							nextDirection = Direction.DOWN;
						}
					break;
			}
		}
		
		//this.currentElevatorDirection = nextDirection;
		return nextDirection;
	}
	
	/**
	 * Get the highest floor from this elevator's scheduled stops.
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
	 * Get the lowest floor from this elevator's scheduled stops.
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
	 * Add a first trip to the queue. This is to add a trip to an idle elevator.
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
	 * Attempt to add an en-route trip request to the trip request queue. 
	 * Queue must be empty for an en-route trip to be added. Check to ensure that the new tripRequest is in the same direction as the tripRequestQueue.
	 * Also needs to leverage the current elevator direction, to know whether this trip can be accommodated as an en-route request (only if the elevatorDirection matches the trip request queue's direction)
	 * Also checks the current elevator floor location to determine whether or not the trip Request can be accommodated. (if the pickup for the new tripRequest has been passed, depending on the direction)
	 * Duplicate requests are ignored (as sets are used).
	 * 
	 * @param tripRequest
	 * @return
	 */
	public boolean addEnRouteTripRequest(TripRequest tripRequest) {
		if (!this.isEmpty()) {
			//The tripRequest must be in the same direction as the queue direction.
			//The elevator must be moving in the same direction as the tripRequestQueue to be considered in service (as opposed to travelling in the opposite direction of the tripRequestQueue direction to service the first tripRequest)
			// An exception to the above statement, if the currentElevatorDirection is not equal yet to the queue direction but the NEXT direction will be (this will allow an elevator to take any pending requests that start at AT LEAST at the same pickup floor and go the same direction)
			if ((this.queueDirection == tripRequest.getDirection()) && ((this.elevatorState.getDirection() == this.queueDirection) || (this.getNextElevatorDirection() == this.queueDirection)) ) {	
				
				//If the pickup floor of the request is where the elevator is, only accept the trip if the elevator is stopped and doors are still open
				if (this.elevatorState.getCurrentFloor() == tripRequest.getPickupFloor()) {
					
					//If either the elevator is not stopped or the door status is not open then do not accept this trip
					if ((this.elevatorState.getCurrentStatus() != ElevatorStatus.STOPPED) || (this.elevatorState.getDoorStatus() != ElevatorDoorStatus.OPENED)){
						return false;
					}
				} else {
					//Depending on the direction of the queue, determine whether the elevator has already passed the pickup floor of the tripRequest
					switch(this.queueDirection) {
						case UP:
							//If this elevator is already passed the pickup floor then the elevator would have to backtrack to take this tripRequest, do not accept this trip.
							if (this.elevatorState.getCurrentFloor() > tripRequest.getPickupFloor()) {
								return false;
							}
							break;
						case DOWN:
							//If this elevator is already passed the pickup floor then the elevator would have to backtrack to take this tripRequest, do not accept this trip.
							if (this.elevatorState.getCurrentFloor() < tripRequest.getPickupFloor()) {
								return false;
							}
							break;
					}
				}
				
				//The trip is accepted.
				queue.add(tripRequest);
				this.destinationFloors.add(tripRequest.getDestinationFloor());
				//If the elevator is at the pickup floor it does not need to be added to the pickupFloors queue.
				if (this.elevatorState.getCurrentFloor() != tripRequest.getPickupFloor()) {
					this.pickupFloors.add(tripRequest.getPickupFloor());
				}
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * Determine whether a stop is required at this floor. 
	 * If the floor is a registered, destination stop and the queue is in service, then a stop is necessary.
	 * OR the queue is IDLE (not in service) and the elevator is at it's starting floor, then a stop is necessary.
	 * OR if the floor is a pickup floor, then regardless of the direction of the elevator, a stop is necessary.
	 * @param floor
	 * @return
	 */
	public boolean isStopRequired(int floor) {
		//If either, the floor is a destination stop AND the queue is in service (the elevator direction matches the queue direction)
		//OR, if the queue is not in service (idle), and the elevator is at it's starting floor.
		//OR, if the floor is a pickup floor and the elevator's 
		if ((this.isDestinationFloor(floor) && (this.elevatorState.getDirection() == this.queueDirection)) 
				|| ((this.queueDirection ==  Direction.IDLE) && (this.elevatorState.getCurrentFloor() == this.elevatorState.getStartFloor()))
				|| (this.isPickupFloor(floor))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method is used to advise the ElevatorMonitor that a stop has occurred. This method will then update the ElevatorMonitor given a stop occurred. It leverages the elevator's current 
	 * state to know where the stop has occurred. This method will clear the registered stop for the floor that the elevator is at. If the elevator has stopped at a destination floor, then
	 * the original trip request can be marked as completed.
	 */
	public HashSet<TripRequest> stopOccurred() {
		HashSet<TripRequest> completedTrips = new HashSet<TripRequest>();
		
		//IS this stop a destination? If so, this destination floor can be removed from the destination queue (this removes the tripRequest as well from the tripRequestQueue, marks as successfully compelted)
		if (this.isDestinationFloor(this.elevatorState.getCurrentFloor())) {
			if (this.removeDestinationFloor(this.elevatorState.getCurrentFloor())) {
				completedTrips = this.removeTripsWithDestinationFloor(this.elevatorState.getCurrentFloor());
			}
		
			//Update the queue direction to IDLE if there are no more trips left in the queue
			if (this.isEmpty()) {
				this.queueDirection = Direction.IDLE;
			}	
		}
		
		//Is this stop a pickup? IF so, this pickup Floor can be removed from the pickup queue (this does not mark a trip as successfully completed in the tripRequestQueue)
		if (this.isPickupFloor(this.elevatorState.getCurrentFloor())){
			this.removePickupFloor(this.elevatorState.getCurrentFloor());
		}
		
		return completedTrips;
	}
	
	/**
	 * Remove a floor from the registered destination floors, if the floor is registered..
	 * @param floor
	 * @return
	 */
	private boolean removeDestinationFloor(int floor) {
		if (this.destinationFloors.contains(floor)) {
			this.destinationFloors.remove(floor);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a pickup floor from the registered pickup floors, if the floor is registered..
	 * @param floor
	 * @return
	 */
	private boolean removePickupFloor(int floor) {
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
	private boolean containsFloor(int floor) {
		if (this.pickupFloors.contains(floor) || this.destinationFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determine whether floor is a destination floor.
	 * 
	 * @param floor
	 * @return
	 */
	public boolean isDestinationFloor(int floor) {
		if (this.destinationFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determine whether floor is a pickup floor.
	 * 
	 * @param floor
	 * @return
	 */
	public boolean isPickupFloor(int floor) {
		if (this.pickupFloors.contains(floor)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Remove any trip requests from the trip queue that have this destination.
	 * 
	 * @param destination
	 */
	private HashSet<TripRequest> removeTripsWithDestinationFloor(int destination) {
		HashSet<TripRequest> completedTrips = new HashSet<TripRequest>();
		
		//An iterator is used instead of a simple foreach over the set because in a foreach elements cannot be removed from a hashset properly.
		Iterator<TripRequest> iterator = queue.iterator();
		while (iterator.hasNext()) {
			TripRequest tripRequest = iterator.next();
			if (destination == tripRequest.getDestinationFloor()) {
				this.successfullyCompletedTripRequests.add(tripRequest);
				completedTrips.add(tripRequest);
				iterator.remove();
			}
		}
		return completedTrips;
	}
	
	/**
	 * Get the current direction of the queue.
	 * @return
	 */
	public Direction getQueueDirection() {
		return this.queueDirection;
	}
	
	
	/**
	 * Create a string output that contains the elevator state and the state of the queue's.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("Elevator name: " + this.elevatorName + "\n");
		sb.append("Current floor: " + this.elevatorState.getCurrentFloor() + "\n");
		sb.append("Current direction: " + this.elevatorState.getDirection() + "\n");
		sb.append("Current elevator status: " + this.elevatorState.getCurrentStatus() + "\n");
		sb.append("Current door status: " + this.elevatorState.getDoorStatus() + "\n");
		sb.append("Trip request queue: ");
		
		sb.append("[");
		Iterator<TripRequest> queueIterator = this.queue.iterator();
		while (queueIterator.hasNext()) {
			TripRequest tripRequest = queueIterator.next();
			sb.append(tripRequest.toString());
			if (queueIterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]\n");
		
		sb.append("Floor pickups remaining: " + this.pickupFloors.toString() + "\n");
		sb.append("Floor destinations remaining: " + this.destinationFloors.toString() + "\n");
		
		sb.append("Completed trips: ");
		sb.append("[");
		Iterator<TripRequest> completedTripsIterator = this.successfullyCompletedTripRequests.iterator();
		while (completedTripsIterator.hasNext()) {
			TripRequest tripRequest = completedTripsIterator.next();
			sb.append(tripRequest.toString());
			if (completedTripsIterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("]\n");
		return sb.toString();
	}
}
