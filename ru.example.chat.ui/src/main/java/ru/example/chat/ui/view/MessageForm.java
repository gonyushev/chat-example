package ru.example.chat.ui.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ru.example.chat.server.model.Message;
import ru.example.chat.server.model.User;
import ru.example.chat.ui.Client;
import ru.example.chat.ui.ClientLauncher;

public class MessageForm {

    private Text history;

    private Text input;

    private Button send;

    private SashForm textSash;

    MessageForm(Composite parent, User receiver, Client client) {
        textSash = new SashForm(parent, SWT.BORDER | SWT.VERTICAL);
        textSash.setLayout(new GridLayout());
        textSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite historyPanel = new Composite(textSash, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        historyPanel.setLayout(gridLayout);
        historyPanel
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(historyPanel, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                false));
        String name = (null == receiver) ? "Общий чат" : receiver.toString();
        label.setText(name);

        history =
                new Text(historyPanel, SWT.BORDER | SWT.MULTI | SWT.WRAP
                        | SWT.READ_ONLY);
        history.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        history.setToolTipText("История");

        Composite inputPanel = new Composite(textSash, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        inputPanel.setLayout(gridLayout);
        inputPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        textSash.setWeights(new int[] {80, 20});

        input = new Text(inputPanel, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        input.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        input.setToolTipText("Сообщение");

        send = new Button(inputPanel, SWT.PUSH);
        send.setText("Отправить");
        send.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false,
                false));

        send.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                Message message =
                        Message.builder().author(ClientLauncher.user)
                                .receiver(receiver).text(input.getText())
                                .build();
                input.setText("");
                StringBuilder historyText =
                        new StringBuilder(history.getText());
                historyText.append(System.lineSeparator())
                        .append(message.getAuthor()).append(": ")
                        .append(message.getText());
                history.setText(historyText.toString());
                input.getParent().layout();
                try {
                    client.sendMessage(message);
                } catch (ClassNotFoundException | IOException e) {
                    Logger.getLogger(ClientLauncher.class.getName()).log(
                            Level.SEVERE, e.getMessage(), e);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
    }

    public Text getHistory() {
        return history;
    }

    public Text getInput() {
        return input;
    }

    public Button getSend() {
        return send;
    }

    public SashForm getTextSash() {
        return textSash;
    }

}
