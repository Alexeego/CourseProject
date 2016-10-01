package frames;

import client.ClientController;
import connection.MessageType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexey on 22.09.2016.
 */
public class MainFrame extends AbstractFrame{

    private JTextArea textAreaShowInformation;
    private JTextField textFieldSendInformation;
    private JButton buttonSendDataMessage;

    private JButton buttonAddNewRay;

    public MainFrame(ClientController controller) {
        super(controller);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new FlowLayout());

        textAreaShowInformation = new JTextArea(10, 50);
        textAreaShowInformation.setEditable(false);
        textFieldSendInformation = new JTextField(50);
        buttonSendDataMessage = new JButton("Отправить информационное сообщение");

        add(new JScrollPane(textAreaShowInformation));
        add(textFieldSendInformation);
        add(buttonSendDataMessage);

        buttonSendDataMessage.addActionListener(event ->{
            controller.sendInfoMessage(textFieldSendInformation.getText());
            textFieldSendInformation.setText("");
        });

        buttonAddNewRay = new JButton("Добавить новый рейс");
        add(buttonAddNewRay);

        buttonAddNewRay.addActionListener(event -> {
            controller.addNewRay();
        });
    }

    @Override
    public void event(MessageType type, Object object) {
        switch (type){
            case DATA: {
                textAreaShowInformation.append(object + "\n");
                break;
            }
        }
    }
}
