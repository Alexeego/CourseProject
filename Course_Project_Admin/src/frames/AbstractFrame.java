package frames;

import client.ClientController;

import javax.swing.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public abstract class AbstractFrame extends JPanel{

    protected final ClientController controller;

    protected AbstractFrame(ClientController controller) {
        this.controller = controller;
        initializationWindow();
    }

    protected abstract void initializationWindow();
}
