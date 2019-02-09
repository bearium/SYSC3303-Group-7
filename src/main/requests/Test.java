package main.requests;

import java.net.DatagramPacket;

import main.global.Direction;

public class Test {
	public static void main(String[] args){
		Request request = new ElevatorMotorRequest("ELEVATOR NAME", Direction.UP);
		request.setSource("Elevator 1");
		try {
			DatagramPacket packet = Helper.CreateRequest(request);
			Request r = Helper.ParseRequest(packet);
			System.out.println(r.getClass().getName());
		} catch (InvalidRequestException e) {
			e.printStackTrace();
		}
	}
}	
