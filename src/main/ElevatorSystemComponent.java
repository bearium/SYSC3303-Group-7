package main;

import main.requests.Request;

public interface ElevatorSystemComponent {
	public final static String EVENT_RECEIVED = "[EVENT RECEIVED]";
	public final static String EVENT_SENT = "[EVENT SENT]";
	public final static String ACTION = "[ACTION]";
	
	public void receiveEvent(Request event);
	public Request getNextEvent();
	public String getName();
}
