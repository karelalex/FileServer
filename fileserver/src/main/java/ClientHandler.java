import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
                            AuthMsg msg = (AuthMsg)obj;
                            if(msg.getAct()==AuthAction.singIn){
                                nick=msg.getUsername();
                                System.out.println(nick);
                                String pass=msg.getPassword();
                                System.out.println(pass);
                                if (nick != null) {
                                    if (server.isNickBusy(nick)) {
                                        out.writeObject(new AuthMsg(AuthAction.alreadyIn, nick));
                                        continue;
                                    }
                                    if(nick.equals("user1") && pass.equals("pass1"))
                                    {
                                        System.out.println("Ник и пароль правильные");
                                        out.writeObject(new AuthMsg(AuthAction.success, nick));
                                        server.subscribe(this);
                                        System.out.println("Клиент подключися");
                                        break;
                                    }
                                } else {
                                    out.writeObject(new AuthMsg(AuthAction.wrongCredits, nick));
                                }
                            }


                        }
                        else out.writeObject(new AuthMsg(AuthAction.requireAuth));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    while (true) {
                        Object obj = null;
                        try {
                            obj = in.readObject();

                            if (obj instanceof FileClass) {

                                Files.write(Paths.get(Properties.MAIN_PATH+nick+"\\"+((FileClass) obj).name), ((FileClass) obj).body, StandardOpenOption.CREATE);
                                System.out.println("написал файл");

                            }
                            //if (obj instanceof )
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
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
