package org.addy.swing;

import org.addy.util.TypeConverter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

;

public class JCalendarCombo extends JPanel implements PropertyChangeListener {
	private static final String POPUP_MENU_CANCEL_PROPERTY = "JPopupMenu.firePopupMenuCanceled";
	private static final String DEFAULT_DATE_TIME_FORMAT = "d MMMM yyyy";

	private LocalDateTime dateTime;
	private String dateTimeFormat;

	private JSpinner spinner;
	private JButton button;
	private JPopupMenu popupMenu;
	private JCalendar calendar;

	private boolean allowPopupHide;

	public JCalendarCombo() {
		this(LocalDateTime.now(), DEFAULT_DATE_TIME_FORMAT, false);
	}

	public JCalendarCombo(LocalDateTime dateTime) {
		this(dateTime, DEFAULT_DATE_TIME_FORMAT, false);
	}

	public JCalendarCombo(LocalDateTime dateTime, String dateTimeFormat) {
		this(dateTime, dateTimeFormat, false);
	}

	public JCalendarCombo(LocalDateTime dateTime, String dateTimeFormat, boolean checkBoxVisible) {
		super(new BorderLayout());
		initGUI();
		setDateTime(dateTime);
		setDateTimeFormat(dateTimeFormat);
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		if (!Objects.equals(dateTime, this.dateTime)) {
			LocalDateTime oldDate = this.dateTime;
			this.dateTime = dateTime;
			firePropertyChange("dateTime", oldDate, dateTime);
			spinner.setValue(TypeConverter.toDate(dateTime));
		}
	}

	public LocalDate getDate() {
		return dateTime != null ? dateTime.toLocalDate() : null;
	}

	public void setDate(LocalDate date) {
		setDateTime(date != null ? date.atStartOfDay() : null);
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		if (!Objects.equals(dateTimeFormat, this.dateTimeFormat)) {
			String oldDateFormat = this.dateTimeFormat;
			this.dateTimeFormat = Objects.requireNonNull(dateTimeFormat);
			firePropertyChange("dateTimeFormat", oldDateFormat, dateTimeFormat);
			updateDateEditor();
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
		allowPopupHide = false;
		calendar.setActiveDateTime(dateTime != null ? dateTime : LocalDateTime.now());
		calendar.setActiveView(CalendarView.MONTH);

		Point pt = getPopupLocation();
		popupMenu.show(this, pt.x, pt.y);
		calendar.requestFocus();
	}

	public void hidePopupMenu() {
		allowPopupHide = true;
		popupMenu.setVisible(false);
		calendar.clearHighlight();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (popupMenu != null)
			SwingUtilities.updateComponentTreeUI(popupMenu);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() == calendar && e.getPropertyName().equals("selectedDateTime")) {
			hidePopupMenu();
			setDateTime(calendar.getSelectedDateTime());
		}
	}

	private void initGUI() {
		Border emptyBorder = new EmptyBorder(0, 0, 0, 0);
		spinner = new JSpinner(new SpinnerDateModel());
		spinner.setBorder(emptyBorder);
		spinner.getEditor().setBorder(emptyBorder);
		spinner.addChangeListener(e -> setDateTime(TypeConverter.toLocalDateTime(spinner.getValue())));
		add(spinner, BorderLayout.CENTER);

		button = new BasicArrowButton(SwingConstants.SOUTH);
		button.setFocusable(false);
		button.addActionListener(e -> {
			requestFocusInWindow();
			if (popupMenu.isVisible())
				hidePopupMenu();
			else
				showPopupMenu();
		});
		add(button, BorderLayout.LINE_END);

		popupMenu = new JPopupMenu() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setVisible(boolean visible) {
				Boolean popupCancelled = (Boolean) getClientProperty(POPUP_MENU_CANCEL_PROPERTY);
				if (visible || allowPopupHide || (popupCancelled != null && popupCancelled))
					super.setVisible(visible);
			}
		};
		popupMenu.setLightWeightPopupEnabled(true);

		calendar = new JCalendar();
		calendar.addPropertyChangeListener(this);
		popupMenu.add(calendar);

		setBackground(UIManager.getColor("ComboBox.background"));
		setBorder(UIManager.getBorder("ComboBox.border"));
	}

	private void updateDateEditor() {
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, dateTimeFormat);
		spinner.setEditor(dateEditor);
		dateEditor.getTextField().setHorizontalAlignment(SwingConstants.LEADING);
	}

	private Point getPopupLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension calendarSize = calendar.getPreferredSize();
		Point pt = new Point(0, getHeight());

		Point ptScr = (Point) pt.clone();
		SwingUtilities.convertPointToScreen(ptScr, this);

		if (ptScr.x + calendarSize.width > screenSize.width)
			pt.x = getWidth() - calendarSize.width;
		if (ptScr.y + calendarSize.height > screenSize.height)
			pt.y = -calendarSize.height;

		return pt;
	}
}