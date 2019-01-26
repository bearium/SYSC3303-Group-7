package main.global;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
	 * Method used to parse config.xml and return a map of configurations for multiple items.
	 * This will return a map containing the attributes for all instances of 'element'. 
	 * 
	 * The Map is structured as such: [ <element name> : [<attribute name> : <attribute value>, 
	 * 													  <attribute name> : <attribute value>],
	 * 									<element name> : [<attribute name> : <attribute value>, 
	 * 													  <attribute name> : <attribute value>],
	 * 									... ]
	 * @param element		- the tag associated with an element to parse from config.xml (ie. Elevator, Floor, Scheduler)
	 * @return
	 */
	static public HashMap<String, HashMap<String, String>> getConfigurations(String element){
		HashMap<String, HashMap<String, String>> config = new HashMap<String, HashMap<String, String>>();
		if (configDocument == null) {
			readConfig();
		}
		
		NodeList nodeList = configDocument.getElementsByTagName(element);
		Node node = null;
		
		//Iterate through each of the element nodes
		for (int i = 0; i < nodeList.getLength(); i++) {
			HashMap<String, String> attributesMap = new HashMap<String, String>();
			
			//For each 'element' node get a NamedNodeMap consisting of key value pairs of all of this element's attributes
			node = nodeList.item(i);
			NamedNodeMap nm = node.getAttributes();

			//Iterate through each of the attributes and construct a map of key value pairs for each
			int numberOfAttributes = nm.getLength();
			for (int j = 0; j < numberOfAttributes; j++) {
				   Node temp = nm.item(j);
				   String attributeName = temp.getNodeName();
				   String attributeValue = temp.getNodeValue();
				   attributesMap.put(attributeName, attributeValue);
			}
			
			//Get the name of this element, a name attribute must be specified in the config.xml to use as a key for the configuration map.
			//If a name is present remove from attributesMap and add to config.
			String name = attributesMap.get("name");
			if (name != null) {
				attributesMap.remove("name");
				config.put(name, attributesMap);
			}
		}
		return config;
	}
	
	/**
	 * Method used to parse config.xml and return a map of configurations for a single item.
	 * This will return a map containing the attributes for all instances of 'element'. 
	 * 
	 * The Map is structured as such: [ <attribute name> : <attribute value>, 
	 * 								    <attribute name> : <attribute value>,
	 * 									... ]
	 * @param element	- the tag associated with an element to parse from config.xml (ie. Scheduler, etc.)
	 * @return
	 */
	static public HashMap<String, String> getConfiguration(String element){
		HashMap<String, String> attributesMap = new HashMap<String, String>();
		if (configDocument == null) {
			readConfig();
		}
		
		NodeList nodeList = configDocument.getElementsByTagName(element);
		Node node = null;
		
		
		//For each 'element' node get a NamedNodeMap consisting of key value pairs of all of this element's attributes
		node = nodeList.item(0);
		NamedNodeMap nm = node.getAttributes();

		//Iterate through each of the attributes and construct a map of key value pairs for each
		int numberOfAttributes = nm.getLength();
		for (int j = 0; j < numberOfAttributes; j++) {
			   Node temp = nm.item(j);
			   String attributeName = temp.getNodeName();
			   String attributeValue = temp.getNodeValue();
			   attributesMap.put(attributeName, attributeValue);
		}
		
		return attributesMap;
	}
	
	/**
	 * Get configuration for all elevators.
	 * 
	 * @return - HashMap containing an entry for each elevator, where the key is the elevator name and the value is the port (each elevator will listen at this specified port)
	 */
	static public HashMap<String, HashMap<String, String>> getAllElevatorSubsystemConfigurations(){
		return getConfigurations("Elevator");
	}
	
	/**
	 * Get configuration for all floors.
	 * 
	 * @return - HashMap containing an entry for each elevator, where the key is the elevator name and the value is the port (each elevator will listen at this specified port)
	 */
	static public HashMap<String, HashMap<String, String>> getAllFloorSubsytemConfigurations(){
		return getConfigurations("Floor");
	}
	
	static public HashMap<String, String> getSchedulerConfiguration(){
		return getConfiguration("Scheduler");
	}
}
