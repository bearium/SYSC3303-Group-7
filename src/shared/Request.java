package shared;
import main.global.SystemComponent;
public class Request {

	
	SystemComponent Source;
	SystemComponent Destination;
	
	String SourceName;
	String DestinationName;
	public byte[] RequestType;
	
	protected Request(){
		
	}
	protected Request(SystemComponent Source, SystemComponent Destination){
		
	}
	
	public SystemComponent getSource() {
		return Source;
	}
	public void setSource(SystemComponent source) {
		Source = source;
	}
	public SystemComponent getDestination() {
		return Destination;
	}
	public void setDestination(SystemComponent destination) {
		Destination = destination;
	}
	public String getSourceName() {
		return SourceName;
	}
	public void setSourceName(String sourceName) {
		SourceName = sourceName;
	}
	public String getDestinationName() {
		return DestinationName;
	}
	public void setDestinationName(String destinationName) {
		DestinationName = destinationName;
	}
	
}
