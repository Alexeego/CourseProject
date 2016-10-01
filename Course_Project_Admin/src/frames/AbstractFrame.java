package frames;

import client.ClientController;
import connection.MessageType;

import javax.swing.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public abstract class AbstractFrame extends JPanel implements InfoListener{

    protected final ClientController controller;

    protected AbstractFrame(ClientController controller) {
        this.controller = controller;
        initializationWindow();
    }

    protected abstract void initializationWindow();

    @Override
    public void event(MessageType type, Object object) {
        // Void
    }
}
