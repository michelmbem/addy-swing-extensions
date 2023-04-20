package org.addy.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.ResourceBundle;

public class JCalendar extends JPanel {
	public static final Dimension BUTTON_SIZE = new Dimension(16, 16);

	private LocalDateTime activeDateTime = LocalDateTime.now();
	private LocalDateTime selectedDateTime = null;

	private Color viewSelectorForeground = KnownColor.DARK_SLATE;
	private Color viewSelectorBackground = KnownColor.LIGHTSKYBLUE;
	private Font viewSelectorFont = new Font("SansSerif", Font.BOLD, 13);

	private Color selectedDateColor = KnownColor.RED;
	private Color activeDateColor = KnownColor.MEDIUMBLUE;
	private Color inactiveDateColor = KnownColor.METALLIC_SILVER;
	private Color rolloverDateColor = KnownColor.WHITE_BLUE;

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
		if (!Objects.equals(activeView, this.activeView)) {
			CalendarView oldView = this.activeView;
			this.activeView = Objects.requireNonNull(activeView);
			firePropertyChange("activeView", oldView, activeView);
			fillCalendar();
			((CardLayout) middlePane.getLayout()).show(middlePane, activeView.toString());
		}
	}

	public LocalDateTime getActiveDateTime() {
		return activeDateTime;
	}

	public void setActiveDateTime(LocalDateTime activeDateTime) {
		if (!Objects.equals(activeDateTime, this.activeDateTime)) {
			LocalDateTime oldActiveCalendar = this.activeDateTime;
			this.activeDateTime = Objects.requireNonNull(activeDateTime);
			firePropertyChange("activeDateTime", oldActiveCalendar, activeDateTime);
			fillCalendar();
		}
	}

	public LocalDateTime getSelectedDateTime() {
		return selectedDateTime;
	}

	public void setSelectedDateTime(LocalDateTime selectedDateTime) {
		if (!Objects.equals(selectedDateTime, this.selectedDateTime)) {
			LocalDateTime oldSelectedCalendar = this.selectedDateTime;
			this.selectedDateTime = selectedDateTime;
			firePropertyChange("selectedDateTime", oldSelectedCalendar, selectedDateTime);
			setActiveDateTime(selectedDateTime != null ? selectedDateTime : LocalDateTime.now());
		}
	}

	public LocalDate getSelectedDate() {
		return selectedDateTime != null ? selectedDateTime.toLocalDate() : null;
	}

	public void setSelectedDate(LocalDate selectedDate) {
		setSelectedDateTime(selectedDate != null ? selectedDate.atStartOfDay() : null);
	}

	public Color getViewSelectorBackground() {
		return viewSelectorBackground;
	}

	public void setViewSelectorBackground(Color viewSelectorBackground) {
		if (!Objects.equals(viewSelectorBackground, this.viewSelectorBackground)) {
			Color oldViewSelectorBackground = this.viewSelectorBackground;
			this.viewSelectorBackground = Objects.requireNonNull(viewSelectorBackground);
			firePropertyChange("viewSelectorBackground", oldViewSelectorBackground, viewSelectorBackground);
			topPane.setBackground(viewSelectorBackground);
		}
	}

	public Color getViewSelectorForeground() {
		return viewSelectorForeground;
	}

	public void setViewSelectorForeground(Color viewSelectorForeground) {
		if (!Objects.equals(viewSelectorForeground, this.viewSelectorForeground)) {
			Color oldViewSelectorForeground = this.viewSelectorForeground;
			this.viewSelectorForeground = Objects.requireNonNull(viewSelectorForeground);
			firePropertyChange("viewSelectorForeground", oldViewSelectorForeground, viewSelectorForeground);
			selectViewButton.setForeground(viewSelectorForeground);
		}
	}

	public Font getViewSelectorFont() {
		return viewSelectorFont;
	}

	public void setViewSelectorFont(Font viewSelectorFont) {
		if (!Objects.equals(viewSelectorFont, this.viewSelectorFont)) {
			Font oldViewSelectorFont = this.viewSelectorFont;
			this.viewSelectorFont = Objects.requireNonNull(viewSelectorFont);
			firePropertyChange("viewSelectorFont", oldViewSelectorFont, viewSelectorFont);
			selectViewButton.setFont(viewSelectorFont);
		}
	}

	public Color getSelectedDateColor() {
		return selectedDateColor;
	}

	public void setSelectedDateColor(Color selectedDateColor) {
		if (!Objects.equals(selectedDateColor, this.selectedDateColor)) {
			Color oldMonthYearBackground = this.selectedDateColor;
			this.selectedDateColor = Objects.requireNonNull(selectedDateColor);
			firePropertyChange("selectedDateColor", oldMonthYearBackground, selectedDateColor);
			if (selectedDateButton != null) selectedDateButton.setForeground(selectedDateColor);
		}
	}

	public Color getActiveDateColor() {
		return activeDateColor;
	}

	public void setActiveDateColor(Color activeDateColor) {
		if (!Objects.equals(activeDateColor, this.activeDateColor)) {
			Color oldMonthYearBackground = this.activeDateColor;
			this.activeDateColor = Objects.requireNonNull(activeDateColor);
			firePropertyChange("activeDateColor", oldMonthYearBackground, activeDateColor);
			if (activeDateButton != null) activeDateButton.setForeground(activeDateColor);
		}
	}

	public Color getRolloverDateColor() {
		return rolloverDateColor;
	}

	public void setRolloverDateColor(Color rolloverDateColor) {
		if (!Objects.equals(rolloverDateColor, this.rolloverDateColor)) {
			Color oldMonthYearBackground = this.rolloverDateColor;
			this.rolloverDateColor = Objects.requireNonNull(rolloverDateColor);
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
			Font dayOfWeekFont = new Font(font.getFamily(), Font.BOLD, font.getSize());
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

	public void clearHighlight() {
		clearHighlight(dateButtons[0][0]);
		clearHighlight(monthButtons[0][0]);
		clearHighlight(yearButtons[0][0]);
		clearHighlight(decadeButtons[0][0]);
	}

	private void clearHighlight(JButton button) {
		for (MouseListener listener : button.getMouseListeners()) {
			if (listener instanceof FlatButtonMouseAdapter)
				((FlatButtonMouseAdapter) listener).clearHighlight();
		}
	}

	private void initGUI() {
		resources = ResourceBundle.getBundle("org/addy/swing/JCalendar");

		topPane = new JPanel(new GridBagLayout());
		topPane.setBackground(viewSelectorBackground);
		add(topPane, BorderLayout.PAGE_START);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);

		previousPeriodButton = createGraphicButton("previous.png", "previous_hot.png", "previous_pressed.png");
		previousPeriodButton.setPreferredSize(BUTTON_SIZE);
		previousPeriodButton.addActionListener(new DateTranslation(this, 0, -1));
		topPane.add(previousPeriodButton, gbc);

		gbc.gridx = 2;
		nextPeriodButton = createGraphicButton("next.png", "next_hot.png", "next_pressed.png");
		nextPeriodButton.setPreferredSize(BUTTON_SIZE);
		nextPeriodButton.addActionListener(new DateTranslation(this, 0, 1));
		topPane.add(nextPeriodButton, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0D;
		selectViewButton = createFlatButton("");
		selectViewButton.setFont(viewSelectorFont);
		selectViewButton.setForeground(viewSelectorForeground);
		selectViewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

		MouseListener decadeMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener decadeActionListener = e -> {
			requestFocusInWindow();
			setActiveDateTime(LocalDateTime.parse(e.getActionCommand()));
			setActiveView(CalendarView.DECADE);
		};

		decadeButtons = new JButton[3][4];
		for (int i = 0; i < decadeButtons.length; ++i) {
			for (int j = 0; j < decadeButtons[i].length; ++j) {
				decadeButtons[i][j] = createFlatButton("");
				decadeButtons[i][j].addMouseListener(decadeMouseListener);
				decadeButtons[i][j].addActionListener(decadeActionListener);
				centuryPane.add(decadeButtons[i][j]);
			}
		}

		decadePane = new JPanel(new GridLayout(3, 4));
		decadePane.setOpaque(false);
		middlePane.add(decadePane, CalendarView.DECADE.toString());

		MouseListener yearMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener yearActionListener = e -> {
			requestFocusInWindow();
			setActiveDateTime(LocalDateTime.parse(e.getActionCommand()));
			setActiveView(CalendarView.YEAR);
		};

		yearButtons = new JButton[3][4];
		for (int i = 0; i < yearButtons.length; ++i) {
			for (int j = 0; j < yearButtons[0].length; ++j) {
				yearButtons[i][j] = createFlatButton("");
				yearButtons[i][j].addMouseListener(yearMouseListener);
				yearButtons[i][j].addActionListener(yearActionListener);
				decadePane.add(yearButtons[i][j]);
			}
		}

		yearPane = new JPanel(new GridLayout(3, 4));
		yearPane.setOpaque(false);
		middlePane.add(yearPane, CalendarView.YEAR.toString());

		MouseListener monthMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener monthActionListener = e -> {
			requestFocusInWindow();
			setActiveDateTime(LocalDateTime.parse(e.getActionCommand()));
			setActiveView(CalendarView.MONTH);
		};

		monthButtons = new JButton[3][4];
		for (int i = 0; i < monthButtons.length; ++i) {
			for (int j = 0; j < monthButtons[0].length; ++j) {
				monthButtons[i][j] = createFlatButton("");
				monthButtons[i][j].addMouseListener(monthMouseListener);
				monthButtons[i][j].addActionListener(monthActionListener);
				yearPane.add(monthButtons[i][j]);
			}
		}

		monthPane = new JPanel(new GridLayout(7, 7));
		monthPane.setOpaque(false);
		middlePane.add(monthPane, CalendarView.MONTH.toString());

		dayOfWeekLabels = new JLabel[7];
		for (int i = 0; i < dayOfWeekLabels.length; ++i) {
			dayOfWeekLabels[i] = getDayOfWeekLabel(i + 1);
			monthPane.add(dayOfWeekLabels[i]);
		}

		MouseListener dateMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener dateActionListener = e -> {
			requestFocusInWindow();
			setSelectedDateTime(LocalDateTime.parse(e.getActionCommand()));
		};

		dateButtons = new JButton[6][7];
		for (int i = 0; i < dateButtons.length; ++i) {
			for (int j = 0; j < dateButtons[0].length; ++j) {
				dateButtons[i][j] = createFlatButton("");
				dateButtons[i][j].addMouseListener(dateMouseListener);
				dateButtons[i][j].addActionListener(dateActionListener);
				monthPane.add(dateButtons[i][j]);
			}
		}

		bottomPane = new JPanel();
		bottomPane.setOpaque(false);
		add(bottomPane, BorderLayout.PAGE_END);

		todayButton = createFlatButton(String.format(
				resources.getString("todayFormat"),
				DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(LocalDate.now())));

		todayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		todayButton.addActionListener(e -> {
			requestFocusInWindow();
			setActiveView(CalendarView.MONTH);
			setSelectedDateTime(LocalDateTime.now());
		});
		bottomPane.add(todayButton);

		setFocusable(true);
		setBackground(UIManager.getColor("Table.background"));
		setForeground(UIManager.getColor("Table.foreground"));
		setFont(UIManager.getFont("MenuItem.font"));
		setPreferredSize(new Dimension(240, 240));
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

	private JLabel getDayOfWeekLabel(long dayOfWeek) {
		LocalDateTime dateTime = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, dayOfWeek);
		String text = dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, getDefaultLocale());

		JLabel label = new JLabel(text, SwingConstants.CENTER) {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.fillRect(0, getHeight() - 1, getWidth(), 1);    // Trait noir en dessous
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
		DateTimeFormatter formatter = null;
		int year0 = 0;
		int year9 = 9;

		switch (activeView) {
			case CENTURY:
				fillCenturyCalendar();
				year0 = 100 * (activeDateTime.getYear() / 100);
				year9 = year0 + 99;
				break;
			case DECADE:
				fillDecadeCalendar();
				year0 = 10 * (activeDateTime.getYear() / 10);
				year9 = year0 + 9;
				break;
			case YEAR:
				fillYearCalendar();
				formatter = DateTimeFormatter.ofPattern("yyyy");
				break;
			default:
				fillMonthCalendar();
				formatter = DateTimeFormatter.ofPattern(resources.getString("monthYearPattern"));
				break;
		}

		if (formatter != null)
			selectViewButton.setText(formatter.format(activeDateTime));
		else
			selectViewButton.setText(year0 + "-" + year9);
	}

	private void fillCenturyCalendar() {
		LocalDateTime clone = activeDateTime.withYear(100 * (activeDateTime.getYear() / 100) - 10);

		for (int y = 0; y < decadeButtons.length; y++)
			for (int x = 0; x < decadeButtons[0].length; x++) {
				int year0 = clone.getYear();
				int year9 = year0 + 9;
				decadeButtons[y][x].setText("<html>" + year0 + "<br>" + year9 + "</html>");
				decadeButtons[y][x].setActionCommand(clone.toString());

				if (clone.getYear() / 10 == activeDateTime.getYear() / 10) {
					activeDateButton = decadeButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if ((y == 0 && x == 0) || (y == 2 && x == 3)) {
					decadeButtons[y][x].setForeground(inactiveDateColor);
				} else {
					decadeButtons[y][x].setForeground(getForeground());
				}

				clone = clone.plusYears(10);
			}
	}

	private void fillDecadeCalendar() {
		LocalDateTime clone = activeDateTime.withYear(10 * (activeDateTime.getYear() / 10) - 1);

		for (int y = 0; y < yearButtons.length; y++)
			for (int x = 0; x < yearButtons[0].length; x++) {
				yearButtons[y][x].setText(String.valueOf(clone.getYear()));
				yearButtons[y][x].setActionCommand(clone.toString());

				if (clone.getYear() == activeDateTime.getYear()) {
					activeDateButton = yearButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if ((y == 0 && x == 0) || (y == 2 && x == 3)) {
					yearButtons[y][x].setForeground(inactiveDateColor);
				} else {
					yearButtons[y][x].setForeground(getForeground());
				}

				clone = clone.plusYears(1);
			}
	}

	private void fillYearCalendar() {
		LocalDateTime clone = activeDateTime.withMonth(1);

		for (int y = 0; y < monthButtons.length; ++y) {
			for (int x = 0; x < monthButtons[y].length; ++x) {
				monthButtons[y][x].setText(clone.getMonth().getDisplayName(TextStyle.SHORT, getDefaultLocale()));
				monthButtons[y][x].setActionCommand(clone.toString());

				if (clone.equals(activeDateTime)) {
					activeDateButton = monthButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else {
					monthButtons[y][x].setForeground(getForeground());
				}

				clone = clone.plusMonths(1L);
			}
		}
	}

	private void fillMonthCalendar() {
		int yRef = activeDateRowIndex();
		int xRef = activeDateTime.get(ChronoField.DAY_OF_WEEK) - 1;

		LocalDateTime clone = activeDateTime.plusDays(-7L * yRef - xRef);

		for (int y = 0; y < dateButtons.length; ++y)
			for (int x = 0; x < dateButtons[y].length; ++x) {
				dateButtons[y][x].setText(String.valueOf(clone.getDayOfMonth()));
				dateButtons[y][x].setActionCommand(clone.toString());

				if (clone.equals(selectedDateTime)) {
					selectedDateButton = dateButtons[y][x];
					selectedDateButton.setForeground(selectedDateColor);
				} else if (clone.equals(activeDateTime)) {
					activeDateButton = dateButtons[y][x];
					activeDateButton.setForeground(activeDateColor);
				} else if (clone.getMonth().equals(activeDateTime.getMonth())) {
					dateButtons[y][x].setForeground(getForeground());
				} else {
					dateButtons[y][x].setForeground(inactiveDateColor);
				}

				clone = clone.plusDays(1L);
			}
	}

	private int activeDateRowIndex() {
		LocalDateTime nextSunday = activeDateTime.with(ChronoField.DAY_OF_WEEK, 7);
		int nextSundayDays = nextSunday.getDayOfMonth();

		if (!nextSunday.getMonth().equals(activeDateTime.getMonth()))
			nextSundayDays += activeDateTime.getMonth().length(activeDateTime.toLocalDate().isLeapYear());

		return (nextSundayDays - 1) / 7;
	}


	protected static class FlatButtonMouseAdapter extends MouseAdapter {
		private static final String SAVED_BG = "JCalendar.savedBackground";
		private static final String SAVED_CAF = "JCalendar.savedContentAreaFilled";

		private final Color highlightColor;
		private JButton highlightedButton = null;

		public FlatButtonMouseAdapter(Color highlightColor) {
			this.highlightColor = highlightColor;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			highlightedButton = (JButton) e.getSource();
			highlight(highlightedButton);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			unHighlight((JButton) e.getSource());
		}

		public void clearHighlight() {
			if (highlightedButton != null) {
				unHighlight(highlightedButton);
				highlightedButton = null;
			}
		}

		private void highlight(JButton button) {
			button.putClientProperty(SAVED_BG, button.getBackground());
			button.putClientProperty(SAVED_CAF, button.isContentAreaFilled());
			button.setBackground(highlightColor);
			button.setContentAreaFilled(true);
		}

		private void unHighlight(JButton button) {
			if (button.isContentAreaFilled() && button.getBackground() == highlightColor) {
				button.setBackground((Color) button.getClientProperty(SAVED_BG));
				button.setContentAreaFilled((boolean) button.getClientProperty(SAVED_CAF));
			}
		}
	}

	protected static class ActiveDateSelection extends AbstractAction {
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
					monthCalendar.setSelectedDateTime(monthCalendar.activeDateTime);
					monthCalendar.fillCalendar();
			}
		}
	}

	protected static class DateTranslation extends AbstractAction {
		private JCalendar monthCalendar;
		private int field; // 0: PAGE; 1: COLUMN; 2: ROW
		private int amount;

		public DateTranslation(JCalendar monthCalendar, int field, int amount) {
			this.monthCalendar = monthCalendar;
			this.field = field;
			this.amount = amount;
		}

		public void actionPerformed(ActionEvent e) {
			monthCalendar.requestFocusInWindow();
			LocalDateTime clone = monthCalendar.activeDateTime;

			switch (field) {
				case 0:
					switch (monthCalendar.activeView) {
						case MONTH:
							clone = clone.plusMonths(amount);
							break;
						case YEAR:
							clone = clone.plusYears(amount);
							break;
						case DECADE:
							clone = clone.plusYears(10L * amount);
							break;
						default:
							clone = clone.plusYears(100L * amount);
							break;
					}
					break;
				case 1:
					switch (monthCalendar.activeView) {
						case MONTH:
							clone = clone.plusDays(amount);
							break;
						case YEAR:
							clone = clone.plusMonths(amount);
							break;
						case DECADE:
							clone = clone.plusYears(amount);
							break;
						default:
							clone = clone.plusYears(10L * amount);
							break;
					}
					break;
				case 2:
					switch (monthCalendar.activeView) {
						case MONTH:
							clone = clone.plusWeeks(amount);
							break;
						case YEAR:
							clone = clone.plusMonths(4L * amount);
							break;
						case DECADE:
							clone = clone.plusYears(4L * amount);
							break;
						default:
							clone = clone.plusYears(40L * amount);
							break;
					}
					break;
				default:
					Toolkit.getDefaultToolkit().beep();
					return;
			}

			monthCalendar.setActiveDateTime(clone);
		}
	}
}