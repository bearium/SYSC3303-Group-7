package shared;

import java.net.DatagramPacket;

import main.global.Direction;
import main.global.SystemComponent;

public class Test {
	public static void main(String[] args){
		Request request = new ElevatorMotorRequest("ELEVATOR NAME", Direction.UP);
		request.Source = SystemComponent.Elevator;
		request.SourceName = "Hello";
		request.Destination = SystemComponent.Scheduler;
		request.DestinationName = "SWag";
		
		try {
			DatagramPacket packet = Helper.CreateRequest(request);
			Request r = Helper.ParseRequest(packet);
			System.out.println(r.DestinationName);
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	
