package shared;

import main.global.Direction;

public class DirectionLampRequest extends LampRequest{
	Direction LampDirection;

	public DirectionLampRequest(Direction direction, LampAction action) {
		super(action);
		this.LampDirection = direction;
		// TODO Auto-generated constructor stub
	}
	
	public DirectionLampRequest(Direction direction, LampStatus status) {
		super(status);
		// TODO Auto-generated constructor stub
	}
}
