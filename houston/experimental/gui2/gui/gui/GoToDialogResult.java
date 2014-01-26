package gui;

public class GoToDialogResult {
	enum Selection {
		GOTO_ROVER,
		GOTO_MODULE,
		GOTO_LATLONG
	}
	boolean success;
	Selection selectedOption;
	Position enteredPosition;
	public GoToDialogResult(boolean success, Selection selectedOption,
			Position enteredPosition) {
		this.success = success;
		this.selectedOption = selectedOption;
		this.enteredPosition = enteredPosition;
	}
}
