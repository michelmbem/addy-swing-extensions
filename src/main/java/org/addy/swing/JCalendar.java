package org.addy.swing;

import org.addy.util.media.InsetsFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.ResourceBundle;

public class JCalendar extends JPanel {
	public static final Dimension BUTTON_SIZE = new Dimension(16, 16);

	private LocalDate activeDate = LocalDate.now(ZoneId.systemDefault());
	private LocalDate selectedDate = null;

	private Color viewSelectorForeground = KnownColor.DARK_SLATE;
	private Color viewSelectorBackground = KnownColor.LIGHTSKYBLUE;
	private Font viewSelectorFont = new Font("SansSerif", Font.BOLD, 13);

	private Color selectedDateColor = KnownColor.RED;
	private Color activeDateColor = KnownColor.MEDIUMBLUE;
	private Color inactiveDateColor = KnownColor.METALLIC_SILVER;
	private Color rolloverDateColor = KnownColor.WHITE_BLUE;

	private JPanel topPane;
	private JPanel middlePane;
	private JPanel bottomPane;
	private JPanel centuryPane;
	private JPanel decadePane;
	private JPanel yearPane;
	private JPanel monthPane;
	private JButton previousPeriodButton;
	private JButton nextPeriodButton;
	private JButton selectViewButton;
	private JLabel[] dayOfWeekLabels;
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
        if (activeView == this.activeView) return;

