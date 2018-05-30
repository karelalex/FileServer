package ru.naztrans.Filecloud.common;

import java.io.Serializable;

public class FileActionMsg implements Serializable {
    public FileActions action;
    public String filename;
    public String newFilename;

    public FileActionMsg(FileActions action, String filename) {
        this.action = action;
        this.filename = filename;
    }
    public FileActionMsg(FileActions action, String filename, String newFilename){
        this(action, filename);
        this.newFilename=newFilename;
    }

}
