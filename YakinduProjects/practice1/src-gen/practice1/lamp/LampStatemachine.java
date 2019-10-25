/** Generated by YAKINDU Statechart Tools code generator. */
package practice1.lamp;

import practice1.ITimer;

public class LampStatemachine implements ILampStatemachine {
	protected class SCIUIImpl implements SCIUI {
	
		private SCIUIOperationCallback operationCallback;
		
		public void setSCIUIOperationCallback(
				SCIUIOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
		private boolean switchEvent;
		
		
		public void raiseSwitch() {
			switchEvent = true;
		}
		
		private boolean flash;
		
		
		public void raiseFlash() {
			flash = true;
		}
		
		protected void clearEvents() {
			switchEvent = false;
			flash = false;
		}
	}
	
	
	protected SCIUIImpl sCIUI;
	
	private boolean initialized = false;
	
	public enum State {
		main_region_off,
		main_region_on,
		main_region_flash_on,
		main_region_flash_off,
		$NullState$
	};
	
	private final State[] stateVector = new State[1];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[2];
	
	public LampStatemachine() {
		sCIUI = new SCIUIImpl();
	}
	
	public void init() {
		this.initialized = true;
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		if (this.sCIUI.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCIUI must be set.");
		}
		
		for (int i = 0; i < 1; i++) {
			stateVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
	}
	
	public void enter() {
		if (!initialized) {
			throw new IllegalStateException(
				"The state machine needs to be initialized first by calling the init() function."
			);
		}
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		enterSequence_main_region_default();
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_region_off:
				main_region_off_react(true);
				break;
			case main_region_on:
				main_region_on_react(true);
				break;
			case main_region_flash_on:
				main_region_flash_on_react(true);
				break;
			case main_region_flash_off:
				main_region_flash_off_react(true);
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
	public void exit() {
		exitSequence_main_region();
	}
	
	/**
	 * @see IStatemachine#isActive()
	 */
	public boolean isActive() {
		return stateVector[0] != State.$NullState$;
	}
	
	/** 
	* Always returns 'false' since this state machine can never become final.
	*
	* @see IStatemachine#isFinal()
	*/
	public boolean isFinal() {
		return false;
	}
	/**
	* This method resets the incoming events (time events included).
	*/
	protected void clearEvents() {
		sCIUI.clearEvents();
		for (int i=0; i<timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}
	
	/**
	* This method resets the outgoing events.
	*/
	protected void clearOutEvents() {
	}
	
	/**
	* Returns true if the given state is currently active otherwise false.
	*/
	public boolean isStateActive(State state) {
	
		switch (state) {
		case main_region_off:
			return stateVector[0] == State.main_region_off;
		case main_region_on:
			return stateVector[0] == State.main_region_on;
		case main_region_flash_on:
			return stateVector[0] == State.main_region_flash_on;
		case main_region_flash_off:
			return stateVector[0] == State.main_region_flash_off;
		default:
			return false;
		}
	}
	
	/**
	* Set the {@link ITimer} for the state machine. It must be set
	* externally on a timed state machine before a run cycle can be correctly
	* executed.
	* 
	* @param timer
	*/
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}
	
	/**
	* Returns the currently used timer.
	* 
	* @return {@link ITimer}
	*/
	public ITimer getTimer() {
		return timer;
	}
	
	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}
	
	public SCIUI getSCIUI() {
		return sCIUI;
	}
	
	/* Entry action for state 'off'. */
	private void entryAction_main_region_off() {
		sCIUI.operationCallback.turnOn();
	}
	
	/* Entry action for state 'on'. */
	private void entryAction_main_region_on() {
		sCIUI.operationCallback.turnOff();
	}
	
	/* Entry action for state 'flash on'. */
	private void entryAction_main_region_flash_on() {
		timer.setTimer(this, 0, (1 * 1000), false);
		
		sCIUI.operationCallback.turnOn();
	}
	
	/* Entry action for state 'flash off'. */
	private void entryAction_main_region_flash_off() {
		timer.setTimer(this, 1, (1 * 1000), false);
		
		sCIUI.operationCallback.turnOff();
	}
	
	/* Exit action for state 'flash on'. */
	private void exitAction_main_region_flash_on() {
		timer.unsetTimer(this, 0);
	}
	
	/* Exit action for state 'flash off'. */
	private void exitAction_main_region_flash_off() {
		timer.unsetTimer(this, 1);
	}
	
	/* 'default' enter sequence for state off */
	private void enterSequence_main_region_off_default() {
		entryAction_main_region_off();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_off;
	}
	
	/* 'default' enter sequence for state on */
	private void enterSequence_main_region_on_default() {
		entryAction_main_region_on();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_on;
	}
	
	/* 'default' enter sequence for state flash on */
	private void enterSequence_main_region_flash_on_default() {
		entryAction_main_region_flash_on();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_flash_on;
	}
	
	/* 'default' enter sequence for state flash off */
	private void enterSequence_main_region_flash_off_default() {
		entryAction_main_region_flash_off();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_flash_off;
	}
	
	/* 'default' enter sequence for region main region */
	private void enterSequence_main_region_default() {
		react_main_region__entry_Default();
	}
	
	/* Default exit sequence for state off */
	private void exitSequence_main_region_off() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state on */
	private void exitSequence_main_region_on() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state flash on */
	private void exitSequence_main_region_flash_on() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_flash_on();
	}
	
