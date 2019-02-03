SYSC 3303 Term Project. L1G7 - Iteration 1

TO IMPORT THE PROJECT INTO ECLIPSE
1. Import project from archive file into Eclipse
2. From Eclipse
	- Select 'File' menu
	- Select 'Import' menu item
	- Under 'General' select 'Projects From File System or Archive File'
	- Select 'Archive...'
	- Locate 'L1G7_Project_Iteration1.zip' as Import source.
	- Click 'Finish'
3. From within the project "SYSC3303-Group-7"
	- Run Scheduler.java (located src > main > scheduler)
	- Run ElevatorSubsystem.java (located src > main > elevatorSubsystem)
	- Run FloorSubsystem.java (located src > main > floorSubsystem)

FILE EXPLANATIONS (Main files)
There are 3 files that are necessary to run the elevator system.
	- ElevatorSubsystem.java
		- When run from main(), this will instantiate all elevators in separate threads as defined in the config.xml file. Each elevator thread waits for an event from the Scheduler to trigger an action.
	- FloorSubsystem.java
		- When run from main(), this will instantiate all floors in separate threads as defined in the config.xml file. Then the requests.txt file is parsed, each request defined in this file is sent to the corresponding floor (the main method controls the timing of each request such that each request is sent relative in time to the preceding request). When each floor receives a trip request from the main() method, it will send this to the Scheduler. This simulates a trip request coming from each floor.
	- Scheduler.java
		- When run from main(), this will instantiate the scheduler as defined in the config.xml file. The scheduler will then wait to receive and process requests.


All Diagrams are located in the 'doc' folder

BREAKDOWN OF RESPONSIBILITIES for Iteration 1
Dillon Claremont - Scheduler + related classes & State Diagram
Thomas Bryk - FloorSubsystem + related classes
Jacob Martin - ElevatorSubsystem + related classes
Mustafa Abdulmajeed - Request system
Gordon Macdonald - Test framework + UML documentation
