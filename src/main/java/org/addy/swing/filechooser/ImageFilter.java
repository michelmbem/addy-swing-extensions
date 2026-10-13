package org.addy.swing.filechooser;

import org.addy.util.FileUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFilter extends FileFilter {
    private static final java.io.FileFilter innerFilter = new FileUtil.ContentTypeFilter("image/*");

    @Override
    public boolean accept(File f) {
        return innerFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return "All image types";
    }
}
