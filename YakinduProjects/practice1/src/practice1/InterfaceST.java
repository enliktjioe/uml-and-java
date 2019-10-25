package practice1; // <-- NB! Your name
 
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import practice1.lamp.ILampStatemachine.SCIUI;
import practice1.lamp.ILampStatemachine.SCIUIOperationCallback;  // <-- NB!
 
public class InterfaceST implements SCIUIOperationCallback {
    ImageIcon on = new ImageIcon("assets/bulb-on.png");
    ImageIcon off = new ImageIcon("assets/bulb-off.png");
    JLabel image = new JLabel(off);
    JButton switchButton = new JButton("Switch");
    JButton flashButton = new JButton("Flash");
 
    public InterfaceST() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(image, BorderLayout.CENTER);
        container.add(switchButton, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(switchButton);
        buttonPanel.add(flashButton);
        container.add(buttonPanel, BorderLayout.SOUTH);
 
        JFrame frame = new JFrame();
        frame.add(container);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

	@Override
	public void turnOn() {
		// TODO Auto-generated method stub
		image.setIcon(on);
		
	}

	@Override
	public void turnOff() {
		// TODO Auto-generated method stub
		image.setIcon(off);
	}

	public void addEventListener(SCIUI sciui) {
		// TODO Auto-generated method stub
		switchButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            sciui.raiseSwitch();
	        }
	    });
		
		flashButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sciui.raiseFlash();
			}
		});
	}
}