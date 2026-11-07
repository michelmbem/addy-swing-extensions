package org.addy.swing;

import org.addy.swing.imageeditor.CropOverlay;
import org.addy.util.media.FlipAxis;
import org.addy.util.media.ImageHelper;
import org.addy.util.media.RotationAngle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.Objects;
import java.util.ResourceBundle;

/** A small image editor with a {@link JPictureBox} preview and common image operations. */
public class JImageEditor extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final ResourceBundle STRINGS =
            ResourceBundle.getBundle(JImageEditor.class.getName());

    private final JPictureBox pictureBox = new JPictureBox();
    private final JLayeredPane previewLayer = new JLayeredPane() {
        @Override
        public void doLayout() {
            for (Component component : getComponents())
                component.setBounds(0, 0, getWidth(), getHeight());
        }
    };

    private CropOverlay cropOverlay;
    private JPanel toolBarPanel;
    private JToolBar standardToolBar;
    private JToolBar cropToolBar;
    private boolean cropMode;
    private ColorPickerOverlay colorPickerOverlay;

    private Image image;
    private Image originalImage;

    public JImageEditor() {
        this(null);
    }

    public JImageEditor(Image image) {
        super(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(6, 6, 6, 6));
        pictureBox.setSizeMode(SizeMode.CONTAIN);
        cropOverlay = new CropOverlay(pictureBox);
        previewLayer.add(pictureBox, JLayeredPane.DEFAULT_LAYER);
        previewLayer.add(cropOverlay, JLayeredPane.PALETTE_LAYER);
        add(previewLayer, BorderLayout.CENTER);
        standardToolBar = createToolBar();
        cropToolBar = createCropToolBar();
        toolBarPanel = new JPanel(new BorderLayout());
        toolBarPanel.add(standardToolBar, BorderLayout.CENTER);
        add(toolBarPanel, BorderLayout.SOUTH);
        installCropKeyBindings();
        setImage(image);
    }

    /** Returns the image currently being edited. */
    public Image getImage() {
        return image;
    }

    /** Sets the image and saves it as the image restored by {@link #reset()}. */
    public void setImage(Image image) {
        if (cropMode) finishCropSelection();
        if (colorPickerOverlay != null) finishColorPicker();
        Image oldImage = this.image;
        this.image = image;
        this.originalImage = image;
        pictureBox.setImage(image);
        firePropertyChange("image", oldImage, image);
    }

    /** Returns the preview component used to display the edited image. */
    public JPictureBox getPictureBox() {
        return pictureBox;
    }

    /** Restores the image that was most recently passed to {@link #setImage(Image)}. */
    public void reset() {
        apply(originalImage);
    }

    public void rotate90() { rotate(RotationAngle.CW); }
    public void rotate180() { rotate(RotationAngle.DCW); }
    public void rotate270() { rotate(RotationAngle.CCW); }

    public void rotate(RotationAngle angle) {
        requireImage();
        apply(ImageHelper.rotate(image, Objects.requireNonNull(angle)));
    }

    public void flipHorizontal() { flip(FlipAxis.Y); }
    public void flipVertical() { flip(FlipAxis.X); }

    public void flip(FlipAxis axis) {
        requireImage();
        apply(ImageHelper.flip(image, Objects.requireNonNull(axis)));
    }

    public void crop(Rectangle frame) {
        requireImage();
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        if (frame.x < 0 || frame.y < 0 || frame.width <= 0 || frame.height <= 0 ||
                frame.x + frame.width >= imageWidth || frame.y + frame.height >= imageHeight)
            throw new IllegalArgumentException(STRINGS.getString("error.cropBounds"));

        apply(ImageHelper.crop(image, frame));
    }

    public void convertToGrayscale() {
        requireImage();
        BufferedImage copy = ImageHelper.copy(
                ImageHelper.buffer(image, BufferedImage.TYPE_INT_ARGB));
        apply(ImageHelper.grayScale(copy));
    }

    public void convertToSepia() {
        requireImage();
        BufferedImage copy = ImageHelper.copy(
                ImageHelper.buffer(image, BufferedImage.TYPE_INT_ARGB));
        apply(ImageHelper.sepia(copy));
    }

    public void addBorder(Paint paint, Insets insets) {
        requireImage();
        apply(ImageHelper.addBorder(image, Objects.requireNonNull(paint), Objects.requireNonNull(insets)));
    }

    public void stripBorder(Color color) {
        requireImage();
        apply(ImageHelper.stripBorder(image, Objects.requireNonNull(color)));
    }

    private void apply(Image updatedImage) {
        Image oldImage = image;
        image = updatedImage;
        pictureBox.setImage(updatedImage);
        firePropertyChange("image", oldImage, updatedImage);
        previewLayer.doLayout();
    }

    private void requireImage() {
        if (image == null) throw new IllegalStateException(STRINGS.getString("error.noImage"));
    }

    private JToolBar createToolBar() {
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.add(button("crop", "crop", this::startCropSelection));
        tools.add(button("addBorder", "border", this::showBorderDialog));
        tools.add(button("stripBorder", "noBorder", this::showColorPicker));
        tools.addSeparator();
        tools.add(button("rotate90", "rotate90", this::rotate90));
        tools.add(button("rotate180", "rotate180", this::rotate180));
        tools.add(button("rotate270", "rotate270", this::rotate270));
        tools.addSeparator();
        tools.add(button("flipHorizontal", "flipH", this::flipHorizontal));
        tools.add(button("flipVertical", "flipV", this::flipVertical));
        tools.addSeparator();
        tools.add(button("grayscale", "grayscale", this::convertToGrayscale));
        tools.add(button("sepia", "sepia", this::convertToSepia));
        tools.addSeparator();
        tools.add(button("reset", "reset", this::reset));
        return tools;
    }

    private JButton button(String key, String iconName, Runnable action) {
        JButton button = new JButton();
        button.setIcon(loadIcon(iconName));
        button.setToolTipText(STRINGS.getString("button." + key));
        button.setFocusable(false);
        button.addActionListener(event -> {
            if (image == null) return;

            try {
                action.run();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        STRINGS.getString("dialog.errorTitle"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return button;
    }

    private void startCropSelection() {
        if (image == null || cropMode) return;
        cropMode = true;
        cropOverlay.beginSelection();
        toolBarPanel.removeAll();
        toolBarPanel.add(cropToolBar, BorderLayout.CENTER);
        toolBarPanel.revalidate();
        toolBarPanel.repaint();
        previewLayer.repaint();
    }

    private JToolBar createCropToolBar() {
        JButton squareButton = new JButton(loadIcon("square"));
        squareButton.setToolTipText(STRINGS.getString("button.squareCrop"));
        squareButton.addActionListener(event -> cropOverlay.makeSelectionSquare());

        JButton applyButton = new JButton(STRINGS.getString("button.applyCrop"));
        applyButton.setIcon(loadIcon("accept"));
        applyButton.setToolTipText(STRINGS.getString("button.applyCrop"));
        applyButton.addActionListener(event -> applyCropSelection());

        JButton cancelButton = new JButton(STRINGS.getString("button.cancelCrop"));
        cancelButton.setIcon(loadIcon("cancel"));
        cancelButton.setToolTipText(STRINGS.getString("button.cancelCrop"));
        cancelButton.addActionListener(event -> cancelCropSelection());

        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.add(squareButton);
        tools.addSeparator();
        tools.add(applyButton);
        tools.add(cancelButton);
        return tools;
    }
    
    private ImageIcon loadIcon(String iconName) {
        return new ImageIcon(Objects.requireNonNull(
                getClass().getResource("imageeditor/" + iconName + ".png")),
                STRINGS.getString("error.missingIcon") + iconName);
    }

    private void installCropKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "applyCrop");
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancelCrop");
        actionMap.put("applyCrop", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (cropMode) applyCropSelection();
            }
        });
        actionMap.put("cancelCrop", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (cropMode) cancelCropSelection();
            }
        });
    }

    private void applyCropSelection() {
        if (!cropMode) return;
        Rectangle cropBounds = cropOverlay.getSelectedCropBounds();
        if (cropBounds != null) crop(cropBounds);
        finishCropSelection();
    }

    private void cancelCropSelection() {
        if (cropMode) finishCropSelection();
    }

    private void finishCropSelection() {
        cropMode = false;
        cropOverlay.setVisible(false);
        toolBarPanel.removeAll();
        toolBarPanel.add(standardToolBar, BorderLayout.CENTER);
        toolBarPanel.revalidate();
        toolBarPanel.repaint();
        previewLayer.repaint();
    }

    private void showBorderDialog() {
        final int iconWidth = 72;
        final int iconHeight = 18;
        Color[] borderColor = {Color.BLACK};
        JButton colorButton = new JButton(new ColorSwatchIcon(borderColor[0], iconWidth, iconHeight));
        colorButton.setPreferredSize(new Dimension(56, 30));
        colorButton.setToolTipText(STRINGS.getString("label.borderColor"));
        colorButton.getAccessibleContext().setAccessibleName(STRINGS.getString("label.borderColor"));
        colorButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(
                    this,
                    STRINGS.getString("dialog.borderColorTitle"),
                    borderColor[0]);

            if (selectedColor != null) {
                borderColor[0] = selectedColor;
                colorButton.setIcon(new ColorSwatchIcon(selectedColor, iconWidth, iconHeight));
            }
        });

        JSpinner thickness = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        JPanel fields = new JPanel(new GridLayout(2, 2, 8, 8));
        fields.add(new JLabel(STRINGS.getString("label.borderColor")));
        fields.add(colorButton);
        fields.add(new JLabel(STRINGS.getString("label.borderThickness")));
        fields.add(thickness);

        if (JOptionPane.showConfirmDialog(this, fields,
                STRINGS.getString("dialog.borderTitle"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,null
        ) == JOptionPane.OK_OPTION) {
            int size = (Integer) thickness.getValue();
            addBorder(borderColor[0], new Insets(size, size, size, size));
        }
    }

    private void showColorPicker() {
        if (image == null || colorPickerOverlay != null || cropMode) return;
        colorPickerOverlay = new ColorPickerOverlay();
        previewLayer.add(colorPickerOverlay, JLayeredPane.MODAL_LAYER);
        previewLayer.setLayer(colorPickerOverlay, JLayeredPane.MODAL_LAYER);
        colorPickerOverlay.setCursor(createHiddenCursor());
        previewLayer.doLayout();
        colorPickerOverlay.requestFocusInWindow();
        colorPickerOverlay.repaint();
    }

    private void finishColorPicker() {
        if (colorPickerOverlay == null) return;
        previewLayer.remove(colorPickerOverlay);
        colorPickerOverlay = null;
        previewLayer.setCursor(Cursor.getDefaultCursor());
        previewLayer.revalidate();
        previewLayer.repaint();
    }

    private Cursor createHiddenCursor() {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension size = toolkit.getBestCursorSize(16, 16);
            if (size.width == 0 || size.height == 0) return Cursor.getDefaultCursor();

            BufferedImage cursorImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            return toolkit.createCustomCursor(cursorImage, new Point(0, 0), "hidden-color-picker");
        } catch (HeadlessException | IndexOutOfBoundsException exception) {
            return Cursor.getDefaultCursor();
        }
    }

    private final class ColorPickerOverlay extends JComponent {
        @Serial
        private static final long serialVersionUID = 1L;

        private Point pointer;

        private ColorPickerOverlay() {
            setOpaque(false);
            setFocusable(true);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    pointer = e.getPoint();
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    mouseMoved(e);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pointer = e.getPoint();
                    repaint();
                    if (e.getButton() != MouseEvent.BUTTON1) return;
                    Point pixel = imagePixelAt(pointer);
                    if (pixel == null) return;
                    int rgb = ImageHelper.buffer(image, BufferedImage.TYPE_INT_RGB)
                            .getRGB(pixel.x, pixel.y);
                    finishColorPicker();
                    stripBorder(new Color(rgb));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    pointer = null;
                    repaint();
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
                    .getKeyStroke("ESCAPE"), "cancelColorPicker");
            getActionMap().put("cancelColorPicker", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    finishColorPicker();
                }
            });
        }

        private Point imagePixelAt(Point point) {
            if (image == null) return null;

            Insets insets = pictureBox.getInsets();
            int areaWidth = pictureBox.getWidth() - insets.left - insets.right;
            int areaHeight = pictureBox.getHeight() - insets.top - insets.bottom;

            int imageWidth = image.getWidth(pictureBox);
            int imageHeight = image.getHeight(pictureBox);
            if (areaWidth <= 0 || areaHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) return null;

            double scale = Math.min((double) areaWidth / imageWidth, (double) areaHeight / imageHeight);
            int drawnWidth = (int) (imageWidth * scale);
            int drawnHeight = (int) (imageHeight * scale);
            int left = insets.left + (areaWidth - drawnWidth) / 2;
            int top = insets.top + (areaHeight - drawnHeight) / 2;
            int x = point.x - left;
            int y = point.y - top;
            if (x < 0 || y < 0 || x >= drawnWidth || y >= drawnHeight) return null;

            return new Point(x * imageWidth / drawnWidth, y * imageHeight / drawnHeight);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (pointer == null) return;

            int x = pointer.x;
            int y = pointer.y;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(Color.WHITE);
            g.drawLine(x, y, x + 9, y - 9);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(Color.DARK_GRAY);
            g.drawLine(x, y, x + 9, y - 9);
            g.setColor(new Color(215, 215, 215));
            g.fillRoundRect(x + 7, y - 13, 9, 6, 3, 3);
            g.setColor(Color.DARK_GRAY);
            g.drawRoundRect(x + 7, y - 13, 9, 6, 3, 3);
            g.dispose();
        }
    }

}
