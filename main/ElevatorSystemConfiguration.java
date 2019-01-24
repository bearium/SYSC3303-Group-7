package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;



public class ElevatorSystemConfiguration {
	public final static int DEFAULT_PACKET_SIZE=1024;
	public final static boolean SEND_PACKET_EVENT = true;			//This boolean is used to indicate a send packet event occurred
	public final static boolean RECEIVE_PACKET_EVENT = false;		//This boolean is used to indicate a receive packet event occurred
	public static HashMap<String, Integer> elevatorPorts = new HashMap<String, Integer>();
	private static int elevatorPort = 69;
	private static String configFile = "resources/config.xml";
	
	static void test() {
		InputStream inputStream = ElevatorSystemConfiguration.class.getClassLoader().getResourceAsStream("resources/config.xml");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
	    		doc = builder.parse(inputStream);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println(inputStream);
	}
	
	static void registerElevator(String name) {
		int nextAvailableElevatorPort = elevatorPort;
		
		while (elevatorPorts.containsValue(nextAvailableElevatorPort)) {
			nextAvailableElevatorPort++;
		}

		elevatorPorts.put(name, nextAvailableElevatorPort);
	}
	
	static int getElevatorPort(String elevatorName) {
		if (elevatorPorts.containsKey(elevatorName)) {
			return elevatorPorts.get(elevatorName);
		}
		return 0;
	}
}
