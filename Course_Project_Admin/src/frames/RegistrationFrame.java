package frames;

import client.ClientController;

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

    }

    @Override
    public Dimension getDimension() {
        return new Dimension(300, 200);
    }
}
