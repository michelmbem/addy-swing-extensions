package org.addy.swing.imageeditor;

import org.addy.swing.JPictureBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serial;

/** Interactive translucent crop selector layered over a {@link JPictureBox}. */
public final class CropOverlay extends JComponent {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int HANDLE_SIZE = 8;

    private final JPictureBox pictureBox;
    private Rectangle selection;
    private Rectangle dragBounds;
    private Point dragStart;
    private DragMode dragMode = DragMode.NONE;

    public CropOverlay(JPictureBox pictureBox) {
        this.pictureBox = pictureBox;
        setOpaque(false);
        setVisible(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Rectangle imageArea = getImageArea();
                if (!imageArea.contains(event.getPoint())) return;
                dragStart = event.getPoint();
                dragBounds = selection == null ? null : new Rectangle(selection);
                dragMode = getDragMode(event.getPoint());
                if (dragMode == DragMode.NONE) dragMode = DragMode.NEW;
                if (dragMode == DragMode.NEW) selection = new Rectangle(dragStart.x, dragStart.y, 0, 0);
                updateCursor(event.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                updateSelection(event.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                updateSelection(event.getPoint());
                dragStart = null;
                dragBounds = null;
                dragMode = DragMode.NONE;
                updateCursor(event.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                updateCursor(event.getPoint());
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /** Starts a new crop selection, initially covering most of the displayed image. */
    public void beginSelection() {
        Rectangle imageArea = getImageArea();
        int insetX = Math.max(1, imageArea.width / 10);
        int insetY = Math.max(1, imageArea.height / 10);
        selection = new Rectangle(imageArea.x + insetX, imageArea.y + insetY,
                Math.max(1, imageArea.width - insetX * 2), Math.max(1, imageArea.height - insetY * 2));
        setVisible(true);
        repaint();
    }

    /** Returns the selected crop rectangle in source-image pixels, or {@code null}. */
    public Rectangle getSelectedCropBounds() {
        Rectangle imageArea = getImageArea();
        if (selection == null || selection.width < 1 || selection.height < 1 || imageArea.isEmpty())
            return null;

        int imageWidth = pictureBox.getImage().getWidth(pictureBox);
        int imageHeight = pictureBox.getImage().getHeight(pictureBox);
        int left = (int) Math.floor((selection.x - imageArea.x) * imageWidth / (double) imageArea.width);
        int top = (int) Math.floor((selection.y - imageArea.y) * imageHeight / (double) imageArea.height);
        int right = (int) Math.ceil((selection.x + selection.width - imageArea.x) * imageWidth / (double) imageArea.width);
        int bottom = (int) Math.ceil((selection.y + selection.height - imageArea.y) * imageHeight / (double) imageArea.height);
        left = Math.max(0, Math.min(imageWidth - 1, left));
        top = Math.max(0, Math.min(imageHeight - 1, top));
        right = Math.max(left + 1, Math.min(imageWidth, right));
        bottom = Math.max(top + 1, Math.min(imageHeight, bottom));
        return new Rectangle(left, top, right - left, bottom - top);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!isVisible() || pictureBox.getImage() == null) return;

        Graphics2D g = (Graphics2D) graphics.create();
        Rectangle imageArea = getImageArea();
        Area shade = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        if (selection != null && selection.width > 0 && selection.height > 0)
            shade.subtract(new Area(selection));
        g.setColor(new Color(0, 0, 0, 125));
        g.fill(shade);
        if (selection != null && selection.width > 0 && selection.height > 0) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(3f));
            g.draw(selection);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1f));
            g.draw(selection);
            paintHandles(g);
        } else {
            g.setColor(new Color(255, 255, 255, 90));
            g.draw(imageArea);
        }
        g.dispose();
    }

    private void paintHandles(Graphics2D g) {
        int half = HANDLE_SIZE / 2;
        int[][] points = {
                {selection.x, selection.y},
                {selection.x + selection.width / 2, selection.y},
                {selection.x + selection.width, selection.y},
                {selection.x, selection.y + selection.height / 2},
                {selection.x + selection.width, selection.y + selection.height / 2},
                {selection.x, selection.y + selection.height},
                {selection.x + selection.width / 2, selection.y + selection.height},
                {selection.x + selection.width, selection.y + selection.height}
        };
        g.setColor(Color.WHITE);
        for (int[] point : points) g.fillRect(point[0] - half, point[1] - half, HANDLE_SIZE, HANDLE_SIZE);
        g.setColor(new Color(35, 105, 190));
        for (int[] point : points) g.drawRect(point[0] - half, point[1] - half, HANDLE_SIZE, HANDLE_SIZE);
    }

    private void updateSelection(Point point) {
        if (dragStart == null) return;
        Rectangle imageArea = getImageArea();
        Point end = clampToImageArea(point, imageArea);
        int dx = end.x - dragStart.x;
        int dy = end.y - dragStart.y;
        switch (dragMode) {
            case NEW -> selection = rectangleBetween(dragStart, end);
            case MOVE -> {
                int x = Math.max(imageArea.x, Math.min(imageArea.x + imageArea.width - dragBounds.width, dragBounds.x + dx));
                int y = Math.max(imageArea.y, Math.min(imageArea.y + imageArea.height - dragBounds.height, dragBounds.y + dy));
                selection = new Rectangle(x, y, dragBounds.width, dragBounds.height);
            }
            case RESIZE_N, RESIZE_S, RESIZE_E, RESIZE_W,
                    RESIZE_NE, RESIZE_NW, RESIZE_SE, RESIZE_SW -> resizeSelection(end, imageArea);
            case NONE -> { }
        }
        repaint();
    }

