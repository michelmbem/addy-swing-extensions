package org.addy.swing;

import org.addy.swing.input.InputValidator;
import org.addy.swing.input.PredicateValidationRule;
import org.addy.swing.input.ValidationBorders;
import org.addy.swing.input.ValidationPredicates;
import org.addy.swing.input.Validator;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.text.ParseException;
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

        var titleLabel = new JLabel("Contact Info");
        titleLabel.setForeground(KnownColor.NAVY);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

		var firstNameField = new JTextField();
		UIHelper.setFixedSized(firstNameField, fieldSize);
		UIHelper.setMaxLength(firstNameField, 20);
		firstNameField.setInputVerifier(InputValidator.ofText(
                new PredicateValidationRule<>(
                    ValidationPredicates.notBlank(),
                    ValidationBorders.ICON_DANGER,
                    "The first name cannot be empty")));

		var lastNameField = new JTextField();
        UIHelper.setFixedSized(lastNameField, fieldSize);
		UIHelper.setMaxLength(lastNameField, 20);
		lastNameField.setInputVerifier(InputValidator.ofText(
                new PredicateValidationRule<>(
                    ValidationPredicates.notBlank(),
                    ValidationBorders.ICON_DANGER,
                    "The last name cannot be empty")));

		var genderCombo = new JComboBox<String>();
		genderCombo.setModel(new SimpleComboBoxModel<>("", "Male", "Female"));
		genderCombo.setEditable(false);
        UIHelper.setFixedSized(genderCombo, fieldSize);
		genderCombo.setInputVerifier(new InputValidator<>(
                new PredicateValidationRule<>(
                    ValidationPredicates.notBlank(),
                    ValidationBorders.ICON_DANGER,
                    "The gender is required"),
                c -> ((JComboBox<?>) c).getSelectedItem().toString()));

		var ageSpinner = new JSpinner(new SpinnerNumberModel());
        UIHelper.setFixedSized(ageSpinner, fieldSize);
        ageSpinner.setInputVerifier(new InputValidator<>(
                new PredicateValidationRule<>(
                        ValidationPredicates.range(5, 75),
                        ValidationBorders.ICON_DANGER,
                        "The age should range from 5 to 75"),
                c -> (int) ((JSpinner) c).getValue()));

        var jobTitleField = new JTextField();
        UIHelper.setFixedSized(jobTitleField, fieldSize);
        UIHelper.setMaxLength(jobTitleField, 40);
        jobTitleField.setInputVerifier(InputValidator.ofText(
                new PredicateValidationRule<>(
                    ValidationPredicates.notBlank(),
                    ValidationBorders.ICON_DANGER,
                    "The job title cannot be empty")));

        var companyField = new JTextField();
        UIHelper.setFixedSized(companyField, fieldSize);
        UIHelper.setMaxLength(companyField, 40);
        companyField.setInputVerifier(InputValidator.ofText(
                new PredicateValidationRule<>(
                    ValidationPredicates.notBlank(),
                    ValidationBorders.ICON_DANGER,
                    "The company name cannot be empty")));

        var emailField = new JTextField();
        UIHelper.setFixedSized(emailField, fieldSize);
        UIHelper.setMaxLength(emailField, 50);
        emailField.setInputVerifier(InputValidator.ofText(
                new PredicateValidationRule<>(
                    ValidationPredicates.pattern("^\\w+@\\w+\\.\\w+$"),
                    ValidationBorders.ICON_DANGER,
                    "A valid email address is required")));

        var phoneField = new JFormattedTextField(createMaskFormatter("(###) ###-####"));
        UIHelper.setFixedSized(phoneField, fieldSize);
        phoneField.setInputVerifier(new InputValidator<>(
                new PredicateValidationRule<>(
                    JFormattedTextField::isEditValid,
                    ValidationBorders.ICON_DANGER,
                    "A valid phone number is required"),
                c -> (JFormattedTextField) c));

        var bottomPane = new JPanel(new FlowLayout(FlowLayout.CENTER));

        var submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            if (Validator.validate(validationPanel))
                JOptionPane.showMessageDialog(validationPanel, "Successfully submitted!");
            else
                Toolkit.getDefaultToolkit().beep();
        });
        bottomPane.add(submitButton);

        var resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            Validator.restore(validationPanel);
            firstNameField.setText("");
            lastNameField.setText("");
            genderCombo.setSelectedIndex(0);
            genderCombo.repaint();
            ageSpinner.setValue(0);
            jobTitleField.setText("");
            companyField.setText("");
            emailField.setText("");
            phoneField.setText("");
        });
        bottomPane.add(resetButton);

        validationPanel.add(titleLabel, gbc(0, 0, 2, 1, GridBagConstraints.CENTER, 2));
		validationPanel.add(new JLabel("First name:"), gbc(0, 1));
		validationPanel.add(firstNameField, gbc(0, 2));
		validationPanel.add(new JLabel("Last name:"), gbc(1, 1));
		validationPanel.add(lastNameField, gbc(1, 2));
		validationPanel.add(new JLabel("Gender:"), gbc(0, 3));
		validationPanel.add(genderCombo, gbc(0, 4));
		validationPanel.add(new JLabel("Age:"), gbc(1, 3));
		validationPanel.add(ageSpinner, gbc(1, 4));
        validationPanel.add(new JLabel("Job title:"), gbc(0, 5));
        validationPanel.add(jobTitleField, gbc(0, 6));
        validationPanel.add(new JLabel("Company:"), gbc(1, 5));
        validationPanel.add(companyField, gbc(1, 6));
        validationPanel.add(new JLabel("Email address:"), gbc(0, 7));
        validationPanel.add(emailField, gbc(0, 8));
        validationPanel.add(new JLabel("Phone number:"), gbc(1, 7));
        validationPanel.add(phoneField, gbc(1, 8));
        validationPanel.add(bottomPane, gbc(0, 9, 2, 1, GridBagConstraints.CENTER, 2));

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

    private static MaskFormatter createMaskFormatter(String mask) {
        MaskFormatter formatter = null;

        try {
            formatter = new MaskFormatter(mask);
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatter;
    }

    private static GridBagConstraints gbc(int x, int y, int w, int h, int a, int i) {
        var gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.anchor = a;
        gbc.insets = switch (i) {
            case 0 -> new Insets(2, 10, 1, 5);
            case 1 -> new Insets(1, 5, 5, 5);
            default -> new Insets(5, 5, 5, 5);
        };

        return gbc;
    }

    private static GridBagConstraints gbc(int x, int y) {
        return gbc(x, y, 1, 1, GridBagConstraints.BASELINE_LEADING, (y - 1) % 2 );
    }
}
