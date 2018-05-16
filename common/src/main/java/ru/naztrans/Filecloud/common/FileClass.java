package ru.naztrans.Filecloud.common;

import java.io.Serializable;

public class FileClass implements Serializable {
    public String name;
    public byte[] body;

    public FileClass(String name, byte[] body) {
        this.name = name;
        this.body = body;
    }
}
