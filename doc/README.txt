SYSC 3303 Term Project. L1G7 s- Iteration 1

TO IMPORT THE PROJECT INTO ECLIPSE
1. Import project from archive file into Eclipse
2. From Eclipse
	- Select 'File' menu
	- Select 'Import' menu item
	- Under 'General' select 'Projects From File System or Archive File'
	- Select 'Archive...'
	- Locate 'L1G7_Project_Iteration1.zip' as Import source.
	- Click 'Finish'
3. From within the project
	- Run Scheduler.java
	- Run ElevatorSubsystem.java
	- Run FloorSubsystem.java

Main file explanation
There are 3 files that are necessary to run the elevator system.
	- ElevatorSubsystem.java
		- When run from main(), this will instantiate all elevators in separate threads as defined in the config.xml file. Each elevator thread waits for an event from the Scheduler to trigger an action.
	- FloorSubsystem.java
		- When run from main(), this will instantiate all floors in separate threads as defined in the config.xml file. Then the requests.txt file is parsed, each request defined in this file is sent to the corresponding floor (the main method controls the timing of each request such that each request is sent relative in time to the preceding request). When each floor receives a trip request from the main() method, it will send this to the Scheduler. This simulates a trip request coming from each floor.
	- Scheduler.java
		- When run from main(), this will instantiate the scheduler as defined in the config.xml file. The scheduler will then wait to receive and process requests.