	/* Default exit sequence for state flash off */
	private void exitSequence_main_region_flash_off() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_flash_off();
	}
	
	/* Default exit sequence for region main region */
	private void exitSequence_main_region() {
		switch (stateVector[0]) {
		case main_region_off:
			exitSequence_main_region_off();
			break;
		case main_region_on:
			exitSequence_main_region_on();
			break;
		case main_region_flash_on:
			exitSequence_main_region_flash_on();
			break;
		case main_region_flash_off:
			exitSequence_main_region_flash_off();
			break;
		default:
			break;
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region__entry_Default() {
		enterSequence_main_region_off_default();
	}
	
	private boolean react() {
		return false;
	}
	
	private boolean main_region_off_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react()==false) {
				if (sCIUI.switchEvent) {
					exitSequence_main_region_off();
					enterSequence_main_region_on_default();
				} else {
					if (sCIUI.flash) {
						exitSequence_main_region_off();
						enterSequence_main_region_flash_on_default();
					} else {
						did_transition = false;
					}
				}
			}
		}
		return did_transition;
	}
	
	private boolean main_region_on_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react()==false) {
				if (sCIUI.switchEvent) {
					exitSequence_main_region_on();
					enterSequence_main_region_off_default();
				} else {
					if (sCIUI.flash) {
						exitSequence_main_region_on();
						enterSequence_main_region_flash_off_default();
					} else {
						did_transition = false;
					}
				}
			}
		}
		return did_transition;
	}
	
	private boolean main_region_flash_on_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react()==false) {
				if (sCIUI.switchEvent) {
					exitSequence_main_region_flash_on();
					enterSequence_main_region_on_default();
				} else {
					if (timeEvents[0]) {
						exitSequence_main_region_flash_on();
						enterSequence_main_region_flash_off_default();
					} else {
						did_transition = false;
					}
				}
			}
		}
		return did_transition;
	}
	
	private boolean main_region_flash_off_react(boolean try_transition) {
		boolean did_transition = try_transition;
		
		if (try_transition) {
			if (react()==false) {
				if (timeEvents[1]) {
					exitSequence_main_region_flash_off();
					enterSequence_main_region_flash_on_default();
				} else {
					if (sCIUI.switchEvent) {
						exitSequence_main_region_flash_off();
						enterSequence_main_region_on_default();
					} else {
						did_transition = false;
					}
				}
			}
		}
		return did_transition;
	}
	
}
