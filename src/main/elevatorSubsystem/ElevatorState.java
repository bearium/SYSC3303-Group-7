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
	
	public ElevatorState(Integer defaultFloor, Integer currentFloor, Direction direction, ElevatorStatus status, ElevatorDoorStatus doorStatus, Integer maxFloors) {
		this.startFloor = defaultFloor;
		this.currentFloor = currentFloor;
		this.direction = direction;
		this.status = status;
		this.doorStatus = doorStatus;
		this.maxFloor = maxFloors;
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
		this.notifyObservers();
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(Integer floor) {
		this.currentFloor = floor;
		this.setChanged();
		this.notifyObservers();
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		this.setChanged();
		this.notifyObservers();
	}

	public ElevatorStatus getCurrentStatus() {
		return status;
	}

	public void setStatus(ElevatorStatus status) {
		this.status = status;
		this.setChanged();
		this.notifyObservers();
	}

	public ElevatorDoorStatus getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(ElevatorDoorStatus doorStatus) {
		this.doorStatus = doorStatus;
		this.setChanged();
		this.notifyObservers(this.doorStatus);
	}

	public Integer getMaxFloor() {
		return maxFloor;
	}

	public void toggleLamp(Integer floor, Boolean b) {
		lamps.put(floor, b);
		System.out.println("YO I TURNED OFF SOMETHIHNG");
		this.setChanged();
		this.notifyObservers();
	}

	public HashMap<Integer, Boolean> getLamps() {
		// TODO Auto-generated method stub
		return lamps;
	}

}
