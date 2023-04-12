package org.addy.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.JPanel;

public class JPictureBox extends JPanel {
	private static final long serialVersionUID = 1L;

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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		if (image != this.image) {
			Image oldImage = this.image;
			this.image = image;
			setSize(getPreferredSize());
			repaint();
			firePropertyChange("image", oldImage, image);
		}
	}

	public SizeMode getSizeMode() {
		return sizeMode;
	}

	public void setSizeMode(SizeMode sizeMode) {
		if (!Objects.equals(this.sizeMode, sizeMode)) {
			SizeMode oldSizeMode = this.sizeMode;
			this.sizeMode = Objects.requireNonNull(sizeMode);
			setSize(getPreferredSize());
			repaint();
			firePropertyChange("sizeMode", oldSizeMode, sizeMode);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (image == null || !sizeMode.equals(SizeMode.AUTO))
			return super.getPreferredSize();

		Insets borderInsets = getBorderInsets();
		return new Dimension(
				image.getWidth(this) + borderInsets.left + borderInsets.right,
				image.getHeight(this) + borderInsets.top + borderInsets.bottom);
	}

	@Override
	public Dimension getMinimumSize() {
		if (sizeMode.equals(SizeMode.AUTO)) return getPreferredSize();

		return super.getMinimumSize();
	}

	@Override
	public Dimension getMaximumSize() {
		if (sizeMode.equals(SizeMode.AUTO)) return getPreferredSize();

		return super.getMaximumSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			Insets borderInsets = getBorderInsets();

			switch (sizeMode) {
				case AUTO:
					g.drawImage(image, borderInsets.left, borderInsets.top, this);
					break;
				case CENTER:
					paintCenteredImage(g, borderInsets);
					break;
				case STRETCH:
					g.drawImage(
							image,
							borderInsets.left,
							borderInsets.top,
							getWidth() - borderInsets.right - borderInsets.left,
							getHeight() - borderInsets.bottom - borderInsets.top,
							this);
					break;
				case CONTAIN:
					paintContainedImage(g, borderInsets);
					break;
				case COVER:
					paintConverImage(g, borderInsets);
					break;
				default:
					paintImage(g, borderInsets);
					break;
			}
		}
	}

	protected final Insets getBorderInsets() {
		Insets insets = new Insets(0, 0, 0, 0);
		if (getBorder() != null) insets = getBorder().getBorderInsets(this);
		return insets;
	}

	protected final void paintImage(Graphics g, Insets borderInsets) {
		int width = Math.min(getWidth() - borderInsets.left - borderInsets.right, image.getWidth(this));
		int height = Math.min(getHeight() - borderInsets.top - borderInsets.bottom, image.getHeight(this));
		g.drawImage(image, borderInsets.left, borderInsets.top, borderInsets.left + width, borderInsets.top + height, 0, 0, width, height, this);
	}

	protected final void paintCenteredImage(Graphics g, Insets borderInsets) {
		int left = borderInsets.left;
		int top = borderInsets.top;
		int width = getWidth() - borderInsets.left - borderInsets.right;
		int height = getHeight() - borderInsets.top - borderInsets.bottom;
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

	protected final void paintContainedImage(Graphics g, Insets borderInsets) {
		int width = getWidth() - borderInsets.left - borderInsets.right;
		int height = getHeight() - borderInsets.top - borderInsets.bottom;
		double wRatio = (double) image.getWidth(this) / width;
		double hRatio = (double) image.getHeight(this) / height;
		double ratio = (double) image.getWidth(this) / image.getHeight(this);

		int h;
		int w;
		if (wRatio > hRatio) {
			w = width;
			h = (int) (w / ratio);
		} else {
			h = height;
			w = (int) (h * ratio);
		}

		g.drawImage(image, borderInsets.left + (width - w) / 2, borderInsets.top + (height - h) / 2, w, h, this);
	}

	protected final void paintConverImage(Graphics g, Insets borderInsets) {
		int width = getWidth() - borderInsets.left - borderInsets.right;
		int height = getHeight() - borderInsets.top - borderInsets.bottom;
		double wRatio = (double) image.getWidth(this) / width;
		double hRatio = (double) image.getHeight(this) / height;
		double ratio = (double) image.getWidth(this) / image.getHeight(this);
		int x = 0;
		int y = 0;

		int h;
		int w;
		if (wRatio > hRatio) {
			h = image.getHeight(this);
			w = (int) (image.getWidth(this) * width / (height * ratio));
			x = (image.getWidth(this) - w) / 2;
		} else {
			w = image.getWidth(this);
			h = (int) (image.getHeight(this) * height / (width / ratio));
			y = (image.getHeight(this) - h) / 2;
		}

		g.drawImage(image, borderInsets.left, borderInsets.top, borderInsets.left + width, borderInsets.top + height, x, y, x + w, y + h, this);
	}
}