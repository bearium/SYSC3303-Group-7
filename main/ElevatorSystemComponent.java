package main;

public interface ElevatorSystemComponent {
	
	public void receiveEvent(String event);
	public String getNextEvent();
	public String getName();
}
