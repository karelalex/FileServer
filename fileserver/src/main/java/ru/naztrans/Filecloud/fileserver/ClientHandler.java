package ru.naztrans.Filecloud.fileserver;

import ru.naztrans.Filecloud.common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String nick;


    public String getNick() {
        return nick;
    }


    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Всё ништяк");

            new Thread(() -> {
                try {
                    while (true) {
                        Object obj = null;
                        try {
                            obj = in.readObject();
                            System.out.println("Читаю Объект");

                            if (obj instanceof AuthMsg) {
                                AuthMsg msg = (AuthMsg) obj;
                                if (msg.getAct() == AuthAction.SING_IN) {
                                    String username = msg.getUsername();
                                    String pass = msg.getPassword();


                                    if (server.isNickBusy(username)) {
                                        out.writeObject(new AuthMsg(AuthAction.ALREADY_IN, nick));
                                        continue;
                                    }
                                    if (AuthService.checkUser(username, pass)) {
                                        System.out.println("Ник и пароль правильные");
                                        out.writeObject(new AuthMsg(AuthAction.LOGIN_SUCCESS, username));
                                        nick = username;
                                        server.subscribe(this);
                                        System.out.println("Клиент авторизован");
                                        continue;
                                    } else {
                                        out.writeObject(new AuthMsg(AuthAction.WRONG_CREDITS, nick));
                                        continue;
                                    }

                                }
                                if (msg.getAct()==AuthAction.SING_UP){
                                    String username = msg.getUsername();
                                    String pass = msg.getPassword();
                                    if (ServerFileServices.createDIR(username) && AuthService.addUser(username, pass)){
                                        out.writeObject(new AuthMsg(AuthAction.LOGIN_SUCCESS, username));
                                        nick = username;
                                        server.subscribe(this);
                                    };
                                }
                                if (msg.getAct()==AuthAction.LOG_OFF){
                                    socket.close();
                                }


                            }
                            if (nick != null) {
                                if (obj instanceof FileClass) {

                                    ServerFileServices.writeFile(((FileClass) obj).name, nick, ((FileClass) obj).body);
                                    sendFileList();

                                }
                                if (obj instanceof FileActionMsg) {

                                    FileActionMsg msg = (FileActionMsg) obj;
                                    if (msg.action == FileActions.GETFILELIST) {
                                        System.out.println("Получил запрос на список файлов");
                                        sendFileList();

                                    }
                                    if (msg.action==FileActions.DELETE){
                                        if (ServerFileServices.deleteFile(msg.filename, nick)) sendFileList();
                                    }
                                    if (msg.action==FileActions.RENAME){
                                        if (ServerFileServices.renameFile(msg.filename, msg.newFilename, nick)) sendFileList();
                                    }
                                    if (msg.action==FileActions.GET){
                                        FileClass fc=ServerFileServices.createFileObject(msg.filename, nick);
                                        if (fc!=null) {
                                            out.writeObject(fc);
                                        }
                                    }
                                }
                            }
                            else out.writeObject(new AuthMsg(AuthAction.REQUIRE_AUTH));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendFileList() {
        try {

            out.writeObject(new FileListMsg(ServerFileServices.getFileList(nick)));
            System.out.println("Отправил список файлов");
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

}
