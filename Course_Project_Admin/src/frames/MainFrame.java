package frames;

import client.ClientController;
import connection.MessageType;
import ray.Ray;
import ray.StateRay;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alexey on 22.09.2016.
 */
public class MainFrame extends AbstractFrame {

    private JTextArea textAreaShowInformation;
    private JTextField textFieldSendInformation;

    private JPanel panelEditRay;
    private JLabel labelEditInfoAboutRayPanel;
    private JLabel labelEditInfoPanel;
    private JButton buttonEditRay;
    private JButton buttonResumeRay;
    private JButton buttonCancelRay;
    private JPanel panelDateEditSending;
    private Ray selectedRay;

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

        //////////////////////////////////////////////////////////////////////////////////////////
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        add(infoPanel, BorderLayout.EAST);

        textAreaShowInformation = new JTextArea(20, 22);
        textAreaShowInformation.setEditable(false);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        infoPanel.add(new JScrollPane(textAreaShowInformation), gridBagConstraints);

        textFieldSendInformation = new JTextField(22);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        infoPanel.add(textFieldSendInformation, gridBagConstraints);

        JPanel buttonsPanelNorth = new JPanel();
        buttonsPanelNorth.setLayout(new GridLayout(1, 2, 5, 0));
        add(buttonsPanelNorth, BorderLayout.NORTH);

        JButton buttonExit = new JButton("Выйти");
        buttonsPanelNorth.add(buttonExit);
        buttonExit.addActionListener(event -> {
            controller.signOut();
        });

        if (controller.nowSysAdmin()) {
            JButton buttonManageAccounts = new JButton("Управление аккаунтами");
            buttonsPanelNorth.add(buttonManageAccounts);
            buttonManageAccounts.addActionListener(event -> {
                controller.openWindowForManageAccounts();
            });
        }
        ///////////////////////////////////////////////////////////////////////////////////////////

        JPanel buttonsPanelSouth = new JPanel();
        buttonsPanelSouth.setLayout(new GridLayout(1, 2, 5, 0));
        add(buttonsPanelSouth, BorderLayout.SOUTH);

        JButton buttonAddNewRay = new JButton("Добавить новый рейс");
        buttonsPanelSouth.add(buttonAddNewRay);
        buttonAddNewRay.addActionListener(event -> {
            controller.openWindowForAddNewRay();
        });

        JButton buttonSendDataMessage = new JButton("Отправить информационное сообщение");
        buttonsPanelSouth.add(buttonSendDataMessage);

        buttonSendDataMessage.addActionListener(event -> {
            controller.sendInfoMessage(textFieldSendInformation.getText());
            textFieldSendInformation.setText("");
        });
        /////////////////////////////////////////////////////////////////////////////////////////////

        java.util.List<Ray> rayListFromModel = controller.getListRays();
        listModelRays = new DefaultListModel<>();
        if (rayListFromModel != null) {
            rayListFromModel.stream().forEach(listModelRays::addElement);
        }

