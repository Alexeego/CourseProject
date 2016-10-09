package client;

import connection.MessageType;
import frames.*;

import javax.swing.*;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ClientView extends JFrame {
    private final ClientController clientController;

    private AbstractFrame nowPanel = null;

    ClientView(ClientController clientController) {
        this.clientController = clientController;
        initializationWindow();
    }

    private void initializationWindow() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(300, 200);
        setFrame(new ConnectionFrame(clientController));

        setVisible(true);
    }

    void showMessageInfo(Object message, MessageType type, String... title) {
        if (type != null)
            nowPanel.event(type, message);
        else
            JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Инфо", JOptionPane.INFORMATION_MESSAGE);
    }

    void showMessageError(String message, String... title) {
        JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Error", JOptionPane.ERROR_MESSAGE);
    }

    void updateWindow(ClientModel.ConnectionState nowConnectionState) {
        AbstractFrame frame;
        switch (nowConnectionState) {
            case TRY_CONNECTION:
                frame = new ConnectionFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            case AUTHORIZATION:
                frame = new AuthorizationFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            case REGISTRATION: {
                frame = new RegistrationFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            }
            case CONNECTED: {
                frame = new MainFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            }
            case ADD_NEW_RAY: {
                frame = new AddNewRayFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            }
            case SYS_ADMIN_MANAGE_USERS: {
                frame = new ManageAccountsFrame(clientController);
                setSize(frame.getDimension());
                setFrame(frame);
                break;
            }
        }
    }

    private void setFrame(AbstractFrame frame) {
        setLocationRelativeTo(null);
        if (nowPanel != null) {
            remove(nowPanel);
        }
        nowPanel = frame;
        add(frame);
        revalidate();
    }
}
