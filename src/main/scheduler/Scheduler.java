package main.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import main.ElevatorSystemComponent;
import main.global.Direction;
import main.global.ElevatorSystemConfiguration;
import main.server.Server;

public class Scheduler implements Runnable, ElevatorSystemComponent {
	
	private String name;
	private Server server;
	private Thread serverThread;
	private Queue<String> eventsQueue;
	private boolean debug = false;
	private HashMap<String, Integer> portsByElevatorName;								//key -> elevator name, value -> port number
	private HashMap<String, Integer> portsByFloorName;									//key -> floor number, value -> port number
	private HashMap<String, Integer> currentFloorLocationByElevatorName;				//key -> elevator name, value -> current floor location
	private HashMap<String, Integer> startFloorLocationByElevatorName;				//key -> elevator name, value -> current floor location
	private HashMap<String, TripRequestQueue> tripRequestQueueByElevatorName;
	private HashMap<String, Direction> directionByElevatorName;
	private ArrayList<TripRequest> pendingTripRequests;
	
	public Scheduler(String name, int port, HashMap<String, HashMap<String, String>> elevatorConfiguration, HashMap<String, HashMap<String, String>> floorConfigurations) {
		this.name = name;
		this.eventsQueue = new LinkedList<String>();
		this.portsByElevatorName = new HashMap<String, Integer>();
		this.portsByFloorName = new HashMap<String, Integer>();
		this.currentFloorLocationByElevatorName = new HashMap<String, Integer>();
		this.startFloorLocationByElevatorName = new HashMap<String, Integer>();
		this.tripRequestQueueByElevatorName = new HashMap<String, TripRequestQueue>();
		this.directionByElevatorName = new HashMap<String, Direction>();
		this.pendingTripRequests = new ArrayList<TripRequest>();
		
		//Initialize infrastructure configurations (elevators/floors)
		this.init(elevatorConfiguration, floorConfigurations);
		
		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		serverThread = new Thread(new Server(this, port, this.debug), name);
		serverThread.start();
	}
	
	/**
	 * The purpose of this method is to initialize some of the data structures used to monitor the state of the elevator systems by the scheduler.
	 * 
	 * @param elevatorConfiguration
	 * @param floorConfigurations
	 */
	public void init(HashMap<String, HashMap<String, String>> elevatorConfiguration, HashMap<String, HashMap<String, String>> floorConfigurations) {
		//Initialize data structures for both elevators and floors
		for (String elevatorName : elevatorConfiguration.keySet()) {
			HashMap<String, String> config = elevatorConfiguration.get(elevatorName);
			
			this.portsByElevatorName.put(elevatorName, Integer.parseInt(config.get("port")));
			this.currentFloorLocationByElevatorName.put(elevatorName, Integer.parseInt(config.get("startFloor")));
			this.startFloorLocationByElevatorName.put(elevatorName, Integer.parseInt(config.get("startFloor")));
			this.directionByElevatorName.put(elevatorName, Direction.IDLE);
			
			//Initialize tripRequestQueues for each elevator
			this.tripRequestQueueByElevatorName.put(elevatorName, new TripRequestQueue(Integer.parseInt(config.get("startFloor")), Direction.IDLE));
		}
		
		//Initialize data structures for both elevators and floors
		for (String floorName : floorConfigurations.keySet()) {
			HashMap<String, String> config = floorConfigurations.get(floorName);
			
			this.portsByFloorName.put(floorName, Integer.parseInt(config.get("port")));
		}
	}
	
	@Override
	public synchronized void receiveEvent(String event) {
		eventsQueue.add(event);
	}

