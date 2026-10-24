package org.addy.swing.filechooser;

import org.addy.util.FileUtil;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public final class FileFilters {
    private FileFilters() {}

    public static FileFilter of(java.io.FileFilter filter, String description) {
        return new FileFilter() {
            @Override
            public boolean accept(File f) {
                return filter.accept(f);
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }

    public static FileFilter ofExtension(String extensions, String description) {
        return of(new FileUtil.ExtensionFilter(extensions), description);
    }

    public static FileFilter ofContentType(String contentTypes, String description) {
        return of(new FileUtil.ContentTypeFilter(contentTypes), description);
    }

    public static FileFilter ofPattern(String pattern, String description) {
        return of(new FileUtil.PatternFilter(pattern), description);
    }
}
