package ru.naztrans.Filecloud.fileserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private ServerSocket serverSocket;

    private Vector<ClientHandler> clients;

    public Server() {
        try {
            SQLService.init();
            serverSocket = new ServerSocket(8189);
            clients = new Vector<ClientHandler>();
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLService.quit();
        }
    }







    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);

    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);

    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }
}
