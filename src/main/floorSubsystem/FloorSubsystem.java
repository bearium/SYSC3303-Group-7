package main.floorSubsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import main.ElevatorSystemComponent;
import main.global.*;
import main.requests.*;
import main.server.*;


/**
 * The purpose of this class is to create trip requests for passengers to use the elevator system.
 * The floor is responsible for:
 * 	- reading requests from an input file
 *  - creating trip requests to be sent to the elevator
 *  - turning on and off button lamps when uses press them for a request
 */
public class FloorSubsystem implements Runnable, ElevatorSystemComponent {


    private Server server;
    private String name;
    //private Queue<FloorButtonRequest> pickupQueue;                          //Queue of requests to be sent
    private int schedulerPort;
    private final boolean debug = false;
    private final static String requestsFile = "resources/requests.txt";
    private LampStatus buttonLamp_UP;                                       //Button lamp for UP button
    private LampStatus buttonLamp_DOWN;                                     //Button lamp for DOWN button
    private Queue<FloorButtonRequest> upQueue;                              //Queue of requests to be sent to elevator taking UP requests
    private Queue<FloorButtonRequest> downQueue;                            //Queue of requests to be sent to elevator taking DOWN requests
    private HashMap<String, Integer> portsByElevatorName;                   //Map of ports for each elevator
	private Queue<Request> eventsQueue;
	
    /**
     * Constructor for floor
     *
     * @param name
     * @param port
     * @param schedulerPort
     * @param elevatorConfiguration
     */
    private FloorSubsystem(String name, int port, int schedulerPort, HashMap<String, HashMap<String, String>> elevatorConfiguration) {
        //Set fields
        this.name = name;
        this.upQueue = new LinkedList<FloorButtonRequest>();
        this.downQueue = new LinkedList<FloorButtonRequest>();
        this.schedulerPort = schedulerPort;
        this.buttonLamp_UP = LampStatus.OFF;
        this.buttonLamp_DOWN = LampStatus.OFF;
        this.portsByElevatorName = new HashMap<String, Integer>();
		this.eventsQueue = new LinkedList<Request>();

        // Create a server (bound to this Instance of FloorSubsystem) in a new thread.
        // When this server receives requests, they will be added to the pickupQueue of this FloorSubsystem instance.
        server = new Server(this, port, this.debug);
        Thread serverThread = new Thread(server, name);
        serverThread.start();

        //Initialize data structures for elevators
        for (String elevatorName : elevatorConfiguration.keySet()) {
            HashMap<String, String> config = elevatorConfiguration.get(elevatorName);
            this.portsByElevatorName.put(elevatorName, Integer.parseInt(config.get("port")));
        }
    }

    /**
     * Add an event to the pickupQueue.
     *
     * @param event
     */
    public synchronized void receiveEvent(Request event) {
		eventsQueue.add(event);
        this.notifyAll();                        //Notify all listeners
    }

    /**
     * Get next event from the pickupQueue.
     *
     * @return next request
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

    /**
     * Get the name of this floor
     *
     * @return name of floor
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets direction enum from string
     *
     * @param s string of direction
     * @return Direction status
     */
    private static Direction getDirectionFromString(String s) {
        switch (s.toLowerCase()) {
            case "up":
                return Direction.UP;
            case "down":
                return Direction.DOWN;
            default:
                return Direction.IDLE;
        }
    }

