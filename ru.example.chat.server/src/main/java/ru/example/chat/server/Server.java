package ru.example.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

    private Map<Socket, User> clients = new HashMap<>();

    private class ServerThread extends Thread {

        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(port)) {
                while (running) {
                    Socket client = server.accept();
                    threadPool.submit(new ClientListener(client));
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                        ex.getMessage(), ex);
            }
        }

    }

    private class ClientListener implements Runnable {

        private Socket client;

        private ClientListener(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in =
                        new ObjectInputStream(client.getInputStream());
                while (true) {
                    if (client.isClosed()) {
                        removeClient(client);
                        break;
                    }
                    Object clientObj = in.readObject();
                    handleMessageFromClient(clientObj, client);
                }
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                        ex.getMessage(), ex);
                try {
                    removeClient(client);
                } catch (IOException e) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                            ex.getMessage(), ex);
                }
            }
        }

    }

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
        serverThread = new ServerThread();
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

    private void handleMessageFromClient(Object clientObj, Socket client)
            throws IOException {
        if (clientObj instanceof User) {
            User user = (User) clientObj;
            ObjectOutputStream out =
                    new ObjectOutputStream(client.getOutputStream());
            users.put(user, out);
            clients.put(client, user);
            sendUserList();
        } else if (clientObj instanceof Message) {
            Message message = (Message) clientObj;
            if (null != message.getReceiver()) {
                ObjectOutputStream receiverOutputStream =
                        users.get(message.getReceiver());
                if (null != receiverOutputStream) {
                    receiverOutputStream.writeObject(message);
                }
            } else {
                for (Entry<User, ObjectOutputStream> entry : users.entrySet()) {
                    if (message.getAuthor().equals(entry.getKey())) {
                        continue;
                    }
                    entry.getValue().writeObject(message);
                }
            }
        }
    }

    protected void sendUserList() throws IOException {
        for (ObjectOutputStream oos : users.values()) {
            oos.writeObject(new HashSet<>(users.keySet()));
        }
    }

    protected void removeClient(Socket client) throws IOException {
        User removedUser = clients.remove(client);
        users.remove(removedUser);
        sendUserList();
    }

}
