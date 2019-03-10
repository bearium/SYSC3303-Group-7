SYSC 3303 Term Project. L1G7 - Iteration 3

TO IMPORT THE PROJECT INTO ECLIPSE
1. Import project from archive file into Eclipse
2. From Eclipse
	- Select 'File' menu
	- Select 'Import' menu item
	- Under 'General' select 'Projects From File System or Archive File'
	- Select 'Archive...'
	- Locate 'L1G7_Project_Iteration3.zip' as Import source.
	- Click 'Finish'
3. From within the project "SYSC3303-Group-7"
	- Run Scheduler.java (located src > main > scheduler)
	- Run ElevatorSubsystem.java (located src > main > elevatorSubsystem)
	- Run FloorSubsystem.java (located src > main > floorSubsystem)
	- NOTE: 
		- IF an error occurs immediately upon running either the Scheduler, ElevatorSubsystem or FloorSubsystem, the likely cause is due to ports in the local environment that are already in use.
		- To change the configuration the config.xml file needs to be updated. This file is located at src\resources\config.xml
		- Every <Sheduler> <Elevator> and <Floor> element have a port attribute defined. Update the corresponding ports for the Class that has displayed an error (Scheduler / Floor / Elevator)
		- IMPORTANT: All programs MUST be restarted in order for the updated ports to take effect.
	- To change the trips scheduled for the elevators, the requests.txt document must be modified.
		- The file is located at src\resources\requests.txt
		- Trip requests must be entered one per line
			- in the following format: relative time from now, pickup floor, trip direction, destination floor, fault [optional]
				- 00:00:13.000 2 UP 15 Motor
					- relative time from now = 00:00:13.000 - this means this request will be sent in 13 seconds
					- pickup floor = 2 
					- direction = up
					- destination floor = 15
					- fault = Motor
						- this can be either 'Motor' or 'Door'
						- this param is optional.
	
FILE EXPLANATIONS (Main files)
There are 3 files that are necessary to run the elevator system.
	- ElevatorSubsystem.java
		- When run from main(), this will instantiate all elevators in separate threads as defined in the config.xml file. Each elevator thread waits for an event from the Scheduler to trigger an action.
	- FloorSubsystem.java
		- When run from main(), this will instantiate all floors in separate threads as defined in the config.xml file. Then the requests.txt file is parsed, each request defined in this file is sent to the corresponding floor (the main method controls the timing of each request such that each request is sent relative in time to the preceding request). When each floor receives a trip request from the main() method, it will send this to the Scheduler. This simulates a trip request coming from each floor.
	- Scheduler.java
		- When run from main(), this will instantiate the scheduler as defined in the config.xml file. The scheduler will then wait to receive and process requests.


All Diagrams are located in the 'doc' folder

BREAKDOWN OF RESPONSIBILITIES for Iteration 3
Dillon Claremont - Update Scheduler to detect and handle error scenarios (elevator stuck/ door stuck)
Thomas Bryk - Update FloorSubsystem to allow faults (elevator stuck/ door stuck) to be encoded in input file
Jacob Martin - Update ElevatorSubsystem to handle faults (elevator stuck/ door stuck) received from FloorSubsystem
Mustafa Abdulmajeed - Update Request system to support the above mentioned error scenarios. GUI planning + prototyping
Gordon Macdonald - GUI planning

BREAKDOWN OF RESPONSIBILITIES for Iteration 2
Dillon Claremont - Update Scheduler to support destination requests directly from elevator, Diagrams.
Thomas Bryk - Update FloorSubsystem to send destination requests to arriving elevators
Jacob Martin - Update ElevatorSubsystem to receive destination requests from FloorSubsystem and send to Scheduler
Mustafa Abdulmajeed - Update Request system with new requests and necessary modifications
Gordon Macdonald - Test Framework, Diagrams, UML documentation

BREAKDOWN OF RESPONSIBILITIES for Iteration 1
Dillon Claremont - Scheduler + related classes & State Diagram
Thomas Bryk - FloorSubsystem + related classes
Jacob Martin - ElevatorSubsystem + related classes
Mustafa Abdulmajeed - Request system
Gordon Macdonald - Test framework + UML documentation

 

