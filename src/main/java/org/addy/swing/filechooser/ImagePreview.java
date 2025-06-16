package org.addy.swing.filechooser;

import org.addy.swing.JPictureBox;
import org.addy.swing.SizeMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

public class ImagePreview
        extends JPictureBox
        implements PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int VIEWER_SIZE = 256;
    private static final int BORDER_THICKNESS = 8;
    
    private File file = null;

    public ImagePreview(JFileChooser fc) {
        setSizeMode(SizeMode.CONTAIN);
        setPreferredSize(new Dimension(VIEWER_SIZE, VIEWER_SIZE));
        setBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
        fc.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
            case JFileChooser.DIRECTORY_CHANGED_PROPERTY -> file = null;
            case JFileChooser.SELECTED_FILE_CHANGED_PROPERTY -> file = (File) e.getNewValue();
        }

        if (isShowing() && file != null && file.isFile()) {
            try {
                setImage(ImageIO.read(file));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
