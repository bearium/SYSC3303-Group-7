package main.requests;

import java.net.DatagramPacket;
import java.util.Arrays;

public class Populater {
	private byte[] data;
	private MutInt counter;
	private Request request;
	
	public Populater(byte[] data, MutInt counter) {
		this.data = data;
		this.counter = counter;
	}
	
	public Populater() {
		Clear();
	}
	
	/**
	 * Creates a datagram packet from a request class
	 * @param request Request create a packet for
	 * @return Datagram packet containing the data, ready to send (does not contain host or port)
	 * @throws InvalidRequestException  In case the request contains null information, it will be invalid
	 */
	public DatagramPacket PopulateRequest(Request requestIn) throws InvalidRequestException {
		this.request = requestIn;
		/* Populate a general request */
		// add initial 0 byte
		data [counter.getAndIncrement()] = 0;
		// populate optional params
		PopulateSourceDest();
		// Populate type
		PopulateType();
		// Populate based on Type
		PopulateOnType(request);
		DatagramPacket packet = new DatagramPacket(Arrays.copyOf(data, data.length), counter.intValue());
		Clear();
		return packet;
	}
	
	private void Clear() {
		this.data = new byte[Helper.buffer_size];
		this.counter = new MutInt(0);
		this.request = null;
	}
	

	
	private void PopulateSourceDest() {

		boolean IncludeSrcName = request.Sender != null && !request.Sender.isEmpty() ,
				IncludeDestName = request.Receiver != null && !request.Receiver.isEmpty();

		Populate(TF(IncludeSrcName));
		Populate(TF(IncludeDestName));


		// populate sender name with a string
		if(IncludeSrcName){
			String SenderName = request.Sender;
			Populate(SenderName);
		}
		// Populate Receiver name
		if(IncludeDestName){
			String ReceiverName = request.Receiver;
			Populate(ReceiverName);
		}
	}

	private static String TF(boolean tf){
		if(tf) return "T";
		else return "F";
	}

	private void PopulateEnum(Enum<?> E){
		data[counter.getAndIncrement()] = (byte) (E.ordinal() + 1); //add 1 to avoid 0-ordinal values
		data[counter.getAndIncrement()] = 0;
	}

	private void PopulateOnType(Request request) throws InvalidRequestException {
		if(request instanceof DirectionLampRequest){
			/* Direction Lamp Request is of the form 0DIR0STATUS0ACTION */
			DirectionLampRequest req = (DirectionLampRequest) request;
			PopulateEnum(req.getLampDirection());
			PopulateEnum(req.getCurrentStatus());

		} else if(request instanceof ElevatorArrivalRequest){
			/* Elevator Arrival Request is of form 0E_NAME0FLOOR_NAME0 */
			ElevatorArrivalRequest req = (ElevatorArrivalRequest) request;
			Populate(req.getElevatorName());
			Populate(req.getFloorName());
			PopulateEnum(req.getDirection());

		} else if(request instanceof ElevatorDoorRequest){
			/* Elevator Door Request is of form 0E_NAME0ACTION0*/
			ElevatorDoorRequest req = (ElevatorDoorRequest) request;
			if(req.getRequestAction() == null)
				throw Invalid("The request's action is null. Could not populate.");
			Populate(req.getElevatorName());
			PopulateEnum(req.getRequestAction());
		} else if(request instanceof ElevatorLampRequest){
			/* Elevator Lamp Request is of the form 0E_NAME0E_BUTTON0STATUS0ACTION */
			ElevatorLampRequest req = (ElevatorLampRequest) request;

			Populate(req.getElevatorButton());
			PopulateEnum(req.getCurrentStatus());
		} else if(request instanceof ElevatorMotorRequest){
			/* Elevator Motor Request is of the form 0E_NAME0ACTION0 */
			ElevatorMotorRequest req = (ElevatorMotorRequest) request;
			Populate(req.getElevatorName());
			PopulateEnum(req.getRequestAction());
		} else if(request instanceof FloorButtonRequest){
			/* Floor Button Request is of the form 0DATE0FLOOR0DIRECTION0DESTINATION0*/
			FloorButtonRequest req = (FloorButtonRequest) request;
			Populate(req.getTime());
			Populate(req.getFloorName());
			PopulateEnum(req.getDirection());
			Populate(req.getDestinationFloor());
			PopulateOptionalEnum(req.getFault());
		} else if(request instanceof FloorLampRequest){
			/* Floor Button Request is of the form 0DIRECTION0ACTION0 */
			FloorLampRequest req = (FloorLampRequest) request;
			PopulateEnum(req.getDirection());
			PopulateEnum(req.getCurrentStatus());
		} else if(request instanceof ElevatorDestinationRequest){
			/* Floor Button Request is of the form 0FLOOR0 */
			ElevatorDestinationRequest req = (ElevatorDestinationRequest) request;
			Populate(req.getPickupFloor());
			Populate(req.getDestinationFloor());
			Populate(req.getElevatorName());
			PopulateOptionalEnum(req.getFault());
		} else if(request instanceof ElevatorWaitRequest){
			/* Floor Button Request is of the form 0DIRECTION0ACTION0 */
			ElevatorWaitRequest req = (ElevatorWaitRequest) request;
			Populate(req.getElevatorName());
		}
	}

	private void PopulateOptionalEnum(Enum<?> e) {
		if(e != null){ //optional parameter, checking for it before populating/parsing
			Populate(TF(true));
			PopulateEnum(e);
		}
		else {
			Populate(TF(false));
		}
	}

	/**
	 * 
	 * @param data
	 * @param request
	 * @param counter
	 */
	private void PopulateType(){
		byte[] TypeCode = request.IGetRequestType();
		data[counter.getAndIncrement()] = TypeCode[0];
		data[counter.getAndIncrement()] = TypeCode[1];
		data[counter.getAndIncrement()] = 0;

	}

	private void Populate(String stringToPopulate) {
		if(stringToPopulate == null) {
			data[counter.getAndIncrement()] = (byte) -1;
		}
		else {
			CopyArray(data, stringToPopulate.getBytes(), counter);
		}
		data[counter.getAndIncrement()] = 0;
	}

	private static void CopyArray(byte[] array, byte[] array2, MutInt pos){
		for(int i = 0; i < array2.length; i++){
			array[i + pos.intValue()] = array2[i];
		}
		pos.add(array2.length); 
	}

	private static InvalidRequestException Invalid(String message) {
		return new InvalidRequestException(message);
	}
}
