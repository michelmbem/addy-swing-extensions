package org.addy.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignore) {
		}
		
		final JFrame frame = new JFrame("JPictureBox Sample");
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JPictureBox pictureBox = new JPictureBox();
		pictureBox.setBackground(Color.WHITE);
		pictureBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pictureBox.setImage(loadImage("alicia.jpg"));
		pictureBox.setSizeMode(SizeMode.NORMAL);
		frame.getContentPane().add(new JScrollPane(pictureBox), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(3));
		frame.getContentPane().add(panel, BorderLayout.PAGE_START);

		JComboBox<String> sizeCombo = new JComboBox<>();
		sizeCombo.setModel(new DefaultComboBoxModel<>(new String[] {
				"NORMAL", "AUTO", "CENTER", "STRETCH", "FIT", "FILL" }));
		sizeCombo.setEditable(false);
		sizeCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1)
					pictureBox.setSizeMode(SizeMode.valueOf(e.getItem().toString()));
			}
		});
		panel.add(new JLabel("Size mode: "));
		panel.add(sizeCombo);

		JComboBox<String> imageCombo = new JComboBox<>();
		imageCombo.setModel(new DefaultComboBoxModel<>(new String[] {
				"alicia", "ashanti", "jlo", "jlo-back", "mariah", "toni" }));
		imageCombo.setEditable(false);
		imageCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1)
					pictureBox.setImage(loadImage(e.getItem() + ".jpg"));
			}
		});
		panel.add(new JLabel(" Image: "));
		panel.add(imageCombo);

		JButton calendarButton = new JButton("Calendar");
		calendarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dlg = new JDialog(frame, "Month calendar", true);
				dlg.setSize(400, 400);
				dlg.setLocationRelativeTo(frame);
				dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				dlg.add(getCalendarComboPanel(), BorderLayout.PAGE_START);
				dlg.add(new JCalendar(), BorderLayout.CENTER);
				dlg.setVisible(true);
			}
		});
		panel.add(calendarButton);

		frame.setVisible(true);
	}
	
	private static Image loadImage(String path) {
		return new ImageIcon(Main.class.getClassLoader().getResource(path)).getImage();
	}

	private static JPanel getCalendarComboPanel() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Calendar Combo:"));
		panel.add(new JCalendarCombo());
		return panel;
	}
}