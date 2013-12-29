package gui;

public class NetworkCommand {
	private String command, payload;
	public NetworkCommand() {
		command = "";
		payload = "";
	}
	public NetworkCommand(String cmd) {
		command = cmd;
		payload = "";
	}
	public NetworkCommand(String cmd, String data) {
		command = cmd;
		payload = data;
	}
	public String toString() {
		String s;
		s = "HI " + command + " " + payload + "\n";
		return s;
	}
}
