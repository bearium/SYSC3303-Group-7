package main.requests;

import java.net.DatagramPacket;
import java.util.Arrays;

import main.global.Direction;
import main.global.ElevatorDoorStatus;
import main.global.Fault;
import main.global.LampStatus;

public class Parser {
	private byte[] data;
	private MutInt counter;
	
	public Parser(byte[] data, MutInt counter) {
		this.data = data;
		this.counter = counter;
	}
	
	public Parser () {
		Clear();
	}
	
	public Request ParseRequest(DatagramPacket packet) throws InvalidRequestException {
		data = packet.getData();
		counter = new MutInt(0);
		
		if(data[counter.getAndIncrement()] != 0){
			throw Invalid("Could not parse data. Invalid request.");
		}

		String[] SrcDests = ParseSrcDest();


		byte[] RequestType = ParseType();
		Request request = ParseOnType(RequestType);
		IncludeParams(SrcDests, request);
		Clear();
		return request;
	}
	
	private void Clear() {
		this.data = null;
		this.counter = null;
	}
	
	private void IncludeParams(String[] arr, Request request) {
		if(arr[0] != "")
			request.Sender = arr[0];
		if(arr[1] != "")
			request.Receiver = arr[1];
	}

	private String[] ParseSrcDest() throws InvalidRequestException {
		boolean IncludeSrcName = RTF(data[counter.getAndAdd(2)]),
				IncludeDestName = RTF(data[counter.getAndAdd(2)]);


		String SourceName = "";
		String DestinationName = "";


		if(IncludeSrcName){
			SourceName = ParseString();
		}

		if(IncludeDestName){
			DestinationName = ParseString();
		}

		String[] res = new String[] {SourceName, DestinationName};
		return res;
	}

	private Request ParseOnType(byte[] rt) throws InvalidRequestException {
		Request request = null;
		if(Arrays.equals(rt, DirectionLampRequest.getRequestType())){
			/* Parse based on Direction Lamp Request */
			Direction direction = (Direction) ParseEnum(Direction.class);
			LampStatus status = (LampStatus) ParseEnum(LampStatus.class);
			request = new DirectionLampRequest(direction, status);


		} else if(Arrays.equals(rt, ElevatorArrivalRequest.getRequestType())){
			/* Parse based on Elevator Arrival Request */
			String ElevatorName = ParseString();
			String FloorName = ParseString();
			Direction dir = (Direction) ParseEnum(Direction.class);
			request = new ElevatorArrivalRequest(ElevatorName, FloorName, dir);
		} else if(Arrays.equals(rt, ElevatorDoorRequest.getRequestType())){
			/* Parse based on Elevator Door Request */
			String ElevatorName = ParseString();
			ElevatorDoorStatus Action = (ElevatorDoorStatus) ParseEnum(ElevatorDoorStatus.class);
			request = new ElevatorDoorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, ElevatorLampRequest.getRequestType())){
			/* Parse based on Elevator Lamp Request */
			String ElevatorName = ParseString();
			LampStatus status = (LampStatus) ParseEnum(LampStatus.class);
			request = new ElevatorLampRequest(ElevatorName, status);

		} else if(Arrays.equals(rt, ElevatorMotorRequest.getRequestType())){
			/* Parse based on DElevator Motor Request */
			String ElevatorName = ParseString();
			Direction Action = (Direction) ParseEnum(Direction.class);
			request = new ElevatorMotorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, FloorButtonRequest.getRequestType())){
			/* Parse based on Floor Button Request */
			String DateString = ParseString();
			String FloorName = ParseString();
			Direction Direction = (Direction) ParseEnum(Direction.class);
			String Destination = ParseString();
			Fault fault = (Fault) ParseOptionalEnum(Fault.class);
			
			request = new FloorButtonRequest(DateString,FloorName, Direction, Destination, fault);

		} else if(Arrays.equals(rt, FloorLampRequest.getRequestType())){
			/* Parse based on Floor Lamp Request */
			Direction Direction = (Direction) ParseEnum(Direction.class);
			LampStatus status = (LampStatus) ParseEnum(LampStatus.class);
			request = new FloorLampRequest(Direction, status);
		} else if(Arrays.equals(rt, ElevatorDestinationRequest.getRequestType())){
			/* Parse based on Elevator Destination Request */
			String PickupFloor = ParseString();
			String DestFloor = ParseString();
			String ElevatorName = ParseString();
			Fault fault = (Fault) ParseOptionalEnum(Fault.class);
			request = new ElevatorDestinationRequest(PickupFloor,DestFloor,ElevatorName, fault);
		} else if(Arrays.equals(rt, ElevatorWaitRequest.getRequestType())){
			/* Parse based on Elevator Wait Request */
			String elevatorName = ParseString();
			request = new ElevatorWaitRequest(elevatorName);
		} 
		return request;
	}

	private <T extends Enum<T>> Enum<?> ParseOptionalEnum(Class<T> clazz) throws InvalidRequestException {
		boolean parseEnum = RTF(data[counter.getAndAdd(2)]); //checks if fault was included before parsing
		if (parseEnum) {
			return ParseEnum(clazz);
		}
		return null;
	}

	private byte[] ParseType() throws InvalidRequestException {
		byte[] array = new byte[] {data[counter.getAndIncrement()], data[counter.getAndIncrement()]};
		if(data[counter.intValue()] == 0) 
			counter.getAndIncrement();
		else throw Invalid("Could not parse type of request. Data was invalid.");
		return array;
	}

	private String ParseString() {
		String ret = "";
		if(data[counter.intValue()] != 0){
			//System.out.println("data: "+data[counter.intValue()]);
			if(data[counter.intValue()] == (byte) -1){
				counter.add(2);
				return ret;
			}
			//attempt to parse data
			MutInt temp_counter = new MutInt(counter) ;
			while(temp_counter.intValue() != data.length && data[temp_counter.getAndIncrement()]!=0);

			ret = new String(Arrays.copyOfRange(data, counter.intValue(),temp_counter.intValue() - 1));
			counter.setValue(temp_counter);
		}
		return ret;
	}

	private <T extends Enum<T>> Enum<?> ParseEnum(Class<T> clazz) throws InvalidRequestException{
		Enum<?>[] enums = clazz.getEnumConstants();
		if((((int)data[counter.intValue()]) - 1) < enums.length){
			return enums[((int) data[counter.getAndAdd(2)]) - 1];
		}
		else throw Invalid("Could not parse Enum; Invalid data or Enum does not exist.");
	}

	private boolean RTF(byte tf){
		if(tf == 'T') return true;
		else return false;
	}
	
	private InvalidRequestException Invalid(String message) {
		return new InvalidRequestException(message);
	}
}
