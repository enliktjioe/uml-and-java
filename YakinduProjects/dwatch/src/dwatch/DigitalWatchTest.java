package dwatch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DigitalWatchTest extends DigitalWatchTestFixture {

	@Nested
	class Requirement1 {

		@Test
		void testTime() {
			int startTime = time;
			assertTimeRunning(startTime, 10);
		}
	}

	@Nested
	class Requirement2 {

		@Test
		void testIndiglo() {
			buttons.raiseTopRightPressed();
			assertTrue(indiglo);

			buttons.raiseTopRightReleased();
			assertTrue(indiglo);

			timer.timeLeap(1900);
			assertTrue(indiglo);

			timer.timeLeap(200);
			assertFalse(indiglo);
		}

		@Test
		void testIndigloHold() {
			buttons.raiseTopRightPressed();
			assertTrue(indiglo);

			timer.timeLeap(3000);
			assertTrue(indiglo);

			buttons.raiseTopRightReleased();
			assertTrue(indiglo);

			timer.timeLeap(1900);
			assertTrue(indiglo);

			timer.timeLeap(200);
			assertFalse(indiglo);
		}
	}

	@Nested
	class Requirement4 {

		@Test
		void testChronoInitial() {
			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertChronoConstant(0, 100);
		}

		@Test
		void testChronoRun() {
			testChronoInitial();

			buttons.raiseBotRightPressed();
			buttons.raiseBotRightReleased();

			int startChrono = chrono;
			assertChronoRunning(startChrono, 100);
		}

		@Test
		void testChronoPause() {
			int startChrono = chrono;
			testChronoRun();

			buttons.raiseBotRightPressed();
			buttons.raiseBotRightReleased();

			assertChronoConstant(startChrono + 100, 100); // extra 100 ticks in testChronoRun
		}

		@Test
		void testChronoReset() {
			testChronoPause();

			buttons.raiseBotLeftPressed();
			buttons.raiseBotLeftReleased();

			assertChronoConstant(0, 100);
		}

		@Test
		void testChronoResume() {
			int startChrono = chrono;
			testChronoPause();

			buttons.raiseBotRightPressed();
			buttons.raiseBotRightReleased();

			assertChronoRunning(startChrono + 100, 100); // extra 100 ticks in testChronoRun
		}

		@Test
		void testChronoRunningReset() {
			testChronoRun();

			buttons.raiseBotLeftPressed();
			buttons.raiseBotLeftReleased();

			assertChronoRunning(0, 100);
		}

		@Test
		void testChronoInitialTimeChrono() {
			int startTime = time;
			testChronoInitial();

			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertTimeRunning(startTime + 1, 10); // extra 1 time tick (100 chrono ticks) in testChronoInitial

			// copied from testChronoInitial
			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertChronoConstant(0, 100);
		}

		@Test
		void testChronoRunTimeChrono() {
			int startTime = time;
			int startChrono = chrono;
			testChronoRun();

			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertTimeRunning(startTime + 2, 10); // extra 2 time ticks (200 chrono ticks) in testChronoInitial, testChronoRun

			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertChronoRunning(startChrono + 1100, 100); // extra 100 chrono ticks in testChronoRun, extra 1000 chrono ticks (10 time ticks) above
		}

		@Test
		void testChronoPauseTimeChrono() {
			int startTime = time;
			int startChrono = chrono;
			testChronoPause();

			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertTimeRunning(startTime + 3, 10); // extra 3 time ticks (300 chrono ticks) in testChronoInitial, testChronoRun, testChronoPause

			buttons.raiseTopLeftPressed();
			buttons.raiseTopLeftReleased();

			assertChronoConstant(startChrono + 100, 100); // extra 100 ticks in testChronoRun
		}
	}

	@Nested
	class Requirement5 {

		@Test
		void testTimeEditNoEnter() {
			int startTime = time;
			buttons.raiseBotRightPressed();
			timer.timeLeap(900);
			buttons.raiseBotRightReleased();

			assertTimeRunning(startTime + 1, 10); // extra 1 time ticks (100 chrono ticks) above
			assertTimeNoFlashing(10);
		}

		@Test
		void testTimeEditRunning() {
			int startTime = time;
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			assertTimeRunning(startTime + 1, 3);
		}

		@Test
		void testTimeEditIncreaseSingle() {
			testTimeEditRunning();

			int startTime = time;
			buttons.raiseBotLeftPressed();
			buttons.raiseBotLeftReleased();

			assertEquals(startTime + 60 * 60, time);
		}

		@Test
		void testTimeEditIncreaseHold() {
			testTimeEditRunning();

			int startTime = time;
			buttons.raiseBotLeftPressed();

			for (int i = 0; i < 4; i++) { // only 4 because 5 would increase second normally on last iteration
				int expectedTime = startTime + (i + 1) * 60 * 60;
				assertEquals(expectedTime, time);
				timer.timeLeap(250);
			}
		}

		@Test
		void testTimeEditIncreaseAll() {
			testTimeEditRunning();

			int startTime = time;
			int expectedTime = startTime;

			for (int i = 0; i < 6; i++) {
				buttons.raiseBotLeftPressed();
				buttons.raiseBotLeftReleased();

				expectedTime += TIME_POS_DELTAS[i];
				assertEquals(expectedTime, time);

				buttons.raiseBotRightPressed();
				buttons.raiseBotRightReleased();
			}
		}

		@Test
		void testTimeEditIncreaseWraparound() {
			testTimeEditRunning();

			int startTime = time;
			for (int i = 0; i < 6; i++) {
				buttons.raiseBotRightPressed();
				buttons.raiseBotRightReleased();
			}

			buttons.raiseBotLeftPressed();
			buttons.raiseBotLeftReleased();

			assertEquals(startTime + 60 * 60, time);
		}

		@Test
		void testTimeEditFlashingSingle() {
			// copied from testTimeEditFrozen
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			assertTimeFlashing(0, 10);
		}

		@Test
		void testTimeEditFlashingAll() {
			// copied from testTimeEditFrozen
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			for (int expectedPos = 0; expectedPos < 6; expectedPos++) {
				assertTimeFlashing(expectedPos, 10);

				buttons.raiseBotRightPressed();
				buttons.raiseBotRightReleased();
			}
		}

		@Test
		void testTimeEditFlashingWraparound() {
			// copied from testTimeEditFrozen
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			for (int i = 0; i < 6; i++) {
				buttons.raiseBotRightPressed();
				buttons.raiseBotRightReleased();
			}

			assertTimeFlashing(0, 10);
		}

		@Test
		void testTimeEditExitTimeout() {
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			int startTime = time;
			timer.timeLeap(5100);

			assertTimeRunning(startTime + 5, 10);
			assertTimeNoFlashing(10);
		}

		@Test
		void testTimeEditNoExitButton() {
			buttons.raiseBotRightPressed();
			timer.timeLeap(1100);
			buttons.raiseBotRightReleased();

			buttons.raiseBotRightPressed();
			timer.timeLeap(2100);
			buttons.raiseBotRightReleased();

			assertTimeFlashing(1, 10);
		}
	}
}
