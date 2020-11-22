package org.addy.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JCalendarCombo extends JPanel implements ChangeListener, PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String POPUP_MENU_CANCEL_PROPERTY = "JPopupMenu.firePopupMenuCanceled";
	private static final String DEFAULT_DATE_FORMAT = "d MMMM yyyy";
	
	private Date date;
	private String dateFormat;
	private boolean checkBoxVisible;
	private boolean checked;
	
	private JCheckBox checkBox;
	private JSpinner spinner;
	private JButton button;
	private JPopupMenu popupMenu;
	private JCalendar calendar;
	
	private boolean allowPopupHide;

	public JCalendarCombo() {
		this(new Date(), DEFAULT_DATE_FORMAT, false);
	}

	public JCalendarCombo(Date date) {
		this(date, DEFAULT_DATE_FORMAT, false);
	}

	public JCalendarCombo(Date date, String dateFormat) {
		this(date, dateFormat, false);
	}

	public JCalendarCombo(Date date, String dateFormat, boolean checkBoxVisible) {
		super(new BorderLayout());
		initGUI();
		setDate(date);
		setDateFormat(dateFormat);
		setCheckBoxVisible(checkBoxVisible);
		setChecked(true);
		setOpaque(false);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date can't be null");
		}

		if (!date.equals(this.date)) {
			Date oldDate = this.date;
			this.date = date;
			firePropertyChange("date", oldDate, date);
			getSpinner().setValue(date);
			setChecked(true);
		}
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		if ((dateFormat == null && this.dateFormat != null) ||
				(dateFormat != null && !dateFormat.equals(this.dateFormat))) {
			
			String oldDateFormat = this.dateFormat;
			this.dateFormat = dateFormat;
			firePropertyChange("dateFormat", oldDateFormat, dateFormat);
			updateDateEditor();
		}
	}

	public boolean isCheckBoxVisible() {
		return checkBoxVisible;
	}

	public void setCheckBoxVisible(boolean checkBoxVisible) {
		if (checkBoxVisible != this.checkBoxVisible) {
			this.checkBoxVisible = checkBoxVisible;
			firePropertyChange("checkBoxVisible", !checkBoxVisible, checkBoxVisible);
			checkBox.setVisible(checkBoxVisible);
		}
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		if (checked != this.checked) {
			this.checked = checked;
			firePropertyChange("checked", !checked, checked);
			checkBox.setSelected(checked);
			getSpinner().setEnabled(checked);
		}
	}

	public JSpinner getSpinner() {
		return spinner;
	}

	public JCalendar getCalendar() {
		return calendar;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		spinner.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (calendar != null)
			calendar.setBackground(bg);
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (spinner != null) {
			spinner.getEditor().setForeground(fg);
		}
		if (calendar != null)
			calendar.setForeground(fg);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (spinner != null) {
			spinner.getEditor().setFont(font);
		}
		if (calendar != null)
			calendar.setFont(font);
	}

	public void showPopupMenu() {
		setChecked(true);
		allowPopupHide = false;

		Calendar tmpCal = new GregorianCalendar();
		tmpCal.setTime(date);
		calendar.setActiveCalendar(tmpCal);
		calendar.setActiveView(CalendarView.MONTH);

		Point pt = getPopupLocation();
		popupMenu.show(this, pt.x, pt.y);
		calendar.requestFocus();
	}

	public void hidePopupMenu() {
		allowPopupHide = true;
		popupMenu.setVisible(false);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (popupMenu != null)
			SwingUtilities.updateComponentTreeUI(popupMenu);
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == checkBox)
			setChecked(checkBox.isSelected());
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() == calendar && e.getPropertyName().equals("selectedCalendar")) {
			hidePopupMenu();
			setDate(calendar.getDate());
		}
	}

	private void initGUI() {
		checkBox = new JCheckBox();
		checkBox.setVisible(false);
		checkBox.setFocusable(false);
		checkBox.setBorderPainted(false);
		checkBox.setContentAreaFilled(false);
		checkBox.addChangeListener(this);
		add(checkBox, BorderLayout.LINE_START);

		spinner = new JSpinner(new SpinnerDateModel());
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setDate((Date) spinner.getValue());
			}
		});
		add(spinner, BorderLayout.CENTER);

		button = new JButton(new ImageIcon(getClass().getResource("resources/calendar.gif")));
		button.setOpaque(false);
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(20, 20));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestFocusInWindow();
				if (popupMenu.isVisible())
					hidePopupMenu();
				else
					showPopupMenu();
			}
		});
		add(button, BorderLayout.LINE_END);

		popupMenu = new JPopupMenu() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setVisible(boolean visible) {
				Boolean popupCancelled = (Boolean) getClientProperty(POPUP_MENU_CANCEL_PROPERTY);
				if (visible || allowPopupHide || (popupCancelled != null && popupCancelled.booleanValue()))
					super.setVisible(visible);
			}
		};
		popupMenu.setLightWeightPopupEnabled(true);

		calendar = new JCalendar();
		calendar.setPreferredSize(new Dimension(320, 240));
		calendar.addPropertyChangeListener(this);
		popupMenu.add(calendar);
	}

	private void updateDateEditor() {
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(getSpinner(), dateFormat);
		spinner.setEditor(dateEditor);
		dateEditor.getTextField().setHorizontalAlignment(SwingConstants.LEADING);
	}

	private Point getPopupLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension calendarSize = calendar.getPreferredSize();
		Point pt = new Point(0, getHeight());
		
		Point ptScr = (Point) pt.clone();
		SwingUtilities.convertPointToScreen(ptScr, button);
		
		if (ptScr.x + calendarSize.width > screenSize.width)
			pt.x = getWidth() - calendarSize.width;
		if (ptScr.y + calendarSize.height > screenSize.height)
			pt.y = -calendarSize.height;
		
		return pt;
	}
}