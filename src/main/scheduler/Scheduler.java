package main.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import main.ElevatorSystemComponent;
import main.global.*;
import main.requests.*;
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
	private Queue<Request> eventsQueue;
	private boolean debug = false;
	private HashMap<String, Integer> portsByElevatorName;										//key -> elevator name, value -> port number
	private HashMap<String, Integer> portsByFloorName;											//key -> floor number, value -> port number
	private HashMap<String, ElevatorMonitor> elevatorMonitorByElevatorName;						//key -> elevator name, value -> elevator monitor
	private ArrayList<TripRequest> pendingTripRequests;
	
	public Scheduler(String name, int port, HashMap<String, HashMap<String, String>> elevatorConfiguration, HashMap<String, HashMap<String, String>> floorConfigurations) {
		this.name = name;
		this.eventsQueue = new LinkedList<Request>();
		this.portsByElevatorName = new HashMap<String, Integer>();
		this.portsByFloorName = new HashMap<String, Integer>();
		this.elevatorMonitorByElevatorName = new HashMap<String, ElevatorMonitor>();
		this.pendingTripRequests = new ArrayList<TripRequest>();
		
		//Initialize infrastructure configurations (elevators/floors)
		this.init(elevatorConfiguration, floorConfigurations);
		
		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		this.server = new Server(this, port, this.debug);
		serverThread = new Thread(server, name);
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
			
			//Initialize elevatorMonitors for each elevator
			this.elevatorMonitorByElevatorName.put(
					elevatorName, 
					new ElevatorMonitor(
							elevatorName, 
							Integer.parseInt(config.get("startFloor")), 
							Integer.parseInt(config.get("startFloor")), 
							Direction.IDLE, 
							ElevatorStatus.STOPPED, 
							ElevatorDoorStatus.OPENED));

		}
		
		//Initialize data structures for floors
		for (String floorName : floorConfigurations.keySet()) {
			HashMap<String, String> config = floorConfigurations.get(floorName);
			
			this.portsByFloorName.put(floorName, Integer.parseInt(config.get("port")));
		}
	}
	
	@Override
	/**
	 * Add an event to the eventQueue. 
	 */
	public synchronized void receiveEvent(Request request) {
		eventsQueue.add(request);
		this.notifyAll();
	}

	@Override
	/**
	 * Get next event from the eventQueue.
	 */
	public synchronized Request getNextEvent() {
		while (eventsQueue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return eventsQueue.poll();
	}

	@Override
	/**
	 * Get the name of this scheduler.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public void run() {
		while (true) {
			this.handleEvent(this.getNextEvent());
		}
	}
	
	/**
	 * This method will determine the type of Request and call the appropriate event handler method for this request.
	 * @param event
	 */
	private void handleEvent(Request event) {
		//switch statement corresponding to different "event handlers"
		if (event instanceof FloorButtonRequest) {
			FloorButtonRequest request = (FloorButtonRequest) event;
			this.eventTripRequestReceived(Integer.parseInt(request.getFloorName()), Integer.parseInt(request.getDestinationFloor()), request.getDirection());
		} else if (event instanceof ElevatorArrivalRequest) {
			ElevatorArrivalRequest request = (ElevatorArrivalRequest) event;
			this.eventElevatorArrivalNotice(request.getElevatorName(), Integer.parseInt(request.getFloorName()));
		} else if (event instanceof ElevatorDoorRequest) {
			ElevatorDoorRequest request = (ElevatorDoorRequest) event;
			if (request.getRequestAction() == ElevatorDoorStatus.OPENED) {
				this.eventElevatorDoorOpened(request.getElevatorName());
			} else if (request.getRequestAction() == ElevatorDoorStatus.CLOSED) {
				this.eventElevatorDoorClosed(request.getElevatorName());
			}
		} else if (event instanceof ElevatorMotorRequest) {
			ElevatorMotorRequest request = (ElevatorMotorRequest) event;
			if (request.getRequestAction() == Direction.IDLE) {
				this.eventElevatorStopped(request.getElevatorName());
			}
		}
	}
	
	/**
	 * Send a request to port using this object's server.
	 * @param request
	 * @param elevatorName
	 */
	private void sendToServer(Request request, int port) {
		try {
			this.server.send(request, InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
				
				//TODO remove this in iteration 2 - as the elevator will manage its own floor buttons.
				//Send event to elevator to light floor button for new destination. 
				this.sendToServer(new ElevatorLampRequest(elevatorName, String.valueOf(destinationFloorNumber), LampStatus.ON), this.portsByElevatorName.get(elevatorName));
				return;
			} 
		}
		
		
		//See if there are any idle elevators to service this tripRequest
		for (String elevatorName : elevatorMonitorByElevatorName.keySet()) {
			ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
			if (this.assignTripToIdleElevator(elevatorName, tripRequest)) {
				this.consoleOutput("Trip request " + tripRequest + " was assigned to " + elevatorName + ".");
				
				//TODO remove this in iteration 2 - as the elevator will manage its own floor buttons.
				//Send event to elevator to light floor button for new destination. 
				this.sendToServer(new ElevatorLampRequest(elevatorName, String.valueOf(destinationFloorNumber), LampStatus.ON), this.portsByElevatorName.get(elevatorName));
				
				if (elevatorMonitor.getElevatorStatus() == ElevatorStatus.STOPPED) {
					
					//Send an elevator Door Close event
					this.consoleOutput("Sending an Elevator 'DoorClose' event to  " + elevatorName + ".");
					this.sendToServer(new ElevatorDoorRequest(elevatorName, ElevatorDoorStatus.CLOSED), this.portsByElevatorName.get(elevatorName));
				}
				return;
			} 
		}
		
		//Add this tripRequest to the pendingTripRequests queue
		this.pendingTripRequests.add(tripRequest);
		this.consoleOutput("Trip request " + tripRequest + " was unable to be assigned immediately. It has been added to pending requests." + this.pendingTripRequests);
	}

	/**
	 * When an elevator arrives at a floor,  update the elevatorMonitor then check if the elevator needs to stop.
	 * If the elevator needs to stop, send a stop request to the elevator.
	 * If the elevator needs to stop because this floor is a pickup, then a request needs to be sent to the floor to turn off the floor direction lamp.
	 * If not stop is needed, check the elevatorMonitor to determine the next direction for the elevator and send a motor request in that direction.
	 * @param elevatorName
	 * @param floorNumber
	 */
	private void eventElevatorArrivalNotice(String elevatorName, int floorNumber) {
		this.consoleOutput("Arrival notification received from " + elevatorName + " at floor " + floorNumber);
		
		//Get the elevatorMonitor for this elevator
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Update the elevatorMonitor with the new floor of the elevator
		elevatorMonitor.updateElevatorFloorLocation(floorNumber);
	
		//Check if this elevator needs to stop at this floor
		if(elevatorMonitor.isStopRequired(floorNumber)) {
			this.consoleOutput("Stop is required for " + elevatorName + " at floor " + floorNumber);

			//Check if this floor is a pickup, if so, send a message to the floor to turn off the floor direction lamp
			if (elevatorMonitor.isPickupFloor(floorNumber)) {
				this.consoleOutput("Sending request to turn off floor direction lamp to " + floorNumber + " as " + elevatorName + " has arrived.");
				//TODO send floorLampRequest when its completed.
				//Send event to floor to light floor direction lamp 

			}
			this.consoleOutput("Sending an elevator stop request to " + elevatorName);
			this.sendToServer(new ElevatorMotorRequest(elevatorName, Direction.IDLE), this.portsByElevatorName.get(elevatorName));
		} else {
			this.consoleOutput("Stop is not required for " + elevatorName + " at floor " + floorNumber);
			//The reason we evaluate this direction again, is because in certain circumstances, the direction may change 
			//Example, if the elevator is going down to it's starting floor, but a trip request had been assigned to it before it reached its destination, the elevator needs to change directions
			Direction nextDirection = elevatorMonitor.getNextElevatorDirection();
			this.sendElevatorMoveEvent(elevatorName, nextDirection);
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
		elevatorMonitor.updateElevatorStatus(ElevatorStatus.STOPPED);
		
		//The elevatorMonitor needs to be advised this stop has occurred
		HashSet<TripRequest> completedTrips = elevatorMonitor.stopOccurred();
		if (!completedTrips.isEmpty()) {
			this.consoleOutput("The following trips have been completed at this stop by " + elevatorName + ":" + completedTrips);
		}
		
		this.consoleOutput("Sending an Elevator 'OpenDoor' event to " + elevatorName);
		this.sendToServer(new ElevatorDoorRequest(elevatorName, ElevatorDoorStatus.OPENED), this.portsByElevatorName.get(elevatorName));
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
		elevatorMonitor.updateElevatorDoorStatus(ElevatorDoorStatus.OPENED);
		
		//Checking pending requests?
		if (!this.pendingTripRequests.isEmpty()) {
			HashSet<TripRequest> assignedPendingRequests = this.assignPendingRequestsToElevator(elevatorName);
			if (!assignedPendingRequests.isEmpty()) {
				this.consoleOutput("The following pending trip requests have been assigned to " + elevatorName + "  : " + assignedPendingRequests);
			}
		}
		
		//Are there still more floors to visit?
		if (!elevatorMonitor.isEmpty()) {
			this.consoleOutput("There are more floors to visit for this elevator. Sending an Elevator 'CloseDoor' event to " + elevatorName);
			
			this.consoleOutput("Sending an Elevator 'DoorClose' event to  " + elevatorName + ".");
			this.sendToServer(new ElevatorDoorRequest(elevatorName, ElevatorDoorStatus.CLOSED), this.portsByElevatorName.get(elevatorName));
		} else {
			Integer currentFloor = elevatorMonitor.getElevatorFloorLocation();
			Integer startFloor = elevatorMonitor.getElevatorStartingFloorLocation();
			boolean isElevatorOnStartFloor;
			
			if (currentFloor == startFloor) {
				isElevatorOnStartFloor = true;
			} else {
				isElevatorOnStartFloor = false;
			}
			
			if (isElevatorOnStartFloor) {
				//Update direction of elevator to IDLE
				elevatorMonitor.updateElevatorDirection(Direction.IDLE);
				
				this.consoleOutput("There are no available trip requests for " + elevatorName + ", and elevator is already on it's starting floor [" + startFloor + "]. Waiting for next trip request...");
			} else {
				this.consoleOutput("There are no available trip requests for " + elevatorName + ", returning elevator to it's starting floor [" + startFloor + "]. Sending an Elevator 'CloseDoor' event to " + elevatorName);

				this.consoleOutput("Sending an Elevator 'DoorClose' event to  " + elevatorName + ".");
				this.sendToServer(new ElevatorDoorRequest(elevatorName, ElevatorDoorStatus.CLOSED), this.portsByElevatorName.get(elevatorName));
			}
		}
	}
	
	/**
	 * When the elevator has confirmed that it's door has closed, then determine the next direction the elevator should go from the ElevatorMonitor and send a motor request to the elevator.
	 * @param elevatorName
	 */
	private void eventElevatorDoorClosed(String elevatorName) {
		this.consoleOutput("Confirmation received that " + elevatorName + " has closed its doors.");
		
		//Get the elevatorMonitor for this elevator
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);

		//Update current elevator door status
		elevatorMonitor.updateElevatorDoorStatus(ElevatorDoorStatus.CLOSED);
		
		//Get the next direction for this elevator based on the elevatorMonitor
		Direction nextDirection = elevatorMonitor.getNextElevatorDirection();
		
		//Update elevator current direction
		elevatorMonitor.updateElevatorDirection(nextDirection);
		
		//send an elevator move event in the next direction it needs to go
		this.sendElevatorMoveEvent(elevatorName, nextDirection);
	}
	
	/**
	 * Send a motor request to an elevator to go either UP, DOWN, or IDLE. If IDLE is specified, this means stop the motor.
	 * @param elevatorName
	 * @param direction
	 */
	private void sendElevatorMoveEvent(String elevatorName, Direction direction) {
		ElevatorMonitor elevatorMonitor = this.elevatorMonitorByElevatorName.get(elevatorName);
		
		//Update elevator status to Moving
		elevatorMonitor.updateElevatorStatus(ElevatorStatus.MOVING);
		//Update elevator direction 
		elevatorMonitor.updateElevatorDirection(direction);
		
		this.consoleOutput("Sending an Elevator 'MOVE " + direction + "' event to " + elevatorName + ".");
		this.sendToServer(new ElevatorMotorRequest(elevatorName, direction), this.portsByElevatorName.get(elevatorName));
	}
	
	/**
	 * Attempt to assign any pending requests to an elevator. If the elevator queue is empty, assigns it automatically as a first trip request.
	 * Then attempt to see if any subsequent pending trips can be assigned (as an enroute trip to this elevator).
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
	
	public static void main (String[] args) {
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();
		
		//This will return a Map of Maps. First key -> floor Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration.getAllFloorSubsytemConfigurations();
		
		//This will return a Map of all attributes for the Scheduler (as per config.xml)
		HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();
		
		//Instantiate the scheduler
		Scheduler scheduler = new Scheduler(schedulerConfiguration.get("name"), Integer.parseInt(schedulerConfiguration.get("port")), elevatorConfigurations, floorConfigurations);
		
		//Spawn and start a new thread for this Scheduler
		Thread schedulerThread = new Thread(scheduler, schedulerConfiguration.get("name"));
		schedulerThread.start();
	}
}
