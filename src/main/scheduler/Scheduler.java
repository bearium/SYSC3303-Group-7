package main.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import main.ElevatorSystemComponent;
import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;
import main.global.ElevatorSystemConfiguration;
import main.server.Server;

/**
 * The purpose of this class is to schedule the events required to coordinate an elevator system. 
 * The scheduler is responsible for controlling:
 * 	- assigning and maintaining queue's for trip requests received for each elevator
 *  - sequences for each elevators operations including: motor on/off up/down, lamps on/off, doors open/close
 * A primary goal of the scheduler is to ensure all trip requests are actioned in a timely manner, ensuring none wait indefinitely.
 * The scheduler maintains a complete state of the elevator subsystem at any given time, this includes:
 * 	- state of the elevators locations, directions, status (moving/stopped), doors (opened/closed)
 *
 */
public class Scheduler implements Runnable, ElevatorSystemComponent {
	
	private String name;
	private Server server;
	private Thread serverThread;
	private Queue<String> eventsQueue;
	private boolean debug = false;
	private HashMap<String, Integer> portsByElevatorName;										//key -> elevator name, value -> port number
	private HashMap<String, Integer> portsByFloorName;											//key -> floor number, value -> port number
	private HashMap<String, Integer> currentFloorLocationByElevatorName;						//key -> elevator name, value -> current floor location
	private HashMap<String, Integer> startFloorLocationByElevatorName;							//key -> elevator name, value -> starting floor location
	private HashMap<String, ElevatorMonitor> elevatorMonitorByElevatorName;						//key -> elevator name, value -> elevator monitor
	private HashMap<String, Direction> currentDirectionByElevatorName;							//key -> elevator name, value -> current elevator direction
	private HashMap<String, ElevatorStatus> currentElevatorStatusByElevatorName; 				//key -> elevator name, value -> current elevator status
	private HashMap<String, ElevatorDoorStatus> currentElevatorDoorStatusByElevatorName; 		//key -> elevator name, value -> current elevator door status
	private ArrayList<TripRequest> pendingTripRequests;
	
