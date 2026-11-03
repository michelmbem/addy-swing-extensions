package org.addy.swing;

import org.addy.swing.imageeditor.CropOverlay;
import org.addy.util.media.FlipAxis;
import org.addy.util.media.ImageHelper;
import org.addy.util.media.RotationAngle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    public void flipHorizontal() { flip(FlipAxis.X); }
    public void flipVertical() { flip(FlipAxis.Y); }

    public void flip(FlipAxis axis) {
        requireImage();
        apply(ImageHelper.flip(image, Objects.requireNonNull(axis)));
    }

    public void crop(int left, int top, int right, int bottom) {
        requireImage();
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
                (long) left + right >= imageWidth || (long) top + bottom >= imageHeight)
            throw new IllegalArgumentException(STRINGS.getString("error.cropBounds"));

        apply(ImageHelper.crop(image, left, top, right, bottom));
    }

    public void convertToGrayscale() {
        requireImage();
        BufferedImage buffered = ImageHelper.buffer(image, BufferedImage.TYPE_INT_ARGB);
        BufferedImage copy = new BufferedImage(
                buffered.getColorModel(),
                buffered.copyData(null),
                buffered.isAlphaPremultiplied(),
                null);
        apply(ImageHelper.grayScale(copy));
    }

    public void addBorder(Paint paint, Insets insets) {
        requireImage();
        apply(ImageHelper.addBorder(image, Objects.requireNonNull(paint), Objects.requireNonNull(insets)));
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
        tools.add(button("crop", "image-editor-crop.png", this::startCropSelection));
        tools.add(button("rotate90", "image-editor-rotate90.png", this::rotate90));
        tools.add(button("rotate180", "image-editor-rotate180.png", this::rotate180));
        tools.add(button("rotate270", "image-editor-rotate270.png", this::rotate270));
        tools.add(button("flipHorizontal", "image-editor-flipH.png", this::flipHorizontal));
        tools.add(button("flipVertical", "image-editor-flipV.png", this::flipVertical));
        tools.add(button("grayscale", "image-editor-grayscale.png", this::convertToGrayscale));
        tools.add(button("border", "image-editor-border.png", this::showBorderDialog));
        tools.add(button("reset", "image-editor-reset.png", this::reset));
        return tools;
    }

    private JButton button(String key, String iconName, Runnable action) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(Objects.requireNonNull(
                JImageEditor.class.getResource(iconName),
                STRINGS.getString("error.missingIcon") + iconName)));
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
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        JButton applyButton = new JButton(STRINGS.getString("button.applyCrop"));
        applyButton.setToolTipText(STRINGS.getString("button.applyCrop"));
        applyButton.addActionListener(event -> applyCropSelection());
        JButton cancelButton = new JButton(STRINGS.getString("button.cancelCrop"));
        cancelButton.setToolTipText(STRINGS.getString("button.cancelCrop"));
        cancelButton.addActionListener(event -> cancelCropSelection());
        tools.add(applyButton);
        tools.add(cancelButton);
        return tools;
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
        if (cropBounds != null) {
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);
            crop(cropBounds.x, cropBounds.y,
                    imageWidth - cropBounds.x - cropBounds.width,
                    imageHeight - cropBounds.y - cropBounds.height);
        }
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

        if (JOptionPane.showConfirmDialog(
                this,
                fields,
                STRINGS.getString("dialog.borderTitle"),
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int size = (Integer) thickness.getValue();
            addBorder(borderColor[0], new Insets(size, size, size, size));
        }
    }

    private static final class ColorSwatchIcon implements Icon {
        private final Color color;
        private final int width;
        private final int height;

        private ColorSwatchIcon(Color color, int width, int height) {
            this.color = Objects.requireNonNull(color);
            this.width = Math.max(width, 8);
            this.height = Math.max(height, 8);
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            double luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255.0;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(color);
            g.fillRect(x + 2, y + 2, width - 4, height - 4);
            g.setColor(luminance < 0.5 ? Color.WHITE : Color.BLACK);
            g.drawRect(x + 2, y + 2, width - 5, height - 5);
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