        raysList = new JList<>(listModelRays);
        raysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        raysList.setFixedCellWidth(285);
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
                        new SimpleDateFormat("HH:mm:ss").format(new Date(ray.timeInWay - 7200000)) +
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
                selectedRay = raysList.getSelectedValue();
                setEditPanel();
            }
        });
        add(new JScrollPane(raysList), BorderLayout.WEST);
        /////////////////////////////////////////////////////////////////////////////////////////////////

        panelEditRay = new JPanel();
        panelEditRay.setLayout(null);
        panelEditRay.setVisible(false);
        JPanel panelParentEdit = new JPanel();
        panelParentEdit.setLayout(new BorderLayout());
        panelParentEdit.add(panelEditRay, BorderLayout.CENTER);
        add(panelParentEdit, BorderLayout.CENTER);

        labelEditInfoPanel = new JLabel();
        labelEditInfoPanel.setBounds(30, 10, 200, 40);
        panelEditRay.add(labelEditInfoPanel);

        labelEditInfoAboutRayPanel = new JLabel();
        labelEditInfoAboutRayPanel.setBounds(8, 55, 250, 90);
        panelEditRay.add(labelEditInfoAboutRayPanel);

        panelDateEditSending = new JPanel();
        panelDateEditSending.setLayout(new FlowLayout());
        panelDateEditSending.setBounds(3, 140, 260, 75);
        panelEditRay.add(panelDateEditSending);

        JLabel labelEditTextInfo = new JLabel("<html><div style='text-align: center;'>Дата отправления <br>(укажите в формате 'дд.мм.гггг')</div></html>");
        panelDateEditSending.add(labelEditTextInfo);

        JTextField textFieldDateSendingRay = new JTextField(7);
        panelDateEditSending.add(textFieldDateSendingRay);

        JLabel labelHours = new JLabel("Часы:");
        panelDateEditSending.add(labelHours);
        JTextField textFieldNewRaySendingHours = new JTextField(2);
        panelDateEditSending.add(textFieldNewRaySendingHours);
        JLabel labelMinutes = new JLabel("Минуты:");
        panelDateEditSending.add(labelMinutes);
        JTextField textFieldNewRaySendingMinutes = new JTextField(2);
        panelDateEditSending.add(textFieldNewRaySendingMinutes);

        buttonEditRay = new JButton("Изменить рейс");
        buttonEditRay.setBounds(60, 220, 130, 30);
        String[] optionsEditRay = {"Да, изменить", "Нет, не изменять"};
        buttonEditRay.addActionListener(event -> {
            if (selectedRay != null) {
                String hoursSending = textFieldNewRaySendingHours.getText().trim().equals("") ? "0" : textFieldNewRaySendingHours.getText();
                String minutesSending = textFieldNewRaySendingMinutes.getText().trim().equals("") ? "0" : textFieldNewRaySendingMinutes.getText();
                Date newDateSending = AddNewRayFrame.validateDate(textFieldDateSendingRay.getText(), hoursSending, minutesSending);
                if(newDateSending != null) {
                    int i = JOptionPane.showOptionDialog(this, "Изменить рейс " + selectedRay.coordinates + "?", "Изменение рейса",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsEditRay, optionsEditRay[1]);
                    if (i == 0) {
                        textFieldNewRaySendingHours.setText("");
                        textFieldNewRaySendingMinutes.setText("");
                        textFieldDateSendingRay.setText("");
                        controller.editRay(new Ray(selectedRay.id, selectedRay.coordinates, StateRay.NEW,
                                newDateSending, selectedRay.timeInWay, selectedRay.numberRay, selectedRay.places));
                    }
                } else {
                    buttonEditRay.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "<html>Невалидно указана дата (Дату указывайте с нынешнего момента)</html>", "Невнимательность - это плохо!", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        });
        panelEditRay.add(buttonEditRay);

        buttonCancelRay = new JButton("Отменить рейс");
        buttonCancelRay.setBounds(60, 260, 130, 30);
        String[] optionsCancelRay = {"Да, отменить", "Нет, не отменять"};
        buttonCancelRay.addActionListener(event -> {
            if (selectedRay != null) {
                int i = JOptionPane.showOptionDialog(this, "Отменить рейс " + selectedRay.coordinates + "?", "Отмена рейса",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsCancelRay, optionsCancelRay[1]);
                if (i == 0) {
                    controller.editRay(new Ray(selectedRay.id, selectedRay.coordinates, StateRay.CANCEL,
                            selectedRay.timeSending, selectedRay.timeInWay, selectedRay.numberRay, selectedRay.places));
                }
            }
        });
        panelEditRay.add(buttonCancelRay);

        buttonResumeRay = new JButton("Возобновить рейс");
        buttonResumeRay.setBounds(45, 260, 160, 30);
        String[] optionsResumeRay = {"Да, возобновить", "Нет, не возобновлять"};
        buttonResumeRay.addActionListener(event -> {
            if (selectedRay != null) {
                int i = JOptionPane.showOptionDialog(this, "Возобновить рейс " + selectedRay.coordinates + "?", "Возобновление рейса",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsResumeRay, optionsResumeRay[1]);
                if (i == 0) {
                    controller.editRay(new Ray(selectedRay.id, selectedRay.coordinates, StateRay.NEW,
                            selectedRay.timeSending, selectedRay.timeInWay, selectedRay.numberRay, selectedRay.places));
                }
            }
        });
        panelEditRay.add(buttonResumeRay);

    }


    String[] colors = {"#4f7af1", "#fdba00", "#FF4081", "#00d904", "red"};

    private void setEditPanel() {
        panelEditRay.setVisible(true);
        labelEditInfoPanel.setText("<html><div style='width: 150px; border: 2px double black; text-align: center'>Панель управления рейсом<br>"
                + selectedRay.coordinates
                + "</div></html>");
        String itemList = "<html><style>\n" +
                "    div.wrapper {\n" +
                "        width:190px;\n" +
                "    }\n" +
                "</style>\n" +
                "<div class=\"wrapper\">\n" +
                "    <div>\n" +
                "<span style='color: blue; font-size: 13px;'>" +
                (int) selectedRay.id +
                " </span><span style='font-size: 13px;'>" +
                selectedRay.coordinates.toString() +
                " </span><br>\n" +
                "    <span>Дата отправления: " +
                new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(selectedRay.timeSending) +
                "</span><br>" +
                "<span>Время в пути: " +
                new SimpleDateFormat("HH:mm:ss").format(new Date(selectedRay.timeInWay - 7200000)) +
                "</span><br>\n" +
                "    <span>Статус: </span><span style='color: " + colors[selectedRay.stateRay.ordinal()] + ";'>" +
                selectedRay.stateRay +
                "</span>\n" +
                "</div>" +
                "</html>";
        labelEditInfoAboutRayPanel.setText(itemList);
        if (selectedRay.stateRay != StateRay.COMPLETED && selectedRay.stateRay != StateRay.SENDING) {
            buttonResumeRay.setVisible(selectedRay.stateRay == StateRay.CANCEL);
            buttonCancelRay.setVisible(selectedRay.stateRay != StateRay.CANCEL);
            panelDateEditSending.setVisible(true);
            buttonEditRay.setVisible(true);
        } else {
            panelDateEditSending.setVisible(false);
            buttonEditRay.setVisible(false);
            buttonResumeRay.setVisible(false);
            buttonCancelRay.setVisible(false);
        }
    }

    @Override
    public void event(MessageType type, Object object) {
        switch (type) {
            case DATA: {
                textAreaShowInformation.append(object + "\n");
                break;
            }
            case RAY_LIST: {
                java.util.List<Ray> rayListFromModel = controller.getListRays();
                if (rayListFromModel != null) {
                    if (selectedRay != null) {
                        if (rayListFromModel.contains(selectedRay)) {
                            selectedRay = rayListFromModel.get(rayListFromModel.indexOf(selectedRay));
                            setEditPanel();
                        }
                        else panelEditRay.setVisible(false);
                    }
                    SwingUtilities.invokeLater(() -> {
                        listModelRays.clear();
                        rayListFromModel.stream().forEach(listModelRays::addElement);
                        SwingUtilities.invokeLater(this::revalidate);
                    });
                }

            }
        }
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(835, 500);
    }
}
