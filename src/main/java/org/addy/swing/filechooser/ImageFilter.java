package org.addy.swing.filechooser;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.addy.util.FileUtil;

public class ImageFilter extends FileFilter {
    private static final java.io.FileFilter innerFilter = new FileUtil.ContentTypeFilter("image/*");

    @Override
    public boolean accept(File f) {
        return innerFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return "All image types (*.jpg, *.png, *.gif, *.tiff, *.bmp...)";
    }
}
