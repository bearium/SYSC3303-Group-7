package shared;

public class Request {
	public enum SystemComponent {Elevator, Scheduler, Floor, ArrivalSensor}
	
	SystemComponent Source;
	SystemComponent Destination;
	
	String SourceName;
}
