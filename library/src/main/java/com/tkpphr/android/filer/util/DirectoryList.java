package com.tkpphr.android.filer.util;

import java.io.File;

public class DirectoryList extends FileList {

    @Override
    public boolean isAddable(File file) {
        return super.isAddable(file) && file.isDirectory() && file.canRead();
    }
}