        CalendarView oldActiveView = this.activeView;
        this.activeView = Objects.requireNonNull(activeView);
        firePropertyChange("activeView", oldActiveView, activeView);
        fillCalendar();
        ((CardLayout) middlePane.getLayout()).show(middlePane, activeView.name());
    }

	public LocalDate getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(LocalDate activeDate) {
        if (Objects.equals(activeDate, this.activeDate)) return;

        LocalDate oldActiveDate = this.activeDate;
        this.activeDate = Objects.requireNonNull(activeDate);
        firePropertyChange("activeDate", oldActiveDate, activeDate);
        fillCalendar();
    }

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(LocalDate selectedDate) {
        if (Objects.equals(selectedDate, this.selectedDate)) return;

        LocalDate oldSelectedDate = this.selectedDate;
        this.selectedDate = selectedDate;
        firePropertyChange("selectedDate", oldSelectedDate, selectedDate);
        setActiveDate(selectedDate != null ? selectedDate : LocalDate.now(ZoneId.systemDefault()));
    }

	public Color getViewSelectorBackground() {
		return viewSelectorBackground;
	}

	public void setViewSelectorBackground(Color viewSelectorBackground) {
        if (Objects.equals(viewSelectorBackground, this.viewSelectorBackground)) return;

        Color oldViewSelectorBackground = this.viewSelectorBackground;
        this.viewSelectorBackground = Objects.requireNonNull(viewSelectorBackground);
        firePropertyChange("viewSelectorBackground",
				oldViewSelectorBackground, viewSelectorBackground);
        topPane.setBackground(viewSelectorBackground);
    }

	public Color getViewSelectorForeground() {
		return viewSelectorForeground;
	}

	public void setViewSelectorForeground(Color viewSelectorForeground) {
        if (Objects.equals(viewSelectorForeground, this.viewSelectorForeground)) return;

        Color oldViewSelectorForeground = this.viewSelectorForeground;
        this.viewSelectorForeground = Objects.requireNonNull(viewSelectorForeground);
        firePropertyChange("viewSelectorForeground",
				oldViewSelectorForeground, viewSelectorForeground);
        selectViewButton.setForeground(viewSelectorForeground);
    }

	public Font getViewSelectorFont() {
		return viewSelectorFont;
	}

	public void setViewSelectorFont(Font viewSelectorFont) {
        if (Objects.equals(viewSelectorFont, this.viewSelectorFont)) return;

        Font oldViewSelectorFont = this.viewSelectorFont;
        this.viewSelectorFont = Objects.requireNonNull(viewSelectorFont);
        firePropertyChange("viewSelectorFont", oldViewSelectorFont, viewSelectorFont);
        selectViewButton.setFont(viewSelectorFont);
    }

	public Color getSelectedDateColor() {
		return selectedDateColor;
	}

	public void setSelectedDateColor(Color selectedDateColor) {
        if (Objects.equals(selectedDateColor, this.selectedDateColor)) return;

        Color oldSelectedDateColor = this.selectedDateColor;
        this.selectedDateColor = Objects.requireNonNull(selectedDateColor);
        firePropertyChange("selectedDateColor", oldSelectedDateColor, selectedDateColor);
        if (selectedDateButton != null) selectedDateButton.setForeground(selectedDateColor);
    }

	public Color getActiveDateColor() {
		return activeDateColor;
	}

	public void setActiveDateColor(Color activeDateColor) {
        if (Objects.equals(activeDateColor, this.activeDateColor)) return;

        Color oldActiveDateColor = this.activeDateColor;
        this.activeDateColor = Objects.requireNonNull(activeDateColor);
        firePropertyChange("activeDateColor", oldActiveDateColor, activeDateColor);
        if (activeDateButton != null) activeDateButton.setForeground(activeDateColor);
    }

	public Color getRolloverDateColor() {
		return rolloverDateColor;
	}

	public void setRolloverDateColor(Color rolloverDateColor) {
        if (Objects.equals(rolloverDateColor, this.rolloverDateColor)) return;

        Color oldRolloverDateColor = this.rolloverDateColor;
        this.rolloverDateColor = Objects.requireNonNull(rolloverDateColor);
        firePropertyChange("rolloverDateColor", oldRolloverDateColor, rolloverDateColor);
    }

	@Override
	public void setForeground(Color foreground) {
		super.setForeground(foreground);

		if (dayOfWeekLabels != null) {
			for (JLabel dayOfWeekLabel : dayOfWeekLabels) {
				dayOfWeekLabel.setForeground(foreground);
			}
		}

		for (JButton[][] buttonMatrix : getButtonMatrices()) {
			if (buttonMatrix == null) continue;

			for (JButton[] buttonRow : buttonMatrix) {
				for (JButton button : buttonRow) {
					if (button != selectedDateButton && button != activeDateButton)
						button.setForeground(foreground);
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

		for (JButton[][] buttonMatrix : getButtonMatrices()) {
			if (buttonMatrix == null) continue;

			for (JButton[] buttonRow : buttonMatrix) {
				for (JButton button : buttonRow) {
					button.setFont(font);
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
			if (listener instanceof FlatButtonMouseAdapter flatButtonMouseAdapter)
				flatButtonMouseAdapter.clearHighlight();
		}
	}

	private void initGUI() {
		resources = ResourceBundle.getBundle(JCalendar.class.getName());

		topPane = new JPanel(new GridBagLayout());
		topPane.setBackground(viewSelectorBackground);
		add(topPane, BorderLayout.PAGE_START);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = InsetsFactory.uniform(2);

		previousPeriodButton = createGraphicButton("previous");
		previousPeriodButton.setPreferredSize(BUTTON_SIZE);
		previousPeriodButton.addActionListener(
				new DateTranslation(this, TranslationField.PAGE, -1));
		topPane.add(previousPeriodButton, gbc);

		gbc.gridx = 2;
		nextPeriodButton = createGraphicButton("next");
		nextPeriodButton.setPreferredSize(BUTTON_SIZE);
		nextPeriodButton.addActionListener(
				new DateTranslation(this, TranslationField.PAGE, 1));
		topPane.add(nextPeriodButton, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0D;
		selectViewButton = createFlatButton("");
		selectViewButton.setFont(viewSelectorFont);
		selectViewButton.setForeground(viewSelectorForeground);
		selectViewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		selectViewButton.addActionListener(e -> {
			requestFocusInWindow();
			switchActiveView();
		});
		topPane.add(selectViewButton, gbc);

		CardLayout cardLayout = new CardLayout();

		middlePane = new JPanel(cardLayout);
		middlePane.setOpaque(false);
		add(middlePane, BorderLayout.CENTER);

		centuryPane = new JPanel(new GridLayout(3, 4));
		centuryPane.setOpaque(false);
		middlePane.add(centuryPane, CalendarView.CENTURY.name());

		MouseListener decadeMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener decadeActionListener = e -> {
			requestFocusInWindow();
			setActiveDate(LocalDate.parse(e.getActionCommand()));
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
		middlePane.add(decadePane, CalendarView.DECADE.name());

		MouseListener yearMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener yearActionListener = e -> {
			requestFocusInWindow();
			setActiveDate(LocalDate.parse(e.getActionCommand()));
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
		middlePane.add(yearPane, CalendarView.YEAR.name());

		MouseListener monthMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener monthActionListener = e -> {
			requestFocusInWindow();
			setActiveDate(LocalDate.parse(e.getActionCommand()));
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
		middlePane.add(monthPane, CalendarView.MONTH.name());

		dayOfWeekLabels = new JLabel[7];
		for (int i = 0; i < dayOfWeekLabels.length; ++i) {
			dayOfWeekLabels[i] = getDayOfWeekLabel(i + 1L);
			monthPane.add(dayOfWeekLabels[i]);
		}

		MouseListener dateMouseListener = new FlatButtonMouseAdapter(rolloverDateColor);

		ActionListener dateActionListener = e -> {
			requestFocusInWindow();
			setSelectedDate(LocalDate.parse(e.getActionCommand()));
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
				DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
						.format(LocalDate.now(ZoneId.systemDefault()))));

		todayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		todayButton.addActionListener(e -> {
			requestFocusInWindow();
			setActiveView(CalendarView.MONTH);
			setSelectedDate(LocalDate.now(ZoneId.systemDefault()));
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
		actionMap.put("left",
				new DateTranslation(this, TranslationField.COLUMN, -1));
		actionMap.put("right",
				new DateTranslation(this, TranslationField.COLUMN, 1));
		actionMap.put("up",
				new DateTranslation(this, TranslationField.ROW, -1));
		actionMap.put("down",
				new DateTranslation(this, TranslationField.ROW, 1));
		actionMap.put("enter", new ActiveDateSelection(this));
	}

	private void switchActiveView() {
        switch (activeView) {
            case MONTH -> setActiveView(CalendarView.YEAR);
            case YEAR -> setActiveView(CalendarView.DECADE);
            case DECADE -> setActiveView(CalendarView.CENTURY);
            default -> getToolkit().beep();
        }
	}

	private JLabel getDayOfWeekLabel(long dayOfWeek) {
		LocalDate date = LocalDate.now(ZoneId.systemDefault())
				.with(ChronoField.DAY_OF_WEEK, dayOfWeek);

		String text = date.getDayOfWeek()
				.getDisplayName(TextStyle.SHORT, getDefaultLocale());

		JLabel label = new JLabel(text, SwingConstants.CENTER) {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.fillRect(0, getHeight() - 1, getWidth(), 1); // Trait noir en dessous
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

	private JButton createGraphicButton(String icon) {
		JButton button = createFlatButton("");
		button.setRolloverEnabled(true);
		button.setIcon(new ImageIcon(getClass().getResource(icon + ".png")));
		button.setRolloverIcon(new ImageIcon(getClass().getResource(icon + "_hot.png")));
		button.setPressedIcon(new ImageIcon(getClass().getResource(icon + "_pressed.png")));
		return button;
	}

	private void fillCalendar() {
		DateTimeFormatter formatter = null;
		int year0 = 0;
		int year9 = 9;

		switch (activeView) {
			case CENTURY:
				year0 = 100 * (activeDate.getYear() / 100);
				year9 = year0 + 99;
				fillCenturyCalendar(year0 - 10);
				break;
			case DECADE:
				year0 = 10 * (activeDate.getYear() / 10);
				year9 = year0 + 9;
				fillDecadeCalendar(year0 - 1);
				break;
			case YEAR:
				formatter = DateTimeFormatter.ofPattern("yyyy");
				fillYearCalendar();
				break;
			default:
				formatter = DateTimeFormatter.ofPattern(resources.getString("monthYearPattern"));
				fillMonthCalendar();
				break;
		}

		if (formatter != null)
			selectViewButton.setText(formatter.format(activeDate));
		else
			selectViewButton.setText(year0 + "-" + year9);
	}

	private void fillCenturyCalendar(int startYear) {
		LocalDate clone = activeDate.withYear(startYear);

		for (int y = 0; y < decadeButtons.length; y++)
			for (int x = 0; x < decadeButtons[0].length; x++) {
				int year0 = clone.getYear();
				int year9 = year0 + 9;
				decadeButtons[y][x].setText("<html>%d<br>%d</html>".formatted(year0, year9));
				decadeButtons[y][x].setActionCommand(clone.toString());

				if (clone.getYear() / 10 == activeDate.getYear() / 10) {
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

	private void fillDecadeCalendar(int startYear) {
		LocalDate clone = activeDate.withYear(startYear);

		for (int y = 0; y < yearButtons.length; y++)
			for (int x = 0; x < yearButtons[0].length; x++) {
				yearButtons[y][x].setText(String.valueOf(clone.getYear()));
				yearButtons[y][x].setActionCommand(clone.toString());

				if (clone.getYear() == activeDate.getYear()) {
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
		LocalDate clone = activeDate.withMonth(1);

        for (JButton[] buttonRow : monthButtons) {
            for (JButton monthButton : buttonRow) {
                monthButton.setText(clone.getMonth()
						.getDisplayName(TextStyle.SHORT, getDefaultLocale()));
                monthButton.setActionCommand(clone.toString());

                if (clone.equals(activeDate)) {
                    activeDateButton = monthButton;
                    activeDateButton.setForeground(activeDateColor);
                } else {
                    monthButton.setForeground(getForeground());
                }

                clone = clone.plusMonths(1L);
            }
        }
	}

	private void fillMonthCalendar() {
		int yRef = activeDateRowIndex();
		int xRef = activeDate.get(ChronoField.DAY_OF_WEEK) - 1;

		LocalDate clone = activeDate.plusDays(-7L * yRef - xRef);

        for (JButton[] buttonRow : dateButtons)
            for (JButton dateButton : buttonRow) {
                dateButton.setText(String.valueOf(clone.getDayOfMonth()));
                dateButton.setActionCommand(clone.toString());

                if (clone.equals(selectedDate)) {
                    selectedDateButton = dateButton;
                    selectedDateButton.setForeground(selectedDateColor);
                } else if (clone.equals(activeDate)) {
                    activeDateButton = dateButton;
                    activeDateButton.setForeground(activeDateColor);
                } else if (clone.getMonth().equals(activeDate.getMonth())) {
                    dateButton.setForeground(getForeground());
                } else {
                    dateButton.setForeground(inactiveDateColor);
                }

                clone = clone.plusDays(1L);
            }
	}

	private int activeDateRowIndex() {
		LocalDate nextSunday = activeDate.with(ChronoField.DAY_OF_WEEK, 7);
		int nextSundayDays = nextSunday.getDayOfMonth();

		if (!nextSunday.getMonth().equals(activeDate.getMonth()))
			nextSundayDays += activeDate.getMonth().length(activeDate.isLeapYear());

		return (nextSundayDays - 1) / 7;
	}

	private JButton[][][] getButtonMatrices() {
		return new JButton[][][] { dateButtons, monthButtons, yearButtons, decadeButtons };
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
		private final JCalendar calendar;

		public ActiveDateSelection(JCalendar calendar) {
			this.calendar = calendar;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
            switch (calendar.activeView) {
                case CENTURY -> calendar.setActiveView(CalendarView.DECADE);
                case DECADE -> calendar.setActiveView(CalendarView.YEAR);
                case YEAR -> calendar.setActiveView(CalendarView.MONTH);
                default -> {
                    calendar.setSelectedDate(calendar.activeDate);
                    calendar.fillCalendar();
                }
            }
		}
	}

	protected enum TranslationField { PAGE, COLUMN, ROW }

	protected static class DateTranslation extends AbstractAction {
		private final JCalendar calendar;
		private final TranslationField field;
		private final int amount;

		public DateTranslation(JCalendar calendar, TranslationField field, int amount) {
			this.calendar = calendar;
			this.field = field;
			this.amount = amount;
		}

		public void actionPerformed(ActionEvent e) {
			calendar.requestFocusInWindow();
			LocalDate clone = calendar.activeDate;

            switch (field) {
				case PAGE -> clone = switch (calendar.activeView) {
                    case MONTH -> clone.plusMonths(amount);
                    case YEAR -> clone.plusYears(amount);
                    case DECADE -> clone.plusYears(10L * amount);
                    default -> clone.plusYears(100L * amount);
                };
				case COLUMN -> clone = switch (calendar.activeView) {
                    case MONTH -> clone.plusDays(amount);
                    case YEAR -> clone.plusMonths(amount);
                    case DECADE -> clone.plusYears(amount);
                    default -> clone.plusYears(10L * amount);
                };
                case ROW -> clone = switch (calendar.activeView) {
                    case MONTH -> clone.plusWeeks(amount);
                    case YEAR -> clone.plusMonths(4L * amount);
                    case DECADE -> clone.plusYears(4L * amount);
                    default -> clone.plusYears(40L * amount);
                };
                default -> {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }

			calendar.setActiveDate(clone);
		}
	}
}
