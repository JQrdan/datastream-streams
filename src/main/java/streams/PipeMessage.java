package streams;

public class PipeMessage {
	
	private String message;
	
	public PipeMessage() {}
	
	public PipeMessage(String message){
		this.message = message;
	}
		
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return "PipeMessage{"
				+ "\n\tmessage: " + this.message
				+ "\n}";
	}
}