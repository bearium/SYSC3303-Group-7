package main.elevatorSubsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import main.ElevatorSystemComponent;
import main.global.*;
import main.requests.*;
import main.server.Server;

public class ElevatorSubsystem implements Runnable, ElevatorSystemComponent {
	//class variables
	private Server server;
	private Thread serverThread;
	private String name;
	private int travelTime;
	private int passengerTime;
	private int doorTime;
	private ElevatorState state;
	private Queue<Request> eventsQueue;
	private boolean debug = false;
	private int schedulerPort;
	private boolean destinationRequestFlag = false;
	private boolean motorFaultFlag = false;
	private boolean doorFaultFlag = false;
	private InetAddress host;
	
	public ElevatorSubsystem(String name, int port, int startFloor, int schedulerPort, int maxFloor, int travelTime, int passengerTime, int doorTime, String host){
		this.name = name;
		this.travelTime = travelTime;
		this.passengerTime = passengerTime;
		this.doorTime = doorTime;
		this.eventsQueue = new LinkedList<Request>();
		this.state = new ElevatorState(startFloor,startFloor, Direction.IDLE, ElevatorStatus.STOPPED, ElevatorDoorStatus.OPENED, maxFloor,travelTime,passengerTime,doorTime);
		this.schedulerPort = schedulerPort;
		try {
			this.host =  InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		server = new Server(this, port, this.debug);
		serverThread = new Thread(server, name);
		serverThread.start();
	}
	
	public synchronized void receiveEvent(Request event) {
		eventsQueue.add(event);
		this.notifyAll();
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
		this.consoleOutput(this.name + " is online. Waiting for a command from Scheduler...");
		while (true) {
			this.handleEvent(this.getNextEvent());
		}
	}

	private void handleEvent(Request event) {
		//switch statement corresponding to different "event handlers"
		if (event instanceof ElevatorArrivalRequest) {
			ElevatorArrivalRequest request = (ElevatorArrivalRequest) event;
			this.consoleOutput("Sending arrival notice.");
			this.sendToServer(request);
		} else if (event instanceof ElevatorDoorRequest) {
			ElevatorDoorRequest request = (ElevatorDoorRequest) event;
			if (request.getRequestAction() == ElevatorDoorStatus.OPENED) {
				this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Open elevator doors.");
				this.handleElevatorOpenDoor();
			} else if (request.getRequestAction() == ElevatorDoorStatus.CLOSED) {
				this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Close elevator doors.");
				this.handleElevatorCloseDoor();
			}
		} else if (event instanceof ElevatorMotorRequest) {
			ElevatorMotorRequest request = (ElevatorMotorRequest) event;
			if (request.getRequestAction() == Direction.IDLE) {
				this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Stop elevator.");
				this.handleElevatorStop();
			}
			else if (request.getRequestAction() == Direction.UP) {
				this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Move elevator up.");
				this.handleElevatorMoveUP();
			}
			else if (request.getRequestAction() == Direction.DOWN) {
				this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Move elevator down.");
				this.handleElevatorMoveDown();
			}
		} else if (event instanceof ElevatorDestinationRequest) {
			ElevatorDestinationRequest request = (ElevatorDestinationRequest) event;
			this.consoleOutput(RequestEvent.RECEIVED, "Floor " + request.getPickupFloor() , "Destination request to floor " + request.getDestinationFloor());
			this.handleDestinationRequest(request);
		}
		else if (event instanceof ElevatorWaitRequest) {
			this.consoleOutput(RequestEvent.RECEIVED, "Scheduler", "Waiting For Passengers");
			this.handleWaitForPassengers();
		}
	}

	//toggles lamp state dependent on floor provided
	private void toggleLamp(int floor, Boolean b){
		this.state.toggleLamp(floor, b);
	}

	//toggles lamp state dependent on floor provided
	private void handleElevatorStop(){
		this.state.setDirection(Direction.IDLE);
		this.state.setStatus(ElevatorStatus.STOPPED);
		this.consoleOutput("Turn off floor " + this.state.getCurrentFloor() + " button lamp if on.");
		this.toggleLamp(this.state.getCurrentFloor(), false);
		ElevatorMotorRequest request = new ElevatorMotorRequest(this.name, Direction.IDLE);
		this.consoleOutput(RequestEvent.SENT, "Scheduler", "Stopped at " + this.state.getCurrentFloor() + ".");
		this.sendToServer(request);
	}



	private void handleElevatorMoveUP(){
		if(this.state.getDoorStatus() != ElevatorDoorStatus.OPENED) {
			this.state.setDirection(Direction.UP);
			this.state.setStatus(ElevatorStatus.MOVING);
			this.consoleOutput("Elevator motor set to move up. Simulating travel time...");
			//check if fault
			if(!this.motorFaultFlag) {
				try {
					Thread.sleep(this.travelTime);
				} catch (java.lang.InterruptedException e) {
					e.printStackTrace();
				}
				this.state.setCurrentFloor(this.state.getCurrentFloor() + 1);
				this.consoleOutput(RequestEvent.SENT, "Scheduler", "Arriving at floor " + this.state.getCurrentFloor() + ".");
				ElevatorArrivalRequest request = new ElevatorArrivalRequest(this.name, Integer.toString(this.state.getCurrentFloor()), this.state.getDirection());
				this.sendToServer(request);
			}
		}
	}

	private void handleElevatorMoveDown(){
		if(this.state.getDoorStatus() != ElevatorDoorStatus.OPENED) {
			this.state.setDirection(Direction.DOWN);
			this.state.setStatus(ElevatorStatus.MOVING);
			this.consoleOutput("Elevator motor set to move down. Simulating travel time...");
			//check if fault
			if(!this.motorFaultFlag) {
				try {
					Thread.sleep(this.travelTime);
				} catch (java.lang.InterruptedException e) {
					e.printStackTrace();
				}
				this.state.setCurrentFloor(this.state.getCurrentFloor() - 1);
				this.consoleOutput(RequestEvent.SENT, "Scheduler", "Arriving at floor " + this.state.getCurrentFloor() + ".");
				ElevatorArrivalRequest request = new ElevatorArrivalRequest(this.name, Integer.toString(this.state.getCurrentFloor()), this.state.getDirection());
				this.sendToServer(request);
			}
            else{
                this.motorFaultFlag=false;
            }

		}

	}

	private void handleElevatorOpenDoor(){
		this.consoleOutput("Elevator opening doors...");
		//check if fault
		if(!this.doorFaultFlag) {
			try {
				Thread.sleep(this.doorTime);
			} catch (java.lang.InterruptedException e) {
				e.printStackTrace();
			}
			this.state.setDoorStatus(ElevatorDoorStatus.OPENED);
			this.consoleOutput(RequestEvent.SENT, "Scheduler", "Doors are opened.");
			ElevatorDoorRequest request = new ElevatorDoorRequest(this.name, ElevatorDoorStatus.OPENED);
			this.sendToServer(request);
		}
		else{
            this.doorFaultFlag=false;
        }
	}

	private void handleElevatorCloseDoor(){
		this.consoleOutput("Elevator closing doors...");
		if(!this.doorFaultFlag) {
			try {
				Thread.sleep(this.doorTime);
			} catch (java.lang.InterruptedException e) {
				e.printStackTrace();
			}
			this.state.setDoorStatus(ElevatorDoorStatus.CLOSED);
			this.consoleOutput(RequestEvent.SENT, "Scheduler", "Doors are closed.");
			ElevatorDoorRequest request = new ElevatorDoorRequest(this.name, ElevatorDoorStatus.CLOSED);
			this.sendToServer(request);
		}
        else{
            this.doorFaultFlag=false;
        }
	}

	private void handleWaitForPassengers(){
		this.consoleOutput("Elevator Waiting while loading/unloading passengers...");
		try {
			Thread.sleep(this.passengerTime);
		} catch (java.lang.InterruptedException e) {
			e.printStackTrace();
		}
		//creates temp queue
		Queue<Request> tmp = new LinkedList<Request>();
		int size = this.eventsQueue.size();
		//loop through entire queue
		for (int count = 0; count < size; count++) {
			Request head = eventsQueue.poll();// check to see if head is A destination request
			if (head instanceof ElevatorDestinationRequest){
				eventsQueue.offer(head);
				this.destinationRequestFlag=true;
			}
			else {
				tmp.offer(head);
			}
		} //arrange queue so that destination requests are at the front
		eventsQueue.addAll(tmp);
		if(!this.destinationRequestFlag){ // send event if no more destination requests
			ElevatorWaitRequest request = new ElevatorWaitRequest(this.name);
			this.sendToServer(request);
		}
	}

	private void handleDestinationRequest(ElevatorDestinationRequest request){
		this.toggleLamp(Integer.parseInt(request.getDestinationFloor()), true);
		//check if fault in request and set appropriate flag
		if(request.getFault()!=null){
			if(request.getFault() == Fault.MOTOR){
				this.motorFaultFlag=true;
			}
			else{
				this.doorFaultFlag=true;
			}
		}
		this.consoleOutput(RequestEvent.SENT, "Scheduler", "Destination request to " + request.getDestinationFloor());
		this.sendToServer(request);
		boolean tempflag = false;
		if(this.destinationRequestFlag) {
			//This works because the collection has been ordered to ensure all ElevatorDestinationRequests to the front
			Request head = eventsQueue.peek();
			if (!(head instanceof ElevatorDestinationRequest)) {
				tempflag = true;
			}
			if (tempflag) { // if no more destination requests send wait event
				ElevatorWaitRequest sendRequest = new ElevatorWaitRequest(this.name);
				this.consoleOutput(RequestEvent.SENT, "Scheduler", "Wait complete.");
				this.sendToServer(sendRequest);
				this.destinationRequestFlag = false;
			}
		}
	}


	private void sendToServer(Request request) {
		this.server.send(request, this.host.getHostAddress(), this.schedulerPort);
	}



	private void consoleOutput(String output) {
		System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + this.name + " : " + output);
	}

	private void consoleOutput(RequestEvent event, String target, String output) {
		if (event.equals(RequestEvent.SENT)) {
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + this.name + " : [EVENT SENT TO " + target + "] " + output);
		} else if (event.equals(RequestEvent.RECEIVED)) {
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + this.name + " : [EVENT RECEIVED FROM " + target + "] " + output);
		}
	}

	public static void main (String[] args){
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
					Integer.parseInt(elevatorConfiguration.get("startFloor")), Integer.parseInt(schedulerConfiguration.get("port")),tempfloor,
					Integer.parseInt(elevatorConfiguration.get("timeBetweenFloors")), Integer.parseInt(elevatorConfiguration.get("passengerWaitTime")),
					Integer.parseInt(elevatorConfiguration.get("doorOperationTime")), schedulerConfiguration.get("host"));
			
			//Spawn and start a new thread for this ElevatorSubsystem instance
			Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorName);
			elevatorSubsystemThread.start();
		}

	}

}
