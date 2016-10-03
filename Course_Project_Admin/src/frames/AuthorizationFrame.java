package frames;

import client.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public class AuthorizationFrame extends AbstractFrame {

    private JLabel labelInfoName;
    private JLabel labelInfoPass;
    private JTextField textFieldName;
    private JPasswordField textFieldPass;
    private JButton buttonAuthorization;

    public AuthorizationFrame(ClientController clientController) {
        super(clientController);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 13);

        labelInfoName = new JLabel("Введите логин");
        labelInfoName.setFont(font);
        labelInfoName.setForeground(Color.WHITE);
        add(labelInfoName);
        textFieldName = new JTextField(20);
        add(textFieldName);

        labelInfoPass = new JLabel("Введите пароль");
        labelInfoPass.setFont(font);
        labelInfoPass.setForeground(Color.WHITE);
        add(labelInfoPass);
        textFieldPass = new JPasswordField(20);
        add(textFieldPass);

        buttonAuthorization = new JButton("Вход");
        add(buttonAuthorization);

        buttonAuthorization.addActionListener(e -> controller.authorization(textFieldName.getText(), textFieldPass.getText()));
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(300, 200);
    }
}
