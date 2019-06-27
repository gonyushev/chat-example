package ru.example.chat.ui.view;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.example.chat.server.model.User;

public class LoginDialog extends TitleAreaDialog {

    private Text txtFirstName;

    private Text lastNameText;

    private User user;

    public LoginDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Аутентификация");
        setMessage("Введите имя пользователя", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createFirstName(container);
        createLastName(container);

        return area;
    }

    private void createFirstName(Composite container) {
        Label lbtFirstName = new Label(container, SWT.NONE);
        lbtFirstName.setText("Имя");

        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        txtFirstName = new Text(container, SWT.BORDER);
        txtFirstName.setLayoutData(dataFirstName);
    }

    private void createLastName(Composite container) {
        Label lbtLastName = new Label(container, SWT.NONE);
        lbtLastName.setText("Фамилия");

        GridData dataLastName = new GridData();
        dataLastName.grabExcessHorizontalSpace = true;
        dataLastName.horizontalAlignment = GridData.FILL;
        lastNameText = new Text(container, SWT.BORDER);
        lastNameText.setLayoutData(dataLastName);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void saveInput() {
        user =
                User.builder().name(txtFirstName.getText())
                        .lastname(lastNameText.getText()).build();

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public User getUser() {
        return user;
    }

}
