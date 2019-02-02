package main.elevatorSubsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import main.ElevatorSystemComponent;

import main.floorSubsystem.FloorSubsystem;
import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorSystemConfiguration;
import main.requests.*;
import main.server.Server;
import main.global.ElevatorStatus;

public class ElevatorSubsystem implements Runnable, ElevatorSystemComponent {
	//class variables
	private Server server;
	private Thread serverThread;
	private String name;
	private ElevatorState state;
	private Queue<Request> eventsQueue;
	private boolean debug = false;
	private int schedulerPort;
	
	public ElevatorSubsystem(String name, int port, int startFloor, int schedulerPort, int maxFloor){
		this.name = name;
		this.eventsQueue = new LinkedList<Request>();
		this.state = new ElevatorState(startFloor,startFloor, Direction.IDLE, ElevatorStatus.STOPPED, ElevatorDoorStatus.OPENED, maxFloor);
		this.schedulerPort = schedulerPort;

		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		server = new Server(this, port, this.debug);
		serverThread = new Thread(server, name);
		serverThread.start();
	}
	
	public synchronized void receiveEvent(Request event) {
		eventsQueue.add(event);
	}

	//goes through event queue till empty then waits till next event received
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
	
	public String getName() {
		return this.name;
	}

	//thread run
	@Override
	public void run() {
		while (true) {
			this.handleEvent(this.getNextEvent());
		}
	}

	private void handleEvent(Request event) {
		//switch statement corresponding to different "event handlers"
		if (event instanceof ElevatorArrivalRequest) {
			ElevatorArrivalRequest request = (ElevatorArrivalRequest) event;
				this.sendToServer(request);
				consoleOutput("Sending arrival notice.");
		} else if (event instanceof ElevatorDoorRequest) {
			ElevatorDoorRequest request = (ElevatorDoorRequest) event;
			if (request.getRequestAction() == ElevatorDoorStatus.OPENED) {
				this. handleElevatorOpenDoor();
			} else if (request.getRequestAction() == ElevatorDoorStatus.CLOSED) {
				this.handleElevatorCloseDoor();
			}
		} else if (event instanceof ElevatorMotorRequest) {
			ElevatorMotorRequest request = (ElevatorMotorRequest) event;
			if (request.getRequestAction() == Direction.IDLE) {
				this.handleElevatorStop();
			}
			else if (request.getRequestAction() == Direction.UP) {
				this.handleElevatorMoveUP();
			}
			else if (request.getRequestAction() == Direction.DOWN) {
				this.handleElevatorMoveDown();
			}
		}
		else if (event instanceof ElevatorLampRequest) {
			ElevatorLampRequest request = (ElevatorLampRequest) event;
			toggleLamp(Integer.parseInt(request.getElevatorButton()));
		}
	}
	//toggles lamp state dependent on floor provided
	private void toggleLamp(int floor){
		this.state.toggleLamp(floor);
	}

	//toggles lamp state dependent on floor provided
	private void handleElevatorStop(){
		this.state.setDirection(Direction.IDLE);
		this.state.setStatus(ElevatorStatus.STOPPED);
		this.state.toggleLamp(this.state.getCurrentFloor());
		ElevatorMotorRequest request = new ElevatorMotorRequest(this.name, Direction.IDLE);
		consoleOutput("Sending stop confirmation.");
		this.sendToServer(request);
	}



	private void handleElevatorMoveUP(){
		if (this.state.getDoorStatus() != ElevatorDoorStatus.OPENED) {
			this.state.setDirection(Direction.UP);
			this.state.setStatus(ElevatorStatus.MOVING);
			consoleOutput("Simulating Travel Time");
			try {
				Thread.sleep(5000);
			} catch (java.lang.InterruptedException e) {
				e.printStackTrace();
			}
			this.state.setCurrentFloor(this.state.getCurrentFloor() + 1);
			ElevatorArrivalRequest request = new ElevatorArrivalRequest(this.name, Integer.toString(this.state.getCurrentFloor()));
			this.sendToServer(request);
		}
	}

	private void handleElevatorMoveDown() {
		if (this.state.getDoorStatus() != ElevatorDoorStatus.OPENED) {
			this.state.setDirection(Direction.DOWN);
			this.state.setStatus(ElevatorStatus.MOVING);
			consoleOutput("Simulating Travel Time");
			try {
				Thread.sleep(5000);
			} catch (java.lang.InterruptedException e) {
				e.printStackTrace();
			}
			this.state.setCurrentFloor(this.state.getCurrentFloor() - 1);
			ElevatorArrivalRequest request = new ElevatorArrivalRequest(this.name, Integer.toString(this.state.getCurrentFloor()));
			this.sendToServer(request);
		}
	}

	private void handleElevatorOpenDoor(){
		this.state.setDoorStatus(ElevatorDoorStatus.OPENED);
		consoleOutput("Doors Opened, Travelers Loading in");
		try {
			Thread.sleep(5000);
		} catch (java.lang.InterruptedException e) {
			e.printStackTrace();
		}
		ElevatorDoorRequest request = new ElevatorDoorRequest(this.name, ElevatorDoorStatus.OPENED);
		this.sendToServer(request);
	}

	private void handleElevatorCloseDoor(){
		this.state.setDoorStatus(ElevatorDoorStatus.CLOSED);
		consoleOutput("Doors closed, Ready to travel");
		ElevatorDoorRequest request = new ElevatorDoorRequest(this.name, ElevatorDoorStatus.CLOSED);
		this.sendToServer(request);
	}


	private void sendToServer(Request request) {
		try {
			this.server.send(request, InetAddress.getLocalHost(), this.schedulerPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static void consoleOutput(String output) {
		System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + output);
	}

	public static void main(String[] args){
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();

		//This will return a Map of all attributes for the Scheduler (as per config.xml)
		HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();

		HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration
				.getAllFloorSubsytemConfigurations();

		int tempfloor = 0;
		for (String floorName : floorConfigurations.keySet()) {
			// find amount of floors
			tempfloor+= tempfloor;
		}

		//Iterate through each elevator and create an instance of an ElevatorSubsystem
		for (String elevatorName : elevatorConfigurations.keySet()) {
			//Get the configuration for this particular 'elevatorName'
			HashMap<String, String> elevatorConfiguration = elevatorConfigurations.get(elevatorName);
			
			//Create an instance of ElevatorSubsystem for this 'elevatorName'
			ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorName, Integer.parseInt(elevatorConfiguration.get("port")),
					Integer.parseInt(elevatorConfiguration.get("startFloor")), Integer.parseInt(schedulerConfiguration.get("port")),tempfloor);
			
			//Spawn and start a new thread for this ElevatorSubsystem instance
			Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorName);
			elevatorSubsystemThread.start();
		}

	}

}
