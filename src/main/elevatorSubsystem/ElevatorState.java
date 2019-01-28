package main.elevatorSubsystem;

import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.ElevatorStatus;

public class ElevatorState {
	private Integer startFloor;
	private Integer currentFloor;
	private Direction direction;
	private ElevatorStatus status;
	private ElevatorDoorStatus doorStatus;
	
	public ElevatorState(Integer defaultFloor, Integer currentFloor, Direction direction, ElevatorStatus status, ElevatorDoorStatus doorStatus) {
		this.startFloor = defaultFloor;
		this.currentFloor = currentFloor;
		this.direction = direction;
		this.status = status;
		this.doorStatus = doorStatus;
	}

	public Integer getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(Integer floor) {
		this.startFloor = floor;
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(Integer floor) {
		this.currentFloor = floor;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public ElevatorStatus getCurrentStatus() {
		return status;
	}

	public void setStatus(ElevatorStatus status) {
		this.status = status;
	}

	public ElevatorDoorStatus getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(ElevatorDoorStatus doorStatus) {
		this.doorStatus = doorStatus;
	}
}
