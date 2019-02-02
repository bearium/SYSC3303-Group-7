package main;

import main.requests.Request;

public interface ElevatorSystemComponent {
	public void receiveEvent(Request event);
	public Request getNextEvent();
	public String getName();
}
