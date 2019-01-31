package shared;

import java.net.DatagramPacket;

import main.global.Direction;
import main.global.SystemComponent;

public class Test {
	public static void main(String[] args){
		Request request = new ElevatorMotorRequest("ELEVATOR NAME", Direction.UP);
		
		try {
			DatagramPacket packet = Helper.CreateRequest(request);
			Request r = Helper.ParseRequest(packet);
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	
