package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable, ElevatorSystemComponent {
	
	private String name;
	private Server server;
	private Thread serverThread;
	private Queue<String> eventsQueue;
	private boolean debug = false;
	private HashMap<String, Integer> portsByElevatorName;					//key -> elevator name, value -> port number
	private HashMap<String, Integer> portsByFloorName;						//key -> floor name, value -> port number
	private HashMap<String, String> currentLocationByElevatorName;			//key -> elevator name, value -> current floor location
	
	public Scheduler(String name, int port) {
		this.name = name;
		this.eventsQueue = new LinkedList<String>();
		this.portsByElevatorName = new HashMap<String, Integer>();
		this.portsByFloorName = new HashMap<String, Integer>();
		this.currentLocationByElevatorName = new HashMap<String, String>();
		
		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		serverThread = new Thread(new Server(this, port, this.debug), name);
		serverThread.start();
	}
	
	public void init(HashMap<String, HashMap<String, String>> elevatorConfiguration, HashMap<String, HashMap<String, String>> floorConfigurations) {
		//Initialize data structures for both elevators and floors
		for (String elevatorName : elevatorConfiguration.keySet()) {
			HashMap<String, String> config = elevatorConfiguration.get(elevatorName);
			
			portsByElevatorName.put(elevatorName, Integer.parseInt(config.get("port")));
			currentLocationByElevatorName.put(elevatorName, config.get("startFloor"));
		}
		
		//Initialize data structures for both elevators and floors
		for (String floorName : floorConfigurations.keySet()) {
			HashMap<String, String> config = floorConfigurations.get(floorName);
			
			portsByFloorName.put(floorName, Integer.parseInt(config.get("port")));
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
	
	private void eventElevatorRequestReceived() {
		
	}

	private void eventElevatorArrivalNotice() {
		
	}
	
	private void eventElevatorStopped() {
		
	}
	
	private void eventElevatorDoorOpened() {
		
	}
	
	private void eventElevatorDoorClosed() {
		
	}
	
	public static void main (String[] args) {
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();
		
		//This will return a Map of Maps. First key -> floor Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration.getAllFloorSubsytemConfigurations();
		
		//This will return a Map of all attributes for the Scheduler (as per config.xml)
		HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();
		Scheduler scheduler = new Scheduler(schedulerConfiguration.get("name"), Integer.parseInt(schedulerConfiguration.get("port")));
		
		//Before the scheduler starts it needs to be initialized
		scheduler.init(elevatorConfigurations, floorConfigurations);
		
		//Spawn and start a new thread for this ElevatorSubsystem instance
		Thread schedulerThread = new Thread(scheduler, schedulerConfiguration.get("name"));
		schedulerThread.start();
	}
}
