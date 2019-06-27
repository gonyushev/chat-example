package ru.example.chat.server;

import java.io.IOException;

/**
 * Запускатель сервера.
 */
public class ServerLauncher {

    /**
     * Запускает сервер.
     * 
     * @param args аргументы запуска
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        System.out.println("start server");
        Server server = new Server(6666);
        server.start();
    }

}