	@Override
	public synchronized String getNextEvent() {
		return eventsQueue.poll();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void run() {
		System.out.println("");
	}
	
	private void handleEvent() {
		//switch statement corresponding to different "event handlers"
	}
	

	/**
	 * This method attempts to assign an incoming tripRequest to one of the elevators.
	 * The first preference is to assign the tripRequest to an in service elevator, if the trip is en route. 
	 * If there are no en route options, attempt to find an idle elevator to service the tripRequest. 
	 * If this is not possible then the tripRequest will be put in a pending queue.
	 * 
	 * @param pickupFloorNumber
	 * @param destinationFloorNumber
	 * @param direction
	 */
	private void eventTripRequestReceived(int pickupFloorNumber, int destinationFloorNumber, Direction direction) {
		TripRequest tripRequest = new TripRequest(pickupFloorNumber, destinationFloorNumber);
		
		//See if any elevators currently in service can take this tripRequest as an en-route trip
		if (this.assignTripToInServiceElevator(tripRequest)) {
			//TODO send lamps
			return;
		} 
		
		//See if any idle elevators are available to take this tripRequest
		if (this.assignTripToIdleElevator(tripRequest)) {
			//TODO send lamps
			return;
		} 
		
		//Add this tripRequest to the pendingTripRequests queue
		this.pendingTripRequests.add(tripRequest);
	}

	/**
	 * 
	 * @param elevatorName
	 * @param floorNumber
	 */
	private void eventElevatorArrivalNotice(String elevatorName, int floorNumber) {
		TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);
		
		//Update the scheduler and tripRequestQueue with the new floor of the elevator
		this.currentFloorLocationByElevatorName.put(elevatorName, floorNumber);
		tripRequestQueue.updateCurrentElevatorFloorLocation(floorNumber);

		//Check if this elevator needs to stop at this floor
		if(tripRequestQueue.isStopRequired(floorNumber)) {
			//TODO send an elevator stop event
			System.out.println("DEBUG - Sending an Elevator Stop event");
		}
				

	}
	
	/**
	 * When an elevator stops, take this opportunity to update the tripRequestQueue.
	 * If this stop is a destination for a tripRequest, the corresponding tripRequest is completed, and the tripRequestQueue needs to be updated to reflect this
	 * If this stop is a pickup for a tripRequest, the tripRequest needs to remove this pickup from it's queue of pickups. but the tripRequest still is in service in the tripRequestQueue.
	 * 
	 * When this is completed, send an elevator open door event.
	 * @param elevatorName
	 */
	private void eventElevatorStopped(String elevatorName) {
		TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);
		int currentFloor = this.currentFloorLocationByElevatorName.get(elevatorName);
		
		//IS this stop a destination? If so, this destination floor can be removed from the destination queue (this removes the tripRequest as well from the tripRequestQueue, marks as successfully compelted)
		if (tripRequestQueue.containsDestinationFloor(currentFloor)) {
			tripRequestQueue.removeDestinationFloor(currentFloor);
		}
		//Is this stop a pickup? IF so, this pickup Floor can be removed from the pickup queue (this does not mark a trip as successfully completed in the tripRequestQueue)
		if (tripRequestQueue.containsPickupFloor(currentFloor)){
			tripRequestQueue.removePickupFloor(currentFloor);
		}
		
