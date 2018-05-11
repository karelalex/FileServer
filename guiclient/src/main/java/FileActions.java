import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
