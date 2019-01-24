package main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable{

	private DatagramSocket socket;
	private String role;
	private ElevatorSystemComponent elevatorSystemComponent;
	private boolean debug;
	
	public Server(ElevatorSystemComponent elevatorSystemComponent, int port, boolean debug) {
		this.elevatorSystemComponent = elevatorSystemComponent;
		this.role = elevatorSystemComponent.getName() + "_server";
		this.debug = debug;
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receive a packet, no timeout specified, wait indefinitely.
	 * Prints details about the packet receive event (Leverages printPacketEventDetails()).
	 * 
	 * @param socket
	 * @return
	 */
	public DatagramPacket receive(DatagramSocket socket) {
		DatagramPacket packet = null;
		try {
			packet = receive(socket, 0);
		} catch (Exception e) {
			//Since no timeout was specified, we know this is not a socket time out exception
			System.out.println ("Unhandled Exception Occurred. Exiting.");
			System.exit(1);
		}
		return packet;
	}
	
	/**D
	 * Receive a packet with a timeout specified.
	 * Waits for specified timeout.
	 * Prints details about the packet receive event (Leverages printPacketEventDetails()).
	 * 
	 * @param socket
	 * @param timeout - in milliseconds
	 * @return
	 */
	public DatagramPacket receive(DatagramSocket socket, int timeout) throws IOException {
		//Wait for 'packet' on 'socket'
		DatagramPacket packet = waitForPacket(socket, timeout);
		if(this.debug) {
			printPacketEventDetails(ElevatorSystemConfiguration.RECEIVE_PACKET_EVENT, packet, socket);
		}
		
		return packet;
	}
	
	/**
	 * 
	 * @param socket
	 * @return
	 */
	private DatagramPacket waitForPacket(DatagramSocket socket, int timeout) throws IOException {
		// Construct a DatagramPacket for receiving packets up to 100 bytes long (the length of the byte array).
		byte data[] = new byte[ElevatorSystemConfiguration.DEFAULT_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		// Block until a packet is received on socket.
		socket.setSoTimeout(timeout);
		if (this.debug) {
			if (timeout == 0) {
				System.out.println(this.role + ": Waiting for a packet on port " + socket.getLocalPort() + "... \n");
			} else {
				System.out.println(this.role + ": Waiting " + timeout/1000 + "s for a packet on port " + socket.getLocalPort() + "... \n");
			}
		}
		socket.receive(packet);

		return packet;
	}
	
	/**
	 * Print relevant details about the packet event. Context is specific to whether the event is a send or a receive.
	 * Displays:
	 *  - event description & the 'role' of the Host the event occurred on.
	 *  - If sending packet: the internet address and target port of the recipient of the packet; as well as the port the packet is sent on
	 *  - If receiving packet: the internet address and source port of the sender of the packet; as well as the port the packet is received on
	 *  - length of the packet message (# of bytes)
	 *  - String representation of the packet contents.
	 *  - byte representation of the packet contents.
	 * @param send
	 * @param packet
	 * @param socket
	 */
	private void printPacketEventDetails(boolean packetEvent, DatagramPacket packet, DatagramSocket socket) {
		int len = packet.getLength();
		if (packetEvent == ElevatorSystemConfiguration.SEND_PACKET_EVENT) {
			System.out.println(this.role + ": Sending Packet");
			System.out.println("To Host: " + packet.getAddress());
			System.out.println("To Host port: " + packet.getPort());
			System.out.println("Sent using port: " + socket.getLocalPort());
		} else if (packetEvent == ElevatorSystemConfiguration.RECEIVE_PACKET_EVENT) {
			System.out.println(this.role + ": Received Packet");
			System.out.println("From Host: " + packet.getAddress());
			System.out.println("From Host port: " + packet.getPort());
			System.out.println("Received on Port: " + socket.getLocalPort());
		}
		System.out.println("Packet length: " + len);
		System.out.println("Packet contains (String): " + new String(packet.getData(),0,len));
		System.out.println("Packet contains (bytes): " + getPacketDataBytesAsString(packet));
		System.out.println();
	}
	
	/**
	 * Translates the packet data buffer of bytes into a String representation of the byte values.
	 * 
	 * @param packet
	 * @return
	 */
	private String getPacketDataBytesAsString(DatagramPacket packet) {
		StringBuilder sb = new StringBuilder();
		byte[] buffer = packet.getData();
		sb.append("[");
		for (int i = 0; i < packet.getLength(); i ++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(buffer[i]);
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			DatagramPacket packet = null;
			try {
				packet = receive(socket, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			elevatorSystemComponent.receiveEvent(new String(packet.getData(), 0, packet.getLength()));
		}
	}
}
