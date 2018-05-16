package ru.naztrans.Filecloud.common;

import java.io.Serializable;
import java.util.ArrayList;

public class FileListMsg implements Serializable {
        public ArrayList<FileView> fileList;

    public FileListMsg(ArrayList<FileView> fileList) {
        this.fileList = fileList;
    }
}
