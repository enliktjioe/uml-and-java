package dwatch;

import java.util.Calendar;
import java.util.GregorianCalendar;

import dwatch.digitalwatch.IDigitalwatchStatemachine.SCILogicUnitOperationCallback;

public class DigitalWatchController implements SCILogicUnitOperationCallback {

	private int timeHour;
	private int timeMinute;
	private int timeSecond;
	private int chronoMinute;
	private int chronoSecond;
	private int chronoCentisec;
	private int day;
	private int month;
	private int year;
	private boolean alarm;

	public DigitalWatchController() {
		readSystemTime();
		resetChrono();
		alarm = false;
	}

	public void readSystemTime() {
		Calendar calendar = Calendar.getInstance();
		timeHour = calendar.get(Calendar.HOUR_OF_DAY);
		timeMinute = calendar.get(Calendar.MINUTE);
		timeSecond = calendar.get(Calendar.SECOND);

		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);
	}

	public String getTime() {
		return String.format("%02d:%02d:%02d", timeHour, timeMinute, timeSecond);
	}

	@Override
	public void increaseTimeByOne() {

		timeSecond++;
		if (timeSecond == 60) {
			timeSecond = 0;
			timeMinute++;
			if (timeMinute == 60) {
				timeMinute = 0;
				timeHour++;
				if (timeHour == 24)
					timeHour = 0;
			}
		}

		//System.out.println("TICK: " + getTime());
	}

	public String getChrono() {
		return String.format("%02d:%02d:%02d", chronoMinute, chronoSecond, chronoCentisec);
	}

	@Override
	public void increaseChronoByOne() {
		chronoCentisec++;
		if (chronoCentisec == 100) {
			chronoCentisec = 0;
			chronoSecond++;
			if (chronoSecond == 60) {
				chronoSecond = 0;
				chronoMinute++;
				if (chronoMinute == 60)
					chronoMinute = 0;
			}
		}
	}

	@Override
	public void resetChrono() {
		chronoMinute = chronoSecond = chronoCentisec = 0;
	}

	public boolean getAlarm() {
		return alarm;
	}

	public String getDate() {
		return String.format("%02d/%02d/%02d", month, day, year % 100);
	}

	public String getTimeLabelAsForShowing() {
		return String.format("%02d:%02d:%02d", timeHour, timeMinute, timeSecond);
	}

	public String getTimeLabelAsForHiding(int position) {
		String label;
		switch (position) {
		case 0:
			label = String.format("  :%02d:%02d", timeMinute, timeSecond);
			break;
		case 1:
			label = String.format("%02d:  :%02d", timeHour, timeSecond);
			break;
		case 2:
			label = String.format("%02d:%02d:  ", timeHour, timeMinute);
			break;
		default:
			label = String.format("%02d:%02d:%02d", timeHour, timeMinute, timeSecond);
		}

		return label;
	}

	public String getDateLabelAsForShowing() {
		return String.format("%02d/%02d/%02d", month, day, year % 100);
	}

	public String getDateLabelAsForHiding(int position) {
		String label;
		switch (position) {
		case 3:
			label = String.format("  /%02d/%02d", day, year % 100);
			break;
		case 4:
			label = String.format("%02d/  /%02d", month, year % 100);
			break;
		case 5:
			label = String.format("%02d/%02d/  ", month, day);
			break;
		default:
			label = String.format("%02d/%02d/%02d", month, day, year % 100);
		}

		return label;
	}

	@Override
	public void increasePos(long n) {
		Calendar current = new GregorianCalendar(year, month, day);
		int currentSelection = (int) n;
		switch (currentSelection) {
		case 0:
			timeHour = (timeHour + 1) % 24;
			break;
		case 1:
			timeMinute = (timeMinute + 1) % 60;
			break;
		case 2:
			timeSecond = (timeSecond + 1) % 60;
			break;
		case 3:
			month = month + 1 <= 12 ? month + 1 : 1;
			break;
		case 4:
			int maxdays = current.getActualMaximum(Calendar.DAY_OF_MONTH);
			day = day + 1 <= maxdays ? day + 1 : 1;
			break;
		case 5:
			year++;
		}

	}

}
