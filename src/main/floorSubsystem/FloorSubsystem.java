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

public class FloorSubsystem implements Runnable, ElevatorSystemComponent {

	/*
     * Constructor for floor
	 * 
	 * @param Request - will take in data from request
	 */

    private Server server;
    private Thread serverThread;
    private String name;
    private Queue<Request> requestsQueue;
    private int schedulerPort;
    private boolean debug = false;
    private static String requestsFile = "resources/requests.txt";
    private LampStatus buttonLamp_UP;
    private LampStatus buttonLamp_DOWN;

    public FloorSubsystem(String name, int port, int schedulerPort) {
        this.name = name;
        this.requestsQueue = new LinkedList<Request>();
        this.schedulerPort = schedulerPort;
        this.buttonLamp_UP = LampStatus.OFF;
        this.buttonLamp_DOWN = LampStatus.OFF;

        // Create a server (bound to this Instance of FloorSubsystem) in a new thread.
        // When this server receives requests, they will be added to the requestsQueue of this FloorSubsystem instance.
        server = new Server(this, port, this.debug);
        serverThread = new Thread(server, name);
        serverThread.start();
    }

    public synchronized void receiveEvent(Request event) {
        requestsQueue.add(event);
        this.notifyAll();
    }

    public synchronized Request getNextEvent() {
        while (requestsQueue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return requestsQueue.poll();
    }

    public String getName() {
        return this.name;
    }

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

    private void toggleFloorLamp(Direction direction, LampStatus lampStatus) {
        consoleOutput("Floor " + this.getName() + ": Turning " + direction.toString() + " button lamp " + lampStatus.toString() + ".");
        if (direction == Direction.UP)
            buttonLamp_UP = lampStatus;
        else if (direction == Direction.DOWN)
            buttonLamp_DOWN = lampStatus;
    }

    private static Date convertTime(String s) {
        DateFormat format = new SimpleDateFormat("hh:mm:ss.SSS", Locale.ENGLISH);
        try {
            return format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<FloorButtonRequest> readInputFromFile() {
        FileReader input = null;
        try {
            String requestsFilePath = new File(FloorSubsystem.class.getClassLoader().getResource(requestsFile).getFile()).getAbsolutePath().replace("%20", " ");
            input = new FileReader(requestsFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;

        List<FloorButtonRequest> requests = new LinkedList<FloorButtonRequest>();

        try {
            while ((myLine = bufRead.readLine()) != null) {
                String[] info = myLine.split(" ");

                String time = info[0];
                String floorName = info[1];
                Direction direction = getDirectionFromString(info[2]);
                String destinationFloor = info[3];

                FloorButtonRequest currRequest = new FloorButtonRequest(time, floorName, direction, destinationFloor);
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

    private void handleEvent(Request event) {
        //switch statement corresponding to different "event handlers"
        if (event instanceof FloorButtonRequest) {
            FloorButtonRequest request = (FloorButtonRequest) event;
            try {
                this.server.send(request, InetAddress.getLocalHost(), schedulerPort);
                consoleOutput("Floor " + this.getName() + ": Floor trip request sent.");
                toggleFloorLamp(request.getDirection(), LampStatus.ON);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if (event instanceof FloorLampRequest) {
            consoleOutput("Floor " + this.getName() + ": An elevator has arrived for pickup.");
            FloorLampRequest request = (FloorLampRequest) event;
            toggleFloorLamp(request.getDirection(), LampStatus.OFF);
        }
    }

    private static void consoleOutput(String output) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.S")) + "] " + output);
    }

    public static void main(String[] args) {
        List<FloorSubsystem> floors = new LinkedList<FloorSubsystem>();

        //This will return a Map of all attributes for the Scheduler (as per config.xml)
        HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();

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
                    Integer.parseInt(floorConfiguration.get("port")), Integer.parseInt(schedulerConfiguration.get("port")));
            floors.add(floorSubsystem);

            // Spawn and start a new thread for this floorSubsystem instance
            Thread floorSubsystemThread = new Thread(floorSubsystem, floorName);
            floorSubsystemThread.start();
        }

        List<FloorButtonRequest> requests = readInputFromFile();

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

        for (FloorButtonRequest currRequest : requests) {

            for (FloorSubsystem currFloor : floors) {
                if (currFloor.getName().equalsIgnoreCase(currRequest.getFloorName())) {
                    long currReqTime = (convertTime(currRequest.getTime())).getTime();

                    if (lastTime != 0) {
                        long timeDiff = currReqTime - lastTime;
                        try {
                            Thread.sleep(timeDiff);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    consoleOutput("Request details // Time:" + currRequest.getTime() + "  Floor Name: " + currRequest.getFloorName() + "  Direction: " + currRequest.getDirection() + "  Dest Floor: " + currRequest.getDestinationFloor());
                    currFloor.receiveEvent(currRequest);
                    lastTime = currReqTime;
                }
            }
        }
    }
}
