package shared;

public class TestHelper {
	
	
	
	
	@Before
	public void setup(){
		//instantiate every request subtype (including invalid request and null)
	}
	
	
	@Test
	public void testCreateRequest(){
		//attempt to create DatagramPackets from instances of Request using createRequest **consult mustafa about naming
		//verify output
	}
	
	@Test
	public void testParseRequest(){
		//create packets from request if testCreateRequest passes
		//attempt to parse DatagramPacket using parseRequest
		//verify output
	}
}
