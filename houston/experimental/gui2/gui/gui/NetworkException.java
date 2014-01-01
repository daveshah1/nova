package gui;

public class NetworkException extends Exception {

	private static final long serialVersionUID = 5622726798275639631L;
	private NetworkStatus errorCode;
	private String description;
	public NetworkException() {
		errorCode = NetworkStatus.UNKNOWN_ERROR;
		description = "";
	}
	public NetworkException(NetworkStatus status) {
		errorCode = status;
		description = "";
	}
	public NetworkException(NetworkStatus status, String desc) {
		errorCode = status;
		description = desc;
	}
	public NetworkStatus getErrorCode() {
		return errorCode;
	}
	public String getDescription() {
		return description;
	}
}
