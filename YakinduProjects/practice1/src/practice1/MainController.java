package practice1;

import practice1.lamp.LampStatemachine;

public class MainController {
	public static void main(String[] args) {
	    // Create your state machine
	    LampStatemachine sm = new LampStatemachine();
	    // Create the instance of your interface handler
	    InterfaceST ui = new InterfaceST();
	    // Add the event listener(s) to the button(s)
	    ui.addEventListener(sm.getSCIUI());
	    // Link callback methods to statechart
	    sm.getSCIUI().setSCIUIOperationCallback(ui);
	    // Set the timer service (initially commented out)
	    sm.setTimer(new TimerService());
	    // Initialize the internal objects of the statechart
	    sm.init();
	    // Enter the initial state
	    sm.enter();
	    // Create statemachine with a cycle period of 100 ms.
	    RuntimeService.getInstance().registerStatemachine(sm, 100);
	}
}