	public Scheduler(String name, int port, HashMap<String, HashMap<String, String>> elevatorConfiguration, HashMap<String, HashMap<String, String>> floorConfigurations) {
		this.name = name;
		this.eventsQueue = new LinkedList<String>();
		this.portsByElevatorName = new HashMap<String, Integer>();
		this.portsByFloorName = new HashMap<String, Integer>();
		this.currentFloorLocationByElevatorName = new HashMap<String, Integer>();
		this.startFloorLocationByElevatorName = new HashMap<String, Integer>();
		this.elevatorMonitorByElevatorName = new HashMap<String, ElevatorMonitor>();
		this.currentDirectionByElevatorName = new HashMap<String, Direction>();
		this.currentElevatorStatusByElevatorName = new HashMap<String, ElevatorStatus>();
		this.currentElevatorDoorStatusByElevatorName = new HashMap<String, ElevatorDoorStatus>();
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
		//Initialize data structures for elevators
		for (String elevatorName : elevatorConfiguration.keySet()) {
			HashMap<String, String> config = elevatorConfiguration.get(elevatorName);
			
			this.portsByElevatorName.put(elevatorName, Integer.parseInt(config.get("port")));
			this.currentFloorLocationByElevatorName.put(elevatorName, Integer.parseInt(config.get("startFloor")));
			this.startFloorLocationByElevatorName.put(elevatorName, Integer.parseInt(config.get("startFloor")));
			this.currentDirectionByElevatorName.put(elevatorName, Direction.IDLE);
			this.currentElevatorStatusByElevatorName.put(elevatorName, ElevatorStatus.STOPPED);
			this.currentElevatorDoorStatusByElevatorName.put(elevatorName, ElevatorDoorStatus.OPENED);
			
			//Initialize elevatorMonitors for each elevator
			this.elevatorMonitorByElevatorName.put(
					elevatorName, 
					new ElevatorMonitor(
							elevatorName,
							Integer.parseInt(config.get("startFloor")), 
							this.currentDirectionByElevatorName.get(elevatorName), 
							this.currentElevatorStatusByElevatorName.get(elevatorName), 
							this.currentElevatorDoorStatusByElevatorName.get(elevatorName)));
		}
		
		//Initialize data structures for floors
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
	 * If there are no en-route options, attempt to find an idle elevator to service the tripRequest. 
	 * If this is not possible then the tripRequest will be put in a pending queue.
	 * 
	 * @param pickupFloorNumber
	 * @param destinationFloorNumber
	 * @param direction
	 */
	private void eventTripRequestReceived(int pickupFloorNumber, int destinationFloorNumber, Direction direction) {
		this.consoleOutput("Trip request received from floor " + pickupFloorNumber + " to " + destinationFloorNumber + ".");
		
		//Create a TripRequest object
		TripRequest tripRequest = new TripRequest(pickupFloorNumber, destinationFloorNumber);
		
		//See if any elevators currently in service can take this tripRequest as an en-route trip
		for (String elevatorName : elevatorMonitorByElevatorName.keySet()) {
			if (this.assignTripToInServiceElevator(elevatorName, tripRequest)) {
				this.consoleOutput("Trip request " + tripRequest + " was added to " + elevatorName + "'s current trip.");
				//TODO send lamps
				return;
			} 
		}
		
		
		//See if there are any idle elevators to service this tripRequest
		for (String elevatorName : elevatorMonitorByElevatorName.keySet()) {
			if (this.assignTripToIdleElevator(elevatorName, tripRequest)) {
				this.consoleOutput("Trip request " + tripRequest + " was assigned to " + elevatorName + ".");
				//TODO send lamps
				return;
			} 
		}
		
		this.consoleOutput("Trip request " + tripRequest + " was unable to be assigned immediately. It has been added to pending requests.");
		
		//Add this tripRequest to the pendingTripRequests queue
		this.pendingTripRequests.add(tripRequest);
	}

	/**
	 * 
	 * @param elevatorName
	 * @param floorNumber
	 */
	private void eventElevatorArrivalNotice(String elevatorName, int floorNumber) {
		this.consoleOutput("Arrival notification received from " + elevatorName + " at floor " + floorNumber);
		
		//Get the elevatorMonitor for this elevator
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Update the scheduler and elevatorMonitor with the new floor of the elevator
		this.currentFloorLocationByElevatorName.put(elevatorName, floorNumber);
		elevatorMonitor.updateCurrentElevatorFloorLocation(floorNumber);

		//Check if this elevator needs to stop at this floor
		if(elevatorMonitor.isStopRequired(floorNumber)) {
			this.consoleOutput("Stop is required for " + elevatorName + " at floor " + floorNumber);
			//TODO send an elevator stop event
			this.consoleOutput("Sending an elevator stop request to " + elevatorName);
		} else {
			this.consoleOutput("Stop is not required for " + elevatorName + " at floor " + floorNumber);
			//TODO send an elevator move event ?
		}
				

	}
	
	/**
	 * When an elevator stops, take this opportunity to update the elevatorMonitor.
	 * If this stop is a destination for a tripRequest, the corresponding tripRequest is completed, and the elevatorMonitor needs to be updated to reflect this
	 * If this stop is a pickup for a tripRequest, the tripRequest needs to remove this pickup from it's queue of pickups. but the tripRequest still is in service in the elevatorMonitor.
	 * 
	 * When this is completed, send an elevator open door event.
	 * @param elevatorName
	 */
	private void eventElevatorStopped(String elevatorName) {
		this.consoleOutput("Confirmation received that " + elevatorName + " has stopped.");
		
		//Get elevatorMonitor for the elevator.
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Update elevator status to Stopped
		this.currentElevatorStatusByElevatorName.put(elevatorName, ElevatorStatus.STOPPED);
		elevatorMonitor.updateCurrentElevatorStatus(ElevatorStatus.STOPPED);
		
		//The elevatorMonitor needs to be advised this stop has occurred
		HashSet<TripRequest> completedTrips = elevatorMonitor.stopOccurred();
		if (!completedTrips.isEmpty()) {
			this.consoleOutput("The following trips have been completed at this stop by " + elevatorName + ":" + completedTrips);
		}
		
		this.consoleOutput("Sending an Elevator 'OpenDoor' event to " + elevatorName);
		//TODO send an elevator open door event
	}
	
	/**
	 * When confirmation has been received that the elevator has opened its doors, determine whether this elevator has more trips.
	 * If the elevatorMonitor is not empty, then 
	 * @param elevatorName
	 */
	private void eventElevatorDoorOpened(String elevatorName) {
		this.consoleOutput("Confirmation received that " + elevatorName + " has opened its doors.");
		
		//Get elevatorMonitor for the elevator.
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);

		//Update current elevator door status
		this.currentElevatorDoorStatusByElevatorName.put(elevatorName, ElevatorDoorStatus.OPENED);
		elevatorMonitor.updateCurrentElevatorDoorStatus(ElevatorDoorStatus.OPENED);
		
		//Checking pending requests?
		if (!this.pendingTripRequests.isEmpty()) {
			HashSet<TripRequest> assignedPendingRequests = this.assignPendingRequestsToElevator(elevatorName);
			if (!assignedPendingRequests.isEmpty()) {
				this.consoleOutput("The following pending trip requests have been assigned to " + elevatorName + "  : " + assignedPendingRequests);
			}
		}
		
		//Are there still more floors to visit?
		if (!elevatorMonitor.isEmpty()) {
			this.consoleOutput("Sending an Elevator 'CloseDoor' event to " + elevatorName);
			//TODO send a close door event
		} else {
			this.currentDirectionByElevatorName.put(elevatorName, Direction.IDLE);
			elevatorMonitor.updateCurrentElevatorDirection(Direction.IDLE);
		}
	}
	
