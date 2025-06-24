package org.addy.swing;

import org.addy.swing.input.InputValidator;
import org.addy.swing.input.PredicateValidationRule;
import org.addy.swing.input.ValidationBorders;
import org.addy.swing.input.ValidationPredicates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.time.LocalDateTime;

public class Demo {
	public static final String BROWSE = "<browse...>";
	static final String[] pictures = new String[] { "alicia", "ashanti", "jlo", "jlo-back", "mariah", "toni", BROWSE};

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.printf("Unable to load the '%s' look-and-feel%n", UIManager.getSystemLookAndFeelClassName());
		}

		createDemoFrame().setVisible(true);
	}

	private static JFrame createDemoFrame() {
		var frame = new JFrame("Addy Swing Demo");
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		var tabPane = new JTabbedPane(SwingConstants.TOP);
		tabPane.addTab("PictureBox", createPictureBoxPanel(frame));
		tabPane.addTab("Calendar", createCalendarPanel());
		tabPane.addTab("Validation", createValidationPanel());
		frame.setContentPane(tabPane);

		return frame;
	}

	private static JPanel createPictureBoxPanel(JFrame frame) {
		var pictureBoxPanel = new JPanel(new BorderLayout());

		var topPane = createTopPanel();
		pictureBoxPanel.add(topPane, BorderLayout.PAGE_START);

		var pictureBox = new JPictureBox();
		pictureBox.setBackground(Color.WHITE);
		pictureBox.setImageSource(embeddedImage(pictures[0]));
		pictureBox.setSizeMode(SizeMode.NORMAL);
		pictureBoxPanel.add(new JScrollPane(pictureBox), BorderLayout.CENTER);

		var imageCombo = new JComboBox<String>();
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

		var sizeCombo = new JComboBox<SizeMode>();
		sizeCombo.setModel(new SimpleComboBoxModel<>(SizeMode.values()));
		sizeCombo.setEditable(false);
		sizeCombo.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		sizeCombo.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				pictureBox.setSizeMode((SizeMode) e.getItem());
		});

		topPane.add(new JLabel(" Image:"));
		topPane.add(Box.createRigidArea(new Dimension(5, 0)));
		topPane.add(imageCombo);
		topPane.add(Box.createHorizontalGlue());
		topPane.add(new JLabel("Sizing mode:"));
		topPane.add(Box.createRigidArea(new Dimension(5, 0)));
		topPane.add(sizeCombo);

		return pictureBoxPanel;
	}

	private static JPanel createCalendarPanel() {
		var calendarPanel = new JPanel(new BorderLayout());

		var topPane = createTopPanel();
		calendarPanel.add(topPane, BorderLayout.PAGE_START);

		var calendar = new JCalendar();
		calendarPanel.add(calendar, BorderLayout.CENTER);

		var calendarCombo = new JCalendarCombo();
		calendarCombo.setCheckBoxVisible(true);
		calendarCombo.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		calendarCombo.addPropertyChangeListener("dateTime",
				e -> calendar.setSelectedDateTime((LocalDateTime) e.getNewValue()));

		topPane.add(Box.createHorizontalGlue());
		topPane.add(new JLabel("Calendar Combo:"));
		topPane.add(Box.createRigidArea(new Dimension(5, 0)));
		topPane.add(calendarCombo);
		topPane.add(Box.createHorizontalGlue());

		return calendarPanel;
	}

	private static JPanel createValidationPanel() {
		var validationPanel = new JPanel(new GridBagLayout());
        var fieldSize = new Dimension(250, 24);

		var firstNameField = new JTextField();
		UIHelper.setFixedSized(firstNameField, fieldSize);
		UIHelper.setMaxLength(firstNameField, 45);
		firstNameField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
				ValidationPredicates.notEmpty(),
				ValidationBorders.ICON_DANGER,
				"The first name cannot be empty"
		)));

		var lastNameField = new JTextField();
        UIHelper.setFixedSized(lastNameField, fieldSize);
		UIHelper.setMaxLength(lastNameField, 45);
		lastNameField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
				ValidationPredicates.notEmpty(),
				ValidationBorders.ICON_DANGER,
				"The last name cannot be empty"
		)));

		var genderCombo = new JComboBox<String>();
		genderCombo.setModel(new SimpleComboBoxModel<>("", "Male", "Female"));
		genderCombo.setEditable(false);
        UIHelper.setFixedSized(genderCombo, fieldSize);
		genderCombo.setInputVerifier(new InputValidator<>(
                new PredicateValidationRule<>(
                    ValidationPredicates.notEmpty(),
                    ValidationBorders.ICON_DANGER,
                    "The gender is required"),
                c -> ((JComboBox<?>) c).getSelectedItem().toString()));

		var ageSpinner = new JSpinner(new SpinnerNumberModel());
        UIHelper.setFixedSized(ageSpinner, fieldSize);
		ageSpinner.setInputVerifier(new InputValidator<>(
				new PredicateValidationRule<>(
					ValidationPredicates.range(15, 35),
					ValidationBorders.ICON_DANGER,
					"The age should range from 15 to 35"),
				c -> (Integer) ((JSpinner) c).getValue()));

        var jobTitleField = new JTextField();
        UIHelper.setFixedSized(jobTitleField, fieldSize);
        UIHelper.setMaxLength(jobTitleField, 80);
        jobTitleField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
                ValidationPredicates.notEmpty(),
                ValidationBorders.ICON_DANGER,
                "The job title cannot be empty"
        )));

        var companyField = new JTextField();
        UIHelper.setFixedSized(companyField, fieldSize);
        UIHelper.setMaxLength(companyField, 45);
        companyField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
                ValidationPredicates.notEmpty(),
                ValidationBorders.ICON_DANGER,
                "The company name cannot be empty"
        )));

        var emailField = new JTextField();
        UIHelper.setFixedSized(emailField, fieldSize);
        UIHelper.setMaxLength(emailField, 50);
        emailField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
                ValidationPredicates.pattern("^\\w+@\\w+\\.\\w+$"),
                ValidationBorders.ICON_DANGER,
                "A valid email address is required"
        )));

        var phoneField = new JTextField();
        UIHelper.setFixedSized(phoneField, fieldSize);
        UIHelper.setMaxLength(phoneField, 15);
        phoneField.setInputVerifier(InputValidator.ofText(new PredicateValidationRule<>(
                ValidationPredicates.pattern("^\\d+$"),
                ValidationBorders.ICON_DANGER,
                "A valid phone number is required"
        )));

		validationPanel.add(new JLabel("First name:"), gbc(0, 0));
		validationPanel.add(firstNameField, gbc(0, 1));
		validationPanel.add(new JLabel("Last name:"), gbc(1, 0));
		validationPanel.add(lastNameField, gbc(1, 1));
		validationPanel.add(new JLabel("Gender:"), gbc(0, 2));
		validationPanel.add(genderCombo, gbc(0, 3));
		validationPanel.add(new JLabel("Age:"), gbc(1, 2));
		validationPanel.add(ageSpinner, gbc(1, 3));
        validationPanel.add(new JLabel("Job title:"), gbc(0, 4));
        validationPanel.add(jobTitleField, gbc(0, 5));
        validationPanel.add(new JLabel("Company:"), gbc(1, 4));
        validationPanel.add(companyField, gbc(1, 5));
        validationPanel.add(new JLabel("Email:"), gbc(0, 6));
        validationPanel.add(emailField, gbc(0, 7));
        validationPanel.add(new JLabel("Phone:"), gbc(1, 6));
        validationPanel.add(phoneField, gbc(1, 7));

		return validationPanel;
	}

	private static JPanel createTopPanel() {
		var panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		return panel;
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

    private static GridBagConstraints gbc(int x, int y) {
        var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.BASELINE_LEADING;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = y % 2 == 0
                ? new Insets(2, 10, 1, 5)
                :new Insets(1, 5, 5, 5);

        return gbc;
    }
}
