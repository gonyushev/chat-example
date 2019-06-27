package ru.example.chat.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChatView {

    private Text users;

    private Text history;

    private Text input;

    private Button send;

    public void createContent(Shell shell) {
        SashForm main = new SashForm(shell, SWT.BORDER | SWT.HORIZONTAL);
        main.setLayout(new GridLayout());
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        users = new Text(main, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
        users.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        users.setToolTipText("Пользователи");

        SashForm textSash = new SashForm(main, SWT.BORDER | SWT.VERTICAL);
        textSash.setLayout(new GridLayout());
        textSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        history =
                new Text(textSash, SWT.BORDER | SWT.MULTI | SWT.WRAP
                        | SWT.READ_ONLY);
        history.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        history.setToolTipText("История");

        input = new Text(textSash, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        input.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        input.setToolTipText("Сообщение");

        send = new Button(textSash, SWT.PUSH);
        send.setText("Отправить");

        main.setWeights(new int[] {20, 80});
        textSash.setWeights(new int[] {70, 20, 10});
    }

    public Text getUsers() {
        return users;
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

}
