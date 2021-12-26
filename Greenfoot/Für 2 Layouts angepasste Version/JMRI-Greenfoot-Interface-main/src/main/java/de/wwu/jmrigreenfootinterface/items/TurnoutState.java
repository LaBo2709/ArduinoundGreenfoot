package de.wwu.jmrigreenfootinterface.items;

public enum TurnoutState {

	THROWN(4), CLOSED(2), UNKNOWN(1);
	
	private int stateCode;
	
	private TurnoutState(int stateCode) {
		this.stateCode = stateCode;
	}
	
	public int getStateCode() {
		return stateCode;
	}
	
	public static TurnoutState fromCode(int code) {
		for(TurnoutState currentState : values()) {
			if(currentState.getStateCode() == code) {
				return currentState;
			}
		}
		return null;
	}
	
}
