package dwatch;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dwatch.digitalwatch.DigitalwatchStatemachine;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCIButtons;
import dwatch.digitalwatch.IDigitalwatchStatemachine.SCIDisplayOperationCallback;

public class DigitalWatchViewImpl implements SCIDisplayOperationCallback {
	private JPanel displayPanel;
	private JLabel timeLabel;
	private JLabel dateLabel;
	private JLabel alarmIcon;
	private JButton topLeftButton;
	private JButton topRightButton;
	private JButton bottomLeftButton;
	private JButton bottomRightButton;

	private SCIButtons listener;
	private DigitalWatchController model;
	private DigitalwatchStatemachine sm;

	public DigitalWatchViewImpl(DigitalWatchController model, SCIButtons listener, DigitalwatchStatemachine sm) {
		this.model = model;
		this.listener = listener;
		this.sm = sm;
		initializeGUI();
	}

	@Override
	public void refreshTimeDisplay() {
		timeLabel.setText(model.getTime());
		dateLabel.setText(model.getDate());
	}

	@Override
	public void refreshChronoDisplay() {
		timeLabel.setText(model.getChrono());
	}


	@Override
	public void hidePos(long pos) {
		timeLabel.setText(model.getTimeLabelAsForHiding((int)pos));
		dateLabel.setText(model.getDateLabelAsForHiding((int)pos));
	}

	@Override
	public void setIndiglo() {
		displayPanel.setBackground(Color.CYAN);
	}

	@Override
	public void unsetIndiglo() {
		displayPanel.setBackground(Color.WHITE);
	}

	private void initializeGUI() {
		JLayeredPane lpane = new JLayeredPane();
		final Image image = new ImageIcon(getClass().getResource("/watch.gif")).getImage();
		@SuppressWarnings("serial")
		JPanel imagePanel = new JPanel() {
			public void paintComponent(Graphics g) { g.drawImage(image, 0, 0, null); }
		};
		Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
		imagePanel.setPreferredSize(size);
		imagePanel.setMinimumSize(size);
		imagePanel.setMaximumSize(size);
		imagePanel.setSize(size);
        imagePanel.setLayout(null);

		displayPanel = new JPanel();
		displayPanel.setBounds(52,97,122,54);
		displayPanel.setBackground(Color.WHITE);
		displayPanel.setForeground(Color.BLACK);

		dateLabel = new JLabel();
		dateLabel.setBounds(94,100,60,20);
		dateLabel.setFont(new Font("Courier", Font.PLAIN, 12));

		timeLabel = new JLabel();
		timeLabel.setBounds(84,120,80,20);
		timeLabel.setFont(new Font("Courier", Font.PLAIN, 14));

		alarmIcon = new JLabel();
		alarmIcon.setBounds(154,97,20,20);
		alarmIcon.setVisible(true);

		topLeftButton = createButton(new Rectangle(0, 52, 30, 30),
				listener::raiseTopLeftPressed, listener::raiseTopLeftReleased);
		topRightButton = createButton(new Rectangle(size.width - 30, 52, 30, 30),
				listener::raiseTopRightPressed, listener::raiseTopRightReleased);
		bottomLeftButton = createButton(new Rectangle(0, 150, 30, 30),
				listener::raiseBotLeftPressed, listener::raiseBotLeftReleased);
		bottomRightButton = createButton(new Rectangle(size.width - 30, 150, 30, 30),
				listener::raiseBotRightPressed, listener::raiseBotRightReleased);

		lpane.add(imagePanel, 0, 0);
		lpane.add(displayPanel, 1, 0);
		lpane.add(dateLabel, 3, 0);
		lpane.add(timeLabel, 3, 0);
		lpane.add(alarmIcon, 3, 0);
		lpane.add(topLeftButton, 4, 0);
		lpane.add(topRightButton, 4, 0);
		lpane.add(bottomLeftButton, 4, 0);
		lpane.add(bottomRightButton, 4, 0);

		JFrame frame = new JFrame();
	    frame.getContentPane().add(lpane);
	    frame.setPreferredSize(imagePanel.getPreferredSize());
	    frame.setResizable(false);
	    frame.pack();
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}

	private JButton createButton(Rectangle bounds, Runnable onPressed, Runnable onReleased) {
		JButton button = new JButton();
		button.setBounds(bounds);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		// button.setBorder(BorderFactory.createLineBorder(Color.yellow, 1));
		button.setBorder(null);
		button.setFocusable(false); // hide single visible pixel when focused

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					onPressed.run();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					onReleased.run();
			}
		});

		return button;
	}
}
