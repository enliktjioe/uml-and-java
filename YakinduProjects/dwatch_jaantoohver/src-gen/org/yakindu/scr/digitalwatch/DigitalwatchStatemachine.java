package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	protected class SCIButtonsImpl implements SCIButtons {
	
		private boolean topLeftPressed;
		
		public void raiseTopLeftPressed() {
			topLeftPressed = true;
		}
		
		private boolean topLeftReleased;
		
		public void raiseTopLeftReleased() {
			topLeftReleased = true;
		}
		
		private boolean topRightPressed;
		
		public void raiseTopRightPressed() {
			topRightPressed = true;
		}
		
		private boolean topRightReleased;
		
		public void raiseTopRightReleased() {
			topRightReleased = true;
		}
		
		private boolean bottomLeftPressed;
		
		public void raiseBottomLeftPressed() {
			bottomLeftPressed = true;
		}
		
		private boolean bottomLeftReleased;
		
		public void raiseBottomLeftReleased() {
			bottomLeftReleased = true;
		}
		
		private boolean bottomRightPressed;
		
		public void raiseBottomRightPressed() {
			bottomRightPressed = true;
		}
		
		private boolean bottomRightReleased;
		
		public void raiseBottomRightReleased() {
			bottomRightReleased = true;
		}
		
		protected void clearEvents() {
			topLeftPressed = false;
			topLeftReleased = false;
			topRightPressed = false;
			topRightReleased = false;
			bottomLeftPressed = false;
			bottomLeftReleased = false;
			bottomRightPressed = false;
			bottomRightReleased = false;
		}
	}
	
	protected SCIButtonsImpl sCIButtons;
	
	protected class SCIDisplayImpl implements SCIDisplay {
	
		private SCIDisplayOperationCallback operationCallback;
		
		public void setSCIDisplayOperationCallback(
				SCIDisplayOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
	}
	
	protected SCIDisplayImpl sCIDisplay;
	
	protected class SCILogicUnitImpl implements SCILogicUnit {
	
		private SCILogicUnitOperationCallback operationCallback;
		
		public void setSCILogicUnitOperationCallback(
				SCILogicUnitOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}
		private boolean startAlarm;
		
		public void raiseStartAlarm() {
			startAlarm = true;
		}
		
		protected void clearEvents() {
			startAlarm = false;
		}
	}
	
	protected SCILogicUnitImpl sCILogicUnit;
	
	private boolean initialized = false;
	
	public enum State {
		main_region_runningWatch,
		main_region_runningWatch_watch_chronoMode,
		main_region_runningWatch_watch_chronoMode_chrono_runningChrono,
		main_region_runningWatch_watch_chronoMode_chrono_resetChrono,
		main_region_runningWatch_watch_chronoMode_chrono_idle,
		main_region_runningWatch_watch_chronoMode_chrono_pauseChrono,
		main_region_runningWatch_watch_timeMode,
		main_region_runningWatch_watch_alarmEditMode,
		main_region_runningWatch_watch_alarmEditMode_alarmEdit_main,
		main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection,
		main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection,
		main_region_runningWatch_watch_toggleAlarm,
		main_region_runningWatch_watch_timeEditMode,
		main_region_runningWatch_watch_timeEditMode_timeEdit_main,
		main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection,
		main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection,
		main_region_runningWatch_watch_bottomRightPressed,
		$NullState$
	};
	
	private final State[] stateVector = new State[1];
	
	private int nextStateIndex;
	
	private ITimer timer;
	
	private final boolean[] timeEvents = new boolean[20];
	
	
	private boolean chronoRunning;
	
	protected void setChronoRunning(boolean value) {
		chronoRunning = value;
	}
	
	protected boolean getChronoRunning() {
		return chronoRunning;
	}
	
	private boolean topRightPressed;
	
	protected void setTopRightPressed(boolean value) {
		topRightPressed = value;
	}
	
	protected boolean getTopRightPressed() {
		return topRightPressed;
	}
	
	private long lightsOffCounter;
	
	protected void setLightsOffCounter(long value) {
		lightsOffCounter = value;
	}
	
	protected long getLightsOffCounter() {
		return lightsOffCounter;
	}
	
	private boolean selectionVisible;
	
	protected void setSelectionVisible(boolean value) {
		selectionVisible = value;
	}
	
	protected boolean getSelectionVisible() {
		return selectionVisible;
	}
	
	private long editTimeout;
	
	protected void setEditTimeout(long value) {
		editTimeout = value;
	}
	
	protected long getEditTimeout() {
		return editTimeout;
	}
	
	private boolean alarmStarted;
	
	protected void setAlarmStarted(boolean value) {
		alarmStarted = value;
	}
	
	protected boolean getAlarmStarted() {
		return alarmStarted;
	}
	
	private boolean alarmHighlighted;
	
	protected void setAlarmHighlighted(boolean value) {
		alarmHighlighted = value;
	}
	
	protected boolean getAlarmHighlighted() {
		return alarmHighlighted;
	}
	
	private boolean editingTime;
	
	protected void setEditingTime(boolean value) {
		editingTime = value;
	}
	
	protected boolean getEditingTime() {
		return editingTime;
	}
	
	private boolean snooze;
	
	protected void setSnooze(boolean value) {
		snooze = value;
	}
	
	protected boolean getSnooze() {
		return snooze;
	}
	
	private long snoozeCounter;
	
	protected void setSnoozeCounter(long value) {
		snoozeCounter = value;
	}
	
	protected long getSnoozeCounter() {
		return snoozeCounter;
	}
	
	private long alarmCounter;
	
	protected void setAlarmCounter(long value) {
		alarmCounter = value;
	}
	
	protected long getAlarmCounter() {
		return alarmCounter;
	}
	
	public DigitalwatchStatemachine() {
		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
	}
	
	public void init() {
		this.initialized = true;
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		
		if (this.sCIDisplay.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCIDisplay must be set.");
		}
		
		if (this.sCILogicUnit.operationCallback == null) {
			throw new IllegalStateException("Operation callback for interface sCILogicUnit must be set.");
		}
		
		for (int i = 0; i < 1; i++) {
			stateVector[i] = State.$NullState$;
		}
		clearEvents();
		clearOutEvents();
		setChronoRunning(false);
		
		setTopRightPressed(false);
		
		setLightsOffCounter(2000);
		
		setSelectionVisible(false);
		
		setEditTimeout(5000);
		
		setAlarmStarted(false);
		
		setAlarmHighlighted(false);
		
		setEditingTime(false);
		
		setSnooze(false);
		
		setSnoozeCounter(0);
		
		setAlarmCounter(0);
	}
	
	public void enter() {
		if (!initialized) {
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		}
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
	
		enterSequence_main_region_default();
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
		sCIButtons.clearEvents();
		sCILogicUnit.clearEvents();
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
		case main_region_runningWatch:
			return stateVector[0].ordinal() >= State.
					main_region_runningWatch.ordinal()&& stateVector[0].ordinal() <= State.main_region_runningWatch_watch_bottomRightPressed.ordinal();
		case main_region_runningWatch_watch_chronoMode:
			return stateVector[0].ordinal() >= State.
					main_region_runningWatch_watch_chronoMode.ordinal()&& stateVector[0].ordinal() <= State.main_region_runningWatch_watch_chronoMode_chrono_pauseChrono.ordinal();
		case main_region_runningWatch_watch_chronoMode_chrono_runningChrono:
			return stateVector[0] == State.main_region_runningWatch_watch_chronoMode_chrono_runningChrono;
		case main_region_runningWatch_watch_chronoMode_chrono_resetChrono:
			return stateVector[0] == State.main_region_runningWatch_watch_chronoMode_chrono_resetChrono;
		case main_region_runningWatch_watch_chronoMode_chrono_idle:
			return stateVector[0] == State.main_region_runningWatch_watch_chronoMode_chrono_idle;
		case main_region_runningWatch_watch_chronoMode_chrono_pauseChrono:
			return stateVector[0] == State.main_region_runningWatch_watch_chronoMode_chrono_pauseChrono;
		case main_region_runningWatch_watch_timeMode:
			return stateVector[0] == State.main_region_runningWatch_watch_timeMode;
		case main_region_runningWatch_watch_alarmEditMode:
			return stateVector[0].ordinal() >= State.
					main_region_runningWatch_watch_alarmEditMode.ordinal()&& stateVector[0].ordinal() <= State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection.ordinal();
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_main:
			return stateVector[0] == State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_main;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection:
			return stateVector[0] == State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection:
			return stateVector[0] == State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection;
		case main_region_runningWatch_watch_toggleAlarm:
			return stateVector[0] == State.main_region_runningWatch_watch_toggleAlarm;
		case main_region_runningWatch_watch_timeEditMode:
			return stateVector[0].ordinal() >= State.
					main_region_runningWatch_watch_timeEditMode.ordinal()&& stateVector[0].ordinal() <= State.main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection.ordinal();
		case main_region_runningWatch_watch_timeEditMode_timeEdit_main:
			return stateVector[0] == State.main_region_runningWatch_watch_timeEditMode_timeEdit_main;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection:
			return stateVector[0] == State.main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection:
			return stateVector[0] == State.main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection;
		case main_region_runningWatch_watch_bottomRightPressed:
			return stateVector[0] == State.main_region_runningWatch_watch_bottomRightPressed;
		default:
			return false;
		}
	}
	
	/**
	* Set the {@link ITimer} for the state machine. It must be set
	* externally on a timed state machine before a run cycle can be correct
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
	
	public SCIButtons getSCIButtons() {
		return sCIButtons;
	}
	
	public SCIDisplay getSCIDisplay() {
		return sCIDisplay;
	}
	
	public SCILogicUnit getSCILogicUnit() {
		return sCILogicUnit;
	}
	
	private boolean check_main_region_runningWatch_lr0_lr0() {
		return (timeEvents[0]) && (!getEditingTime());
	}
	
	private boolean check_main_region_runningWatch_lr1_lr1() {
		return (timeEvents[1]) && (getChronoRunning());
	}
	
	private boolean check_main_region_runningWatch_lr3_lr3() {
		return sCIButtons.topRightPressed;
	}
	
	private boolean check_main_region_runningWatch_lr4_lr4() {
		return sCIButtons.topRightReleased;
	}
	
	private boolean check_main_region_runningWatch_lr5_lr5() {
		return sCILogicUnit.startAlarm;
	}
	
	private boolean check_main_region_runningWatch_lr6_lr6() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_lr7_lr7() {
		return (timeEvents[2]) && (getAlarmStarted() && getAlarmHighlighted() && getAlarmCounter()<8);
	}
	
	private boolean check_main_region_runningWatch_lr8_lr8() {
		return (timeEvents[3]) && (getAlarmStarted() && getAlarmHighlighted() && getAlarmCounter()<8);
	}
	
	private boolean check_main_region_runningWatch_lr9_lr9() {
		return (timeEvents[4]) && (!getTopRightPressed());
	}
	
	private boolean check_main_region_runningWatch_lr10_lr10() {
		return (true) && (getLightsOffCounter()==0);
	}
	
	private boolean check_main_region_runningWatch_lr11_lr11() {
		return (true) && (getAlarmCounter()==8);
	}
	
	private boolean check_main_region_runningWatch_lr12_lr12() {
		return (timeEvents[5]) && (getSnooze());
	}
	
	private boolean check_main_region_runningWatch_lr13_lr13() {
		return (true) && (getSnoozeCounter()==60);
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_lr0_lr0() {
		return timeEvents[6];
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr0_tr0() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr1_tr1() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_tr0_tr0() {
		return true;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr0_tr0() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr2_tr2() {
		return getChronoRunning();
	}
	
	private boolean check_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono_tr0_tr0() {
		return true;
	}
	
	private boolean check_main_region_runningWatch_watch_timeMode_lr1_lr1() {
		return timeEvents[7];
	}
	
	private boolean check_main_region_runningWatch_watch_timeMode_tr0_tr0() {
		return sCIButtons.topLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_timeMode_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_timeMode_tr2_tr2() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_lr1_lr1() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_lr2_lr2() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_lr3_lr3() {
		return timeEvents[8];
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr1_lr1() {
		return (timeEvents[9]) && (getSelectionVisible());
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr2_lr2() {
		return (timeEvents[10]) && (!getSelectionVisible());
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr0_tr0() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr2_tr2() {
		return (true) && (getEditTimeout()==0);
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr1_tr1() {
		return timeEvents[11];
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_lr1_lr1() {
		return timeEvents[12];
	}
	
	private boolean check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_toggleAlarm_tr0_tr0() {
		return timeEvents[13];
	}
	
	private boolean check_main_region_runningWatch_watch_toggleAlarm_tr1_tr1() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_lr1_lr1() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_lr2_lr2() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_lr3_lr3() {
		return timeEvents[14];
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr1_lr1() {
		return (timeEvents[15]) && (getSelectionVisible());
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr2_lr2() {
		return (timeEvents[16]) && (!getSelectionVisible());
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr0_tr0() {
		return sCIButtons.bottomRightPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr1_tr1() {
		return sCIButtons.bottomLeftPressed;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr2_tr2() {
		return (true) && (getEditTimeout()==0);
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr1_tr1() {
		return timeEvents[17];
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_lr1_lr1() {
		return timeEvents[18];
	}
	
	private boolean check_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_tr0_tr0() {
		return sCIButtons.bottomLeftReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_bottomRightPressed_tr0_tr0() {
		return sCIButtons.bottomRightReleased;
	}
	
	private boolean check_main_region_runningWatch_watch_bottomRightPressed_tr1_tr1() {
		return timeEvents[19];
	}
	
	private void effect_main_region_runningWatch_lr0_lr0() {
		sCILogicUnit.operationCallback.increaseTimeByOne();
		
		sCIDisplay.operationCallback.refreshDateDisplay();
	}
	
	private void effect_main_region_runningWatch_lr1_lr1() {
		sCILogicUnit.operationCallback.increaseChronoByOne();
	}
	
	private void effect_main_region_runningWatch_lr3_lr3() {
		sCIDisplay.operationCallback.setIndiglo();
		
		setTopRightPressed(true);
	}
	
	private void effect_main_region_runningWatch_lr4_lr4() {
		setTopRightPressed(false);
		
		setLightsOffCounter(2000);
	}
	
	private void effect_main_region_runningWatch_lr5_lr5() {
		setAlarmStarted(true);
		
		setAlarmHighlighted(true);
		
		sCIDisplay.operationCallback.setIndiglo();
	}
	
	private void effect_main_region_runningWatch_lr6_lr6() {
		sCIDisplay.operationCallback.unsetIndiglo();
		
		setAlarmStarted(false);
		
		setAlarmHighlighted(false);
		
		setAlarmCounter(0);
		
		setSnooze(false);
		
		setSnoozeCounter(0);
	}
	
	private void effect_main_region_runningWatch_lr7_lr7() {
		sCIDisplay.operationCallback.unsetIndiglo();
		
		setAlarmHighlighted(false);
		
		setAlarmCounter(getAlarmCounter() + 1);
	}
	
	private void effect_main_region_runningWatch_lr8_lr8() {
		sCIDisplay.operationCallback.setIndiglo();
		
		setAlarmHighlighted(true);
		
		setAlarmCounter(getAlarmCounter() + 1);
	}
	
	private void effect_main_region_runningWatch_lr9_lr9() {
		setLightsOffCounter(getLightsOffCounter() - 100);
	}
	
	private void effect_main_region_runningWatch_lr10_lr10() {
		sCIDisplay.operationCallback.unsetIndiglo();
	}
	
	private void effect_main_region_runningWatch_lr11_lr11() {
		sCIDisplay.operationCallback.unsetIndiglo();
		
		setAlarmStarted(false);
		
		setAlarmHighlighted(false);
		
		setAlarmCounter(0);
		
		setSnooze(true);
		
		setSnoozeCounter(0);
	}
	
	private void effect_main_region_runningWatch_lr12_lr12() {
		setSnoozeCounter(getSnoozeCounter() + 1);
	}
	
	private void effect_main_region_runningWatch_lr13_lr13() {
		setAlarmStarted(true);
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_lr0_lr0() {
		sCIDisplay.operationCallback.refreshChronoDisplay();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_tr0() {
		exitSequence_main_region_runningWatch_watch_chronoMode();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr0() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr1() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_tr0() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_idle_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr0() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr1() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr2() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_default();
	}
	
	private void effect_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono_tr0() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_idle_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeMode_lr1_lr1() {
		sCIDisplay.operationCallback.refreshTimeDisplay();
	}
	
	private void effect_main_region_runningWatch_watch_timeMode_tr0() {
		exitSequence_main_region_runningWatch_watch_timeMode();
		enterSequence_main_region_runningWatch_watch_chronoMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeMode_tr1() {
		exitSequence_main_region_runningWatch_watch_timeMode();
		enterSequence_main_region_runningWatch_watch_toggleAlarm_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeMode_tr2() {
		exitSequence_main_region_runningWatch_watch_timeMode();
		enterSequence_main_region_runningWatch_watch_bottomRightPressed_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_lr1_lr1() {
		setEditTimeout(5);
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_lr2_lr2() {
		setEditTimeout(5);
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_lr3_lr3() {
		setEditTimeout(getEditTimeout() - 1);
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr1_lr1() {
		sCIDisplay.operationCallback.hideSelection();
		
		setSelectionVisible(false);
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr2_lr2() {
		sCIDisplay.operationCallback.showSelection();
		
		setSelectionVisible(true);
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr0() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr1() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr2() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr0() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr1() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_lr1_lr1() {
		sCILogicUnit.operationCallback.increaseSelection();
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	private void effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_tr0() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_default();
	}
	
	private void effect_main_region_runningWatch_watch_toggleAlarm_tr0() {
		exitSequence_main_region_runningWatch_watch_toggleAlarm();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_toggleAlarm_tr1() {
		exitSequence_main_region_runningWatch_watch_toggleAlarm();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_lr1_lr1() {
		setEditTimeout(5);
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_lr2_lr2() {
		setEditTimeout(5);
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_lr3_lr3() {
		setEditTimeout(getEditTimeout() - 1);
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr1_lr1() {
		sCIDisplay.operationCallback.hideSelection();
		
		setSelectionVisible(false);
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr2_lr2() {
		sCIDisplay.operationCallback.showSelection();
		
		setSelectionVisible(true);
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr0() {
		exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr1() {
		exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr2() {
		exitSequence_main_region_runningWatch_watch_timeEditMode();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr0() {
		exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr1() {
		exitSequence_main_region_runningWatch_watch_timeEditMode();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_lr1_lr1() {
		sCILogicUnit.operationCallback.increaseSelection();
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	private void effect_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_tr0() {
		exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main_default();
	}
	
	private void effect_main_region_runningWatch_watch_bottomRightPressed_tr0() {
		exitSequence_main_region_runningWatch_watch_bottomRightPressed();
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	private void effect_main_region_runningWatch_watch_bottomRightPressed_tr1() {
		exitSequence_main_region_runningWatch_watch_bottomRightPressed();
		enterSequence_main_region_runningWatch_watch_timeEditMode_default();
	}
	
	/* Entry action for state 'runningWatch'. */
	private void entryAction_main_region_runningWatch() {
		timer.setTimer(this, 0, 1 * 1000, true);
		
		timer.setTimer(this, 1, 10, true);
		
		timer.setTimer(this, 2, 500, true);
		
		timer.setTimer(this, 3, 1000, true);
		
		timer.setTimer(this, 4, 100, true);
		
		timer.setTimer(this, 5, 1 * 1000, true);
		
		sCIDisplay.operationCallback.refreshDateDisplay();
	}
	
	/* Entry action for state 'chronoMode'. */
	private void entryAction_main_region_runningWatch_watch_chronoMode() {
		timer.setTimer(this, 6, 1, true);
	}
	
	/* Entry action for state 'runningChrono'. */
	private void entryAction_main_region_runningWatch_watch_chronoMode_chrono_runningChrono() {
		setChronoRunning(true);
	}
	
	/* Entry action for state 'resetChrono'. */
	private void entryAction_main_region_runningWatch_watch_chronoMode_chrono_resetChrono() {
		setChronoRunning(false);
		
		sCILogicUnit.operationCallback.resetChrono();
	}
	
	/* Entry action for state 'pauseChrono'. */
	private void entryAction_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono() {
		setChronoRunning(false);
	}
	
	/* Entry action for state 'timeMode'. */
	private void entryAction_main_region_runningWatch_watch_timeMode() {
		timer.setTimer(this, 7, 1 * 1000, true);
		
		sCIDisplay.operationCallback.refreshTimeDisplay();
	}
	
	/* Entry action for state 'alarmEditMode'. */
	private void entryAction_main_region_runningWatch_watch_alarmEditMode() {
		timer.setTimer(this, 8, 1 * 1000, true);
		
		sCILogicUnit.operationCallback.startAlarmEditMode();
		
		setEditTimeout(5);
	}
	
	/* Entry action for state 'main'. */
	private void entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main() {
		timer.setTimer(this, 9, 500, true);
		
		timer.setTimer(this, 10, 1 * 1000, true);
		
		setSelectionVisible(true);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'changeSelection'. */
	private void entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection() {
		timer.setTimer(this, 11, 2 * 1000, false);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'increaseSelection'. */
	private void entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection() {
		timer.setTimer(this, 12, 300, true);
		
		sCIDisplay.operationCallback.showSelection();
		
		setSelectionVisible(true);
		
		sCILogicUnit.operationCallback.increaseSelection();
	}
	
	/* Entry action for state 'toggleAlarm'. */
	private void entryAction_main_region_runningWatch_watch_toggleAlarm() {
		timer.setTimer(this, 13, 1500, false);
		
		sCILogicUnit.operationCallback.setAlarm();
		
		sCIDisplay.operationCallback.refreshAlarmDisplay();
	}
	
	/* Entry action for state 'timeEditMode'. */
	private void entryAction_main_region_runningWatch_watch_timeEditMode() {
		timer.setTimer(this, 14, 1 * 1000, true);
		
		sCILogicUnit.operationCallback.startTimeEditMode();
		
		setEditingTime(true);
		
		setEditTimeout(5);
	}
	
	/* Entry action for state 'main'. */
	private void entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_main() {
		timer.setTimer(this, 15, 500, true);
		
		timer.setTimer(this, 16, 1 * 1000, true);
		
		setSelectionVisible(true);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'changeSelection'. */
	private void entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection() {
		timer.setTimer(this, 17, 2 * 1000, false);
		
		sCIDisplay.operationCallback.showSelection();
	}
	
	/* Entry action for state 'increaseSelection'. */
	private void entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection() {
		timer.setTimer(this, 18, 300, true);
		
		sCIDisplay.operationCallback.showSelection();
		
		setSelectionVisible(true);
		
		sCILogicUnit.operationCallback.increaseSelection();
	}
	
	/* Entry action for state 'bottomRightPressed'. */
	private void entryAction_main_region_runningWatch_watch_bottomRightPressed() {
		timer.setTimer(this, 19, 1500, false);
	}
	
	/* Exit action for state 'runningWatch'. */
	private void exitAction_main_region_runningWatch() {
		timer.unsetTimer(this, 0);
		
		timer.unsetTimer(this, 1);
		
		timer.unsetTimer(this, 2);
		
		timer.unsetTimer(this, 3);
		
		timer.unsetTimer(this, 4);
		
		timer.unsetTimer(this, 5);
	}
	
	/* Exit action for state 'chronoMode'. */
	private void exitAction_main_region_runningWatch_watch_chronoMode() {
		timer.unsetTimer(this, 6);
	}
	
	/* Exit action for state 'timeMode'. */
	private void exitAction_main_region_runningWatch_watch_timeMode() {
		timer.unsetTimer(this, 7);
	}
	
	/* Exit action for state 'alarmEditMode'. */
	private void exitAction_main_region_runningWatch_watch_alarmEditMode() {
		timer.unsetTimer(this, 8);
	}
	
	/* Exit action for state 'main'. */
	private void exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main() {
		timer.unsetTimer(this, 9);
		
		timer.unsetTimer(this, 10);
	}
	
	/* Exit action for state 'changeSelection'. */
	private void exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection() {
		timer.unsetTimer(this, 11);
		
		sCILogicUnit.operationCallback.selectNext();
	}
	
	/* Exit action for state 'increaseSelection'. */
	private void exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection() {
		timer.unsetTimer(this, 12);
	}
	
	/* Exit action for state 'toggleAlarm'. */
	private void exitAction_main_region_runningWatch_watch_toggleAlarm() {
		timer.unsetTimer(this, 13);
	}
	
	/* Exit action for state 'timeEditMode'. */
	private void exitAction_main_region_runningWatch_watch_timeEditMode() {
		timer.unsetTimer(this, 14);
		
		setEditingTime(false);
	}
	
	/* Exit action for state 'main'. */
	private void exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_main() {
		timer.unsetTimer(this, 15);
		
		timer.unsetTimer(this, 16);
	}
	
	/* Exit action for state 'changeSelection'. */
	private void exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection() {
		timer.unsetTimer(this, 17);
		
		sCILogicUnit.operationCallback.selectNext();
	}
	
	/* Exit action for state 'increaseSelection'. */
	private void exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection() {
		timer.unsetTimer(this, 18);
	}
	
	/* Exit action for state 'bottomRightPressed'. */
	private void exitAction_main_region_runningWatch_watch_bottomRightPressed() {
		timer.unsetTimer(this, 19);
	}
	
	/* 'default' enter sequence for state runningWatch */
	private void enterSequence_main_region_runningWatch_default() {
		entryAction_main_region_runningWatch();
		enterSequence_main_region_runningWatch_watch_default();
	}
	
	/* 'default' enter sequence for state chronoMode */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_default() {
		entryAction_main_region_runningWatch_watch_chronoMode();
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_default();
	}
	
	/* 'default' enter sequence for state runningChrono */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_default() {
		entryAction_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_chronoMode_chrono_runningChrono;
	}
	
	/* 'default' enter sequence for state resetChrono */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_default() {
		entryAction_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_chronoMode_chrono_resetChrono;
	}
	
	/* 'default' enter sequence for state idle */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_chrono_idle_default() {
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_chronoMode_chrono_idle;
	}
	
	/* 'default' enter sequence for state pauseChrono */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono_default() {
		entryAction_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_chronoMode_chrono_pauseChrono;
	}
	
	/* 'default' enter sequence for state timeMode */
	private void enterSequence_main_region_runningWatch_watch_timeMode_default() {
		entryAction_main_region_runningWatch_watch_timeMode();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_timeMode;
	}
	
	/* 'default' enter sequence for state alarmEditMode */
	private void enterSequence_main_region_runningWatch_watch_alarmEditMode_default() {
		entryAction_main_region_runningWatch_watch_alarmEditMode();
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_default();
	}
	
	/* 'default' enter sequence for state main */
	private void enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_default() {
		entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_main;
	}
	
	/* 'default' enter sequence for state changeSelection */
	private void enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_default() {
		entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection;
	}
	
	/* 'default' enter sequence for state increaseSelection */
	private void enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_default() {
		entryAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection;
	}
	
	/* 'default' enter sequence for state toggleAlarm */
	private void enterSequence_main_region_runningWatch_watch_toggleAlarm_default() {
		entryAction_main_region_runningWatch_watch_toggleAlarm();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_toggleAlarm;
	}
	
	/* 'default' enter sequence for state timeEditMode */
	private void enterSequence_main_region_runningWatch_watch_timeEditMode_default() {
		entryAction_main_region_runningWatch_watch_timeEditMode();
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_default();
	}
	
	/* 'default' enter sequence for state main */
	private void enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main_default() {
		entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_timeEditMode_timeEdit_main;
	}
	
	/* 'default' enter sequence for state changeSelection */
	private void enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_default() {
		entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection;
	}
	
	/* 'default' enter sequence for state increaseSelection */
	private void enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_default() {
		entryAction_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection;
	}
	
	/* 'default' enter sequence for state bottomRightPressed */
	private void enterSequence_main_region_runningWatch_watch_bottomRightPressed_default() {
		entryAction_main_region_runningWatch_watch_bottomRightPressed();
		nextStateIndex = 0;
		stateVector[0] = State.main_region_runningWatch_watch_bottomRightPressed;
	}
	
	/* 'default' enter sequence for region main region */
	private void enterSequence_main_region_default() {
		react_main_region__entry_Default();
	}
	
	/* 'default' enter sequence for region watch */
	private void enterSequence_main_region_runningWatch_watch_default() {
		react_main_region_runningWatch_watch__entry_Default();
	}
	
	/* 'default' enter sequence for region chrono */
	private void enterSequence_main_region_runningWatch_watch_chronoMode_chrono_default() {
		react_main_region_runningWatch_watch_chronoMode_chrono__entry_Default();
	}
	
	/* 'default' enter sequence for region alarmEdit */
	private void enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_default() {
		react_main_region_runningWatch_watch_alarmEditMode_alarmEdit__entry_Default();
	}
	
	/* 'default' enter sequence for region timeEdit */
	private void enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_default() {
		react_main_region_runningWatch_watch_timeEditMode_timeEdit__entry_Default();
	}
	
	/* Default exit sequence for state chronoMode */
	private void exitSequence_main_region_runningWatch_watch_chronoMode() {
		exitSequence_main_region_runningWatch_watch_chronoMode_chrono();
		exitAction_main_region_runningWatch_watch_chronoMode();
	}
	
	/* Default exit sequence for state runningChrono */
	private void exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state resetChrono */
	private void exitSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state idle */
	private void exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state pauseChrono */
	private void exitSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
	}
	
	/* Default exit sequence for state timeMode */
	private void exitSequence_main_region_runningWatch_watch_timeMode() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_timeMode();
	}
	
	/* Default exit sequence for state alarmEditMode */
	private void exitSequence_main_region_runningWatch_watch_alarmEditMode() {
		exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit();
		exitAction_main_region_runningWatch_watch_alarmEditMode();
	}
	
	/* Default exit sequence for state main */
	private void exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
	}
	
	/* Default exit sequence for state changeSelection */
	private void exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
	}
	
	/* Default exit sequence for state increaseSelection */
	private void exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
	}
	
	/* Default exit sequence for state toggleAlarm */
	private void exitSequence_main_region_runningWatch_watch_toggleAlarm() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_toggleAlarm();
	}
	
	/* Default exit sequence for state timeEditMode */
	private void exitSequence_main_region_runningWatch_watch_timeEditMode() {
		exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit();
		exitAction_main_region_runningWatch_watch_timeEditMode();
	}
	
	/* Default exit sequence for state main */
	private void exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
	}
	
	/* Default exit sequence for state changeSelection */
	private void exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
	}
	
	/* Default exit sequence for state increaseSelection */
	private void exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
	}
	
	/* Default exit sequence for state bottomRightPressed */
	private void exitSequence_main_region_runningWatch_watch_bottomRightPressed() {
		nextStateIndex = 0;
		stateVector[0] = State.$NullState$;
		
		exitAction_main_region_runningWatch_watch_bottomRightPressed();
	}
	
	/* Default exit sequence for region main region */
	private void exitSequence_main_region() {
		switch (stateVector[0]) {
		case main_region_runningWatch_watch_chronoMode_chrono_runningChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_resetChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_idle:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
			exitAction_main_region_runningWatch_watch_chronoMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_pauseChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_timeMode:
			exitSequence_main_region_runningWatch_watch_timeMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_main:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_toggleAlarm:
			exitSequence_main_region_runningWatch_watch_toggleAlarm();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_main:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			exitAction_main_region_runningWatch();
			break;
		case main_region_runningWatch_watch_bottomRightPressed:
			exitSequence_main_region_runningWatch_watch_bottomRightPressed();
			exitAction_main_region_runningWatch();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region watch */
	private void exitSequence_main_region_runningWatch_watch() {
		switch (stateVector[0]) {
		case main_region_runningWatch_watch_chronoMode_chrono_runningChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_resetChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_idle:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
			exitAction_main_region_runningWatch_watch_chronoMode();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_pauseChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
			exitAction_main_region_runningWatch_watch_chronoMode();
			break;
		case main_region_runningWatch_watch_timeMode:
			exitSequence_main_region_runningWatch_watch_timeMode();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_main:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
			exitAction_main_region_runningWatch_watch_alarmEditMode();
			break;
		case main_region_runningWatch_watch_toggleAlarm:
			exitSequence_main_region_runningWatch_watch_toggleAlarm();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_main:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
			exitAction_main_region_runningWatch_watch_timeEditMode();
			break;
		case main_region_runningWatch_watch_bottomRightPressed:
			exitSequence_main_region_runningWatch_watch_bottomRightPressed();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region chrono */
	private void exitSequence_main_region_runningWatch_watch_chronoMode_chrono() {
		switch (stateVector[0]) {
		case main_region_runningWatch_watch_chronoMode_chrono_runningChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_resetChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_idle:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_idle();
			break;
		case main_region_runningWatch_watch_chronoMode_chrono_pauseChrono:
			exitSequence_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region alarmEdit */
	private void exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit() {
		switch (stateVector[0]) {
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_main:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
			break;
		case main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
			break;
		default:
			break;
		}
	}
	
	/* Default exit sequence for region timeEdit */
	private void exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit() {
		switch (stateVector[0]) {
		case main_region_runningWatch_watch_timeEditMode_timeEdit_main:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
			break;
		case main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection:
			exitSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
			break;
		default:
			break;
		}
	}
	
	/* The reactions of state runningChrono. */
	private void react_main_region_runningWatch_watch_chronoMode_chrono_runningChrono() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_chronoMode_tr0_tr0()) {
			effect_main_region_runningWatch_watch_chronoMode_tr0();
		} else {
			if (check_main_region_runningWatch_watch_chronoMode_lr0_lr0()) {
				effect_main_region_runningWatch_watch_chronoMode_lr0_lr0();
			}
			if (check_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr0_tr0()) {
				effect_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr0();
			} else {
				if (check_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr1_tr1()) {
					effect_main_region_runningWatch_watch_chronoMode_chrono_runningChrono_tr1();
				}
			}
		}
	}
	
	/* The reactions of state resetChrono. */
	private void react_main_region_runningWatch_watch_chronoMode_chrono_resetChrono() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_chronoMode_tr0_tr0()) {
			effect_main_region_runningWatch_watch_chronoMode_tr0();
		} else {
			if (check_main_region_runningWatch_watch_chronoMode_lr0_lr0()) {
				effect_main_region_runningWatch_watch_chronoMode_lr0_lr0();
			}
			effect_main_region_runningWatch_watch_chronoMode_chrono_resetChrono_tr0();
		}
	}
	
	/* The reactions of state idle. */
	private void react_main_region_runningWatch_watch_chronoMode_chrono_idle() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_chronoMode_tr0_tr0()) {
			effect_main_region_runningWatch_watch_chronoMode_tr0();
		} else {
			if (check_main_region_runningWatch_watch_chronoMode_lr0_lr0()) {
				effect_main_region_runningWatch_watch_chronoMode_lr0_lr0();
			}
			if (check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr0_tr0()) {
				effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr0();
			} else {
				if (check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr1_tr1()) {
					effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr1();
				} else {
					if (check_main_region_runningWatch_watch_chronoMode_chrono_idle_tr2_tr2()) {
						effect_main_region_runningWatch_watch_chronoMode_chrono_idle_tr2();
					}
				}
			}
		}
	}
	
	/* The reactions of state pauseChrono. */
	private void react_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_chronoMode_tr0_tr0()) {
			effect_main_region_runningWatch_watch_chronoMode_tr0();
		} else {
			if (check_main_region_runningWatch_watch_chronoMode_lr0_lr0()) {
				effect_main_region_runningWatch_watch_chronoMode_lr0_lr0();
			}
			effect_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono_tr0();
		}
	}
	
	/* The reactions of state timeMode. */
	private void react_main_region_runningWatch_watch_timeMode() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_timeMode_tr0_tr0()) {
			effect_main_region_runningWatch_watch_timeMode_tr0();
		} else {
			if (check_main_region_runningWatch_watch_timeMode_tr1_tr1()) {
				effect_main_region_runningWatch_watch_timeMode_tr1();
			} else {
				if (check_main_region_runningWatch_watch_timeMode_tr2_tr2()) {
					effect_main_region_runningWatch_watch_timeMode_tr2();
				} else {
					if (check_main_region_runningWatch_watch_timeMode_lr1_lr1()) {
						effect_main_region_runningWatch_watch_timeMode_lr1_lr1();
					}
				}
			}
		}
	}
	
	/* The reactions of state main. */
	private void react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr0_tr0()) {
			effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr0();
		} else {
			if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr1_tr1()) {
				effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr1();
			} else {
				if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr2_tr2()) {
					effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_tr2();
				} else {
					if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr1_lr1()) {
						effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr1_lr1();
					}
					if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr2_lr2()) {
						effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_lr2_lr2();
					}
				}
			}
		}
	}
	
	/* The reactions of state changeSelection. */
	private void react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr0_tr0()) {
			effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr0();
		} else {
			if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr1_tr1()) {
				effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection_tr1();
			}
		}
	}
	
	/* The reactions of state increaseSelection. */
	private void react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_alarmEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_tr0_tr0()) {
			effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_tr0();
		} else {
			if (check_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_lr1_lr1()) {
				effect_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection_lr1_lr1();
			}
		}
	}
	
	/* The reactions of state toggleAlarm. */
	private void react_main_region_runningWatch_watch_toggleAlarm() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_toggleAlarm_tr0_tr0()) {
			effect_main_region_runningWatch_watch_toggleAlarm_tr0();
		} else {
			if (check_main_region_runningWatch_watch_toggleAlarm_tr1_tr1()) {
				effect_main_region_runningWatch_watch_toggleAlarm_tr1();
			}
		}
	}
	
	/* The reactions of state main. */
	private void react_main_region_runningWatch_watch_timeEditMode_timeEdit_main() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr0_tr0()) {
			effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr0();
		} else {
			if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr1_tr1()) {
				effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr1();
			} else {
				if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr2_tr2()) {
					effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_tr2();
				} else {
					if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr1_lr1()) {
						effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr1_lr1();
					}
					if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr2_lr2()) {
						effect_main_region_runningWatch_watch_timeEditMode_timeEdit_main_lr2_lr2();
					}
				}
			}
		}
	}
	
	/* The reactions of state changeSelection. */
	private void react_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr0_tr0()) {
			effect_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr0();
		} else {
			if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr1_tr1()) {
				effect_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection_tr1();
			}
		}
	}
	
	/* The reactions of state increaseSelection. */
	private void react_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr1_lr1()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr1_lr1();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr2_lr2()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr2_lr2();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_lr3_lr3()) {
			effect_main_region_runningWatch_watch_timeEditMode_lr3_lr3();
		}
		if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_tr0_tr0()) {
			effect_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_tr0();
		} else {
			if (check_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_lr1_lr1()) {
				effect_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection_lr1_lr1();
			}
		}
	}
	
	/* The reactions of state bottomRightPressed. */
	private void react_main_region_runningWatch_watch_bottomRightPressed() {
		if (check_main_region_runningWatch_lr0_lr0()) {
			effect_main_region_runningWatch_lr0_lr0();
		}
		if (check_main_region_runningWatch_lr1_lr1()) {
			effect_main_region_runningWatch_lr1_lr1();
		}
		if (check_main_region_runningWatch_lr3_lr3()) {
			effect_main_region_runningWatch_lr3_lr3();
		}
		if (check_main_region_runningWatch_lr4_lr4()) {
			effect_main_region_runningWatch_lr4_lr4();
		}
		if (check_main_region_runningWatch_lr5_lr5()) {
			effect_main_region_runningWatch_lr5_lr5();
		}
		if (check_main_region_runningWatch_lr6_lr6()) {
			effect_main_region_runningWatch_lr6_lr6();
		}
		if (check_main_region_runningWatch_lr7_lr7()) {
			effect_main_region_runningWatch_lr7_lr7();
		}
		if (check_main_region_runningWatch_lr8_lr8()) {
			effect_main_region_runningWatch_lr8_lr8();
		}
		if (check_main_region_runningWatch_lr9_lr9()) {
			effect_main_region_runningWatch_lr9_lr9();
		}
		if (check_main_region_runningWatch_lr10_lr10()) {
			effect_main_region_runningWatch_lr10_lr10();
		}
		if (check_main_region_runningWatch_lr11_lr11()) {
			effect_main_region_runningWatch_lr11_lr11();
		}
		if (check_main_region_runningWatch_lr12_lr12()) {
			effect_main_region_runningWatch_lr12_lr12();
		}
		if (check_main_region_runningWatch_lr13_lr13()) {
			effect_main_region_runningWatch_lr13_lr13();
		}
		if (check_main_region_runningWatch_watch_bottomRightPressed_tr0_tr0()) {
			effect_main_region_runningWatch_watch_bottomRightPressed_tr0();
		} else {
			if (check_main_region_runningWatch_watch_bottomRightPressed_tr1_tr1()) {
				effect_main_region_runningWatch_watch_bottomRightPressed_tr1();
			}
		}
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_runningWatch_watch__entry_Default() {
		enterSequence_main_region_runningWatch_watch_timeMode_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_runningWatch_watch_chronoMode_chrono__entry_Default() {
		enterSequence_main_region_runningWatch_watch_chronoMode_chrono_idle_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_runningWatch_watch_alarmEditMode_alarmEdit__entry_Default() {
		enterSequence_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region_runningWatch_watch_timeEditMode_timeEdit__entry_Default() {
		enterSequence_main_region_runningWatch_watch_timeEditMode_timeEdit_main_default();
	}
	
	/* Default react sequence for initial entry  */
	private void react_main_region__entry_Default() {
		enterSequence_main_region_runningWatch_default();
	}
	
	public void runCycle() {
		if (!initialized)
			throw new IllegalStateException(
					"The state machine needs to be initialized first by calling the init() function.");
		clearOutEvents();
		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {
			switch (stateVector[nextStateIndex]) {
			case main_region_runningWatch_watch_chronoMode_chrono_runningChrono:
				react_main_region_runningWatch_watch_chronoMode_chrono_runningChrono();
				break;
			case main_region_runningWatch_watch_chronoMode_chrono_resetChrono:
				react_main_region_runningWatch_watch_chronoMode_chrono_resetChrono();
				break;
			case main_region_runningWatch_watch_chronoMode_chrono_idle:
				react_main_region_runningWatch_watch_chronoMode_chrono_idle();
				break;
			case main_region_runningWatch_watch_chronoMode_chrono_pauseChrono:
				react_main_region_runningWatch_watch_chronoMode_chrono_pauseChrono();
				break;
			case main_region_runningWatch_watch_timeMode:
				react_main_region_runningWatch_watch_timeMode();
				break;
			case main_region_runningWatch_watch_alarmEditMode_alarmEdit_main:
				react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_main();
				break;
			case main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection:
				react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_changeSelection();
				break;
			case main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection:
				react_main_region_runningWatch_watch_alarmEditMode_alarmEdit_increaseSelection();
				break;
			case main_region_runningWatch_watch_toggleAlarm:
				react_main_region_runningWatch_watch_toggleAlarm();
				break;
			case main_region_runningWatch_watch_timeEditMode_timeEdit_main:
				react_main_region_runningWatch_watch_timeEditMode_timeEdit_main();
				break;
			case main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection:
				react_main_region_runningWatch_watch_timeEditMode_timeEdit_changeSelection();
				break;
			case main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection:
				react_main_region_runningWatch_watch_timeEditMode_timeEdit_increaseSelection();
				break;
			case main_region_runningWatch_watch_bottomRightPressed:
				react_main_region_runningWatch_watch_bottomRightPressed();
				break;
			default:
				// $NullState$
			}
		}
		clearEvents();
	}
}
