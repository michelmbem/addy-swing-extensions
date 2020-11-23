package org.addy.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Main {
	
	static final String[] pictures = new String[] { "alicia", "ashanti", "jlo", "jlo-back", "mariah", "toni" };
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {
		}
		
		final JFrame frame = new JFrame("Addy Swing Utilities Demo");
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
		panel.setLayout(new FlowLayout(SwingConstants.LEADING));
		frame.getContentPane().add(panel, BorderLayout.PAGE_START);

		JComboBox<SizeMode> sizeCombo = new JComboBox<>();
		sizeCombo.setModel(new DefaultComboBoxModel<>(SizeMode.values()));
		sizeCombo.setEditable(false);
		sizeCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				pictureBox.setSizeMode((SizeMode) e.getItem());
		});
		panel.add(new JLabel("Sizing mode: "));
		panel.add(sizeCombo);

		JComboBox<String> imageCombo = new JComboBox<>();
		imageCombo.setModel(new DefaultComboBoxModel<>(pictures));
		imageCombo.setEditable(false);
		imageCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				pictureBox.setImage(loadImage(e.getItem() + ".jpg"));
		});
		panel.add(new JLabel(" Image: "));
		panel.add(imageCombo);

		JButton calendarButton = new JButton("Calendar");
		calendarButton.addActionListener(e -> {
			JCalendar calendar = new JCalendar();
			JDialog dlg = new JDialog(frame, "JCalendar", true);
			dlg.setSize(350, 350);
			dlg.setLocationRelativeTo(frame);
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dlg.add(getCalendarComboPanel(e1 -> {
				calendar.setDate((Date) e1.getNewValue());
			}), BorderLayout.PAGE_START);
			dlg.add(calendar, BorderLayout.CENTER);
			dlg.setVisible(true);
		});
		panel.add(calendarButton);

		frame.setVisible(true);
	}
	
	private static Image loadImage(String path) {
		return new ImageIcon(Main.class.getClassLoader().getResource(path)).getImage();
	}

	private static JPanel getCalendarComboPanel(PropertyChangeListener listener) {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Calendar Combo:"));
		JCalendarCombo calendarCombo = new JCalendarCombo();
		calendarCombo.setCheckBoxVisible(true);
		calendarCombo.addPropertyChangeListener("date", listener);
		panel.add(calendarCombo);
		return panel;
	}
}