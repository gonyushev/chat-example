package ru.example.chat.ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private Socket clientSocket;

    private ObjectOutputStream out;

    private ObjectInputStream in;

    public void startConnection(String ip, int port)
            throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
    }

    public void sendMessage(Object msg) throws IOException,
            ClassNotFoundException {
        getOut().writeObject(msg);
    }

    public Object readMessage() throws IOException, ClassNotFoundException {
        return getIn().readObject();
    }

    public void stopConnection() throws IOException {
        if (null != in) {
            in.close();
        }
        if (null != out) {
            out.close();
        }
        clientSocket.close();
    }

    private ObjectOutputStream getOut() throws IOException {
        if (null == out) {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        }
        return out;
    }

    private ObjectInputStream getIn() throws IOException {
        if (null == in) {
            in = new ObjectInputStream(clientSocket.getInputStream());
        }
        return in;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

}
