package main.requests;
public class Request {

	/**
	 * The name of the source of the request
	 */
	String Source;
	
	/**
	 * The name of the destination of the request
	 */
	String Destination;
	
	/**
	 * Type of request for parsing purposes
	 */
	private byte[] RequestType;
	
	
	protected Request(){
		
	}
	
	/**
	 * Create a request with given source and destination names
	 * @param source
	 * @param destination
	 */
	protected Request(String source, String destination){
		this.Destination = destination;
		this.Source = source;
	}

	/**
	 * {@link Request#Source}
	 */
	public String getSource() {
		return Source;
	}
	
	/**
	 * {@link Request#Source}
	 */
	public void setSource(String sourceName) {
		Source = sourceName;
	}
	
	/**
	 * {@link Request#Destination}
	 */
	public String getDestination() {
		return Destination;
	}
	
	/**
	 * {@link Request#Destination}
	 */
	public void setDestination(String destinationName) {
		Destination = destinationName;
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
	
}
