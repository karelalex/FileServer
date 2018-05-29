package ru.naztrans.Filecloud.guiclient.nonGuiServices;

import ru.naztrans.Filecloud.common.FileActionMsg;
import ru.naztrans.Filecloud.common.FileActions;
import ru.naztrans.Filecloud.common.FileClass;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileService {
    static public void sendFile(File file, ObjectOutputStream out){
        Path p=file.toPath();
        String filename=p.getFileName().toString();
        try {
            byte[] bytes=Files.readAllBytes(p);
            FileClass fo=new FileClass(filename, bytes);
            out.writeObject(fo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static public void getFileList(ObjectOutputStream out) throws IOException{
        FileActionMsg msg = new FileActionMsg(FileActions.GETFILELIST, "");
        out.writeObject(msg);



    }
    static public void saveFile(File file, byte[] body){
        Path p=file.toPath();
        try {
            Files.write(p, body, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static public void deleteFile(String filename, ObjectOutputStream out) {
        FileActionMsg msg = new FileActionMsg(FileActions.DELETE, filename);
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void askFile(String fileName, ObjectOutputStream out) {
        FileActionMsg msg = new FileActionMsg(FileActions.GET, fileName);
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
