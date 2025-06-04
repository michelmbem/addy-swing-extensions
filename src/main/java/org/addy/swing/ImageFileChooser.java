package org.addy.swing;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.addy.swing.filechooser.ImageFileView;
import org.addy.swing.filechooser.ImageFilter;
import org.addy.swing.filechooser.ImagePreview;
import org.addy.util.FileUtil;

public class ImageFileChooser extends JFileChooser {
    @Serial
    private static final long serialVersionUID = 1L;

    public ImageFileChooser() {
        super();
        init();
    }

    public ImageFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        init();
    }

    public ImageFileChooser(File currentDirectory) {
        super(currentDirectory);
        init();
    }

    public ImageFileChooser(FileSystemView fsv) {
        super(fsv);
        init();
    }

    public ImageFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        init();
    }

    public ImageFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        init();
    }

    private void init() {
        ImageFilter filter = new ImageFilter();
        addChoosableFileFilter(filter);
        setFileFilter(filter);
        setAcceptAllFileFilterUsed(false);
        setFileView(new ImageFileView());
        setAccessory(new ImagePreview(this));
    }

    public byte[] readFileBytes() {
        try {
            return FileUtil.readAllBytes(getSelectedFile());
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeFileBytes(byte[] bytes, boolean append) {
        try {
            FileUtil.writeAllBytes(getSelectedFile(), bytes, append);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
