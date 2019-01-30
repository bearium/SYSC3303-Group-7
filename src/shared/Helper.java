package shared;

import java.net.DatagramPacket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import shared.ElevatorDoorRequest.DoorAction;
import shared.ElevatorMotorRequest.MotorAction;
import shared.LampRequest.LampAction;
import shared.LampRequest.LampStatus;
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
		if(data.length != data_length) throw Invalid();
		Integer counter = 0;
		if(data[counter++] != 0){
			throw Invalid();
		}

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

	private static Request ParseOnType(byte[] data, byte[] rt, Integer counter) {
		Request request = null;
		if(Arrays.equals(rt, DirectionLampRequest.RequestType)){
			/* Parse based on Direction Lamp Request */
			Direction direction = (Direction) ParseEnum(data, Direction.class, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new DirectionLampRequest(direction, status);
			} else if(action != null){
				request = new DirectionLampRequest(direction, action);
			}

		} else if(Arrays.equals(rt, ElevatorArrivalRequest.RequestType)){
			/* Parse based on Elevator Arrival Request */
			String ElevatorName = ParseString(data, counter);
			String FloorName = ParseString(data, counter);
			request = new ElevatorArrivalRequest(ElevatorName, FloorName);
		} else if(Arrays.equals(rt, ElevatorDoorRequest.RequestType)){
			/* Parse based on Elevator Door Request */
			String ElevatorName = ParseString(data, counter);
			DoorAction Action = (DoorAction) ParseEnum(data, DoorAction.class, counter);
			request = new ElevatorDoorRequest(ElevatorName, Action);
		} else if(Arrays.equals(rt, ElevatorLampRequest.RequestType)){
			/* Parse based on Elevator Lamp Request */
			String ElevatorName = ParseString(data, counter);
			String ButtonName = ParseString(data, counter);
			LampStatus status = (LampStatus) ParseEnum(data, LampStatus.class, counter);
			LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new ElevatorLampRequest(ElevatorName, ButtonName, action);
			} else if(action != null){
				request = new ElevatorLampRequest(ElevatorName, ButtonName, status);
			}
		} else if(Arrays.equals(rt, ElevatorMotorRequest.RequestType)){
			/* Parse based on DElevator Motor Request */
			String ElevatorName = ParseString(data, counter);
			MotorAction Action = (MotorAction) ParseEnum(data, MotorAction.class, counter);
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
			LampAction action = (LampAction) ParseEnum(data, LampAction.class, counter);
			if(status != null) {
				request = new FloorLampRequest(FloorName, action);
			} else if(action != null){
				request = new FloorLampRequest(FloorName, status);
			}
		} 
			return request;
	}

	private static byte[] ParseType(byte[] data, Integer counter) {
		byte[] array = new byte[] {data[counter++], data[counter++]};
		if(data[counter] == 0) 
			counter++;
		return array;
	}

	private static String ParseString(byte[] data, Integer counter) {

		String ret = "";
		if(data[++counter] != 0){

			//attempt to parse data
			int temp_counter = counter;

			while(temp_counter != data.length && data[temp_counter++]!=0);

			ret = new String(Arrays.copyOfRange(data, counter,temp_counter - 1));
			counter = temp_counter;
		}
		return ret;
	}

	private static <T extends Enum<T>> Enum<?> ParseEnum(byte[] data, Class<T> clazz, Integer counter){

		return clazz.getEnumConstants()[((int) data[counter++]) - 1];
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
		
		DatagramPacket packet = new DatagramPacket(data, counter);
		return packet;

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
			PopulateEnum(data, req.RequestAction, counter);
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
