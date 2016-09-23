package frames;

import client.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public class AuthorizationFrame extends AbstractFrame {

    private JTextArea textInfoName;
    private JTextArea textInfoPass;
    private JTextField textFieldName;
    private JTextField textFieldPass;
    private JButton buttonAuthorization;

    public AuthorizationFrame(ClientController clientController) {
        super(clientController);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());

        textInfoName = new JTextArea("Введите логин");
        textInfoPass = new JTextArea("Введите пароль");
        textFieldName = new JTextField(20);
        textFieldPass = new JTextField(20);
        buttonAuthorization = new JButton("Вход");

        add(textInfoName);
        add(textFieldName);
        add(textInfoPass);
        add(textFieldPass);
        add(buttonAuthorization);

        buttonAuthorization.addActionListener(e -> {
            controller.authorization(textFieldName.getText(), textFieldPass.getText());
        });
    }
}
