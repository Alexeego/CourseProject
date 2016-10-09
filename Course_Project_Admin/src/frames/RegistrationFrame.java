package frames;

import client.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public class RegistrationFrame extends AbstractFrame {

    public RegistrationFrame(ClientController clientController) {
        super(clientController);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 13);

        JLabel labelReg = new JLabel("<html><h2>Регистрация</h2></html>");
        labelReg.setFont(font);
        labelReg.setForeground(Color.WHITE);
        add(labelReg);

        JLabel labelLog = new JLabel("Введите логин для нового аккаунта:");
        labelLog.setFont(font);
        labelLog.setForeground(Color.WHITE);
        add(labelLog);
        JTextField nameEditField = new JTextField(20);
        add(nameEditField);

        JLabel labelFirstPass = new JLabel("Введите пароль:");
        labelFirstPass.setFont(font);
        labelFirstPass.setForeground(Color.WHITE);
        add(labelFirstPass);
        JTextField passwordFirstEditField = new JTextField(20);
        add(passwordFirstEditField);


        JLabel labelSecondPass = new JLabel("Введите пароль ещё раз:");
        labelSecondPass.setFont(font);
        labelSecondPass.setForeground(Color.WHITE);
        add(labelSecondPass);
        JTextField passwordSecondEditField = new JTextField(20);
        add(passwordSecondEditField);

        JButton buttonRegistration = new JButton("Зарегистрироваться");
        add(buttonRegistration);
        buttonRegistration.addActionListener(event -> {
            if (!nameEditField.getText().trim().equals("") && !passwordFirstEditField.getText().trim().equals("")
                    && passwordFirstEditField.getText().equals(passwordSecondEditField.getText()))
                controller.registration(nameEditField.getText(), passwordFirstEditField.getText());
            else
                JOptionPane.showMessageDialog(this, "Некорректно заполнены поля!", "Ошибка регистрации", JOptionPane.ERROR_MESSAGE);
        });


        JButton buttonCancel = new JButton("Назад");
        add(buttonCancel);
        buttonCancel.addActionListener(event -> controller.toBackPressed(this));
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(300, 300);
    }
}