		//TODO send an elevator open door event
		System.out.println("DEBUG - Sending an Elevator OpenDoor event");
	}
	
	/**
	 * When confirmation has been received that the elevator has opened its doors, determine whether this elevator has more trips.
	 * If the tripRequestQueue is not empty, then 
	 * @param elevatorName
	 */
	private void eventElevatorDoorOpened(String elevatorName) {
		TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);

		//Are there still more floors to visit?
		if (!tripRequestQueue.isEmpty()) {
			//TODO send a close door event
			System.out.println("DEBUG - Sending an Elevator CloseDoor event");
		} else {
			this.directionByElevatorName.put(elevatorName, Direction.IDLE);
			tripRequestQueue.updateCurrentElevatorDirection(Direction.IDLE);
			//Are there are pending trip requests, these need to get assigned to this elevator
			if (this.pendingTripRequests.isEmpty()) {
				//For now, this elevator can go into idle mode until next request (wait at current floor with doors open)
				//TODO create a go back to default floor method

			} else {
				
			}
		}
	}
	
	/**
	 * 
	 * @param elevatorName
	 */
	private void eventElevatorDoorClosed(String elevatorName) {
		//Get the tripRequestQueue for this elevator
		TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);

		//Get the next direction for this elevator based on the tripRequestQueue
		Direction nextDirection = tripRequestQueue.getNextElevatorDirection();
		
		//Update elevator location in the scheduler
		this.directionByElevatorName.put(elevatorName, nextDirection);
		
		//TODO send an elevator move event in the next direction it needs to go
		System.out.println("DEBUG - Sending an Elevator Move " + nextDirection.toString() + " event");
	}
	
	private void sendElevatorMoveEvent(String elevatorName, Direction direction) {

	}
	
	private boolean assignPendingRequestsToElevator(String elevatorName) {
		//grab and remove the first pending request
		TripRequest firstPriorityPendingRequest = this.pendingTripRequests.remove(0);
		
		//find all other requests that can be serviced given the first request
		TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);
		Direction elevatorCurrentDirection = this.directionByElevatorName.get(elevatorName);
		
		//If this elevator is currently not in service
		//and the tripRequest is in the same direction as the tripRequestQueue,
		//then attempt to add this tripRequest to the elevator's tripRequestQueue
		if (tripRequestQueue.addFirstTripRequest(firstPriorityPendingRequest)) {
			
		}
		
		return true;
	}
	
	/**
	 * This method attempts to assign a tripRequest to an idle Elevator. 
	 * @param tripRequest
	 * @return
	 */
	private boolean assignTripToIdleElevator(TripRequest tripRequest) {
		//Iterate through each of the elevators to find any elevators currently idle that can take this trip.
		for (String elevatorName : tripRequestQueueByElevatorName.keySet()) {
			TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);
			
			if (tripRequestQueue.isEmpty()) {
				//Try to add this trip to the tripQueue
				if (tripRequestQueue.addFirstTripRequest(tripRequest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method attempts to assign a tripRequest to an in service Elevator. 
	 * The following logic is followed:
	 * 	- Search each elevator for any elevator whose tripRequestQueue is not empty and the tripRequestQueue direction is the same as the tripRequest
	 *  - For elevators that match these conditions, determine whether the elevator on it's way to service the tripRequestQueue, or if it is in the middle of servicing the queue.
	 *  	- Check if the elevator is moving in the same direction as the tripRequestQueue
	 *         - If the elevator's direction is up, assign the queue to the elevator if the elevator's current floor is lower than the pickup floor of the tripRequest
	 *         - Conversely, if the elevator's direction is down, assign the queue to the elevator if the elevator's current floor is higher than the pickup floor of the tripRequest
	 * @param tripRequest
	 * @return
	 */
	private boolean assignTripToInServiceElevator(TripRequest tripRequest) {
		//Iterate through each of the elevators to find any elevators currently in service that can take this trip as an en route trip
		for (String elevatorName : tripRequestQueueByElevatorName.keySet()) {

			TripRequestQueue tripRequestQueue = this.tripRequestQueueByElevatorName.get(elevatorName);
			
			//Find any enroute elevators
			//If this elevator is currently in service (tripRequestQueue is not empty),
			//and the tripRequest is in the same direction as the tripRequestQueue,
			//then attempt to add this tripRequest to the elevator's tripRequestQueue
			if (!tripRequestQueue.isEmpty() && (tripRequestQueue.getQueueDirection() == tripRequest.getDirection())) {
				//Try to add this trip to the tripQueue
				if (tripRequestQueue.addEnRouteTripRequest(tripRequest)) {
					return true;
				}
			}
		}
		return false;
	}

	
	public static void main (String[] args) {
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();
		
		//This will return a Map of Maps. First key -> floor Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration.getAllFloorSubsytemConfigurations();
		
		//This will return a Map of all attributes for the Scheduler (as per config.xml)
		HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();
		
		//Instantiate the scheduler
		Scheduler scheduler = new Scheduler(schedulerConfiguration.get("name"), Integer.parseInt(schedulerConfiguration.get("port")), elevatorConfigurations, floorConfigurations);

		
		
		//Some basic testing...
		
		//Simulate an incoming trip request
		//Expecting it to be assigned to E1
		scheduler.eventTripRequestReceived(2, 4, Direction.UP);
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an incoming trip request
		//Expecting it to be assigned to E1
		scheduler.eventTripRequestReceived(5, 8, Direction.UP);
		
		//Simulate an incoming trip request
		//Expecting it to be assigned to E2
		scheduler.eventTripRequestReceived(8, 1, Direction.DOWN);
		
		//Simulate an elevator Arrival notice for E1 (floor 2)
		//pickup should occur, no change to queued trips 
		scheduler.eventElevatorArrivalNotice("E1", 2);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an incoming trip request
		//Expecting it to be pending
		scheduler.eventTripRequestReceived(1, 6, Direction.UP);
		
		//Simulate an elevator Arrival notice for E1 (floor 3)
		//no stop
		scheduler.eventElevatorArrivalNotice("E1", 3);

		//Simulate an elevator Arrival notice for E1 (floor 4)
		//stop. first trip complete.
		scheduler.eventElevatorArrivalNotice("E1", 4);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 5)
		//stop. pickup.
		scheduler.eventElevatorArrivalNotice("E1", 5);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 5)
		//stop.
		scheduler.eventElevatorArrivalNotice("E1", 6);
		
		//Simulate an elevator Arrival notice for E1 (floor 6)
		//no stop
		scheduler.eventElevatorArrivalNotice("E1", 7);
		
		//Simulate an elevator Arrival notice for E1 (floor 7)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 8);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		
		//Spawn and start a new thread for this ElevatorSubsystem instance
		Thread schedulerThread = new Thread(scheduler, schedulerConfiguration.get("name"));
		schedulerThread.start();
	}
}
