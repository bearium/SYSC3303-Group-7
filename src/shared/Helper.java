package shared;

import java.net.DatagramPacket;

public final class Helper {
	
	/**
	 * Parses a request class from a datagram packet
	 * @param packet
	 * @return
	 * @throws InvalidRequestException
	 */
	public static Request ParseRequest(DatagramPacket packet) throws InvalidRequestException{
		byte[] data = packet.getData();
		
		int counter = 0;
		if(data[counter++] != 0){
			throw Invalid();
		}
		
		
		return null;
		
	}
	
	/**
	 * Creates a datagram packet from a request class
	 * @param request
	 * @return
	 */
	public static DatagramPacket CreateRequest(Request request){
		return null;
		
	}
	public static InvalidRequestException Invalid() {
		return new InvalidRequestException();
	}
}
