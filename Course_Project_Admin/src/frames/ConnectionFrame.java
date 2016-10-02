package frames;

import client.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ConnectionFrame extends AbstractFrame {

    private JLabel labelInfoIp;
    private JLabel labelInfoPort;
    private JTextField textFieldIp;
    private JTextField textFieldPort;
    private JButton buttonTryConnection;

    private String ip;
    private int port;

    public ConnectionFrame(ClientController clientController) throws HeadlessException {
        super(clientController);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());
        setBackground(Color.DARK_GRAY);

        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 13);

        labelInfoIp = new JLabel("Введите IP-адрес сервера");
        labelInfoIp.setFont(font);
        labelInfoIp.setForeground(Color.WHITE);
        add(labelInfoIp);
        textFieldIp = new JTextField(20);
        // TODO delete. For test
        textFieldIp.setText("127.0.0.1");
        add(textFieldIp);

        labelInfoPort = new JLabel("Введите порт сервера");
        labelInfoPort.setFont(font);
        labelInfoPort.setForeground(Color.WHITE);
        add(labelInfoPort);
        textFieldPort = new JTextField(20);
        // TODO delete. For test
        textFieldPort.setText("1329");
        add(textFieldPort);

        buttonTryConnection = new JButton("Подключиться");
        add(buttonTryConnection);

        buttonTryConnection.addActionListener(e -> {
            try {
                ip = textFieldIp.getText();
                port = Integer.parseInt(textFieldPort.getText());
                controller.tryConnection(ip, port);
            } catch (NumberFormatException ignore) {
                JOptionPane.showMessageDialog(this, "С портом явно что-то не так, ну ка исправляй!", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
