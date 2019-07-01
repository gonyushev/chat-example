package ru.example.chat.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ru.example.chat.server.model.User;
import ru.example.chat.ui.Client;

public class ChatView {

    private TableViewer usersViewer;

    private Map<User, MessageForm> messageForms = new HashMap<>();

    private StackLayout stack;

    private Composite messageForm;

    private Client client;

    public ChatView(Client client) {
        this.client = client;
    }

    public void createContent(Shell shell) {
        SashForm main = new SashForm(shell, SWT.BORDER | SWT.HORIZONTAL);
        main.setLayout(new GridLayout());
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createUserList(main);

        messageForm = new Composite(main, SWT.NONE);
        stack = new StackLayout();
        messageForm.setLayout(stack);
        messageForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createMessageForm(messageForm, null);

        main.setWeights(new int[] {30, 70});
    }

    protected void createMessageForm(Composite parent, User user) {
        MessageForm form = new MessageForm(parent, user, client);
        messageForms.put(user, form);
        stack.topControl = form.getTextSash();
        messageForm.layout();
    }

    private void createUserList(SashForm main) {
        usersViewer = new TableViewer(main);
        usersViewer.setContentProvider(ArrayContentProvider.getInstance());
        usersViewer.getTable().setHeaderVisible(true);
        usersViewer.getTable().setLinesVisible(true);
        TableViewerColumn viewerColumn =
                new TableViewerColumn(usersViewer, SWT.NONE);
        viewerColumn.getColumn().setWidth(300);
        viewerColumn.getColumn().setText("Пользователи");
        viewerColumn.setLabelProvider(new ColumnLabelProvider());
        usersViewer.setInput(new String[0]);
        GridLayoutFactory.fillDefaults().generateLayout(usersViewer.getTable());

        usersViewer.addOpenListener(new IOpenListener() {

            @Override
            public void open(OpenEvent event) {
                Object selected =
                        ((IStructuredSelection) event.getSelection())
                                .getFirstElement();
                User receiver =
                        (selected instanceof User) ? (User) selected : null;
                switchToReceiver(receiver);
            }

        });
    }

    public TableViewer getUsersViewer() {
        return usersViewer;
    }

    public MessageForm getMessageForm(User user) {
        return messageForms.get(user);
    }

    public void switchToReceiver(User receiver) {
        MessageForm form = messageForms.get(receiver);
        if (null == form) {
            createMessageForm(messageForm, receiver);
        } else {
            stack.topControl = form.getTextSash();
            messageForm.layout();
        }
    }

}
