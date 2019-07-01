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
        int port =
                (null != args && args.length != 0) ? Integer.parseInt(args[0])
                        : 7777;
        Server server = new Server(port);
        server.start();
    }

}
