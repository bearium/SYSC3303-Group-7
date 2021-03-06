package main.requests;
public class Request {

	/**
	 * The name of the destination of the request
	 */
	String Receiver;
	
	/**
	 * The name of the source of the request
	 */
	String Sender;
	
	/**
	 * Type of request for parsing purposes
	 */
	private byte[] RequestType;
	private long startTime, endTime, elapsedTime;
	
	protected Request(){
		
	}
	
	/**
	 * Create a request with given source and destination names
	 * @param source
	 * @param destination
	 */
	protected Request(String source, String destination){
		this.Sender = source;
		this.Receiver = destination;
	}

	/**
	 * {@link Request#Receiver}
	 */
	public String getSource() {
		return Receiver;
	}
	
	/**
	 * {@link Request#Receiver}
	 */
	public void setSource(String sourceName) {
		Receiver = sourceName;
	}
	
	/**
	 * {@link Request#Sender}
	 */
	public String getSender() {
		return Sender;
	}
	
	/**
	 * {@link Request#Sender}
	 */
	public void setSender(String destinationName) {
		Sender = destinationName;
	}
	
	/**
	 * Sets this class's request type to the given request type
	 * @param RequestType
	 */
	protected void setRequestType(byte[] RequestType){
		RequestType = new byte[]{RequestType[0],RequestType[1]};
		this.RequestType = RequestType;
	}
	
	/**
	 * Get type of the general request
	 * @return
	 */
	public byte[] IGetRequestType(){
		return this.RequestType;
	}
	
	/**
	 * Set's the start time for this Request as the current value of System.nanoTime()
	 */
	public void setStartTime() {
		this.startTime = System.nanoTime();
	}
	
	/**
	 * Set's the end time for this Request as the current value of System.nanoTime()
	 */
	public void setEndTime() {
		this.endTime = System.nanoTime();
		this.elapsedTime = this.endTime - this.startTime;
	}
	
	/**
	 * Returns the elapsed time for this Request in milliseconds
	 * @return
	 */
	public double getElapsedTime() {
		return (double) this.elapsedTime/1000000;
	}
	
}
