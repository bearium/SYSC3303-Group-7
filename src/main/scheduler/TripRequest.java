package main.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import main.global.Direction;

/**
 * The TripRequest will model a trip request. It includes a pickup floor, destination floor and a direction.
 *
 */
public class TripRequest extends Observable{
	private int pickupFloor;
	private boolean completed;
	private int destinationFloor;
	private Direction direction;
	private boolean hasDestination;
	private long creationTime, startTime, completedTime;
	//private Date creationTime, startTime, completedTime;
	
	public TripRequest(int pickupFloor, Direction direction) {
		this.pickupFloor = pickupFloor;
		this.hasDestination = false;
		this.direction = direction;
		this.creationTime = System.currentTimeMillis();
		this.completed = false;
	}
	
	public boolean hasDestination() {
		return this.hasDestination;
	}
	/**
	 * Get the pickup floor.
	 * @return
	 */
	public int getPickupFloor() {
		return this.pickupFloor;
	}
	
	/**
	 * Get the destination floor.
	 * @return
	 */
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	/**
	 * Set the destination floor.
	 * @param destinationFloor
	 */
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
		this.hasDestination = true;
		this.setChanged();
		notifyObservers();
	}
	
	/**
	 * Get the direction.
	 * @return
	 */
	public Direction getDirection() {
		return this.direction;
	}
	
	/**
	 * A way to compare trip request objects. This is used to prevent duplicate trip requests in any set collection.
	 * @param tripRequest
	 * @return
	 */
	public boolean equals(TripRequest tripRequest) {
		if ((this.pickupFloor == tripRequest.getPickupFloor()) && (this.destinationFloor == tripRequest.getDestinationFloor()) && (this.direction == tripRequest.getDirection()) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Set the start time for this TripRequest. This should be the time the pickup floor is reached.
	 */
	public void setStarted() {
		this.startTime = System.currentTimeMillis();
		this.setChanged();
		notifyObservers();
	}
	
	/**
	 * Set the end time for this TripRequest. This should be the time the destination floor is reached.
	 */
	public void setCompleted() {
		this.completedTime = System.currentTimeMillis();
		this.completed = true;
		this.setChanged();
		notifyObservers();
	}
	
	/**
	 * Return the time at which the TripRequest was created
	 * @return
	 */
	public String getCreationTime() {
		return this.getTime(this.creationTime);
	}
	
	/**
	 * Return the time at which the TripRequest was started (elevator reached the pickup floor)
	 * @return
	 */
	public String getStartTime() {
		return this.getTime(this.startTime);
	}
	
	/**
	 * Return the time at which the TripRequest was started (elevator reached the pickup floor)
	 * @return
	 */
	public long getStartTimeLong() {
		return this.startTime;
	}
	
	/**
	 * Return the time at which the TripRequest was completed (elevator reached the destination floor)
	 * @return
	 */
	public String getCompletionTime() {
		return this.getTime(this.completedTime);
	}
	
	/**
	 * Return the time at which the TripRequest was completed (elevator reached the destination floor)
	 * @return
	 */
	public long getCompletionTimeLong() {
		return this.completedTime;
	}
	
	/**
	 * Get the elevator response time in the format mm:ss.
	 * This represents the elapsed time between the moment the request is created and the moment the pickup floor is reached.
	 * @return - elapsed response time in format mm:ss
	 */
	public String getResponseTime() {
		return getElapsedTime(this.startTime, this.creationTime);
	}
	
	/**
	 * Get the total trip time in the format mm:ss.
	 * This represents the elapsed time between the moment the request is created and the moment the destination floor is reached.
	 * @return - elapsed trip time in format mm:ss
	 */
	public String getTripTime() {
		return getElapsedTime(this.completedTime, this.creationTime);
	}
	
	/**
	 * Check whether trip is completed or not
	 */
	public boolean isCompleted() {
		return this.completed;
	}
	
	/**
	 * Calculate the difference between endTime and startTime. 
	 * endTime and startTime are expected to be non-zero time values (in milliseconds). 
	 * The time is returned as a String in the format mm:ss.
	 * 
	 * @param endTime
	 * @param startTime
	 * @return
	 */
	private String getElapsedTime(long endTime, long startTime) {
		long elapsedTime = endTime - startTime;
		if (elapsedTime > 0) {
	        long hr = TimeUnit.MILLISECONDS.toHours(elapsedTime);
	        long min = TimeUnit.MILLISECONDS.toMinutes(elapsedTime - TimeUnit.HOURS.toMillis(hr));
	        long sec = TimeUnit.MILLISECONDS.toSeconds(elapsedTime - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
	        return String.format("%02d:%02d", min, sec);
		}
		return "--";
	}
	
	/**
	 * Return a time formatted in HH:mm:ss. 
	 * @param time - millisecond representation of time.
	 * @return
	 */
	private String getTime(long time) {
		if (time >0) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			return format.format(new Date(time));
		} 
		return "--";
	}
	
	
	/**
	 * Coordinate notation to depict a trip request ex -> (pickup, destination)
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append(this.pickupFloor);
		sb.append(",");
		sb.append(this.direction);
		sb.append(",");
		if (this.hasDestination) {
			sb.append(this.destinationFloor);
		} else {
			sb.append("?");
		}
		sb.append(")");

		return sb.toString();
	}
}
