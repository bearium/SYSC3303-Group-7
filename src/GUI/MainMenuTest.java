package GUI;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import main.elevatorSubsystem.ElevatorSubsystem;
import main.floorSubsystem.FloorSubsystem;
import main.global.ElevatorSystemConfiguration;
import main.scheduler.Scheduler;



public class MainMenuTest implements ActionListener{

	private JFrame frame;
	

	/**
	 * Create the application.
	 */
	public MainMenuTest() {
		initialize();
	}
	
	

	private void initialize() {
		frame = new JFrame("Main Menu");
		frame.setBounds(100, 100, 640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		JButton initFloorSub = new JButton("Run Program");
		initFloorSub.addActionListener(this);
		initFloorSub.setSize(100,50);
		initFloorSub.setActionCommand("run");
		

		JButton initElevSub = new JButton("Initialize Elevator Systems");
		initElevSub.addActionListener(this);
		initElevSub.setSize(100,50);
		initElevSub.setActionCommand("initialize");
		
		
		frame.getContentPane().add(initElevSub);
		frame.getContentPane().add(initFloorSub);
		frame.getContentPane().setLayout(new GridLayout());
		frame.pack();
		frame.setSize(400, 500);
		frame.setVisible(true);
		
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		switch(command) {
		case ("initialize"):
			runScheduler();
			runElevatorSubsystem();
		break;
		
		case ("run"):
			runFloorSubsystem();
		break;
		}
			
		}

	private void runScheduler(){
		
		//This will return a Map of Maps. First key -> elevator Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> elevatorConfigurations = ElevatorSystemConfiguration.getAllElevatorSubsystemConfigurations();
				
		//This will return a Map of Maps. First key -> floor Name, Value -> map of all attributes for that elevator (as per config.xml)
		HashMap<String, HashMap<String, String>> floorConfigurations = ElevatorSystemConfiguration.getAllFloorSubsytemConfigurations();
				
		//This will return a Map of all attributes for the Scheduler (as per config.xml)
		HashMap<String, String> schedulerConfiguration = ElevatorSystemConfiguration.getSchedulerConfiguration();
				
		//Instantiate the scheduler
		Scheduler scheduler = new Scheduler(schedulerConfiguration.get("name"), Integer.parseInt(schedulerConfiguration.get("port")), elevatorConfigurations, floorConfigurations);
				
		//Spawn and start a new thread for this Scheduler
		Thread schedulerThread = new Thread(scheduler, schedulerConfiguration.get("name"));
		ElevatorFrame frame = new ElevatorFrame(scheduler.getElevatorMonitorByElevatorName());
		schedulerThread.start();
	}
		
		


	private void runElevatorSubsystem() {
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
							Integer.parseInt(elevatorConfiguration.get("doorOperationTime")));
							
					//Spawn and start a new thread for this ElevatorSubsystem instance
					Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorName);
					elevatorSubsystemThread.start();
				}
				
		
	}
	
	
	
	/**
	 * KNOWN BUG: Currently floor subsystem does not run properly due to restricted permissions to helper methods.
	 * 3/27/2019
	 */
	private void runFloorSubsystem() {
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


		
	}

	public static void main(String[] args) {
		MainMenuTest menu = new MainMenuTest();
		
		

	}

}
