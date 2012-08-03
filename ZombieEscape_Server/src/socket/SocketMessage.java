package socket;

public class SocketMessage {
	
	public SocketMessage() {
		
	}
	
	public SocketMessage(String command, Object value) {
		this.command = command;
		this.value = value;
	}
	public String command;
	public Object value;

}
