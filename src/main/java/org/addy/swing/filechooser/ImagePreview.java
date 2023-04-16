package org.addy.swing.filechooser;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import org.addy.swing.JPictureBox;
import org.addy.swing.SizeMode;

public class ImagePreview extends JPictureBox implements PropertyChangeListener {
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
        String prop = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
        }
        else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
        }

        if (isShowing() && file != null) {
            try {
                setImage(ImageIO.read(file));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
