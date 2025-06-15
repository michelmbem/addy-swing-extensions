package org.addy.swing;

import org.addy.swing.filechooser.ImageFileView;
import org.addy.swing.filechooser.ImageFilter;
import org.addy.swing.filechooser.ImagePreview;
import org.addy.util.FileUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.Objects;

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
        var filter = new ImageFilter();
        addChoosableFileFilter(filter);
        setFileFilter(filter);
        setAcceptAllFileFilterUsed(false);
        setFileView(new ImageFileView());
        setAccessory(new ImagePreview(this));
    }

    public Image getSelectedImage() {
        File selectedFile = getSelectedFile();

        if (selectedFile == null) return null;

        try {
            return ImageIO.read(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] readFileBytes() {
        File selectedFile = getSelectedFile();

        if (selectedFile == null) return new byte[0];

        try {
            return FileUtil.readAllBytes(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void writeFileBytes(byte[] bytes, boolean append) {
        File selectedFile = getSelectedFile();

        if (selectedFile == null) return;

        try {
            FileUtil.writeAllBytes(getSelectedFile(), Objects.requireNonNull(bytes), append);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
