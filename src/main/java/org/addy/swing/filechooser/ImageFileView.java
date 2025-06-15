package org.addy.swing.filechooser;

import org.addy.swing.UIHelper;
import org.addy.util.FileUtil;
import org.addy.util.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageFileView extends FileView {
    @Override
    public String getName(File f) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public String getDescription(File f) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public Boolean isTraversable(File f) {
        return null; // let the L&F FileView figure this out
    }

    @Override
    public String getTypeDescription(File f) {
        try {
            return Stream.of(FileUtil.getContentType(f).split("/"))
                    .map(StringUtil::pascalCase)
                    .collect(Collectors.joining(" "));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Icon getIcon(File f) {
        Icon icon = null;
    
        if (f.isFile()) {
            try {
                String mimeType = FileUtil.getContentType(f);
                icon = switch (mimeType) {
                    case "image/jpeg" -> createImageIcon("JPEG");
                    case "image/png" -> createImageIcon("PNG");
                    case "image/gif" -> createImageIcon("GIF");
                    case "image/tiff" -> createImageIcon("TIF");
                    default -> createImageIcon("BMP");
                };
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return icon;
    }

    private ImageIcon createImageIcon(String name) {
        return UIHelper.loadIcon(getClass(), name + ".png", 16, -1);
    }
}
