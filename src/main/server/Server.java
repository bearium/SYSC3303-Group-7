package main.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import main.ElevatorSystemComponent;
import main.global.ElevatorSystemConfiguration;

public class Server implements Runnable{

	private DatagramSocket receiveSocket;
	private DatagramSocket sendSocket;
	private String role;
	private ElevatorSystemComponent elevatorSystemComponent;
	private boolean debug;
	
	public Server(ElevatorSystemComponent elevatorSystemComponent, int port, boolean debug) {
		this.elevatorSystemComponent = elevatorSystemComponent;
		this.role = elevatorSystemComponent.getName() + "_server";
		this.debug = debug;
		try {
			//Instantiate a socket to be used for receiving packets on specific port.
			this.receiveSocket = new DatagramSocket(port);
			//Instantiate a socket to be used for sending and receiving packets
			this.sendSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a request packet.
	 * Accepts a pre formed packet. Expecting the packet to contain byte[] encoded with the event details. The packet is then updated to be sent to the specified port (and InetAddress)
	 * Prints details about the packet send event (Leverages printPacketEventDetails()).
	 * Socket is not closed when send is complete.
	 * 
	 * @param msg
	 * @param length
	 * @param inetAddress
	 * @param socket
	 * @param port
	 */
	public void send(Request request, InetAddress inetAddress, int port) {
		//TODO awaiting Mustafa's implementation
		//DatagramPacket packet = Helper.createPacket(request);
		
		//Set destination of packet
		packet.setAddress(inetAddress);
		packet.setPort(port);
		
		if(this.debug) {
			printPacketEventDetails(ElevatorSystemConfiguration.SEND_PACKET_EVENT, packet, this.sendSocket);
		}
		
		//Send packet using sendSocket
		try {
			this.sendSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
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
	
	/**
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
	
    public void close() {
        receiveSocket.close();
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			DatagramPacket packet = null;
			try {
				packet = receive(receiveSocket, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//TODO awaiting mustafa's implementation
			//elevatorSystemComponent.receiveEvent(Helper.processPacket(packet));
		}
	}
}
