package dwatch;

import dwatch.TimerService;
import dwatch.digitalwatch.DigitalwatchStatemachine;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DigitalwatchStatemachine sm = new DigitalwatchStatemachine();
		sm.setTimer(new TimerService());
		DigitalWatchController controller = new DigitalWatchController();
		DigitalWatchViewImpl view = new DigitalWatchViewImpl(controller, sm.getSCIButtons(), sm);
		sm.getSCIDisplay().setSCIDisplayOperationCallback(view);
		sm.getSCILogicUnit().setSCILogicUnitOperationCallback(controller);

		sm.init();
		sm.enter();
	}

}
