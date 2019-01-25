package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class ElevatorSystemConfiguration { 
	public final static int DEFAULT_PACKET_SIZE=1024;
	public final static boolean SEND_PACKET_EVENT = true;			//This boolean is used to indicate a send packet event occurred
	public final static boolean RECEIVE_PACKET_EVENT = false;		//This boolean is used to indicate a receive packet event occurred
	private static String configFile = "resources/config.xml";
	private static Document configDocument = null;
	
	/**
	 * Read config file from project resources.
	 */
	static void readConfig() {
		File file = new File(ElevatorSystemConfiguration.class.getClassLoader().getResource(configFile).getFile());
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			configDocument = builder.parse(file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Get configuration for all elevators.
	 * 
	 * @return - HashMap containing an entry for each elevator, where the key is the elevator name and the value is the port (each elevator will listen at this specified port)
	 */
	static HashMap<String, String> getAllElevatorSubsystemConfigurations(){
		HashMap<String, String> config = new HashMap<String, String>();
		if (configDocument == null) {
			readConfig();
		}
		
		NodeList nList = configDocument.getElementsByTagName("Elevator");
		Node node = null;
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			NamedNodeMap nm = node.getAttributes();
			String name = nm.getNamedItem("name").getNodeValue();
			String port = nm.getNamedItem("port").getNodeValue();
			config.put(name, port);
			System.out.println("");
		}
		return config;
	}
	
	/**
	 * Get configuration for all floors.
	 * 
	 * @return - HashMap containing an entry for each elevator, where the key is the elevator name and the value is the port (each elevator will listen at this specified port)
	 */
	static HashMap<String, String> getAllFloorSubsytemConfigurations(){
		HashMap<String, String> config = new HashMap<String, String>();
		if (configDocument == null) {
			readConfig();
		}
		
		NodeList nList = configDocument.getElementsByTagName("Floor");
		Node node = null;
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			NamedNodeMap nm = node.getAttributes();
			String name = nm.getNamedItem("name").getNodeValue();
			String port = nm.getNamedItem("port").getNodeValue();
			config.put(name, port);
			System.out.println("");
		}
		return config;
	}
}
