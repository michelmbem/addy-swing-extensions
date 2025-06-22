package org.addy.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.LocalDateTime;

public class Demo {
	public static final String BROWSE = "<browse...>";
	static final String[] pictures = new String[] { "alicia", "ashanti", "jlo", "jlo-back", "mariah", "toni", BROWSE};

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {
			// Do nothing
		}

		final JFrame frame = new JFrame("Addy Swing Demo");
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JPictureBox pictureBox = new JPictureBox();
		pictureBox.setBackground(Color.WHITE);
		pictureBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pictureBox.setImageSource(embeddedImage(pictures[0]));
		pictureBox.setSizeMode(SizeMode.NORMAL);
		frame.getContentPane().add(new JScrollPane(pictureBox), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		frame.getContentPane().add(panel, BorderLayout.PAGE_START);

		JComboBox<SizeMode> sizeCombo = new JComboBox<>();
		sizeCombo.setModel(new SimpleComboBoxModel<>(SizeMode.values()));
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
		imageCombo.setModel(new SimpleComboBoxModel<>(pictures));
		imageCombo.setEditable(false);
		imageCombo.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		imageCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				pictureBox.setImageSource(BROWSE.equals(e.getItem())
						? chooseImage(frame)
						: embeddedImage(e.getItem()));
			}
		});
		panel.add(new JLabel(" Image:"));
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

	private static URL embeddedImage(Object name) {
		return Demo.class.getClassLoader().getResource(name+ ".jpg");
	}

	private static Image chooseImage(JFrame frame) {
		Image chosenImage = null;
		ImageFileChooser imageChooser = new ImageFileChooser();

		if (imageChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            chosenImage = imageChooser.getSelectedImage();

		return chosenImage;
	}

	private static JPanel getCalendarComboPanel(PropertyChangeListener listener) {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Calendar Combo:"));
		JCalendarCombo calendarCombo = new JCalendarCombo();
		calendarCombo.setCheckBoxVisible(true);
		calendarCombo.addPropertyChangeListener("dateTime", listener);
		panel.add(calendarCombo);
		return panel;
	}
}
