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

        JButton buttonCancel = new JButton("Назад");
        add(buttonCancel);
        buttonCancel.addActionListener(event -> controller.toBackPressed(this));
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(300, 200);
    }
}
