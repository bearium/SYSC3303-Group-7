package main.requests;

import java.net.DatagramPacket;
import java.util.Arrays;

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
		//int data_length = packet.getLength();
		//if(data.length != data_length) throw Invalid();
		MutInt counter = new MutInt(0);
		if(data[counter.getAndIncrement()] != 0){
			throw Invalid();
		}

		String[] SrcDests = ParseSrcDest(data, counter);


		byte[] RequestType = ParseType(data, counter);
		Request request = ParseOnType(data, RequestType, counter);

		IncludeParams(SrcDests, request);



		return request;

	}

	private static void IncludeParams(String[] arr, Request request) {
		if(arr[0] != "")
			request.Source = SystemComponent.valueOf(arr[0]);
		if(arr[1] != "")
			request.SourceName = arr[1];
		if(arr[2] != "")
			request.Destination = SystemComponent.valueOf(arr[2]);
		if(arr[3] != "")
			request.DestinationName = arr[3];
	}

	private static String[] ParseSrcDest(byte[] data, MutInt counter) throws InvalidRequestException {
		boolean IncludeSrc = RTF(data[counter.getAndAdd(2)]),
				IncludeSrcName = RTF(data[counter.getAndAdd(2)]),
				IncludeDest = RTF(data[counter.getAndAdd(2)]),
				IncludeDestName = RTF(data[counter.getAndAdd(2)]);

		String Source = "";
		String SourceName = "";
		String Destination = "";
		String DestinationName = "";

		if(IncludeSrc){
			Source = ParseEnum(data,SystemComponent.class, counter).toString();
		}
		if(IncludeSrcName){
			SourceName = ParseString(data, counter);
		}
		if(IncludeDest){
			Destination = ParseEnum(data,SystemComponent.class, counter).toString();
		}
		if(IncludeDestName){
			DestinationName = ParseString(data, counter);
		}
		
		String[] res = new String[] {Source, SourceName,Destination, DestinationName};
		return res;
	}

	private static Request ParseOnType(byte[] data, byte[] rt, MutInt counter) throws InvalidRequestException {
		Request request = null;
		if(Arrays.equals(rt, DirectionLampRequest.getRequestType())){
			/* Parse based on Direction Lamp Request */
			Direction direction = (Direction) ParseEnum(data, Direction.class, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			request = new DirectionLampRequest(direction, status);


		} else if(Arrays.equals(rt, ElevatorArrivalRequest.getRequestType())){
			/* Parse based on Elevator Arrival Request */
			String ElevatorName = ParseString(data, counter);
			String FloorName = ParseString(data, counter);
			request = new ElevatorArrivalRequest(ElevatorName, FloorName);
		} else if(Arrays.equals(rt, ElevatorDoorRequest.getRequestType())){
			/* Parse based on Elevator Door Request */
			String ElevatorName = ParseString(data, counter);
			ElevatorDoorStatus Action = (ElevatorDoorStatus) ParseEnum(data, ElevatorDoorStatus.class, counter);
			request = new ElevatorDoorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, ElevatorLampRequest.getRequestType())){
			/* Parse based on Elevator Lamp Request */
			String ElevatorName = ParseString(data, counter);
			String ButtonName = ParseString(data, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new ElevatorLampRequest(ElevatorName, status);
			}
			//			} else if(action != null){
			//				request = new ElevatorLampRequest(ElevatorName, ButtonName, status);
			//			}
		} else if(Arrays.equals(rt, ElevatorMotorRequest.getRequestType())){
			/* Parse based on DElevator Motor Request */
			String ElevatorName = ParseString(data, counter);
			Direction Action = (Direction) ParseEnum(data, Direction.class, counter);
			request = new ElevatorMotorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, FloorButtonRequest.getRequestType())){
			/* Parse based on Floor Button Request */
			String DateString = ParseString(data, counter);
			String FloorName = ParseString(data, counter);
			Direction Direction = (Direction) ParseEnum(data, Direction.class, counter);
			String DestinationFloor = ParseString(data, counter);
			request = new FloorButtonRequest(DateString,FloorName, Direction, DestinationFloor);

		} else if(Arrays.equals(rt, FloorLampRequest.getRequestType())){
			/* Parse based on Floor Lamp Request */
			Direction Direction = (Direction) ParseEnum(data, Direction.class, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			//LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new FloorLampRequest(Direction, status);
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
		// populate optional params
		PopulateSourceDest(data, request, counter);
		// Populate type
		PopulateType(data, request, counter);
		// Populate based on Type
		PopulateOnType(data, request, counter);

		DatagramPacket packet = new DatagramPacket(data, counter.intValue());
		return packet;

	}

	private static void PopulateSourceDest(byte[] data, Request request, MutInt counter) {

		boolean IncludeSrc = request.Source != null, 
				IncludeSrcName = request.SourceName != null && !request.SourceName.isEmpty() ,
				IncludeDest = request.Destination != null,
				IncludeDestName = request.DestinationName != null && !request.DestinationName.isEmpty();

		Populate(data, TF(IncludeSrc), counter);
		Populate(data, TF(IncludeSrcName), counter);
		Populate(data, TF(IncludeDest), counter);
		Populate(data, TF(IncludeDestName), counter);

		if(IncludeSrc)
			// add Sender enum byte
			PopulateEnum(data, request.Source, counter);
		// populate sender name with a string
		if(IncludeSrcName){
			String SenderName = request.SourceName;
			Populate(data, SenderName, counter);
		}
		// Receiving end population
		if(IncludeDest)
			PopulateEnum(data, request.Destination, counter);
		// Populate Receiver name
		if(IncludeDestName){
			String ReceiverName = request.DestinationName;
			Populate(data, ReceiverName, counter);
		}
	}

	private static String TF(boolean tf){
		if(tf) return "T";
		else return "F";
	}

	private static boolean RTF(byte tf){
		if(tf == 'T') return true;
		else return false;
	}

	private static void PopulateEnum(byte[] data, Enum<?> E, MutInt counter){
		data[counter.intValue()] = (byte) (E.ordinal() + 1); //add 1 to avoid 0-ordinal values
		counter.increment();
		data[counter.intValue()] = 0;
		counter.increment();
	}
	
	private static void PopulateOnType(byte[] data, Request request, MutInt counter) throws InvalidRequestException {
		if(request instanceof DirectionLampRequest){
			/* Direction Lamp Request is of the form 0DIR0STATUS0ACTION */
			DirectionLampRequest req = (DirectionLampRequest) request;
			PopulateEnum(data, req.getLampDirection(), counter);
			PopulateEnum(data, req.getCurrentStatus(), counter);

		} else if(request instanceof ElevatorArrivalRequest){
			/* Elevator Arrival Request is of form 0E_NAME0FLOOR_NAME0 */
			ElevatorArrivalRequest req = (ElevatorArrivalRequest) request;
			Populate(data, req.getElevatorName(), counter);
			Populate(data, req.getFloorName(), counter);

		} else if(request instanceof ElevatorDoorRequest){
			/* Elevator Door Request is of form 0E_NAME0ACTION0*/
			ElevatorDoorRequest req = (ElevatorDoorRequest) request;
			if(req.getRequestAction() == null || req.getElevatorName() == null)
				throw Invalid();
			Populate(data, req.getElevatorName(), counter);
			PopulateEnum(data, req.getRequestAction(), counter);
		} else if(request instanceof ElevatorLampRequest){
			/* Elevator Lamp Request is of the form 0E_NAME0E_BUTTON0STATUS0ACTION */
			ElevatorLampRequest req = (ElevatorLampRequest) request;
			Populate(data, req.getElevatorButton(), counter);
			PopulateEnum(data, req.getCurrentStatus(), counter);
		} else if(request instanceof ElevatorMotorRequest){
			/* Elevator Motor Request is of the form 0E_NAME0ACTION0 */
			ElevatorMotorRequest req = (ElevatorMotorRequest) request;
			Populate(data, req.getElevatorName(), counter);
			PopulateEnum(data, req.getRequestAction(), counter);
		} else if(request instanceof FloorButtonRequest){
			/* Floor Button Request is of the form 0DATE0FLOOR0DIRECTION0DESTINATION0*/
			FloorButtonRequest req = (FloorButtonRequest) request;
			Populate(data, req.getTime(), counter);
			Populate(data, req.getFloorName(), counter);
			PopulateEnum(data, req.getDirection(), counter);
			Populate(data, req.getDestinationFloor(), counter);
		} else if(request instanceof FloorLampRequest){
			FloorLampRequest req = (FloorLampRequest) request;
			PopulateEnum(data, req.getDirection(), counter);
			PopulateEnum(data, req.getCurrentStatus(), counter);
		}
	}

	/**
	 * 
	 * @param data
	 * @param request
	 * @param counter
	 */
	private static void PopulateType(byte[] data, Request request, MutInt counter){
		byte[] TypeCode = request.RequestType;
		data[counter.intValue()] = TypeCode[0];
		counter.increment();
		data[counter.intValue()] = TypeCode[1];
		counter.increment();
		data[counter.intValue()] = 0;
		counter.increment();

	}

	private static void Populate(byte[] array, String array2, MutInt pos) {
		CopyArray(array, array2.getBytes(), pos);
		array[pos.intValue()] = 0;
		pos.increment();
	}

	private static void CopyArray(byte[] array, byte[] array2, MutInt pos){
		for(int i = 0; i < array2.length; i++){
			array[i + pos.intValue()] = array2[i];
		}
		pos.add(array2.length); 

	}
	private static InvalidRequestException Invalid() {
		return new InvalidRequestException();
	}
}
