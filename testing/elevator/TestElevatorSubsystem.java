package elevator;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Queue;

import org.junit.*;

import main.elevatorSubsystem.ElevatorSubsystem;
import main.global.ElevatorSystemConfiguration;
import main.server.Server;

public class TestElevatorSubsystem {
	
	
	byte[] event1 = "Event1".getBytes();
	byte[] event2 = "Event2".getBytes();
	byte[] event3 = "Event3".getBytes();
	DatagramPacket packet1 = null;
	DatagramPacket packet2 = null;
	DatagramPacket packet3 = null;
	

	@Before
	public void setUp() throws Exception {
		
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();

		//Iterate through each elevator and create an instance of an ElevatorSubsystem
		for (String elevatorName : elevatorConfigurations.keySet()) {
			//Get the configuration for this particular 'elevatorName'
			HashMap<String, String> elevatorConfiguration = elevatorConfigurations.get(elevatorName);
			
			//Create an instance of ElevatorSubsystem for this 'elevatorName'
			ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorName, Integer.parseInt(elevatorConfiguration.get("port")));
			
			Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorName);
			elevatorSubsystemThread.start();
		}
		
		//Wait 10 sec to send test req's to elevator subsystem
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		}
	
	@Test//testing send and receive
	public void testPacketSnR(){
		try {
			//Send to Elevator 1
			packet1 = new DatagramPacket(event1, event1.length, InetAddress.getLocalHost(), 6000);
			//assertEquals -> confirm successful 
			//Send to Elevator 2
			packet2 = new DatagramPacket(event2, event2.length, InetAddress.getLocalHost(), 6001);
			//assertEquals -> confirm successful
			//Send to Elevator 1
			packet3 = new DatagramPacket(event3, event3.length, InetAddress.getLocalHost(), 6000);
			//assertEquals -> confirm successful
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
}
