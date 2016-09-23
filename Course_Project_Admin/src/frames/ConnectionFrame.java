package frames;

import client.ClientController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ConnectionFrame extends AbstractFrame {

    private JTextArea textInfoIp;
    private JTextArea textInfoPort;
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

        textInfoIp = new JTextArea("Введите IP-адрес сервера");
        textInfoPort = new JTextArea("Введите порт сервера");
        textFieldIp = new JTextField(20);
        textFieldPort = new JTextField(20);
        buttonTryConnection = new JButton("Подключиться");

        // TODO delete. For test
        textFieldIp.setText("127.0.0.1");
        textFieldPort.setText("1329");

        add(textInfoIp);
        add(textFieldIp);
        add(textInfoPort);
        add(textFieldPort);
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
