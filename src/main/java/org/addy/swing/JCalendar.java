package org.addy.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class JCalendar extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Calendar activeCalendar = new GregorianCalendar();
	private Calendar selectedCalendar = null;
	
	private Color viewSelectorForeground = new Color(31, 31, 31);
	private Color viewSelectorBackground = new Color(127, 255, 223);
	private Font viewSelectorFont = new Font("SansSerif", Font.BOLD, 13);
	
	private Color selectedDateColor = Color.RED;
	private Color activeDateColor = Color.BLUE;
	private Color rolloverDateColor = Color.CYAN;
	
	private JPanel topPane;
	private JPanel middlePane;
	private JPanel centuryPane;
	private JPanel decadePane;
	private JPanel yearPane;
	private JPanel monthPane;
	private JPanel bottomPane;
	private JLabel[] dayOfWeekLabels;
	private JButton previousPeriodButton;
	private JButton nextPeriodButton;
	private JButton selectViewButton;
	private JButton[][] decadeButtons;
	private JButton[][] yearButtons;
	private JButton[][] monthButtons;
	private JButton[][] dateButtons;
	private JButton selectedDateButton;
	private JButton activeDateButton;
	private JButton todayButton;
	
	private ResourceBundle resources;
	private CalendarView activeView;

	public JCalendar() {
		super(new BorderLayout());
		initGUI();
		setActiveView(CalendarView.MONTH);
	}

	public CalendarView getActiveView() {
		return activeView;
	}

	public void setActiveView(CalendarView activeView) {
		if (activeView == null) {
			throw new IllegalArgumentException("activeView can't be null");
		}

		if (!activeView.equals(this.activeView)) {
			CalendarView oldView = this.activeView;
			this.activeView = activeView;
			firePropertyChange("activeView", oldView, activeView);
			fillCalendar();
			((CardLayout) middlePane.getLayout()).show(middlePane, activeView.toString());
		}
	}

	public Calendar getActiveCalendar() {
		return activeCalendar;
	}

	public void setActiveCalendar(Calendar activeCalendar) {
		if (activeCalendar == null) {
			throw new IllegalArgumentException("activeCalendar can't be null");
		}

		if (!activeCalendar.equals(this.activeCalendar)) {
			Calendar oldActiveCalendar = this.activeCalendar;
			this.activeCalendar = activeCalendar;
			firePropertyChange("activeCalendar", oldActiveCalendar, activeCalendar);
			fillCalendar();
		}
	}

	public Calendar getSelectedCalendar() {
		return selectedCalendar;
	}

	public void setSelectedCalendar(Calendar selectedCalendar) {
		if ((selectedCalendar == null && this.selectedCalendar != null) ||
				(selectedCalendar != null && !selectedCalendar.equals(this.selectedCalendar))) {
			
			Calendar oldSelectedCalendar = this.selectedCalendar;
			this.selectedCalendar = selectedCalendar;
			firePropertyChange("selectedCalendar", oldSelectedCalendar, selectedCalendar);
			setActiveCalendar(selectedCalendar != null ? selectedCalendar : new GregorianCalendar());
		}
	}

	public Date getDate() {
		if (selectedCalendar != null)
			return selectedCalendar.getTime();
		return null;
	}

	public void setDate(Date date) {
		if (date == null) {
			setSelectedCalendar(null);
		} else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			setSelectedCalendar(calendar);
		}
	}

	public Color getViewSelectorBackground() {
		return viewSelectorBackground;
	}

	public void setViewSelectorBackground(Color viewSelectorBackground) {
		if (viewSelectorBackground == null) {
			throw new IllegalArgumentException("viewSelectorBackground can't be null");
		}

		if (!viewSelectorBackground.equals(this.viewSelectorBackground)) {
			Color oldViewSelectorBackground = this.viewSelectorBackground;
			this.viewSelectorBackground = viewSelectorBackground;
			firePropertyChange("viewSelectorBackground", oldViewSelectorBackground, viewSelectorBackground);
			topPane.setBackground(viewSelectorBackground);
		}
	}

	public Color getViewSelectorForeground() {
		return viewSelectorForeground;
	}

	public void setViewSelectorForeground(Color viewSelectorForeground) {
		if (viewSelectorForeground == null) {
			throw new IllegalArgumentException("viewSelectorForeground can't be null");
		}

		if (!viewSelectorForeground.equals(this.viewSelectorForeground)) {
			Color oldViewSelectorForeground = this.viewSelectorForeground;
			this.viewSelectorForeground = viewSelectorForeground;
			firePropertyChange("viewSelectorForeground", oldViewSelectorForeground, viewSelectorForeground);
			selectViewButton.setForeground(viewSelectorForeground);
		}
	}

	public Font getViewSelectorFont() {
		return viewSelectorFont;
	}

	public void setViewSelectorFont(Font viewSelectorFont) {
		if (viewSelectorFont == null) {
			throw new IllegalArgumentException("viewSelectorFont can't be null");
		}

		if (!viewSelectorFont.equals(this.viewSelectorFont)) {
			Font oldViewSelectorFont = this.viewSelectorFont;
			this.viewSelectorFont = viewSelectorFont;
			firePropertyChange("viewSelectorFont", oldViewSelectorFont, viewSelectorFont);
			selectViewButton.setFont(viewSelectorFont);
		}
	}

	public Color getSelectedDateColor() {
		return selectedDateColor;
	}

	public void setSelectedDateColor(Color selectedDateColor) {
		if (selectedDateColor == null) {
			throw new IllegalArgumentException("selectedDateColor can't be null");
		}

		if (!selectedDateColor.equals(this.selectedDateColor)) {
			Color oldMonthYearBackground = this.selectedDateColor;
			this.selectedDateColor = selectedDateColor;
			firePropertyChange("selectedDateColor", oldMonthYearBackground, selectedDateColor);
			if (selectedDateButton != null)
				selectedDateButton.setForeground(selectedDateColor);
		}
	}

	public Color getActiveDateColor() {
		return activeDateColor;
	}

	public void setActiveDateColor(Color activeDateColor) {
		if (activeDateColor == null) {
			throw new IllegalArgumentException("activeDateColor can't be null");
		}

		if (!activeDateColor.equals(this.activeDateColor)) {
			Color oldMonthYearBackground = this.activeDateColor;
			this.activeDateColor = activeDateColor;
			firePropertyChange("activeDateColor", oldMonthYearBackground, activeDateColor);
			if (activeDateButton != null)
				activeDateButton.setForeground(activeDateColor);
		}
	}

	public Color getRolloverDateColor() {
		return rolloverDateColor;
	}

	public void setRolloverDateColor(Color rolloverDateColor) {
		if (rolloverDateColor == null) {
			throw new IllegalArgumentException("rolloverDateColor can't be null");
		}

		if (!rolloverDateColor.equals(this.rolloverDateColor)) {
			Color oldMonthYearBackground = this.rolloverDateColor;
			this.rolloverDateColor = rolloverDateColor;
			firePropertyChange("rolloverDateColor", oldMonthYearBackground, rolloverDateColor);
		}
	}

	@Override
	public void setForeground(Color foreground) {
		super.setForeground(foreground);

		if (dayOfWeekLabels != null) {
			for (JLabel dayOfWeekLabel : dayOfWeekLabels) {
				dayOfWeekLabel.setForeground(foreground);
			}
		}

		if (dateButtons != null) {
			for (int i = 0; i < dateButtons.length; i++) {
				for (int j = 0; j < dateButtons[0].length; j++) {
					JButton dateButton = dateButtons[i][j];
					if (dateButton != selectedDateButton && dateButton != activeDateButton) {
						dateButton.setForeground(foreground);
					}
				}
			}
		}

		if (monthButtons != null) {
			for (int i = 0; i < monthButtons.length; i++) {
				for (int j = 0; j < monthButtons[0].length; j++) {
					JButton monthButton = monthButtons[i][j];
					if (monthButton != activeDateButton) {
						monthButton.setForeground(foreground);
					}
				}
			}
		}

		if (yearButtons != null) {
			for (int i = 0; i < yearButtons.length; i++) {
				for (int j = 0; j < yearButtons[0].length; j++) {
					JButton yearButton = yearButtons[i][j];
					if (yearButton != activeDateButton) {
						yearButton.setForeground(foreground);
					}
				}
			}
		}

		if (decadeButtons != null) {
			for (int i = 0; i < decadeButtons.length; i++) {
				for (int j = 0; j < decadeButtons[0].length; j++) {
					JButton decadeButton = decadeButtons[i][j];
					if (decadeButton != activeDateButton) {
						decadeButton.setForeground(foreground);
					}
				}
			}
		}

		if (todayButton != null)
			todayButton.setForeground(foreground);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);

		if (dayOfWeekLabels != null) {
			Font dayOfWeekFont = new Font(font.getFamily(), 1, font.getSize());
			for (JLabel dayOfWeekLabel : dayOfWeekLabels) {
				dayOfWeekLabel.setFont(dayOfWeekFont);
			}
		}

		if (dateButtons != null) {
			for (int i = 0; i < dateButtons.length; i++) {
				for (int j = 0; j < dateButtons[0].length; j++) {
					dateButtons[i][j].setFont(font);
				}
			}
		}

		if (monthButtons != null) {
			for (int i = 0; i < monthButtons.length; i++) {
				for (int j = 0; j < monthButtons[0].length; j++) {
					monthButtons[i][j].setFont(font);
				}
			}
		}

		if (yearButtons != null) {
			for (int i = 0; i < yearButtons.length; i++) {
				for (int j = 0; j < yearButtons[0].length; j++) {
					yearButtons[i][j].setFont(font);
				}
			}
		}

		if (decadeButtons != null) {
			for (int i = 0; i < decadeButtons.length; i++) {
				for (int j = 0; j < decadeButtons[0].length; j++) {
					decadeButtons[i][j].setFont(font);
				}
			}
		}

		if (todayButton != null)
			todayButton.setFont(font);
	}

	private void initGUI() {
		resources = ResourceBundle.getBundle("org/addy/swing/JCalendar");

		topPane = new JPanel(new GridBagLayout());
		topPane.setBackground(viewSelectorBackground);
		add(topPane, BorderLayout.PAGE_START);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);

		previousPeriodButton = createGraphicButton("previous.png", "previous_hot.png", "previous_pressed.png");
		previousPeriodButton.setPreferredSize(new Dimension(16, 16));
		previousPeriodButton.addActionListener(new DateTranslation(this, 0, -1));
		topPane.add(previousPeriodButton, gbc);

		gbc.gridx = 2;
		nextPeriodButton = createGraphicButton("next.png", "next_hot.png", "next_pressed.png");
		nextPeriodButton.setPreferredSize(new Dimension(16, 16));
		nextPeriodButton.addActionListener(new DateTranslation(this, 0, 1));
		topPane.add(nextPeriodButton, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0D;
		selectViewButton = createFlatButton("");
		selectViewButton.setFont(viewSelectorFont);
		selectViewButton.setForeground(viewSelectorForeground);
		selectViewButton.setCursor(Cursor.getPredefinedCursor(12));
		selectViewButton.addActionListener(e -> {
			requestFocusInWindow();
			JCalendar.this.switchActiveView();
		});
		topPane.add(selectViewButton, gbc);

		CardLayout cardLayout = new CardLayout();

		middlePane = new JPanel(cardLayout);
		middlePane.setOpaque(false);
		add(middlePane, BorderLayout.CENTER);

		centuryPane = new JPanel(new GridLayout(3, 4));
		centuryPane.setOpaque(false);
		middlePane.add(centuryPane, CalendarView.CENTURY.toString());

		ActionListener decadeButtonListener = e -> {
			requestFocusInWindow();
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(e.getActionCommand()));
			setActiveCalendar(calendar);
			setActiveView(CalendarView.DECADE);
		};
		
		decadeButtons = new JButton[3][4];
		for (int i = 0; i < decadeButtons.length; i++) {
			for (int j = 0; j < decadeButtons[0].length; j++) {
				decadeButtons[i][j] = createFlatButton("");
				decadeButtons[i][j].addMouseListener(new FlatButtonMouseAdapter(rolloverDateColor));
				decadeButtons[i][j].addActionListener(decadeButtonListener);
				centuryPane.add(decadeButtons[i][j]);
			}
		}

		decadePane = new JPanel(new GridLayout(3, 4));
		decadePane.setOpaque(false);
		middlePane.add(decadePane, CalendarView.DECADE.toString());

		ActionListener yearButtonListener = e -> {
			requestFocusInWindow();
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(e.getActionCommand()));
			setActiveCalendar(calendar);
			setActiveView(CalendarView.YEAR);
		};
		
		yearButtons = new JButton[3][4];
		for (int i = 0; i < yearButtons.length; i++) {
			for (int j = 0; j < yearButtons[0].length; j++) {
				yearButtons[i][j] = createFlatButton("");
				yearButtons[i][j].addMouseListener(new FlatButtonMouseAdapter(rolloverDateColor));
				yearButtons[i][j].addActionListener(yearButtonListener);
				decadePane.add(yearButtons[i][j]);
			}
		}

		yearPane = new JPanel(new GridLayout(3, 4));
		yearPane.setOpaque(false);
		middlePane.add(yearPane, CalendarView.YEAR.toString());

		ActionListener monthButtonListener = e -> {
			requestFocusInWindow();
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(e.getActionCommand()));
			setActiveCalendar(calendar);
			setActiveView(CalendarView.MONTH);
		};
		
		monthButtons = new JButton[3][4];
		for (int i = 0; i < monthButtons.length; i++) {
			for (int j = 0; j < monthButtons[0].length; j++) {
				monthButtons[i][j] = createFlatButton("");
				monthButtons[i][j].addMouseListener(new FlatButtonMouseAdapter(rolloverDateColor));
				monthButtons[i][j].addActionListener(monthButtonListener);
				yearPane.add(monthButtons[i][j]);
			}
		}

		monthPane = new JPanel(new GridLayout(7, 7));
		monthPane.setOpaque(false);
		middlePane.add(monthPane, CalendarView.MONTH.toString());

		dayOfWeekLabels = new JLabel[7];
		for (int i = 0; i < dayOfWeekLabels.length; i++) {
			dayOfWeekLabels[i] = getDayOfWeekLabel(i, 0);
			monthPane.add(dayOfWeekLabels[i]);
		}

		ActionListener dateButtonListener = e -> {
			requestFocusInWindow();
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(Long.parseLong(e.getActionCommand()));
			setSelectedCalendar(calendar);
		};
		
		dateButtons = new JButton[6][7];
		for (int i = 0; i < dateButtons.length; i++) {
			for (int j = 0; j < dateButtons[0].length; j++) {
				dateButtons[i][j] = createFlatButton("");
				dateButtons[i][j].addMouseListener(new FlatButtonMouseAdapter(rolloverDateColor));
				dateButtons[i][j].addActionListener(dateButtonListener);
				monthPane.add(dateButtons[i][j]);
			}
		}

		bottomPane = new JPanel();
		bottomPane.setOpaque(false);
		add(bottomPane, "Last");

		todayButton = createFlatButton(String.format(
				resources.getString("todayFormat"),
				DateFormat.getDateInstance().format(new Date())));

		todayButton.setCursor(Cursor.getPredefinedCursor(12));
		todayButton.addActionListener(e -> {
			requestFocusInWindow();
			setActiveView(CalendarView.MONTH);
			setSelectedCalendar(new GregorianCalendar());
		});
		bottomPane.add(todayButton);

		setFocusable(true);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		setFont(new Font("SansSerif", Font.PLAIN, 13));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		
		InputMap inputMap = getInputMap();
		inputMap.put(KeyStroke.getKeyStroke("pressed LEFT"), "left");
		inputMap.put(KeyStroke.getKeyStroke("pressed RIGHT"), "right");
		inputMap.put(KeyStroke.getKeyStroke("pressed UP"), "up");
		inputMap.put(KeyStroke.getKeyStroke("pressed DOWN"), "down");
		inputMap.put(KeyStroke.getKeyStroke("pressed ENTER"), "enter");

		ActionMap actionMap = getActionMap();
		actionMap.put("left", new DateTranslation(this, 1, -1));
		actionMap.put("right", new DateTranslation(this, 1, 1));
		actionMap.put("up", new DateTranslation(this, 2, -1));
		actionMap.put("down", new DateTranslation(this, 2, 1));
		actionMap.put("enter", new ActiveDateSelection(this));
	}

	private void switchActiveView() {
		switch (activeView) {
			case MONTH:
				setActiveView(CalendarView.YEAR);
				break;
			case YEAR:
				setActiveView(CalendarView.DECADE);
				break;
			case DECADE:
				setActiveView(CalendarView.CENTURY);
				break;
			default:
				getToolkit().beep();
		}
	}

	private JLabel getDayOfWeekLabel(int dayOfWeek, int alignment) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek + calendar.getFirstDayOfWeek());
		String text = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, getDefaultLocale());

		JLabel label = new JLabel(text, alignment) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.fillRect(0, getHeight() - 1, getWidth(), 1);	// Trait noir en dessous
			}
		};
		
		label.setFont(new Font(getFont().getFamily(), Font.BOLD, getFont().getSize()));
		label.setForeground(getForeground());
		return label;
	}

	private JButton createFlatButton(String text) {
		JButton button = new JButton(text);
		button.setForeground(getForeground());
		button.setFont(getFont());
		button.setBorder(null);
		button.setContentAreaFilled(false);
		button.setFocusable(false);
		return button;
	}

	private JButton createGraphicButton(String iconPath, String hotIconPath, String pressedIconPath) {
		JButton button = createFlatButton("");
		button.setRolloverEnabled(true);
		button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
		button.setRolloverIcon(new ImageIcon(getClass().getResource(hotIconPath)));
		button.setPressedIcon(new ImageIcon(getClass().getResource(pressedIconPath)));
		return button;
	}

	private void fillCalendar() {
		SimpleDateFormat dateFormat = null;
		int year0 = 0;
		int year9 = 9;

		switch (activeView) {
			case CENTURY:
				fillCenturyCalendar();
				year0 = 100 * (activeCalendar.get(Calendar.YEAR) / 100);
				year9 = year0 + 99;
				break;
			case DECADE:
				fillDecadeCalendar();
				year0 = 10 * (activeCalendar.get(Calendar.YEAR) / 10);
				year9 = year0 + 9;
				break;
			case YEAR:
				fillYearCalendar();
				dateFormat = new SimpleDateFormat("yyyy");
				break;
			default:
				fillMonthCalendar();
				dateFormat = new SimpleDateFormat(resources.getString("monthYearPattern"));
				break;
		}

		if (dateFormat != null)
			selectViewButton.setText(dateFormat.format(activeCalendar.getTime()));
		else
			selectViewButton.setText(year0 + "-" + year9);
	}

	private void fillCenturyCalendar() {
		Calendar clone = (Calendar) activeCalendar.clone();
		clone.set(Calendar.YEAR, 100 * (clone.get(Calendar.YEAR) / 100) - 10);

		for (int y = 0; y < decadeButtons.length; y++)
			for (int x = 0; x < decadeButtons[0].length; x++) {
				int year0 = clone.get(Calendar.YEAR);
				int year9 = year0 + 9;
				decadeButtons[y][x].setText("<html>" + year0 + "<br>" + year9 + "</html>");
				decadeButtons[y][x].setActionCommand("" + clone.getTimeInMillis());

				if (clone.get(1) / 10 == activeCalendar.get(1) / 10) {
					activeDateButton = decadeButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if ((y == 0 && x == 0) || (y == 2 && x == 3)) {
					decadeButtons[y][x].setForeground(Color.GRAY);
				} else {
					decadeButtons[y][x].setForeground(getForeground());
				}

				clone.add(Calendar.YEAR, 10);
			}
	}

	private void fillDecadeCalendar() {
		Calendar clone = (Calendar) activeCalendar.clone();
		clone.set(Calendar.YEAR, 10 * (clone.get(Calendar.YEAR) / 10) - 1);

		for (int y = 0; y < yearButtons.length; y++)
			for (int x = 0; x < yearButtons[0].length; x++) {
				yearButtons[y][x].setText("" + clone.get(Calendar.YEAR));
				yearButtons[y][x].setActionCommand("" + clone.getTimeInMillis());

				if (clone.get(1) == activeCalendar.get(1)) {
					activeDateButton = yearButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if ((y == 0 && x == 0) || (y == 2 && x == 3)) {
					yearButtons[y][x].setForeground(Color.GRAY);
				} else {
					yearButtons[y][x].setForeground(getForeground());
				}

				clone.add(Calendar.YEAR, 1);
			}
	}

	private void fillYearCalendar() {
		Calendar clone = (Calendar) activeCalendar.clone();
		clone.set(Calendar.MONTH, 0);

		for (int y = 0; y < monthButtons.length; y++)
			for (int x = 0; x < monthButtons[0].length; x++) {
				monthButtons[y][x].setText(clone.getDisplayName(Calendar.MONTH, Calendar.SHORT, getDefaultLocale()));
				monthButtons[y][x].setActionCommand("" + clone.getTimeInMillis());

				if (clone.equals(activeCalendar)) {
					activeDateButton = monthButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else {
					monthButtons[y][x].setForeground(getForeground());
				}

				clone.add(Calendar.MONTH, 1);
			}
	}

	private void fillMonthCalendar() {
		int y_ref = activeCalendar.get(Calendar.WEEK_OF_MONTH) - 1;
		int x_ref = (activeCalendar.get(Calendar.DAY_OF_WEEK) - activeCalendar.getFirstDayOfWeek() + 7) % 7;

		Calendar clone = (Calendar) activeCalendar.clone();
		clone.add(Calendar.DATE, -(7 * y_ref + x_ref));

		for (int y = 0; y < dateButtons.length; y++)
			for (int x = 0; x < dateButtons[0].length; x++) {
				dateButtons[y][x].setText("" + clone.get(Calendar.DATE));
				dateButtons[y][x].setActionCommand("" + clone.getTimeInMillis());

				if (clone.equals(selectedCalendar)) {
					selectedDateButton = dateButtons[y][x];
					selectedDateButton.setForeground(selectedDateColor);
				} else if (clone.equals(activeCalendar)) {
					activeDateButton = dateButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if (clone.get(Calendar.MONTH) == activeCalendar.get(Calendar.MONTH)) {
					dateButtons[y][x].setForeground(getForeground());
				} else {
					dateButtons[y][x].setForeground(Color.GRAY);
				}

				clone.add(Calendar.DATE, 1);
			}
	}

	protected static class FlatButtonMouseAdapter extends MouseAdapter {

		private static final String SAVED_FOREGROUND = "JCalendar.savedForeground";
		
		private final Color highlight;

		public FlatButtonMouseAdapter(Color highlight) {
			this.highlight = highlight;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			button.putClientProperty(SAVED_FOREGROUND, button.getForeground());
			button.setForeground(highlight);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			if (button.getForeground() == highlight)
				button.setForeground((Color) button.getClientProperty(SAVED_FOREGROUND));
		}
	}

	protected static class ActiveDateSelection extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private JCalendar monthCalendar;

		public ActiveDateSelection(JCalendar monthCalendar) {
			this.monthCalendar = monthCalendar;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (monthCalendar.activeView) {
				case CENTURY:
					monthCalendar.setActiveView(CalendarView.DECADE);
					break;
				case DECADE:
					monthCalendar.setActiveView(CalendarView.YEAR);
					break;
				case YEAR:
					monthCalendar.setActiveView(CalendarView.MONTH);
					break;
				default:
					monthCalendar.setSelectedCalendar(monthCalendar.activeCalendar);
					monthCalendar.fillCalendar();
			}
		}
	}

	protected static class DateTranslation extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private JCalendar monthCalendar;
		private int field;
		private int amount;

		public DateTranslation(JCalendar monthCalendar, int field, int amount) {
			this.monthCalendar = monthCalendar;
			this.field = field;
			this.amount = amount;
		}

		public void actionPerformed(ActionEvent e) {
			monthCalendar.requestFocusInWindow();
			Calendar clone = (Calendar) monthCalendar.activeCalendar.clone();

			switch (field) {
				case 0:
				case 1:
					switch (monthCalendar.activeView) {
						case MONTH:
							clone.add(Calendar.MONTH, amount);
							break;
						case YEAR:
							clone.add(Calendar.YEAR, amount);
							break;
						case DECADE:
							clone.add(Calendar.YEAR, amount);
							break;
						default:
							clone.add(Calendar.YEAR, 10 * amount);
							break;
						}
					break;
				case 2:
					switch (monthCalendar.activeView) {
						case MONTH:
							clone.add(Calendar.WEEK_OF_MONTH, amount);
							break;
						case YEAR:
							clone.add(Calendar.MONTH, 4 * amount);
							break;
						case DECADE:
							clone.add(Calendar.YEAR, 4 * amount);
							break;
						default:
							clone.add(Calendar.YEAR, 40 * amount);
							break;
					}
					break;
				default:
					Toolkit.getDefaultToolkit().beep();
					return;
			}

			monthCalendar.setActiveCalendar(clone);
		}
	}
}