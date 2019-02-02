package main.requests;

public class InvalidRequestException extends Exception{

	/**
	 * This is just here so that eclipse doesn't give me errors
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an invalid request exception
	 * @param message why the request is invalid
	 */
	public InvalidRequestException(String message){
		super(message);
	}
	
}
