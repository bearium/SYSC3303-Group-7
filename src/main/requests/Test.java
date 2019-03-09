package main.requests;

import java.net.DatagramPacket;

import main.global.Direction;
import main.global.Fault;

public class Test {
	public static void main(String[] args){
		//Request request = new ElevatorMotorRequest("ELEVATOR NAME", Direction.UP);
		Request request = new ElevatorDestinationRequest("lol", "lol2", "Lol4", Fault.DOOR);
		request.setSource("Elevator 1");
		try {
			DatagramPacket packet = Helper.CreateRequest(request);
			Request r = Helper.ParseRequest(packet);
			ElevatorDestinationRequest rr = (ElevatorDestinationRequest) r;
			System.out.println(rr.getDestinationFloor()+" "+rr.getElevatorName()+" "+rr.getPickupFloor()+" "+rr.getFault());
		} catch (InvalidRequestException e) {
			e.printStackTrace();
		}
	}
}	
