package frames;

import client.ClientController;
import connection.MessageType;
import ray.Ray;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Created by Alexey on 22.09.2016.
 */
public class MainFrame extends AbstractFrame {

    private JTextArea textAreaShowInformation;
    private JTextField textFieldSendInformation;
    private JButton buttonSendDataMessage;

    private JButton buttonAddNewRay;

    private JList<Ray> raysList;
    private DefaultListModel<Ray> listModelRays;

    public MainFrame(ClientController controller) {
        super(controller);
    }

    @Override
    protected void initializationWindow() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setBackground(Color.DARK_GRAY);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        add(infoPanel, BorderLayout.EAST);

        textAreaShowInformation = new JTextArea(20, 28);
        textAreaShowInformation.setEditable(false);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        infoPanel.add(new JScrollPane(textAreaShowInformation), gridBagConstraints);

        textFieldSendInformation = new JTextField(28);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        infoPanel.add(textFieldSendInformation, gridBagConstraints);


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 0));
        add(buttonsPanel, BorderLayout.SOUTH);

        buttonAddNewRay = new JButton("Добавить новый рейс");
        buttonsPanel.add(buttonAddNewRay);

        buttonAddNewRay.addActionListener(event -> {
            controller.openWindowForAddNewRay();
        });

        buttonSendDataMessage = new JButton("Отправить информационное сообщение");
        buttonsPanel.add(buttonSendDataMessage);

        buttonSendDataMessage.addActionListener(event -> {
            controller.sendInfoMessage(textFieldSendInformation.getText());
            textFieldSendInformation.setText("");
        });

        java.util.List<Ray> rayListFromModel = controller.getListRays();
        listModelRays = new DefaultListModel<>();

        if (rayListFromModel != null) {
            for (Ray ray : rayListFromModel)
                listModelRays.addElement(ray);
        }

        raysList = new JList<>(listModelRays);
        raysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        raysList.setFixedCellWidth(300);
        raysList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                JLabel label = (JLabel) component;
                Ray ray = (Ray) value;
                String itemList = "<html><style>\n" +
                        "    div.wrapper {\n" +
                        "        border: 4px double black;\n" +
                        "        width:218px;\n" +
                        "    }\n" +
                        "</style>\n" +
                        "<div class=\"wrapper\">\n" +
                        "    <div>\n" +
                        "<span style='color: blue; font-size: 15px;'>" +
                        (int) ray.id +
                        " </span><span style='font-size: 15px;'>" +
                        ray.coordinates.toString() +
                        " </span><br>\n" +
                        "    <span>Дата отправления: " +
                        new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(ray.timeSending) +
                        "</span><br>" +
                        "<span>Время в пути: " +
                        new SimpleDateFormat("HH:mm:ss").format(ray.timeInWay) +
                        "</span><br>\n" +
                        "    <span>Статус: </span><span style='color: " + colors[ray.stateRay.ordinal()] + ";'>" +
                        ray.stateRay +
                        "</span>\n" +
                        "</div>" +
                        "</html>";
                label.setText(itemList);
                return label;
            }
        });
        raysList.addListSelectionListener(event -> {
            if (!raysList.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(this, "Выбран " + raysList.getSelectedValue().coordinates);
            }
            raysList.clearSelection();
        });
        add(new JScrollPane(raysList), BorderLayout.WEST);

    }

    private String[] colors = {"#4f7af1", "#fdba00", "#FF4081", "#00d904", "red"};

    @Override
    public void event(MessageType type, Object object) {
        switch (type) {
            case DATA: {
                textAreaShowInformation.append(object + "\n");
                break;
            }
            case RAY_LIST: {
                SwingUtilities.invokeLater(() -> {
                    listModelRays.clear();
                    java.util.List<Ray> rayListFromModel = controller.getListRays();
                    if (rayListFromModel != null) {
                        for (Ray ray : rayListFromModel)
                            listModelRays.addElement(ray);
                        revalidate();
                    }
                });

            }
        }
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(635, 500);
    }
}
