package ru.example.chat.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.example.chat.server.model.Message;
import ru.example.chat.server.model.User;
import ru.example.chat.ui.view.ChatView;
import ru.example.chat.ui.view.LoginDialog;

/**
 * Запускатель клиента.
 */
public class ClientLauncher {

    public static User user;

    /**
     * Запускает клиент.
     * 
     * @param args аргументы запуска
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("start client");

        String host = (null != args && args.length > 0) ? args[0] : "127.0.0.1";
        int port =
                (null != args && args.length > 1) ? Integer.parseInt(args[1])
                        : 7777;

        Client client = new Client();

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Чат");
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ChatView view = new ChatView(client);
        view.createContent(shell);

        shell.open();

        LoginDialog loginDialog = new LoginDialog(shell);
        if (0 == loginDialog.open()) {
            client.startConnection(host, port);
            user = loginDialog.getUser();
            shell.setText("Чат - " + user);
            client.sendMessage(user);
        } else {
            shell.close();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true) {
                if (client.isClosed()) {
                    break;
                }
                Object message;
                try {
                    message = client.readMessage();
                } catch (Exception e) {
                    Logger.getLogger(ClientLauncher.class.getName()).log(
                            Level.SEVERE, e.getMessage(), e);
                    break;
                }
                Display.getDefault().asyncExec(() -> {
                    if (!shell.isDisposed()) {
                        handleResponse(view, message);
                    }
                });
            }
        });

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        executor.shutdown();
        display.dispose();
        client.stopConnection();
    }

    private static void handleResponse(ChatView view, Object response) {
        if (response instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<User> users = (Set<User>) response;
            List<Object> input = new ArrayList<>();
            input.add("Общий чат");
            input.addAll(users);
            view.getUsersViewer().setInput(input.toArray());
            view.getUsersViewer().refresh();
        } else if (response instanceof Message) {
            Message msg = (Message) response;
            User author = (null == msg.getReceiver()) ? null : msg.getAuthor();
            view.switchToReceiver(author);
            Text history = view.getMessageForm(author).getHistory();
            StringBuilder historyText = new StringBuilder(history.getText());
            historyText.append(System.lineSeparator()).append(msg.getAuthor())
                    .append(": ").append(msg.getText());
            history.setText(historyText.toString());
            history.getParent().layout();
        }
    }
}
