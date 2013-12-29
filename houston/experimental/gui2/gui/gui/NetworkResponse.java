package gui;

public class NetworkResponse {
	private NetworkStatus status;
	private String payload;
	public NetworkResponse(String response) {
		if(response.length() < 5) {
			status = NetworkStatus.COMMUNICATION_ERROR;
			payload = "";
		} else {
			if(response.substring(0, 2) != "RP") {
				status = NetworkStatus.COMMUNICATION_ERROR;
				payload = "";
			} else {
				if(response.length() < 7) {
					payload = "";
				} else {
					payload = response.substring(6);
				}
				
				switch(response.substring(3,5)) {
				case "OK":
					status = NetworkStatus.OK;
					break;
				case "CE":
					status = NetworkStatus.COMMUNICATION_ERROR;
					break;
				case "NC":
					status = NetworkStatus.UNKNOWN_COMMAND;
					break;
				case "NP":
					status = NetworkStatus.PARAMETER_ERROR;
					break;
				case "OE":
					status = NetworkStatus.ROVER_ERROR;
					break;
				default:
					status = NetworkStatus.UNKNOWN_ERROR;
					break;
				}
			}
		}
	}
	public NetworkResponse(NetworkStatus error) {
		status = error;
		payload = "";
	}
	public String getPayload() {
		return payload;
	}
	public NetworkStatus getStatus() {
		return status;
	}
}
