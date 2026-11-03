package org.addy.swing;

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

    private Image image;
    private Image originalImage;

    public JImageEditor() {
        this(null);
    }

    public JImageEditor(Image image) {
        super(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(6, 6, 6, 6));
        pictureBox.setSizeMode(SizeMode.CONTAIN);
        add(pictureBox, BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.SOUTH);
        setImage(image);
    }

    /** Returns the image currently being edited. */
    public Image getImage() {
        return image;
    }

    /** Sets the image and saves it as the image restored by {@link #reset()}. */
    public void setImage(Image image) {
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
        doLayout();
    }

    private void requireImage() {
        if (image == null) throw new IllegalStateException(STRINGS.getString("error.noImage"));
    }

    private JToolBar createToolBar() {
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tools.add(button("crop", "image-editor-crop.png", this::showCropDialog));
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

    private void showCropDialog() {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        JSpinner left = new JSpinner(new SpinnerNumberModel(0, 0, width - 1, 1));
        JSpinner top = new JSpinner(new SpinnerNumberModel(0, 0, height - 1, 1));
        JSpinner right = new JSpinner(new SpinnerNumberModel(0, 0, width - 1, 1));
        JSpinner bottom = new JSpinner(new SpinnerNumberModel(0, 0, height - 1, 1));
        JPanel fields = new JPanel(new GridLayout(0, 2, 6, 6));
        fields.add(new JLabel(STRINGS.getString("crop.left"))); fields.add(left);
        fields.add(new JLabel(STRINGS.getString("crop.top"))); fields.add(top);
        fields.add(new JLabel(STRINGS.getString("crop.right"))); fields.add(right);
        fields.add(new JLabel(STRINGS.getString("crop.bottom"))); fields.add(bottom);

        if (JOptionPane.showConfirmDialog(
                this,
                fields,
                STRINGS.getString("dialog.cropTitle"),
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            crop((int) left.getValue(), (int) top.getValue(),
                    (int) right.getValue(), (int) bottom.getValue());
        }
    }

    private void showBorderDialog() {
        Color color = JColorChooser.showDialog(
                this,
                STRINGS.getString("dialog.borderColorTitle"),
                Color.BLACK);

        if (color == null) return;

        JSpinner thickness = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        if (JOptionPane.showConfirmDialog(
                this,
                thickness,
                STRINGS.getString("dialog.borderThicknessTitle"),
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            int size = (Integer) thickness.getValue();
            addBorder(color, new Insets(size, size, size, size));
        }
    }
}
