package ru.naztrans.Filecloud.guiclient.nonGuiServices;

import ru.naztrans.Filecloud.common.FileActionMsg;
import ru.naztrans.Filecloud.common.FileActions;
import ru.naztrans.Filecloud.common.FileClass;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
    static public void saveFile(){

    }
}
