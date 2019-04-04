package main.elevatorSubsystem;

import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class ElevatorState extends Observable{
	private Integer startFloor;
	private Integer currentFloor;
	private Direction direction;
	private ElevatorStatus status;
	private ElevatorDoorStatus doorStatus;
	private Integer maxFloor;
	private HashMap<Integer, Boolean> lamps;
	private Integer timeBetweenFloors;
	private Integer passengerWaitTime;
	private Integer doorOperationTime;

	
	public ElevatorState(Integer defaultFloor, Integer currentFloor, Direction direction, ElevatorStatus status, ElevatorDoorStatus doorStatus,
						 Integer maxFloors, Integer timeBetweenFloors, Integer passengerWaitTime, Integer doorOperationTime) {
		this.startFloor = defaultFloor;
		this.currentFloor = currentFloor;
		this.direction = direction;
		this.status = status;
		this.doorStatus = doorStatus;
		this.maxFloor = maxFloors;
		this.timeBetweenFloors= timeBetweenFloors;
		this.passengerWaitTime= passengerWaitTime;
		this.doorOperationTime= doorOperationTime;
		this.lamps = new HashMap<Integer, Boolean>();

		for (int i = 1; i <= this.maxFloor; i++){
			this.lamps.put(i,false);
	}

}

	public Integer getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(Integer floor) {
		this.startFloor = floor;
		this.setChanged();
		notifyObservers();
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(Integer floor) {
		this.currentFloor = floor;
		this.setChanged();
		notifyObservers();
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		this.setChanged();
		notifyObservers();
	}

	public ElevatorStatus getCurrentStatus() {
		return status;
	}

	public void setStatus(ElevatorStatus status) {
		this.status = status;
		this.setChanged();
		notifyObservers();
	}

	public ElevatorDoorStatus getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(ElevatorDoorStatus doorStatus) {
		this.doorStatus = doorStatus;
		this.setChanged();
		notifyObservers(this.doorStatus);
	}

	public Integer getMaxFloor() {
		return maxFloor;
	}

	public void toggleLamp(Integer floor, Boolean b) {
		lamps.put(floor, b);
		this.setChanged();
		notifyObservers();
	}

	public Integer getPassengerWaitTime() {
		return passengerWaitTime;
	}

	public void setPassengerWaitTime(Integer time) {
		this.passengerWaitTime = time;
	}

	public Integer getDoorOperationTime() {
		return doorOperationTime;
	}

	public void setDoorOperationTime(Integer time) {
		this.doorOperationTime = time;
	}

	public Integer getTimeBetweenFloors() {
		return timeBetweenFloors;
	}

	public void setTimeBetweenFloors(Integer time) {
		this.timeBetweenFloors = time;
	}

	public HashMap<Integer, Boolean> getLamps() {
		// TODO Auto-generated method stub
		return this.lamps;
	}

}