    private void resizeSelection(Point point, Rectangle imageArea) {
        int left = dragBounds.x;
        int top = dragBounds.y;
        int right = dragBounds.x + dragBounds.width;
        int bottom = dragBounds.y + dragBounds.height;
        int minWidth = Math.min(HANDLE_SIZE * 2, imageArea.width);
        int minHeight = Math.min(HANDLE_SIZE * 2, imageArea.height);
        if (dragMode.hasWest()) left = Math.max(imageArea.x, Math.min(right - minWidth, point.x));
        if (dragMode.hasEast()) right = Math.min(imageArea.x + imageArea.width, Math.max(left + minWidth, point.x));
        if (dragMode.hasNorth()) top = Math.max(imageArea.y, Math.min(bottom - minHeight, point.y));
        if (dragMode.hasSouth()) bottom = Math.min(imageArea.y + imageArea.height, Math.max(top + minHeight, point.y));
        selection = new Rectangle(left, top, right - left, bottom - top);
    }

    private void updateCursor(Point point) {
        DragMode mode = getDragMode(point);
        int cursor = switch (mode) {
            case RESIZE_N, RESIZE_S -> Cursor.N_RESIZE_CURSOR;
            case RESIZE_E, RESIZE_W -> Cursor.E_RESIZE_CURSOR;
            case RESIZE_NE, RESIZE_SW -> Cursor.NE_RESIZE_CURSOR;
            case RESIZE_NW, RESIZE_SE -> Cursor.NW_RESIZE_CURSOR;
            default -> Cursor.CROSSHAIR_CURSOR;
        };
        setCursor(Cursor.getPredefinedCursor(cursor));
    }

    private DragMode getDragMode(Point point) {
        if (selection == null) return DragMode.NONE;
        int tolerance = HANDLE_SIZE;
        boolean nearLeft = Math.abs(point.x - selection.x) <= tolerance;
        boolean nearRight = Math.abs(point.x - (selection.x + selection.width)) <= tolerance;
        boolean nearTop = Math.abs(point.y - selection.y) <= tolerance;
        boolean nearBottom = Math.abs(point.y - (selection.y + selection.height)) <= tolerance;
        boolean acrossX = point.x >= selection.x - tolerance && point.x <= selection.x + selection.width + tolerance;
        boolean acrossY = point.y >= selection.y - tolerance && point.y <= selection.y + selection.height + tolerance;
        if (nearTop && nearLeft) return DragMode.RESIZE_NW;
        if (nearTop && nearRight) return DragMode.RESIZE_NE;
        if (nearBottom && nearLeft) return DragMode.RESIZE_SW;
        if (nearBottom && nearRight) return DragMode.RESIZE_SE;
        if (nearTop && acrossX) return DragMode.RESIZE_N;
        if (nearBottom && acrossX) return DragMode.RESIZE_S;
        if (nearLeft && acrossY) return DragMode.RESIZE_W;
        if (nearRight && acrossY) return DragMode.RESIZE_E;
        if (selection.contains(point)) return DragMode.MOVE;
        return DragMode.NONE;
    }

    private Rectangle rectangleBetween(Point a, Point b) {
        return new Rectangle(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(b.x - a.x), Math.abs(b.y - a.y));
    }

    private Point clampToImageArea(Point point, Rectangle imageArea) {
        return new Point(
                Math.max(imageArea.x, Math.min(imageArea.x + imageArea.width, point.x)),
                Math.max(imageArea.y, Math.min(imageArea.y + imageArea.height, point.y)));
    }

    private Rectangle getImageArea() {
        Image image = pictureBox.getImage();
        if (image == null) return new Rectangle();
        Insets insets = pictureBox.getInsets();
        int availableWidth = pictureBox.getWidth() - insets.left - insets.right;
        int availableHeight = pictureBox.getHeight() - insets.top - insets.bottom;
        int imageWidth = image.getWidth(pictureBox);
        int imageHeight = image.getHeight(pictureBox);
        if (availableWidth <= 0 || availableHeight <= 0 || imageWidth <= 0 || imageHeight <= 0)
            return new Rectangle();

        double scale = Math.min(availableWidth / (double) imageWidth, availableHeight / (double) imageHeight);
        int width = (int) Math.round(imageWidth * scale);
        int height = (int) Math.round(imageHeight * scale);
        int x = insets.left + (availableWidth - width) / 2;
        int y = insets.top + (availableHeight - height) / 2;
        return new Rectangle(x, y, width, height);
    }

    private enum DragMode {
        NONE, NEW, MOVE,
        RESIZE_N, RESIZE_S, RESIZE_E, RESIZE_W,
        RESIZE_NE, RESIZE_NW, RESIZE_SE, RESIZE_SW;

        boolean hasNorth() { return this == RESIZE_N || this == RESIZE_NE || this == RESIZE_NW; }
        boolean hasSouth() { return this == RESIZE_S || this == RESIZE_SE || this == RESIZE_SW; }
        boolean hasEast() { return this == RESIZE_E || this == RESIZE_NE || this == RESIZE_SE; }
        boolean hasWest() { return this == RESIZE_W || this == RESIZE_NW || this == RESIZE_SW; }
    }
}
