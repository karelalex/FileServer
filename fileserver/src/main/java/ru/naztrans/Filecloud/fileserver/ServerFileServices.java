package ru.naztrans.Filecloud.fileserver;

import ru.naztrans.Filecloud.common.FileClass;
import ru.naztrans.Filecloud.common.FileView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ServerFileServices {
    public static boolean writeFile (String name, String  nick, byte[] body){
        int i=1;
        while (fileExist(name, nick)) {
            name+=i;
            i++;
        }
        try {
            Files.write(Paths.get(Properties.MAIN_PATH + nick + "\\" + name), body, StandardOpenOption.CREATE);
            System.out.println("Записан файл" + name);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return false;
        }
    }
    public static boolean fileExist(String name, String nick) {
        return Files.exists((Paths.get(Properties.MAIN_PATH + nick + "\\" + name)));
    }
    public static boolean deleteFile(String name, String nick) {
        try {
            return Files.deleteIfExists((Paths.get(Properties.MAIN_PATH + nick + "\\" + name)));
        } catch (IOException e) {
            e.printStackTrace();
        }

            return false;

    }
    public static ArrayList<FileView> getFileList(String nick) throws IOException {
        ArrayList<FileView> list = new ArrayList<>();
        Files.list(Paths.get(Properties.MAIN_PATH + nick)).filter(s -> !Files.isDirectory(s)).forEach(s -> {

            try {
                list.add(new FileView(s.getFileName().toString(), Files.size(s)));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        return list;
    };

    public static FileClass createFileObject(String filename, String nick) {

        try {
            byte[] bytes=Files.readAllBytes(Paths.get(Properties.MAIN_PATH + nick + "\\" + filename));
            FileClass fc=new FileClass(filename, bytes);
            return fc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
