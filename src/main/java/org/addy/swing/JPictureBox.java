package org.addy.swing;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Objects;

public class JPictureBox extends JPanel {
	@Serial
	private static final long serialVersionUID = 1L;

	private Object imageSource;
	private Image image;
	private SizeMode sizeMode;

	public JPictureBox() {
		this(null, SizeMode.NORMAL);
	}

	public JPictureBox(Image image) {
		this(image, SizeMode.NORMAL);
	}

	public JPictureBox(Image image, SizeMode sizeMode) {
		setImage(image);
		setSizeMode(sizeMode);
	}

	public Object getImageSource() {
		return imageSource;
	}

	public void setImageSource(Object imageSource) {
        if (Objects.equals(imageSource, this.imageSource)) return;
        Object oldImageSource = this.imageSource;
		this.imageSource = imageSource;
		setImage(extractImage(imageSource));
		firePropertyChange("imageSource", oldImageSource, imageSource);
    }

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		if (Objects.equals(image, this.image)) return;
        Image oldImage = this.image;
        this.image = image;
        updateComponent();
        firePropertyChange("image", oldImage, image);
    }

	public SizeMode getSizeMode() {
		return sizeMode;
	}

	public void setSizeMode(SizeMode sizeMode) {
        if (Objects.equals(this.sizeMode, sizeMode)) return;
        SizeMode oldSizeMode = this.sizeMode;
        this.sizeMode = Objects.requireNonNull(sizeMode);
        updateComponent();
        firePropertyChange("sizeMode", oldSizeMode, sizeMode);
    }

	@Override
	public Dimension getPreferredSize() {
		if (image == null || sizeMode != SizeMode.AUTO)
			return super.getPreferredSize();

		Insets borderInsets = getBorderInsets();
		return new Dimension(
				image.getWidth(this) + borderInsets.left + borderInsets.right,
				image.getHeight(this) + borderInsets.top + borderInsets.bottom);
	}

	@Override
	public Dimension getMinimumSize() {
        return sizeMode == SizeMode.AUTO ? getPreferredSize() : super.getMinimumSize();
    }

	@Override
	public Dimension getMaximumSize() {
        return sizeMode == SizeMode.AUTO ? getPreferredSize() : super.getMaximumSize();
    }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        if (image == null) return;

        Insets borderInsets = getBorderInsets();
        switch (sizeMode) {
            case NORMAL -> paintImage(g, borderInsets);
            case AUTO -> g.drawImage(image, borderInsets.left, borderInsets.top, this);
            case CENTER -> paintCenteredImage(g, borderInsets);
            case STRETCH -> g.drawImage(
                    image,
                    borderInsets.left,
                    borderInsets.top,
                    getWidth() - borderInsets.right - borderInsets.left,
                    getHeight() - borderInsets.bottom - borderInsets.top,
                    this);
            case CONTAIN -> paintContainedImage(g, borderInsets);
            case COVER -> paintCoverImage(g, borderInsets);
        }
    }

	protected final void updateComponent() {
		setSize(getPreferredSize());
		repaint();
	}

	protected final Insets getBorderInsets() {
		Border border = getBorder();
        return border != null
                ? border.getBorderInsets(this)
                : new Insets(0, 0, 0, 0);
	}

	protected final void paintImage(Graphics g, Insets insets) {
		int width = Math.min(getWidth() - insets.left - insets.right, image.getWidth(this));
		int height = Math.min(getHeight() - insets.top - insets.bottom, image.getHeight(this));
		g.drawImage(image, insets.left, insets.top, insets.left + width, insets.top + height,
				0, 0, width, height, this);
	}

	protected final void paintCenteredImage(Graphics g, Insets insets) {
		int left = insets.left;
		int top = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		int x = 0;
		int y = 0;

		int dx = (image.getWidth(this) - width) / 2;
		int w;
		if (dx < 0) {
			w = image.getWidth(this);
			left -= dx;
		} else {
			w = width;
			x += dx;
		}

		int dy = (image.getHeight(this) - height) / 2;
		int h;
		if (dy < 0) {
			h = image.getHeight(this);
			top -= dy;
		} else {
			h = height;
			y += dy;
		}

		g.drawImage(image, left, top, left + w, top + h, x, y, x + w, y + h, this);
	}

	protected final void paintContainedImage(Graphics g, Insets insets) {
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		double wRatio = (double) image.getWidth(this) / width;
		double hRatio = (double) image.getHeight(this) / height;
		double ratio = (double) image.getWidth(this) / image.getHeight(this);

		int w;
		int h;
		if (wRatio >= hRatio) {
			w = width;
			h = (int) (w / ratio);
		} else {
			h = height;
			w = (int) (h * ratio);
		}

		g.drawImage(image, insets.left + (width - w) / 2, insets.top + (height - h) / 2, w, h, this);
	}

	protected final void paintCoverImage(Graphics g, Insets insets) {
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		double wRatio = (double) image.getWidth(this) / width;
		double hRatio = (double) image.getHeight(this) / height;
		double ratio = (double) image.getWidth(this) / image.getHeight(this);
		int x = 0;
		int y = 0;

		int w;
		int h;
		if (wRatio >= hRatio) {
			h = image.getHeight(this);
			w = (int) (image.getWidth(this) * width / (height * ratio));
			x = (image.getWidth(this) - w) / 2;
		} else {
			w = image.getWidth(this);
			h = (int) (image.getHeight(this) * height / (width / ratio));
			y = (image.getHeight(this) - h) / 2;
		}

		g.drawImage(image, insets.left, insets.top, insets.left + width, insets.top + height,
				x, y, x + w, y + h, this);
	}

	private static Image extractImage(Object source) {
		if (source == null) return null;
		if (source instanceof Image img) return img;
		if (source instanceof ImageIcon icon) return icon.getImage();

		try {
			if (source instanceof InputStream is) return ImageIO.read(is);
			if (source instanceof File file) return ImageIO.read(file);
			if (source instanceof URL url) return ImageIO.read(url);
			if (source instanceof byte[] bytes)
				return ImageIO.read(new ByteArrayInputStream(bytes));
			if (source instanceof CharSequence)
				return ImageIO.read(new FileInputStream(source.toString()));
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not extract an image from the given source", e);
		}

		return extractImageByIntrospection(source);
	}

	private static Image extractImageByIntrospection(Object source) {
		try {
			Method getImageMethod = source.getClass().getMethod("getImage");
			int modifiers = getImageMethod.getModifiers();
			if (Modifier.isPublic(modifiers) &&
					!Modifier.isStatic(modifiers) &&
					Image.class.isAssignableFrom(getImageMethod.getReturnType()))
				return (Image) getImageMethod.invoke(source);
		} catch (NoSuchMethodException |
				 IllegalAccessException |
				 InvocationTargetException ignore) {
		}

		return null;
	}
}
