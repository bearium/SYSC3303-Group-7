package shared;
import main.requests.Helper;
public class TestHelper {
	
	private Helper helper = new Helper();
	
	
	@Before
	public void setup(){
		
		
		static DirectionLampRequest dirLR = new DirectionLampRequest(Direction.UP, Direction.ON);
		static ElevatorArrivalRequest elAR = new ElevatorArrivalRequest("elevator1", "floor1");
		static ElevatorDoorRequest elDR = new ElevatorDoorRequest("elevator1", ElevatorDoorStatus.OPENED);
		static ElevatorLampRequest elLR = new ElevatorLampRequest("button", LampStatus.ON);
		static ElevatorMotorRequest elMR = new ElevatorMotorRequest("elevator1", Direction.UP);
		static FloorButtonRequest flBR = new FloorButtonRequest("time", "floor1", Direction.UP, "floor2");
		static FloorLampRequest flLR = new FloorLampRequesst(direction.UP, LampStatus.ON);
		
	}
	
	
	@Test
	public void testCreateAndParseRequest(){
		//attempt to create DatagramPackets from instances of Request using createRequest 
		PacketdirLR = helper.CreateRequest(dirLR);
		PacketelAR = helper.CreateRequest(elAR);
		PacketelDR = helper.CreateRequest(elDR);
		PacketelLR = helper.CreateRequest(elLR);
		PacketelMR = helper.CreateRequest(elMR);
		PacketflBR = helper.CreateRequest(flBR);
		PacketflLR = helper.CreateRequest(flLR);
		
		//verify output of parse
		assertEquals("Datagram creation and parsing experienced data loss", dirLR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", elAR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", elDR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", elLR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", elMR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", flBR, helper.ParseRequest());
		assertEquals("Datagram creation and parsing experienced data loss", flLR, helper.ParseRequest());
		
		
	}
	
}
