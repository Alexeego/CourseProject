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

    public ClientView(ClientController clientController) {
        this.clientController = clientController;
        initializationWindow();
    }

    private void initializationWindow() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(300, 200);
        setFrame(new ConnectionFrame(clientController));

        setVisible(true);
    }

    public void showMessageInfo(String message, MessageType type, String... title) {
        if (type != null)
            nowPanel.event(type, message);
        else
            JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Инфо", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageError(String message, String... title) {
        JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void updateWindow(ClientModel.ConnectionState nowConnectionState) {
        switch (nowConnectionState) {
            case TRY_CONNECTION:
                setSize(300, 200);
                setFrame(new ConnectionFrame(clientController));
                break;
            case AUTHORIZATION:
                setSize(300, 200);
                setFrame(new AuthorizationFrame(clientController));
                break;
            case REGISTRATION: {
                setFrame(new RegistrationFrame(clientController));
                break;
            }
            case CONNECTED: {
                setSize(600, 400);
                setFrame(new MainFrame(clientController));
                break;
            }
            case ADD_NEW_RAY: {
                setSize(450, 550);
                setFrame(new AddNewRayFrame(clientController));
                break;
            }
        }
    }

    protected void setFrame(AbstractFrame frame) {
        setLocationRelativeTo(null);
        if (nowPanel != null) {
            remove(nowPanel);
        }
        nowPanel = frame;
        add(frame);
        revalidate();
    }
}
