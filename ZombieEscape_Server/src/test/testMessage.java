package test;

public class testMessage{
	String command;
	Object value;
	
	public testMessage(String command, Object value) {
		super();
		this.command = command;
		this.value = value;
	}

	@Override
	public String toString() {
		return "testMessage [command=" + command + ", value=" + value + "]";
	}
	
	
	
}