	/**
	 * 
	 * @param elevatorName
	 */
	private void eventElevatorDoorClosed(String elevatorName) {
		this.consoleOutput("Confirmation received that " + elevatorName + " has closed its doors.");
		
		//Get the elevatorMonitor for this elevator
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);

		//Update current elevator door status
		this.currentElevatorDoorStatusByElevatorName.put(elevatorName, ElevatorDoorStatus.CLOSED);
		elevatorMonitor.updateCurrentElevatorDoorStatus(ElevatorDoorStatus.CLOSED);
		
		//Get the next direction for this elevator based on the elevatorMonitor
		Direction nextDirection = elevatorMonitor.getNextElevatorDirection();
		
		//Update elevator current direction
		this.currentDirectionByElevatorName.put(elevatorName, nextDirection);
		elevatorMonitor.updateCurrentElevatorDirection(nextDirection);
		
		//TODO send an elevator move event in the next direction it needs to go
		this.consoleOutput("Sending an Elevator 'MOVE " + nextDirection + "' event to " + elevatorName + ".");
		this.sendElevatorMoveEvent(elevatorName, nextDirection);
	}
	
	/**
	 * 
	 * @param elevatorName
	 * @param direction
	 */
	private void sendElevatorMoveEvent(String elevatorName, Direction direction) {
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Update elevator status to Moving
		this.currentElevatorStatusByElevatorName.put(elevatorName, ElevatorStatus.MOVING);
		elevatorMonitor.updateCurrentElevatorStatus(ElevatorStatus.MOVING);
		
		//TODO send an actual datagram packet
	}
	
	/**
	 * 
	 * @param elevatorName
	 * @return
	 */
	private HashSet<TripRequest> assignPendingRequestsToElevator(String elevatorName) {
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		HashSet<TripRequest> assignedPendingRequests = new HashSet<TripRequest>();
		
		//If the elevator has no trips in its queue's, then it should take the first pending request
		if (elevatorMonitor.isEmpty()) {
			TripRequest firstPriorityPendingRequest = this.pendingTripRequests.get(0);
			if (assignTripToIdleElevator(elevatorName, firstPriorityPendingRequest)) {
				assignedPendingRequests.add(firstPriorityPendingRequest);
				this.pendingTripRequests.remove(0);
			}
		}
		
		//Now the elevator, should see if its possible to take any of the other pending trips as en-route trip requests
		Iterator<TripRequest> iterator = pendingTripRequests.iterator();
		while (iterator.hasNext()) {
			TripRequest pendingTripRequest = iterator.next();
			if (this.assignTripToInServiceElevator(elevatorName, pendingTripRequest)) {
				assignedPendingRequests.add(pendingTripRequest);
				iterator.remove();
			}
		}
		return assignedPendingRequests;
	}
	
	/**
	 * This method attempts to assign a tripRequest to an idle Elevator. 
	 * 
	 * @param elevatorName
	 * @param tripRequest
	 * @return
	 */
	private boolean assignTripToIdleElevator(String elevatorName, TripRequest tripRequest) {
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		if (elevatorMonitor.isEmpty()) {
			//Try to add this trip to the tripQueue
			if (elevatorMonitor.addFirstTripRequest(tripRequest)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method attempts to assign a tripRequest to an in service Elevator. 
	 * Check whether the elevator's elevatorMonitor is not empty and the elevatorMonitor direction is the same as the tripRequest. IF this is true attempt to add the tripRequest to the 
	 * elevatorMonitor, the queue will return whether this was possible or not (true / false).
	 *   
	 * @param elevatorName        
	 * @param tripRequest
	 * @return
	 */
	private boolean assignTripToInServiceElevator(String elevatorName, TripRequest tripRequest) {
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Determine whether this trip request is en route for this elevators trip request queue.
		//If this elevator is currently in service (elevatorMonitor is not empty), and the tripRequest is in the same direction as the elevatorMonitor,
		//then attempt to add this tripRequest to the elevator's elevatorMonitor
		if (!elevatorMonitor.isEmpty() && (elevatorMonitor.getQueueDirection() == tripRequest.getDirection())) {
			//Try to add this trip to the tripQueue
			if (elevatorMonitor.addEnRouteTripRequest(tripRequest)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param output
	 */
	private void consoleOutput(String output) {
		System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + output);
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		HashSet<String> elevators = new HashSet<String>(this.portsByElevatorName.keySet());
		
		TreeSet<Integer> floors = new TreeSet<Integer>();
		for (String floor : this.portsByFloorName.keySet()) {
			floors.add(Integer.parseInt(floor));
		}
		
		while (!floors.isEmpty()) {
			HashSet<String> elevatorsAtThisFloor = new HashSet<String>();
			int floor = floors.pollLast();
			if (this.currentFloorLocationByElevatorName.containsValue(floor)) {
				for (String elevator : elevators) {
					if (this.currentFloorLocationByElevatorName.get(elevator) == floor) {
						elevatorsAtThisFloor.add(elevator);
					}
				}
			}
			sb.append("floor " + floor + ": " + elevatorsAtThisFloor + "\n");
		}
		
		
		return sb.toString();
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

		scheduler.toString();
		
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
		
		//Simulate an incoming trip request while the doors are still open and the elevator is on the trip request's pickup floor
		//Expecting it to be assigned to E1
		scheduler.eventTripRequestReceived(2, 7, Direction.UP);
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an incoming trip request while the doors are closed and the elevator is on the trip request's pickup floor
		//Expecting it to be assigned to pending
		scheduler.eventTripRequestReceived(2, 5, Direction.UP);
		
		//Simulate an elevator Arrival notice for E1 (floor 3)
		//no stop
		scheduler.eventElevatorArrivalNotice("E1", 3);

		//Simulate an elevator Arrival notice for E1 (floor 4)
		//stop. first trip complete.
		scheduler.eventElevatorArrivalNotice("E1", 4);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an incoming trip request
		//Expecting it to be pending
		scheduler.eventTripRequestReceived(3, 6, Direction.UP);
		
		//Simulate an elevator Arrival notice for E1 (floor 5)
		//stop. pickup.
		scheduler.eventElevatorArrivalNotice("E1", 5);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 5)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 6);
		
		//Simulate an elevator Arrival notice for E1 (floor 8)
		//stop
		scheduler.eventElevatorArrivalNotice("E1", 7);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 8)
		//stop.
		scheduler.eventElevatorArrivalNotice("E1", 8);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 7)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 7);

		//Simulate an elevator Arrival notice for E1 (floor 6)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 6);

		//Simulate an elevator Arrival notice for E1 (floor 5)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 5);
		
		//Simulate an elevator Arrival notice for E1 (floor 4)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 4);
		
		//Simulate an elevator Arrival notice for E1 (floor 3)
		//no stop.
		scheduler.eventElevatorArrivalNotice("E1", 3);
		
		//Simulate an elevator Arrival notice for E1 (floor 2)
		//stop. should be assigned second pending request 3,6 once stopped
		scheduler.eventElevatorArrivalNotice("E1", 2);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		//Simulate an elevator Arrival notice for E1 (floor 3)
		//stop. 
		scheduler.eventElevatorArrivalNotice("E1", 3);
		scheduler.eventElevatorStopped("E1");
		scheduler.eventElevatorDoorOpened("E1");
		scheduler.eventElevatorDoorClosed("E1");
		
		
		//Spawn and start a new thread for this ElevatorSubsystem instance
		Thread schedulerThread = new Thread(scheduler, schedulerConfiguration.get("name"));
		schedulerThread.start();
	}
}
