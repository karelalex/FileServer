package ru.naztrans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import ru.naztrans.AuthMsg;

import javax.swing.undo.AbstractUndoableEdit;

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
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        Object obj = null;
                        try {
                            obj = in.readObject();

                        if (obj instanceof AuthMsg) {
                            AuthMsg msg = (AuthMsg)obj;
                            if(msg.getAct()==AuthAction.singIn){
                                nick=msg.getUsername();
                                String pass=msg.getPassword();
                                if (nick != null) {
                                    if (server.isNickBusy(nick)) {
                                        out.writeObject(new AuthMsg(AuthAction.alreadyIn, nick));
                                        continue;
                                    }
                                    if(nick=="user1" && pass=="pass1")
                                    {
                                        out.writeObject(new AuthMsg(AuthAction.success, nick));
                                        server.subscribe(this);
                                        System.out.println("Клиеннт подключися");
                                        break;
                                    }
                                } else {
                                    out.writeObject(new AuthMsg(AuthAction.wrongCredits, nick));
                                }
                            }


                        }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    while (true) {
                        //some server logic
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finally {
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


}
