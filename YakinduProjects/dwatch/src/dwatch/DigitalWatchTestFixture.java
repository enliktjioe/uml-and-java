package dwatch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import dwatch.digitalwatch.DigitalwatchStatemachine;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCIButtons;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCIDisplay;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCIDisplayOperationCallback;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCILogicUnit;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCILogicUnitOperationCallback;

abstract class DigitalWatchTestFixture implements SCIDisplayOperationCallback, SCILogicUnitOperationCallback {

	protected DigitalwatchStatemachine sm;
	protected SCIButtons buttons;
	protected SCIDisplay display;
	protected SCILogicUnit logic;

	protected VirtualTimer timer;


	protected Boolean indiglo;
	protected int time;
	protected int chrono;
	protected Integer timeDisplay;
	protected Integer chronoDisplay;
	protected Integer hiddenPos;

	protected final int TIME_POS_DELTAS[] = {60 * 60, 60, 1, 30 * 24 * 60 * 60, 24 * 60 * 60, 12 * 30 * 24 * 60 * 60};

	@BeforeEach
	void setUp() {
		sm = new DigitalwatchStatemachine();
		buttons = sm.getSCIButtons();
		display = sm.getSCIDisplay();
		logic = sm.getSCILogicUnit();

		display.setSCIDisplayOperationCallback(this);
		logic.setSCILogicUnitOperationCallback(this);

		timer = new VirtualTimer();
		sm.setTimer(timer);

		indiglo = null;
		time = 0;
		chrono = 0;
		timeDisplay = null;
		chronoDisplay = null;
		hiddenPos = null;

		sm.init();
		sm.enter();
	}

	@AfterEach
	void tearDown() {
		display.setSCIDisplayOperationCallback(null);
		logic.setSCILogicUnitOperationCallback(null);
		sm = null;
		timer = null;
	}

	@Override
	public void increaseTimeByOne() {
		time++;
	}

	@Override
	public void increaseChronoByOne() {
		chrono++;
	}

	@Override
	public void resetChrono() {
		chrono = 0;
	}

	@Override
	public void increasePos(long pos) {
		time += TIME_POS_DELTAS[(int) pos];
	}

	@Override
	public void refreshTimeDisplay() {
		timeDisplay = time;
		hiddenPos = null;
	}

	@Override
	public void refreshChronoDisplay() {
		chronoDisplay = chrono;
		hiddenPos = null;
	}

	@Override
	public void setIndiglo() {
		indiglo = true;
	}

	@Override
	public void unsetIndiglo() {
		indiglo = false;
	}

	@Override
	public void hidePos(long pos) {
		timeDisplay = time;
		hiddenPos = (int) pos;
	}

	protected void assertTimeDisplay(int expectedTime) {
		// assertEquals(expectedTime, time);
		assertTrue(expectedTime - 1 <= timeDisplay, "running behind more than 1s");
		assertTrue(timeDisplay <= expectedTime, "running ahead");
	}

	protected void assertTimeRunning(int startTime, int ticks) {
		for (int i = 0; i < ticks; i++) {
			assertTimeDisplay(startTime + i);
			timer.timeLeap(1000);
		}
	}

	protected void assertTimeFlashing(int expectedPos, int ticks) {
		for (int i = 0; i < ticks; i++) {
			Integer prevHiddenPos = hiddenPos;
			timer.timeLeap(250); // TODO: specify flashing interval

			assertTrue(hiddenPos == null || hiddenPos == expectedPos);
			assertNotEquals(prevHiddenPos, hiddenPos);
		}
	}

	protected void assertTimeNoFlashing(int ticks) {
		for (int i = 0; i < ticks; i++) {
			timer.timeLeap(250); // TODO: specify flashing interval
			assertNull(hiddenPos);
		}
	}

	protected void assertChronoDisplay(int expectedChrono) {
		// assertEquals(expectedChrono, chrono);
		assertTrue(expectedChrono - 1 <= chronoDisplay, "running behind more than 10ms");
		assertTrue(chronoDisplay <= expectedChrono, "running ahead");
	}

	protected void assertChronoRunning(int startChrono, int ticks) {
		for (int i = 0; i < ticks; i++) {
			assertChronoDisplay(startChrono + i);
			timer.timeLeap(10);
		}
	}

	protected void assertChronoConstant(int expectedChrono, int ticks) {
		for (int i = 0; i < ticks; i++) {
			assertEquals((Integer) expectedChrono, chronoDisplay);
			timer.timeLeap(10);
		}
	}
}
