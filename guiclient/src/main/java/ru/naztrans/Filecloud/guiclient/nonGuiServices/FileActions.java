package ru.naztrans.Filecloud.guiclient.nonGuiServices;

import ru.naztrans.Filecloud.common.FileActionMsg;
import ru.naztrans.Filecloud.common.FileClass;
import ru.naztrans.Filecloud.common.FileListMsg;
import ru.naztrans.Filecloud.common.FileView;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileActions {
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
    static public ArrayList<FileView> getFileList(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        FileActionMsg msg = new FileActionMsg(ru.naztrans.Filecloud.common.FileActions.GETFILELIST, "");
        out.writeObject(msg);
        Object obj=in.readObject();
        if (obj instanceof FileListMsg){
            FileListMsg message=(FileListMsg)obj;
            return message.fileList;
        }
        else throw new ClassNotFoundException("Неправильный класс ответного сообщения");
    }
}
