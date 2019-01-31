package main.requests;

import java.net.DatagramPacket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import main.global.*;


public final class Helper {
	public static final int buffer_size = 1024;
	/**
	 * Parses a request class from a datagram packet
	 * @param packet
	 * @return
	 * @throws InvalidRequestException
	 */
	public static Request ParseRequest(DatagramPacket packet) throws InvalidRequestException{
		byte[] data = packet.getData();
		int data_length = packet.getLength();
		//if(data.length != data_length) throw Invalid();
		MutInt counter = new MutInt(0);
		if(data[counter.intValue()] != 0){
			throw Invalid();
		}
		counter.increment();

		SystemComponent Source = (SystemComponent) ParseEnum(data,SystemComponent.class, counter);
		String SourceName = ParseString(data, counter);
		SystemComponent Destination = (SystemComponent) ParseEnum(data,SystemComponent.class, counter);
		String DestinationName = ParseString(data, counter);
		byte[] RequestType = ParseType(data, counter);

		Request request = ParseOnType(data, RequestType, counter);
		request.setSource(Source);
		request.setSourceName(SourceName);
		request.setDestination(Destination);
		request.setDestinationName(DestinationName);
		return request;

	}

	private static Request ParseOnType(byte[] data, byte[] rt, MutInt counter) throws InvalidRequestException {
		Request request = null;
		if(Arrays.equals(rt, DirectionLampRequest.RequestType)){
			/* Parse based on Direction Lamp Request */
			Direction direction = (Direction) ParseEnum(data, Direction.class, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			request = new DirectionLampRequest(direction, status);


		} else if(Arrays.equals(rt, ElevatorArrivalRequest.RequestType)){
			/* Parse based on Elevator Arrival Request */
			String ElevatorName = ParseString(data, counter);
			String FloorName = ParseString(data, counter);
			request = new ElevatorArrivalRequest(ElevatorName, FloorName);
		} else if(Arrays.equals(rt, ElevatorDoorRequest.RequestType)){
			/* Parse based on Elevator Door Request */
			String ElevatorName = ParseString(data, counter);
			ElevatorDoorStatus Action = (ElevatorDoorStatus) ParseEnum(data, ElevatorDoorStatus.class, counter);
			request = new ElevatorDoorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, ElevatorLampRequest.RequestType)){
			/* Parse based on Elevator Lamp Request */
			String ElevatorName = ParseString(data, counter);
			String ButtonName = ParseString(data, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new ElevatorLampRequest(ElevatorName, ButtonName, status);
			}
			//			} else if(action != null){
			//				request = new ElevatorLampRequest(ElevatorName, ButtonName, status);
			//			}
		} else if(Arrays.equals(rt, ElevatorMotorRequest.RequestType)){
			/* Parse based on DElevator Motor Request */
			String ElevatorName = ParseString(data, counter);
			Direction Action = (Direction) ParseEnum(data, Direction.class, counter);
			request = new ElevatorMotorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, FloorButtonRequest.RequestType)){
			/* Parse based on Floor Button Request */
			String DateString = ParseString(data, counter);
			String FloorName = ParseString(data, counter);
			Direction Direction = (Direction) ParseEnum(data, Direction.class, counter);
			String DestinationFloor = ParseString(data, counter);
			SimpleDateFormat format = new SimpleDateFormat();
			Date date = new Date();
			try {
				date = format.parse(DateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request = new FloorButtonRequest(date,FloorName, Direction, DestinationFloor);

		} else if(Arrays.equals(rt, FloorLampRequest.RequestType)){
			/* Parse based on Floor Lamp Request */
			String FloorName = ParseString(data, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new FloorLampRequest(FloorName, status);
			} /*else if(action != null){
				request = new FloorLampRequest(FloorName, status);
			}*/
		} 
		return request;
	}

	private static byte[] ParseType(byte[] data, MutInt counter) throws InvalidRequestException {
		byte[] array = new byte[] {data[counter.getAndIncrement()], data[counter.getAndIncrement()]};
		if(data[counter.intValue()] == 0) 
			counter.getAndIncrement();
		else throw Invalid();
		return array;
	}

	private static String ParseString(byte[] data, MutInt counter) {
		System.out.println("Current counter: Parsing string: "+counter.intValue());
		String ret = "";
		if(data[counter.intValue()] != 0){

			//attempt to parse data
			MutInt temp_counter = new MutInt(counter) ;
			while(temp_counter.intValue() != data.length && data[temp_counter.getAndIncrement()]!=0);
			
			ret = new String(Arrays.copyOfRange(data, counter.intValue(),temp_counter.intValue() - 1));
			counter.setValue(temp_counter);
		}
		return ret;
	}

	private static <T extends Enum<T>> Enum<?> ParseEnum(byte[] data, Class<T> clazz, MutInt counter) throws InvalidRequestException{
		System.out.println("Current counter: "+counter.intValue());
		Enum<?>[] enums = clazz.getEnumConstants();
		if((((int)data[counter.intValue()]) - 1) < enums.length){
			return enums[((int) data[counter.getAndAdd(2)]) - 1];
		}
		else throw Invalid();
	}



	/**
	 * Creates a datagram packet from a request class
	 * @param request
	 * @return
	 * @throws InvalidRequestException 
	 */
	public static DatagramPacket CreateRequest(Request request) throws InvalidRequestException{
		MutInt counter = new MutInt(0);
		byte[] data = new byte[buffer_size];

		/* Populate a general request */
		// add initial 0 byte
		data [counter.getAndIncrement()] = 0;
		// add Sender enum byte
		System.out.println("Before populating: "+counter);
		PopulateEnum(data, request.Source, counter);
		System.out.println("After populating: "+counter);
		// populate sender name with a string
		String SenderName = request.SourceName;
		Populate(data, SenderName, counter);
		// Receiving end population
		PopulateEnum(data, request.Destination, counter);
		// Populate Receiver name
		String ReceiverName = request.DestinationName;
		Populate(data, ReceiverName, counter);
		// Populate type
		PopulateType(data, request, counter);
		// Populate based on Type
		PopulateOnType(data, request, counter);

		DatagramPacket packet = new DatagramPacket(data, counter.intValue());
		return packet;

	}

	private static void PopulateEnum(byte[] data, Enum<?> E, MutInt counter){
		System.out.println((byte)(E.ordinal() + 1));
		data[counter.intValue()] = (byte) (E.ordinal() + 1); //add 1 to avoid 0-ordinal values
		counter.increment();
		data[counter.intValue()] = 0;
		counter.increment();
	}
	private static void PopulateOnType(byte[] data, Request request, MutInt counter) throws InvalidRequestException {
		if(request instanceof DirectionLampRequest){
			/* Direction Lamp Request is of the form 0DIR0STATUS0ACTION */
			DirectionLampRequest req = (DirectionLampRequest) request;
			PopulateEnum(data, req.LampDirection, counter);
			PopulateEnum(data, req.CurrentStatus, counter);

		} else if(request instanceof ElevatorArrivalRequest){
			/* Elevator Arrival Request is of form 0E_NAME0FLOOR_NAME0 */
			ElevatorArrivalRequest req = (ElevatorArrivalRequest) request;
			Populate(data, req.ElevatorName, counter);
			Populate(data, req.FloorName, counter);

		} else if(request instanceof ElevatorDoorRequest){
			/* Elevator Door Request is of form 0E_NAME0ACTION0*/
			ElevatorDoorRequest req = (ElevatorDoorRequest) request;
			if(req.RequestAction == null || req.ElevatorName == null)
				throw Invalid();
			Populate(data, req.ElevatorName, counter);
			PopulateEnum(data, req.RequestAction, counter);
		} else if(request instanceof ElevatorLampRequest){
			/* Elevator Lamp Request is of the form 0E_NAME0E_BUTTON0STATUS0ACTION */
			ElevatorLampRequest req = (ElevatorLampRequest) request;
			Populate(data, req.ElevatorName, counter);
			Populate(data, req.ElevatorButton, counter);
			PopulateEnum(data, req.CurrentStatus, counter);
		} else if(request instanceof ElevatorMotorRequest){
			/* Elevator Motor Request is of the form 0E_NAME0ACTION0 */
			ElevatorMotorRequest req = (ElevatorMotorRequest) request;
			Populate(data, req.ElevatorName, counter);
			PopulateEnum(data, req.RequestAction, counter);
		} else if(request instanceof FloorButtonRequest){
			/* Floor Button Request is of the form 0DATE0FLOOR0DIRECTION0DESTINATION0*/
			FloorButtonRequest req = (FloorButtonRequest) request;
			Populate(data, req.Time.toString(), counter);
			Populate(data, req.FloorName, counter);
			PopulateEnum(data, req.Direction, counter);
			Populate(data, req.DestinationFloor, counter);
		} else if(request instanceof FloorLampRequest){
			FloorLampRequest req = (FloorLampRequest) request;
			Populate(data, req.FloorName, counter);
			PopulateEnum(data, req.CurrentStatus, counter);
		}
	}

	/**
	 * 
	 * @param data
	 * @param request
	 * @param counter
	 */
	public static void PopulateType(byte[] data, Request request, MutInt counter){
		byte[] TypeCode = request.RequestType;
		System.out.println(Arrays.toString(data));
		data[counter.intValue()] = TypeCode[0];
		counter.increment();
		data[counter.intValue()] = TypeCode[1];
		counter.increment();
		data[counter.intValue()] = 0;
		counter.increment();

	}

	public static void Populate(byte[] array, String array2, MutInt pos) {
		CopyArray(array, array2.getBytes(), pos);
		array[pos.intValue()] = 0;
		pos.increment();
	}

	public static void CopyArray(byte[] array, byte[] array2, MutInt pos){
		for(int i = 0; i < array2.length; i++){
			array[i + pos.intValue()] = array2[i];
		}
		pos.add(array2.length); 

	}
	public static InvalidRequestException Invalid() {
		return new InvalidRequestException();
	}
}
