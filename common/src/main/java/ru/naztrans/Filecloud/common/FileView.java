package ru.naztrans.Filecloud.common;

import java.io.Serializable;

public class FileView implements Serializable {
    private String fileName;
    private long size;

    public FileView(String filename, long size) {
        this.fileName = filename;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

}