    /**
     * Send a request to port using this object's server.
     *
     * @param request
     * @param port
     */
    private void sendToServer(Request request, int port) {
        try {
            this.server.send(request, InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns floors up/down button lamps on/off
     *
     * @param direction Button lamp with this direction to be modified
     * @param lampStatus Set button lamp to this status
     */
    private void toggleFloorButtonLamp(Direction direction, LampStatus lampStatus) {
        this.consoleOutput("Turning " + direction.toString() + " button lamp " + lampStatus.toString() + ".");
        if (direction == Direction.UP)
            buttonLamp_UP = lampStatus;
        else if (direction == Direction.DOWN)
            buttonLamp_DOWN = lampStatus;
    }

    /**
     * Converts time in a string to a Date object, and returns it.
     *
     * @param dateString
     * @return Date
     */
    private static Date convertTime(String dateString) {
        DateFormat format = new SimpleDateFormat("hh:mm:ss.SSS", Locale.ENGLISH);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads input file at directory to grab requests to be sent to scheduler
     *
     * @return List of requests
     */
    private static List<FloorButtonRequest> readInputFromFile() {
        FileReader input = null;
        try {
            String requestsFilePath = new File(FloorSubsystem.class.getClassLoader().getResource(requestsFile).getFile()).getAbsolutePath().replace("%20", " "); //Retrieves input file
            input = new FileReader(requestsFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader bufRead = new BufferedReader(input);
        String myLine;

        List<FloorButtonRequest> requests = new LinkedList<FloorButtonRequest>();   //List of requests

        try {
            while ((myLine = bufRead.readLine()) != null) { //Loops through each line in file
                String[] info = myLine.split(" ");  //Splits line based on a space

                //Retrieve data from each line
                String time = info[0];
                String floorName = info[1];
                Direction direction = getDirectionFromString(info[2]);
                String destinationFloor = info[3];


                //Create floor button request with retrieved data, and add to ongoing list
                FloorButtonRequest currRequest = new FloorButtonRequest(time, floorName, direction, destinationFloor, null);
                requests.add(currRequest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requests;
    }

    @Override
    public void run() {
        while (true) {
            this.handleEvent(this.getNextEvent());
        }
    }

    /**
     * This method will determine the type of Request and call the appropriate event handler method for this request.
     * @param event the received event
     */
    private void handleEvent(Request event) {
        //switch statement corresponding to different "event handlers"
        if (event instanceof FloorButtonRequest) {      //If event received is a FloorButtonRequest
            FloorButtonRequest request = (FloorButtonRequest) event;
            
            this.consoleOutput(RequestEvent.RECEIVED, "Simulated Passenger", "Trip request going " + request.getDirection() + " to " + request.getDestinationFloor());

            if (request.getDirection() == Direction.UP){
                upQueue.add(request);
            } else if (request.getDirection() == Direction.DOWN){
                downQueue.add(request);
            }

            try {
                //Sends request to scheduler
                this.consoleOutput(RequestEvent.SENT, "Scheduler", "Trip request going " + request.getDirection());
                this.server.send(request, InetAddress.getLocalHost(), schedulerPort);
                toggleFloorButtonLamp(request.getDirection(), LampStatus.ON);   //Turn button lamp on for direction in request
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if (event instanceof ElevatorArrivalRequest) { //If event received is a ElevatorArrivalRequest
            ElevatorArrivalRequest request = (ElevatorArrivalRequest) event;
            this.consoleOutput(RequestEvent.RECEIVED, "Scheduler" , "Elevator " + request.getElevatorName() + " has arrived. Elevator is headed " + request.getDirection() + ".");
            if (request.getDirection() != Direction.IDLE) {
            	toggleFloorButtonLamp(request.getDirection(), LampStatus.OFF);  //Turn off button lamp since Elevator has arrived
            }
            sendRequestsToElevator(request);    //Elevator is arriving, send it trip requests
        }
    }

    /**
     * Method to send the arriving elevator all trip requests for the direction it will be travelling
     *
     * @param request
     */
    private void sendRequestsToElevator (ElevatorArrivalRequest request) {
        if (request.getDirection() == Direction.UP){    //If Elevator will be going up
            for (FloorButtonRequest currFloorButtonRequest : upQueue){  //Loop through the queue of trip requests going up
                ElevatorDestinationRequest currER = new ElevatorDestinationRequest(this.getName(), currFloorButtonRequest.getDestinationFloor(), request.getElevatorName());    //Create elevator destination request based on data from the queue
                this.consoleOutput(RequestEvent.SENT, request.getElevatorName(), "Destination request to floor " + currFloorButtonRequest.getDestinationFloor());
                sendToServer(currER, this.portsByElevatorName.get(request.getElevatorName()));    //Send the request to the elevator arriving
            }
            upQueue.clear(); //Clear requests from queue, since they've been sent
        } else if (request.getDirection() == Direction.DOWN) {    //If elevator will be going down
            for (FloorButtonRequest currFloorButtonRequest : downQueue){    //Loop through the queue of trip requests going down
                ElevatorDestinationRequest currER = new ElevatorDestinationRequest(this.getName(), currFloorButtonRequest.getDestinationFloor(), request.getElevatorName());    //Create elevator destination request based on data from the queue
                this.consoleOutput(RequestEvent.SENT, request.getElevatorName(), "Destination request to floor" + currFloorButtonRequest.getDestinationFloor());
                sendToServer(currER, this.portsByElevatorName.get(request.getElevatorName()));    //Send the request to the elevator arriving
            }
            downQueue.clear();   //Clear requests from queue, since they've been sent
        }
    }

    /**
     * Prints text with preset beginning and given string
     *
     * @param output string to be printed
     */
    private void consoleOutput(String output) {
		System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] Floor " + this.name + " : " + output);
	}

	private void consoleOutput(RequestEvent event, String target, String output) {
		if (event.equals(RequestEvent.SENT)) {
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] Floor " + this.name + " : [EVENT SENT TO " + target + "] " + output);
		} else if (event.equals(RequestEvent.RECEIVED)) {
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] Floor " + this.name + " : [EVENT RECEIVED FROM " + target + "] " + output);
		}
	}

    public static void main(String[] args) {
        List<FloorSubsystem> floors = new LinkedList<FloorSubsystem>();

        //This will return a Map of all attributes for the Scheduler (as per config.xml)
        HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();

        //This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
        HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();

        // This will return a Map of Maps. First key -> floor Name, Value -> map of
        // all attributes for that floor (as per config.xml)
        HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration
                .getAllFloorSubsytemConfigurations();

        // Iterate through each floor and create an instance of an floorSubsystem
        for (String floorName : floorConfigurations.keySet()) {
            // Get the configuration for this particular 'floorName'
            HashMap<String, String> floorConfiguration = floorConfigurations.get(floorName);

            // Create an instance of floorSubsystem for this 'floorName'
            FloorSubsystem floorSubsystem = new FloorSubsystem(floorName,
                    Integer.parseInt(floorConfiguration.get("port")), Integer.parseInt(schedulerConfiguration.get("port")), elevatorConfigurations);
            floors.add(floorSubsystem);

            // Spawn and start a new thread for this floorSubsystem instance
            Thread floorSubsystemThread = new Thread(floorSubsystem, floorName);
            floorSubsystemThread.start();
        }


        List<FloorButtonRequest> requests = readInputFromFile();    //Retrieve all requests from input file

        //Sort requests based on time to be sent
        Collections.sort(requests, new Comparator<FloorButtonRequest>() {
            @Override
            public int compare(FloorButtonRequest r1, FloorButtonRequest r2) {
                Date r1Time = convertTime(r1.getTime());
                Date r2Time = convertTime(r2.getTime());

                if (r1Time.after(r2Time))
                    return 1;
                else if (r1Time.before(r2Time))
                    return -1;
                else
                    return 0;
            }
        });

        long lastTime = 0;

        for (FloorButtonRequest currRequest : requests) {   //Loop over requests
            for (FloorSubsystem currFloor : floors) {   //Loop over floors
                if (currFloor.getName().equalsIgnoreCase(currRequest.getFloorName())) { //If request is meant for the current floor
                    long currReqTime = (convertTime(currRequest.getTime())).getTime();  //Get time of request

                    //Measure time between last request and current, and sleep for the time difference
                    if (lastTime != 0) {
                        long timeDiff = currReqTime - lastTime;
                        try {
                            Thread.sleep(timeDiff);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //Send request to floor to be sent to scheduler
                    System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] Request details // Time:" + currRequest.getTime() + "  Floor Name: " + currRequest.getFloorName() + "  Direction: " + currRequest.getDirection() + "  Dest Floor: " + currRequest.getDestinationFloor());
                    currFloor.receiveEvent(currRequest);
                    lastTime = currReqTime;
                }
            }
        }
    }
}
