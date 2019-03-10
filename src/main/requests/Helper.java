package main.requests;

import java.net.DatagramPacket;


public final class Helper {
	private static Populater populater = new Populater();
	private static Parser parser = new Parser();
	public static final int buffer_size = 1024; // Max information to be contained in a datagram packet
	
	/**
	 * Creates a datagram packet from a request class
	 * @param request Request create a packet for
	 * @return Datagram packet containing the data, ready to send (does not contain host or port)
	 * @throws InvalidRequestException  In case the request contains null information, it will be invalid
	 */
	public static synchronized DatagramPacket CreateRequest(Request request) throws InvalidRequestException{
		
		return populater.PopulateRequest(request);
	}
	
	/**
	 * Parses a request class from a datagram packet's data
	 * @param packet packet with data to parse
	 * @return a generic Request object. Can be checked using instanceof to find the corresponding request
	 * @throws InvalidRequestException The data in the array was corrupt and could not fit parse criteria
	 */
	public static synchronized Request ParseRequest(DatagramPacket packet) throws InvalidRequestException{
		return parser.ParseRequest(packet);
	}
}
