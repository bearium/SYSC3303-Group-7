package shared;

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

		int counter = 0;
		if(data[counter++] != 0){
			throw Invalid();
		}


		return null;

	}

	/**
	 * Creates a datagram packet from a request class
	 * @param request
	 * @return
	 * @throws InvalidRequestException 
	 */
	public static DatagramPacket CreateRequest(Request request) throws InvalidRequestException{
		Integer counter = 0;
		byte[] data = new byte[buffer_size];

		/* Populate a general request */
		// add initial 0 byte
		data[counter++] = 0;
		// add Sender enum byte
		PopulateEnum(data, request.Source, counter);
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
		return null;

	}

	private static void PopulateEnum(byte[] data, Enum<?> E, Integer counter){
		data[counter++] = (byte) (E.ordinal() + 1); //add 1 to avoid 0-ordinal values
		data[counter++] = 0;
	}
	private static void PopulateOnType(byte[] data, Request request, Integer counter) throws InvalidRequestException {
		if(request instanceof DirectionLampRequest){
			/* Direction Lamp Request is of the form 0DIR0STATUS0ACTION */
			DirectionLampRequest req = (DirectionLampRequest) request;
			PopulateEnum(data, req.LampDirection, counter);
			PopulateEnum(data, req.CurrentStatus, counter);
			PopulateEnum(data, req.RequestAction, counter);

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
			PopulateEnum(data, req.RequestAction, counter);
		} else if(request instanceof ElevatorMotorRequest){
			/* Elevator Motor Request is of the form 0E_NAME0ACTION0 */
			ElevatorMotorRequest req = (ElevatorMotorRequest) request;
			Populate(data, req.ElevatorName, counter);
			PopulateEnum(data, req.RequestAction, counter);
		} else if(request instanceof FloorButtonRequest){
			
		} else if(request instanceof DirectionLampRequest){

		}
	}

	/**
	 * 
	 * @param data
	 * @param request
	 * @param counter
	 */
	public static void PopulateType(byte[] data, Request request, Integer counter){
		byte[] TypeCode = request.RequestType;
		data[counter++] = TypeCode[0];
		data[counter++] = TypeCode[1];
		data[counter++] = 0;

	}

	public static void Populate(byte[] array, String array2, Integer pos) {
		CopyArray(array, array2.getBytes(), pos);
		array[pos++] = 0;
	}

	public static void CopyArray(byte[] array, byte[] array2, Integer pos){
		for(int i = 0; i < array2.length; i++){
			array[i + pos] = array2[i];
		}
		pos += array2.length;

	}
	public static InvalidRequestException Invalid() {
		return new InvalidRequestException();
	}
}
