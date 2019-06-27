package ru.example.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.example.chat.server.model.Message;
import ru.example.chat.server.model.User;

public class Server {

    private final int port;

    private boolean running;

    private final ExecutorService threadPool;

    private Thread serverThread;

    private Map<User, ObjectOutputStream> users = new HashMap<>();

    public Server(int port) {
        this.port = port;
        threadPool = Executors.newFixedThreadPool(10, new ThreadFactory() {

            private final AtomicInteger instanceCount = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                t.setName("HANDLER_" + instanceCount.getAndIncrement());
                return t;
            }
        });
    }

    public void start() {
        running = true;
        serverThread =
                new Thread(
                        () -> {
                            try (ServerSocket server = new ServerSocket(port)) {
                                while (running) {
                                    final Socket client = server.accept();
                                    threadPool.submit(() -> {
                                        try {
                                            ObjectInputStream in =
                                                    new ObjectInputStream(
                                                            client.getInputStream());
                                            while (true) {
                                                Object clientObj =
                                                        in.readObject();
                                                if (clientObj instanceof User) {
                                                    User user =
                                                            (User) clientObj;
                                                    ObjectOutputStream out =
                                                            new ObjectOutputStream(
                                                                    client.getOutputStream());
                                                    users.put(user, out);
                                                    for (ObjectOutputStream oos : users
                                                            .values()) {
                                                        oos.writeObject(new HashSet<>(
                                                                users.keySet()));
                                                    }
                                                } else if (clientObj instanceof Message) {
                                                    Message message =
                                                            (Message) clientObj;
                                                    for (ObjectOutputStream out : users
                                                            .values()) {
                                                        out.writeObject(message);
                                                    }
                                                }
                                            }
                                        } catch (Exception ex) {
                                            Logger.getLogger(
                                                    Server.class.getName())
                                                    .log(Level.SEVERE, null, ex);
                                        }
                                    });
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(Server.class.getName()).log(
                                        Level.SEVERE, null, ex);
                            }
                        });
        serverThread.setName("LISTENER");
        serverThread.start();
    }

    public void stop() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
        threadPool.shutdown();
        serverThread = null;
    }

}
