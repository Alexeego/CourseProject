package client;

import connection.Connection;
import frames.*;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 21.09.2016.
 */
public class ClientView extends JFrame{
    private final ClientController clientController;

    private JPanel nowPanel = null;

    public ClientView(ClientController clientController) {
        this.clientController = clientController;
        initializationWindow();
    }

    private void initializationWindow(){
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setFrame(new ConnectionFrame(clientController));

        setVisible(true);
    }

    public void showMessageInfo(String message, String... title){
        JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Инфо", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageError(String message, String... title){
        JOptionPane.showMessageDialog(this, message, title.length > 0 ? title[0] : "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void updateWindow(ClientModel.ConnectionState nowConnectionState){
        switch (nowConnectionState){
            case TRY_CONNECTION:
                setFrame(new ConnectionFrame(clientController));
                break;
            case AUTHORIZATION:
                setFrame(new AuthorizationFrame(clientController));
                break;
            case REGISTRATION: {
                setFrame(new RegistrationFrame(clientController));
                break;
            }
            case CONNECTED: {
                setFrame(new ConnectionFrame(clientController));
                break;
            }
        }
    }

    protected void setFrame(AbstractFrame frame){
        if(nowPanel != null) {
            remove(nowPanel);
        }
        nowPanel = frame;
        add(frame);
        revalidate();
    }
}
