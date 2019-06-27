package ru.example.chat.ui;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

    private static User user;

    /**
     * Запускает клиент.
     * 
     * @param args аргументы запуска
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("start client");
        Client client = new Client();

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Чат");
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ChatView view = new ChatView();
        view.createContent(shell);

        shell.open();

        LoginDialog loginDialog = new LoginDialog(shell);
        if (0 == loginDialog.open()) {
            client.startConnection("127.0.0.1", 6666);
            user = loginDialog.getUser();
            shell.setText("Чат - " + user);
            client.sendMessage(user);
        } else {
            shell.close();
        }

        view.getSend().addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                Message message =
                        Message.builder().author(user)
                                .text(view.getInput().getText()).build();
                view.getInput().setText("");
                view.getInput().getParent().layout();
                try {
                    client.sendMessage(message);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });

        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                Object message;
                try {
                    message = client.readMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
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
        display.dispose();
        client.stopConnection();
    }

    private static void handleResponse(ChatView view, Object response) {
        if (response instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<User> users = (Set<User>) response;
            view.getUsers()
                    .setText(
                            users.stream()
                                    .map(User::toString)
                                    .collect(
                                            Collectors.joining(System
                                                    .lineSeparator())));
            view.getUsers().getParent().layout();
        } else if (response instanceof Message) {
            Message msg = (Message) response;
            Text history = view.getHistory();
            StringBuilder historyText = new StringBuilder(history.getText());
            historyText.append(System.lineSeparator()).append(msg.getAuthor())
                    .append(": ").append(msg.getText());
            history.setText(historyText.toString());
            history.getParent().layout();
        }
    }
}
