package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import main.global.ElevatorSystemConfiguration;
import main.server.Server;

public class ElevatorSubsystem implements Runnable, ElevatorSystemComponent {

	private Server server;
	private Thread serverThread;
	private String name;
	private Queue<String> eventsQueue;
	private boolean debug = false;
	
	public ElevatorSubsystem(String name, int port){
		this.name = name;
		this.eventsQueue = new LinkedList<String>();

		//Create a server (bound to this Instance of ElevatorSubsystem) in a new thread.
		//When this server receives requests, they will be added to the eventsQueue of THIS ElevatorSubsystem instance.
		serverThread = new Thread(new Server(this, port, this.debug), name);
		serverThread.start();
	}
	
	public synchronized void receiveEvent(String event) {
		eventsQueue.add(event);
	}
	
	public synchronized String getNextEvent() {
		return eventsQueue.poll();
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public void run() {
		while (true) {			
			//After each event check to see if the next event has been set
			String event = this.getNextEvent();
			if (event != null) {
				System.out.println(this.name + ": Event received: " + event);
				System.out.println(this.name + ": Simulating: " + event + " for 5 seconds...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(this.name + ": Simulation of " + event + " complete");
			}
		}
	}

	public static void main(String[] args){
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();

		//Iterate through each elevator and create an instance of an ElevatorSubsystem
		for (String elevatorName : elevatorConfigurations.keySet()) {
			//Get the configuration for this particular 'elevatorName'
			HashMap<String, String> elevatorConfiguration = elevatorConfigurations.get(elevatorName);
			
			//Create an instance of ElevatorSubsystem for this 'elevatorName'
			ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorName, Integer.parseInt(elevatorConfiguration.get("port")));
			
			//Spawn and start a new thread for this ElevatorSubsystem instance
			Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorName);
			elevatorSubsystemThread.start();
		}

		
		//This is some bogus code for testing basic functionality of ElevatorSubsystem / server interactions
		
		//Wait 10 seconds to until sending a fake event to the elevator subsystem
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//Create a bogus packet and send it to the elevator subsystem
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		byte[] event1 = "Event1".getBytes();
		byte[] event2 = "Event2".getBytes();
		byte[] event3 = "Event3".getBytes();
		DatagramPacket packet1 = null;
		DatagramPacket packet2 = null;
		DatagramPacket packet3 = null;
		try {
			//Send to Elevator 1
			packet1 = new DatagramPacket(event1, event1.length, InetAddress.getLocalHost(), 6000);
			//Send to Elevator 2
			packet2 = new DatagramPacket(event2, event2.length, InetAddress.getLocalHost(), 6001);
			//Send to Elevator 1
			packet3 = new DatagramPacket(event3, event3.length, InetAddress.getLocalHost(), 6000);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ds.send(packet1);
			ds.send(packet2);
			ds.send(packet3);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
