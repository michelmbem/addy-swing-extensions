package org.addy.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.util.Objects;

public class Demo {
	static final String[] pictures = new String[] { "alicia", "ashanti", "jlo", "jlo-back", "mariah", "toni" };

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {
		}

		final JFrame frame = new JFrame("Addy Swing Demo");
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JPictureBox pictureBox = new JPictureBox();
		pictureBox.setBackground(Color.WHITE);
		pictureBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pictureBox.setImage(loadImage(pictures[0] + ".jpg"));
		pictureBox.setSizeMode(SizeMode.NORMAL);
		frame.getContentPane().add(new JScrollPane(pictureBox), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		frame.getContentPane().add(panel, BorderLayout.PAGE_START);

		JComboBox<SizeMode> sizeCombo = new JComboBox<>();
		sizeCombo.setModel(new DefaultComboBoxModel<>(SizeMode.values()));
		sizeCombo.setEditable(false);
		sizeCombo.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		sizeCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				pictureBox.setSizeMode((SizeMode) e.getItem());
		});
		panel.add(new JLabel("Sizing mode:"));
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(sizeCombo);

		panel.add(Box.createRigidArea(new Dimension(20, 0)));

		JComboBox<String> imageCombo = new JComboBox<>();
		imageCombo.setModel(new DefaultComboBoxModel<>(pictures));
		imageCombo.setEditable(false);
		imageCombo.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		imageCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				pictureBox.setImage(loadImage(e.getItem() + ".jpg"));
		});
		panel.add(new JLabel(" Image:"));
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(imageCombo);

		panel.add(Box.createHorizontalGlue());

		JButton calendarButton = new JButton("Calendar");
		calendarButton.addActionListener(e -> {
			JCalendar calendar = new JCalendar();
			JDialog dlg = new JDialog(frame, "JCalendar", true);
			dlg.setSize(350, 350);
			dlg.setLocationRelativeTo(frame);
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dlg.add(getCalendarComboPanel(e1 -> calendar.setSelectedDateTime((LocalDateTime) e1.getNewValue())), BorderLayout.PAGE_START);
			dlg.add(calendar, BorderLayout.CENTER);
			dlg.setVisible(true);
		});
		panel.add(calendarButton);

		frame.setVisible(true);
	}

	private static Image loadImage(String path) {
		return new ImageIcon(Objects.requireNonNull(Demo.class.getClassLoader().getResource(path))).getImage();
	}

	private static JPanel getCalendarComboPanel(PropertyChangeListener listener) {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Calendar Combo:"));
		JCalendarCombo calendarCombo = new JCalendarCombo();
		calendarCombo.addPropertyChangeListener("dateTime", listener);
		panel.add(calendarCombo);
		return panel;
	}
}