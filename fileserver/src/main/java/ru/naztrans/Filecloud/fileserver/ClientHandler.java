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
                                if (msg.getAct() == AuthAction.singIn) {
                                    String username = msg.getUsername();
                                    String pass = msg.getPassword();


                                    if (server.isNickBusy(username)) {
                                        out.writeObject(new AuthMsg(AuthAction.alreadyIn, nick));
                                        continue;
                                    }
                                    if (AuthService.checkUser(username, pass)) {
                                        System.out.println("Ник и пароль правильные");
                                        out.writeObject(new AuthMsg(AuthAction.success, username));
                                        nick = username;
                                        server.subscribe(this);
                                        System.out.println("Клиент авторизован");
                                        continue;
                                    } else {
                                        out.writeObject(new AuthMsg(AuthAction.wrongCredits, nick));
                                        continue;
                                    }

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
                                }
                            }
                            else out.writeObject(new AuthMsg(AuthAction.requireAuth));
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
            System.out.println("Отправил ответ");
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

}
